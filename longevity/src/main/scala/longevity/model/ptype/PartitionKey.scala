package longevity.model.ptype

import emblem.TypeKey
import emblem.typeKey

/** the partition key for this persistent type. partition keys are used by the
 * underlying database to partition data across multiple nodes of a distributed
 * database.
 *
 * a [[longevity.model.PType persistent type]] can have zero or
 * one partition keys.
 * 
 * @tparam P the persistent type
 * @param partition describes the portion of the key value to use to determine
 * which node in the partition the data belongs to
 * @param hashed if `true`, then used a hashed partition (as opposed to a
 * ranged partition) when possible
 */
abstract class PartitionKey[P : TypeKey] private[model](
  val partition: Partition[P],
  val hashed: Boolean)
extends Key[P]() {

  def fullyPartitioned = partition.props.size == 1 && keyValProp == partition.props.head

  override def toString = s"PartitionKey[${typeKey[P].name},${keyValTypeKey.name}]"

}
