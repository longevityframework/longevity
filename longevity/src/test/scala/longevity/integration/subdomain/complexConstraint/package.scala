package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a simple shorthand constraint */
package object complexConstraint {

  val subdomain = Subdomain(
    "Complex Constraint",
    PTypePool(ComplexConstraint),
    ETypePool(EType[Email]))

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"{generator.generate[String]}@{generate.generate[String]")
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
