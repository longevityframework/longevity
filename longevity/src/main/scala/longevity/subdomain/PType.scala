package longevity.subdomain

import emblem.TypeKey
import emblem.reflectionUtil.innerModule
import emblem.reflectionUtil.termsWithType
import emblem.typeKey
import longevity.exceptions.subdomain.DuplicateKeyOrIndexException
import longevity.exceptions.subdomain.ptype.MultiplePartitionKeysForPType
import longevity.exceptions.subdomain.ptype.NoKeysForPTypeException
import longevity.exceptions.subdomain.ptype.NoPropsForPTypeException
import longevity.exceptions.subdomain.ptype.PartitionKeyForDerivedPTypeException
import longevity.subdomain.ptype.AnyKey
import longevity.subdomain.ptype.AnyPartitionKey
import longevity.subdomain.ptype.Index
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.Partition
import longevity.subdomain.ptype.PartitionKey
import longevity.subdomain.ptype.Prop
import longevity.subdomain.ptype.QueryDsl

/** a type class for a persistent object
 *
 * @tparam P the persistent type
 */
abstract class PType[P : TypeKey] {

  /** the type key for the persistent type */
  val pTypeKey = typeKey[P]

  /** the [Prop properties] for this persistent type */
  lazy val propSet: Set[Prop[P, _]] = pscan("props")

  /** the keys for this persistent type */
  lazy val keySet: Set[AnyKey[P]] = kscan("keys")

  /** the optional partition key for this persistent type */
  lazy val partitionKey: Option[AnyPartitionKey[P]] = {
    val partitionKeys = keySet.collect { case pk: AnyPartitionKey[P] => pk }
    if (this.isInstanceOf[DerivedPType[_, _]] && partitionKeys.nonEmpty) {
      throw new PartitionKeyForDerivedPTypeException[P]
    }
    if (partitionKeys.size > 1) throw new MultiplePartitionKeysForPType[P]
    partitionKeys.headOption
  }

  /** the indexes for this persistent type */
  lazy val indexSet: Set[Index[P]] = iscan("indexes")

  /** constructs a [[longevity.subdomain.ptype.Prop Prop]] of type `A` from the
   * provided property path.
   *
   * the provided type `A` should match the type of the actual member in the
   * persistent class. type `A` should not contain any collections, or terminate
   * with [[longevity.subdomain.PolyCType polymorphic component]].
   * violations will cause an exception to be thrown on
   * [[longevity.subdomain.Subdomain Subdomain construction]].
   *
   * @tparam A the type of the property
   * @param path the property path
   */
  def prop[A : TypeKey](path: String): Prop[P, A] = Prop(path, pTypeKey, typeKey[A])

  /** constructs a key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the key
   */
  def key[V <: KeyVal[P, V] : TypeKey](keyValProp: Prop[P, V]): Key[P, V] = new Key(keyValProp)

  /** constructs a partition key for this persistent type. the full key value
   * is used to determine the partition
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param hashed if `true`, then used a hashed partition (as opposed to a
   * ranged partition) when possible. defaults to `false`.
   */
  def partitionKey[V <: KeyVal[P, V] : TypeKey](
    keyValProp: Prop[P, V],
    hashed: Boolean = false)
  : PartitionKey[P, V] =
    partitionKey(keyValProp, partition(keyValProp), hashed)

  /** constructs a partition key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param partition describes the portion of the key value to use to determine
   * which node in the partition the data belongs to. this must form a prefix of
   * the `keyValProp`
   */
  def partitionKey[V <: KeyVal[P, V] : TypeKey](
    keyValProp: Prop[P, V],
    partition: Partition[P])
  : PartitionKey[P, V]
  = partitionKey(keyValProp, partition, false)

  private def partitionKey[V <: KeyVal[P, V] : TypeKey](
    keyValProp: Prop[P, V],
    partition: Partition[P],
    hashed: Boolean)
  : PartitionKey[P, V]
  = new PartitionKey(keyValProp, partition, hashed)

  /** a series of properties that determines the partitioning used by the
   * underlying database to distribute data across multiple nodes. used to form a
   * [[longevity.subdomain.ptype.PartitionKey partition key]]
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

  private def pscan(containerName: String): Set[Prop[P, _]] = {
    val props: Any = innerModule(this, "props").getOrElse {
      throw new NoPropsForPTypeException
    }
    implicit val pTypeTag = pTypeKey.tag
    implicit val propTypeKey = typeKey[Prop[P, _]].inMirrorOf(pTypeKey)
    termsWithType[Prop[P, _]](props).toSet
  }

  private def kscan(containerName: String): Set[AnyKey[P]] = {
    val keys: Any = innerModule(this, "keys").getOrElse {
      throw new NoKeysForPTypeException
    }
    implicit val pTypeTag = pTypeKey.tag
    implicit val anyKeyTypeKey = typeKey[AnyKey[P]].inMirrorOf(pTypeKey)
    val keySeq = termsWithType[AnyKey[P]](keys)
    val keySet = keySeq.toSet
    if (keySeq.size != keySet.size) {
      throw new DuplicateKeyOrIndexException(pTypeKey)
    }
    keySet
  }

  private def iscan(containerName: String): Set[Index[P]] = {
    implicit val pTypeTag = pTypeKey.tag
    implicit val indexTypeKey = typeKey[Index[P]].inMirrorOf(pTypeKey)
    val indexSeq = innerModule(this, "indexes").map(termsWithType[Index[P]]).getOrElse(Seq.empty[Index[P]])
    val indexSet = indexSeq.toSet
    if (indexSeq.size != indexSet.size) {
      throw new DuplicateKeyOrIndexException(pTypeKey)
    }
    indexSet
  }

  private[subdomain] def validateKeysAndIndexes(): Unit = {
    var keyValProps: Set[Prop[P, _]] = keySet.map(_.keyValProp)
    indexSet.foreach { index =>
      if (index.props.size == 1 && keyValProps.contains(index.props.head)) {
        throw new DuplicateKeyOrIndexException(pTypeKey)
      }
    }
  }

  override def toString = s"PType[${pTypeKey.name}]"

}
