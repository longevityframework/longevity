package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with a simple shorthand constraint */
package object complexConstraint {

  @subdomain object subdomain

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"${generator.generate[String]}@${generator.generate[String]}")
  }

  val complexConstraintGenerator = CustomGenerator.simpleGenerator[ComplexConstraint] { generator =>
    val primaryEmail = generator.generate[Email]
    ComplexConstraint(
      generator.generate[ComplexConstraintId],
      primaryEmail,
      generator.generate[Set[Email]] + primaryEmail)
  }

  val generators = CustomGeneratorPool.empty + emailGenerator + complexConstraintGenerator

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain, generators)

}
