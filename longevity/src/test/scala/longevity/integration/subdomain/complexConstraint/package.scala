package longevity.integration.subdomain

import longevity.test.TestDataGenerator
import longevity.test.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with a simple shorthand constraint */
package object complexConstraint {

  @subdomain object subdomain

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

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain, generators)

}
