package longevity.integration.subdomain.withComponentWithShorthands

import longevity.subdomain.Shorthand
import org.joda.time.DateTime

case class DateTimeShorthand(dateTime: DateTime)

object DateTimeShorthand extends Shorthand[DateTimeShorthand, DateTime]
