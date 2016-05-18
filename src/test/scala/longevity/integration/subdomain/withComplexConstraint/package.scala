package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGeneratorPool
import emblem.emblematic.traversors.sync.CustomGenerator
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a simple shorthand constraint */
package object withComplexConstraint {

  val subdomain = Subdomain(
    "With Simple Constraint",
    PTypePool(WithComplexConstraint),
    shorthandPool = ShorthandPool(Email))

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"{generator.generate[String]}@{generate.generate[String]")
  }
  val withComplexConstraintGenerator = CustomGenerator.simpleGenerator[WithComplexConstraint] { generator =>
    val primaryEmail = generator.generate[Email]
    WithComplexConstraint(
      generator.generate[String],
      primaryEmail,
      generator.generate[Set[Email]] + primaryEmail)
  }
  val generators = CustomGeneratorPool.empty + emailGenerator + withComplexConstraintGenerator

  val mongoContext = LongevityContext(subdomain, Mongo, customGeneratorPool = generators)
  val cassandraContext = LongevityContext(subdomain, Cassandra, customGeneratorPool = generators)

}
