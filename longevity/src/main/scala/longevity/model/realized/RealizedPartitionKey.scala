package longevity.model.realized

import emblem.TypeKey
import emblem.emblematic.EmblematicPropPath
import longevity.model.KeyVal
import longevity.model.ptype.PartitionKey

private[longevity] case class RealizedPartitionKey[P : TypeKey, V <: KeyVal[P] : TypeKey] private [realized](
  override val key: PartitionKey[P],
  prop: RealizedProp[P, V],
  val partitionProps: Seq[RealizedProp[P, _]],
  val postPartitionProps: Seq[RealizedProp[P, _]],
  val emblematicPropPaths: Seq[EmblematicPropPath[V, _]])
extends RealizedKey[P, V](key, prop) {

  def hashed = key.hashed
  def partition = key.partition
  def fullyPartitioned = key.fullyPartitioned
  def props = partitionProps ++ postPartitionProps

  case class QueryInfo[B](inlinedPath: String, get: (V) => B, typeKey: TypeKey[B])

  lazy val queryInfos = props.zip(emblematicPropPaths).map {
    case (prop, epp) =>
      def qi[B](epp: EmblematicPropPath[V, B]) = QueryInfo(prop.inlinedPath, epp.get, epp.typeKey)
      qi(epp)
  }

}
