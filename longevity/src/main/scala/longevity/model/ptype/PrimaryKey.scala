package longevity.model.ptype

/** the primary key for this persistent type. a
 * [[longevity.model.PType persistent type]] can have no more than one
 * primary key. primary keys are just like other [[Key keys]], but
 * they take advantage of any performance features of the underlying
 * database that can only be applied once per persistent type.
 * 
 * @tparam M the domain model
 * @tparam P the persistent type
 * @tparam V the key value class
 * 
 * @param partition describes the portion of the key value to use to determine
 * which node in the partition the data belongs to
 * @param hashed if `true`, then used a hashed partition (as opposed to a
 * ranged partition) when possible
 */
abstract class PrimaryKey[M, P, V] private[model](
  val partition: Partition[P],
  val hashed: Boolean)
extends Key[M, P, V]() {

  def fullyPartitioned = partition.props.size == 1 && keyValProp == partition.props.head

  override def toString = s"PrimaryKey[${keyValProp.pTypeKey.name},${keyValTypeKey.name}]"

}
