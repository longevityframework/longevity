package longevity.integration.model.longKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class LongKey(id: LongKeyId)

object LongKey {
  implicit val idLongKey = key(props.id)
}
