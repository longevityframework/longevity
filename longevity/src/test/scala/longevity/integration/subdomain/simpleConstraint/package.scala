package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.TestLongevityConfigs
import longevity.subdomain.annotations.subdomain

/** covers a persistent with a simple shorthand constraint */
package object simpleConstraint {

  @subdomain object subdomain

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"${generator.generate[String]}@${generator.generate[String]}")
  }
  val generators = CustomGeneratorPool.empty + emailGenerator

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain, generators)

}
