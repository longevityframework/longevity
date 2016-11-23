package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a persistent with a simple shorthand constraint */
package object simpleConstraint {

  val subdomain = Subdomain(
    "Simple Constraint",
    PTypePool(SimpleConstraint),
    CTypePool(Email))

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"${generator.generate[String]}@${generator.generate[String]}")
  }
  val generators = CustomGeneratorPool.empty + emailGenerator

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain, generators)

}
