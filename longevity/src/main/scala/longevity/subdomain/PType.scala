package longevity.subdomain

import emblem.TypeKey
import emblem.reflectionUtil.innerModule
import emblem.reflectionUtil.termsWithType
import emblem.typeKey
import longevity.exceptions.subdomain.IndexDuplicatesKeyException
import longevity.exceptions.subdomain.ptype.MultiplePartitionKeysForPType
import longevity.exceptions.subdomain.ptype.NoPropsForPTypeException
import longevity.exceptions.subdomain.ptype.PartitionKeyForDerivedPTypeException
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
  // this has to be lazy because the PType must be initialized before we can
  // scan the inner object props. because the inner object props will use PType
  // method `prop` to build the props.
  lazy val propSet: Set[Prop[P, _]] = pscan("props")

  /** the keys for this persistent type */
  val keySet: Set[Key[P]]

  /** an empty key set. this is a convenience method for people using Scala 2.11
   * who wish to declare an empty key set. you can always do it by hand with
   * `Set.empty`, but you will have to declare the element type of the set
   * yourself, like so:
   *
   * {{{
   * import longevity.subdomain.ptype.Key
   * @perstent(keySet = Set.empty[Key[Foo]])
   * }}}
   */
  def emptyKeySet = Set.empty[Key[P]]

  /** the optional partition key for this persistent type */
  lazy val partitionKey: Option[PartitionKey[P]] = {
    val partitionKeys = keySet.collect { case pk: PartitionKey[P] => pk }
    if (this.isInstanceOf[DerivedPType[_, _]] && partitionKeys.nonEmpty) {
      throw new PartitionKeyForDerivedPTypeException[P]
    }
    if (partitionKeys.size > 1) throw new MultiplePartitionKeysForPType[P]
    partitionKeys.headOption
  }

  /** the indexes for this persistent type. defaults to the empty set */
  val indexSet: Set[Index[P]] = Set.empty

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
  def prop[A : TypeKey](path: String): Prop[P, A] = new Prop(path)

  /** constructs a key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the key
   */
  def key[V <: KeyVal[P] : TypeKey](keyValProp: Prop[P, V]): Key[P] = {
    val keyValProp0 = keyValProp
    type V0 = V
    new {
      val keyValProp = keyValProp0
    } with Key[P]() {
      type V = V0
    }
  }

  /** constructs a partition key for this persistent type. the full key value
   * is used to determine the partition
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param hashed if `true`, then used a hashed partition (as opposed to a
   * ranged partition) when possible. defaults to `false`.
   */
  def partitionKey[V <: KeyVal[P] : TypeKey](keyValProp: Prop[P, V], hashed: Boolean = false): Key[P] =
    partitionKey(keyValProp, partition(keyValProp), hashed)

  /** constructs a partition key for this persistent type
   *
   * @tparam V the type of the key value
   * @param keyValProp a property for the primary key
   * @param partition describes the portion of the key value to use to determine
   * which node in the partition the data belongs to. this must form a prefix of
   * the `keyValProp`
   */
  def partitionKey[V <: KeyVal[P] : TypeKey](keyValProp: Prop[P, V], partition: Partition[P]): Key[P] =
    partitionKey(keyValProp, partition, false)

  private def partitionKey[V <: KeyVal[P] : TypeKey](
    keyValProp: Prop[P, V],
    partition: Partition[P],
    hashed: Boolean)
  : Key[P] = {
    val keyValProp0 = keyValProp
    type V0 = V
    new {
      val keyValProp = keyValProp0
    } with PartitionKey(partition, hashed) {
      type V = V0
    }
  }

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

  // TODO rm NoKeysForPTypeException, IndexDuplicatesKeyException

  private[subdomain] def validateKeysAndIndexes(): Unit = {
    var keyValProps: Set[Prop[P, _]] = keySet.map(_.keyValProp)
    indexSet.foreach { index =>
      if (index.props.size == 1 && keyValProps.contains(index.props.head)) {
        throw new IndexDuplicatesKeyException(pTypeKey)
      }
    }
  }

  override def toString = s"PType[${pTypeKey.name}]"

}
