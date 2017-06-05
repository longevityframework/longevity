package longevity.model.realized

import emblem.TypeKey
import emblem.emblematic.EmblematicPropPath
import longevity.model.KVEv
import longevity.model.ptype.PrimaryKey

private[longevity] case class RealizedPrimaryKey[M, P : TypeKey, V] private [realized](
  override val key: PrimaryKey[M, P, V],
  prop: RealizedProp[P, V],
  ev0: KVEv[M, P, V],
  val partitionProps: Seq[RealizedProp[P, _]],
  val postPartitionProps: Seq[RealizedProp[P, _]],
  val emblematicPropPaths: Seq[EmblematicPropPath[V, _]])
extends RealizedKey[M, P, V](key, prop, ev0) {

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
