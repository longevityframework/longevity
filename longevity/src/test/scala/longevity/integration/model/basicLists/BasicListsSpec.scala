package longevity.integration.model.basicLists

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
