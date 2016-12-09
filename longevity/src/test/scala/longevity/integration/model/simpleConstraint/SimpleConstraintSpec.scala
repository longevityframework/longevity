package longevity.integration.model.simpleConstraint

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class SimpleConstraintSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
