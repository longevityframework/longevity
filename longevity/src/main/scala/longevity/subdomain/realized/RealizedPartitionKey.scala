package longevity.subdomain.realized

import emblem.TypeKey
import longevity.exceptions.subdomain.InvalidPartitionException
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.PartitionKey

private[longevity] class RealizedPartitionKey[
  P <: Persistent : TypeKey,
  V <: KeyVal[P, V] : TypeKey] private [subdomain](
  override val key: PartitionKey[P, V],
  realizedProp: RealizedProp[P, V],
  val partitionProps: Seq[RealizedProp[P, _]])
extends RealizedKey[P, V](key, realizedProp) {

  validatePartition()

  private def validatePartition() = {
    val partitionComponents = partitionProps.foldLeft(Seq[RealizedPropComponent[P, _, _]]()) { (acc, prop) =>
      acc ++ prop.realizedPropComponents
    }
    val keyComponents = realizedProp.realizedPropComponents
    if (! (keyComponents.take(partitionComponents.size) == partitionComponents)) {
      throw new InvalidPartitionException[P](key)
    }
  }

}
