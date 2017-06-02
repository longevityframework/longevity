package longevity.integration.model.shorthandSets

import org.joda.time.DateTime
import longevity.model.annotations.component

@component[DomainModel]
case class DateTimeShorthand(dateTime: DateTime)
