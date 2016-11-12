package longevity.subdomain.ptype

import emblem.TypeKey
import emblem.typeKey
import longevity.subdomain.KeyVal

/** the partition key for this persistent type. partition keys are used by the
 * underlying database to partition data across multiple nodes of a distributed
 * database.
 *
 * a [[longevity.subdomain.PType persistent type]] can have zero or
 * one primary keys.
 * 
 * @tparam P the persistent type
 * @tparam V the key value type
 * @param keyValProp a property for the key
 * @param partition describes the portion of the key value to use to determine
 * which node in the partition the data belongs to
 * @param hashed if `true`, then used a hashed partition (as opposed to a
 * ranged partition) when possible
 */
class PartitionKey[P : TypeKey, V <: KeyVal[P, V] : TypeKey] private [subdomain] (
  keyValProp: Prop[P, V],
  val partition: Partition[P],
  val hashed: Boolean)
extends Key[P, V](keyValProp) {

  def fullyPartitioned = partition.props.size == 1 && keyValProp == partition.props.head

  override def toString = s"PartitionKey[${typeKey[P].name},${typeKey[V].name}]"

}
