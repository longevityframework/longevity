package longevity.model.realized

import emblem.TypeKey
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.typeBound.TypeBoundMap
import emblem.typeKey
import longevity.exceptions.model.DuplicateKeyException
import longevity.exceptions.model.InvalidPartitionException
import longevity.model.DerivedPType
import longevity.model.PType
import longevity.model.ptype.Key
import longevity.model.ptype.PrimaryKey
import longevity.model.ptype.Prop

private[longevity] class RealizedPType[M, P](
  pType: PType[M, P],
  polyPTypeOpt: Option[PType[M, _ >: P]],
  emblematic: Emblematic) {

  private val postPartitionProps: Seq[Prop[P, _]] = pType.primaryKey match {
    case Some(key) => postPartitionProps(key)
    case None => Seq()
  }

  private type MyProp[A] = Prop[P, A]
  private type MyRealizedProp[A] = RealizedProp[P, A]

  private val myRealizedProps: TypeBoundMap[Any, MyProp, MyRealizedProp] = {
    val empty = TypeBoundMap[Any, MyProp, MyRealizedProp]()
    def myProps = pType.propSet ++ postPartitionProps.toSet
    myProps.foldLeft(empty) { (acc, prop) =>
      def pair[A](prop: Prop[P, A]) = acc + (prop -> RealizedProp(prop, emblematic))
      pair(prop)
    }
  }

  type PProp[A] = Prop[_ >: P, A]
  type PRealizedProp[A] = RealizedProp[_ >: P, A]

  val realizedProps: TypeBoundMap[Any, PProp, PRealizedProp] = {
    def myWidenedProps = myRealizedProps.widen[PProp, PRealizedProp]
    pType match {
      case derivedPType: DerivedPType[_, P, _] =>
        def polyProps[Poly >: P](polyPTypeKey: TypeKey[Poly]) = {
          val empty = TypeBoundMap[Any, PProp, PRealizedProp]()
          polyPTypeOpt match {
            case None => empty
            case Some(polyPType) => polyPType.propSet.foldLeft(empty) { (acc, prop) =>
              def pair[Poly >: P, A](prop: Prop[Poly, A]) = {
                acc + (prop -> RealizedProp(prop, emblematic))
              }
              pair(prop)
            }
          }
        }
        myWidenedProps ++ polyProps(derivedPType.polyPTypeKey)
      case _ => myWidenedProps
    }
  }

  private val realizedKeyMap: Map[TypeKey[_], RealizedKey[M, P, _]] = {
    val empty = Map[TypeKey[_], RealizedKey[M, P, _]]()
    pType.keySet.foldLeft(empty) { (acc, key) =>
      def kvtk = key.keyValProp.propTypeKey
      if (acc.contains(kvtk)) throw new DuplicateKeyException()(pType.pTypeKey, kvtk)

      def accumulateKey[V](key: Key[M, P, _]) = acc + (kvtk -> realizedKeyForKey(key))

      def accumulatePKey[V](key: PrimaryKey[M, P, _]) =
        acc + (kvtk -> realizedKeyForPrimaryKey(key))

      pType.primaryKey match {
        case Some(pk) if pk == key => accumulatePKey(pk)
        case _ => accumulateKey(key)
      }
    }
  }

  def realizedKey[V : TypeKey]: RealizedKey[M, P, V] = {
    realizedKeyMap(typeKey[V]).asInstanceOf[RealizedKey[M, P, V]]
  }

  lazy val keySet: Set[RealizedKey[M, P, _]] = realizedKeyMap.values.toSet

  val primaryKey: Option[RealizedPrimaryKey[M, P, _]] = realizedKeyMap.values.collectFirst {
    case pk: RealizedPrimaryKey[M, P, _] => pk
  }

  private def postPartitionProps(key: PrimaryKey[M, P, _]): Seq[Prop[P, _]] =
    postPartitionPropInfos(key).map(_.prop)

  private def realizedKeyForKey[V](key: Key[M, P, V]): RealizedKey[M, P, V] = {
    val prop: Prop[P, V] = key.keyValProp
    new RealizedKey[M, P, V](key, myRealizedProps(prop), key.ev)
  }

  private def realizedKeyForPrimaryKey[V](key: PrimaryKey[M, P, V]): RealizedKey[M, P, V] = {
    val vTypeKey = key.keyValTypeKey
    val prop = myRealizedProps(key.keyValProp)
    val pppis = postPartitionPropInfos(key)
    val realizedPartitionProps = key.partition.props.map(myRealizedProps(_)) 
    val realizedPostPartitionProps = pppis.map(_.prop).map(myRealizedProps(_))
    val emblematicPropPaths = {
      def p2ppi[B](prop: Prop[P, B]) = {
        if (prop == key.keyValProp) {
          EmblematicPropPath.empty[V](vTypeKey)
        } else {
          val keyPropPath = prop.path.drop(key.keyValProp.path.size + 1)
          EmblematicPropPath[V, B](emblematic, keyPropPath)(vTypeKey, prop.propTypeKey)
        }
      }
      def pepps  = key.partition.props.map { prop => p2ppi(prop) }
      def ppepps = pppis.map(_.emblematicPropPath)
      pepps ++ ppepps
    }
    RealizedPrimaryKey[M, P, V](
      key,
      prop,
      key.ev,
      realizedPartitionProps,
      realizedPostPartitionProps,
      emblematicPropPaths)(
      pType.pTypeKey)
  }

  /** what we need to know about a properties that are within the primary key,
   * but are not part of the partition
   */
  private def postPartitionPropInfos[V](key: PrimaryKey[M, P, V]): Seq[PostPartitionPropInfo[V, _]] = {

    implicit val pTypeKey = pType.pTypeKey
    val vTypeKey = key.keyValTypeKey

    def ppis(keyProps: Seq[PostPartitionPropInfo[V, _]], partitionProps: Seq[Prop[P, _]])
    : Seq[PostPartitionPropInfo[V, _]] = {
      if (partitionProps.isEmpty) {
        keyProps
      } else if (keyProps.isEmpty) {
        throw new InvalidPartitionException[P](key)(pTypeKey)
      } else if (keyProps.head.prop == partitionProps.head) {
        ppis(keyProps.tail, partitionProps.tail)
      } else {
        val headProp = keyProps.head.prop
        val emblem = emblematic.emblems.getOrElse(
          throw new InvalidPartitionException[P](key)(pTypeKey))(
          headProp.propTypeKey)
        val headSubProps = emblem.props.map { emblemProp =>
          def ppi[B](emblemProp: EmblemProp[_, B]) = {
            val fullPropPath = s"${headProp.path}.${emblemProp.name}"
            val keyPropPath = fullPropPath.drop(key.keyValProp.path.size + 1)
            PostPartitionPropInfo[V, B](
              new Prop[P, B](fullPropPath)(pTypeKey.tag, emblemProp.typeKey.tag),
              EmblematicPropPath[V, B](emblematic, keyPropPath)(vTypeKey, emblemProp.typeKey))
          }
          ppi(emblemProp)
        }
        ppis(headSubProps ++ keyProps.tail, partitionProps)
      }
    }

    val ppi = PostPartitionPropInfo(key.keyValProp, EmblematicPropPath.empty(vTypeKey))
    ppis(Seq(ppi), key.partition.props)
  }

  /** what we need to know about a property that is within the primary key,
   * but is not part of the partition
   */
  private case class PostPartitionPropInfo[V, B](
    prop: Prop[P, B],
    emblematicPropPath: EmblematicPropPath[V, B])

}
