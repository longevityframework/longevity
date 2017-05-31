package longevity.unit.model.annotations.keyValExample

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@persistent[M](keySet = Set(key(props.kv))) case class P(kv: KV)

@keyVal[M, P] case class KV()
