package longevity.unit.model.annotations.keyValExample

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@persistent[M] case class P(kv: KV)

object P {
  implicit lazy val idKey = key(props.kv)
}

@keyVal[M, P] case class KV()
