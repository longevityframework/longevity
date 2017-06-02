package longevity.integration.model.shorthands

import org.joda.time.DateTime
import longevity.model.annotations.component

@component[DomainModel]
case class DateTimeShorthand(dateTime: DateTime)
