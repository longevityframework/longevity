package longevity.integration.model.shorthandLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
