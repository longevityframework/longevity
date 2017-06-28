package longevity.integration.model

import longevity.test.CustomGeneratorPool
import longevity.test.TestDataGenerator
import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import scala.concurrent.Future

/** covers a persistent with a simple shorthand constraint */
package object simpleConstraint {

  @domainModel trait DomainModel

  val emailGenerator = { generator: TestDataGenerator =>
    Email(s"${generator.generate[String]}@${generator.generate[String]}")
  }
  val generators = CustomGeneratorPool.empty + emailGenerator

  val contexts = TestLongevityConfigs.sparseContextMatrix[Future, DomainModel](generators)

}
