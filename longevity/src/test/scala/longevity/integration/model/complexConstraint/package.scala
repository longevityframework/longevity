package longevity.integration.model

import longevity.test.TestDataGenerator
import longevity.test.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.model.annotations.domainModel
import scala.concurrent.Future

/** covers a persistent with a simple shorthand constraint */
package object complexConstraint {

  @domainModel trait DomainModel

  val emailGenerator = { generator: TestDataGenerator =>
    Email(s"${generator.generate[String]}@${generator.generate[String]}")
  }

  val complexConstraintGenerator = { generator: TestDataGenerator =>
    val primaryEmail = generator.generate[Email]
    ComplexConstraint(
      generator.generate[ComplexConstraintId],
      primaryEmail,
      generator.generate[Set[Email]] + primaryEmail)
  }

  val generators = CustomGeneratorPool.empty + emailGenerator + complexConstraintGenerator

  val contexts = TestLongevityConfigs.sparseContextMatrix[Future, DomainModel](generators)

}
