package longevity.integration.subdomain.shorthands

import org.joda.time.DateTime
import longevity.subdomain.annotations.component

@component
case class DateTimeShorthand(dateTime: DateTime)
