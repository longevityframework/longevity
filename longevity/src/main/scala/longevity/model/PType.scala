package longevity.model

import emblem.TypeKey
import emblem.reflectionUtil.innerModule
import emblem.reflectionUtil.termsWithType
import emblem.typeKey
import longevity.exceptions.model.IndexDuplicatesKeyException
import longevity.exceptions.model.ptype.MultiplePrimaryKeysForPType
import longevity.exceptions.model.ptype.NoPropsForPTypeException
import longevity.exceptions.model.ptype.PrimaryKeyForDerivedPTypeException
import longevity.model.ptype.Index
import longevity.model.ptype.Key
import longevity.model.ptype.Partition
import longevity.model.ptype.PrimaryKey
import longevity.model.ptype.Prop
import longevity.model.ptype.QueryDsl
import scala.reflect.runtime.universe.TypeTag

/** a type class for a persistent object
 *
 * @tparam M the domain model
 * @tparam P the persistent class
 */
abstract class PType[M : ModelEv, P : TypeTag] {

  /** the type key for the persistent type */
  private[longevity] val pTypeKey = typeKey[P]

  /** the evidence for the persistent class */
  implicit val pEv = new PEv[M, P](pTypeKey)

  /** the [Prop properties] for this persistent type */
  // this has to be lazy because the PType must be initialized before we can scan the inner object
  // props. because the inner object props will use PType method `prop` to build the props.
  private[longevity] lazy val propSet: Set[Prop[P, _]] = pscan()

  /** the keys for this persistent type */
  // this (and the keys themselves) has to be lazy for similar reasons as above regarding the
  // propSet. a key takes at least one prop, which will in turn require a call to PType.prop
  private[longevity] lazy val keySet: Set[Key[M, P, _]] = kscan()

  /** the optional primary key for this persistent type */
  private[longevity] lazy val primaryKey: Option[PrimaryKey[M, P, _]] = {
    val primaryKeys = keySet.collect { case pk: PrimaryKey[M, P, _] => pk }
    if (this.isInstanceOf[DerivedPType[_, _, _]] && primaryKeys.nonEmpty) {
      throw new PrimaryKeyForDerivedPTypeException[P]
    }
    if (primaryKeys.size > 1) throw new MultiplePrimaryKeysForPType[P]
    primaryKeys.headOption
  }

  /** the indexes for this persistent type. defaults to the empty set */
  lazy val indexSet: Set[Index[P]] = Set.empty

  /** constructs a [[longevity.model.ptype.Prop Prop]] of type `A` from the
   * provided property path.
   *
   * the provided type `A` should match the type of the actual member in the
   * persistent class. type `A` should not contain any collections, or terminate
   * with [[longevity.model.PolyCType polymorphic component]].
   * violations will cause an exception to be thrown on
   * [[longevity.model.ModelType ModelType construction]].
   *
   * @tparam A the type of the property
   * @param path the property path
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = new Prop(path)

  /** constructs a key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the key
   */
  def key[V : KVEv[M, P, ?]](keyValProp: Prop[P, V]): Key[M, P, V] = {
    val keyValProp0 = keyValProp
    new {
      val keyValProp = keyValProp0
    } with Key[M, P, V]() {
      val ev = implicitly[KVEv[M, P, V]]
    }
  }

  /** constructs a primary key for this persistent type. the full key value
   * is used to determine the partition
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param hashed if `true`, then used a hashed partition (as opposed to a
   * ranged partition) when possible. defaults to `false`.
   */
  def primaryKey[V : KVEv[M, P, ?]](keyValProp: Prop[P, V], hashed: Boolean = false): Key[M, P, V] =
    primaryKey(keyValProp, partition(keyValProp), hashed)

  /** constructs a primary key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param partition describes the portion of the key value to use to determine
   * which node in the partition the data belongs to. this must form a prefix of
   * the `keyValProp`
   */
  def primaryKey[V : KVEv[M, P, ?]](keyValProp: Prop[P, V], partition: Partition[P]): Key[M, P, V] =
    primaryKey(keyValProp, partition, false)

  private def primaryKey[V : KVEv[M, P, ?]](
    keyValProp: Prop[P, V],
    partition: Partition[P],
    hashed: Boolean)
  : Key[M, P, V] = {
    val keyValProp0 = keyValProp
    new {
      val keyValProp = keyValProp0
    } with PrimaryKey[M, P, V](partition, hashed) {
      val ev = implicitly[KVEv[M, P, V]]
    }
  }

  /** a series of properties that determines the partitioning used by the
   * underlying database to distribute data across multiple nodes. used to form a
   * [[longevity.model.ptype.PrimaryKey primary key]]
   *
   * @param props the properties that determine the partition
   */
  def partition(props: Prop[P, _]*): Partition[P] = Partition(props)

  /** constructs an index for this persistent type based on the supplied set of
   * index props
   * 
   * @param propsHead the first of the properties that define this index
   * @param propsTail any remaining properties that define this index
   */
  def index(propsHead: Prop[P, _], propsTail: Prop[P, _]*): Index[P] =
    Index(propsHead :: propsTail.toList)

  /** contains implicit imports to make the query DSL work */
  lazy val queryDsl = new QueryDsl[P]

  private def pscan(): Set[Prop[P, _]] = {
    val props: Any = innerModule(this, "props").getOrElse {
      throw new NoPropsForPTypeException
    }
    implicit val propTypeKey = typeKey[Prop[P, _]].inMirrorOf(pTypeKey)
    termsWithType[Prop[P, _]](props).toSet
  }

  private def kscan(): Set[Key[M, P, _]] = {
    implicit val modelTypeTag = implicitly[ModelEv[M]].tag
    implicit val keyTypeKey = typeKey[Key[M, P, _]].inMirrorOf(pTypeKey)
    termsWithType[Key[M, P, _]](this).toSet
  }

  private[model] def validateKeysAndIndexes(): Unit = {
    var keyValProps: Set[Prop[P, _]] = keySet.map(_.keyValProp)
    indexSet.foreach { index =>
      if (index.props.size == 1 && keyValProps.contains(index.props.head)) {
        throw new IndexDuplicatesKeyException(pTypeKey)
      }
    }
  }

  override def toString = s"PType[${pTypeKey.name}]"

}
