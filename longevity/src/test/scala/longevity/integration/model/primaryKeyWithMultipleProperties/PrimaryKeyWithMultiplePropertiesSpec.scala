package longevity.integration.model.primaryKeyWithMultipleProperties

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
