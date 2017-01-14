package longevity.integration.model.primaryKeyInComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
