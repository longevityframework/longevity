package longevity.integration.model.primaryKeyWithComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
