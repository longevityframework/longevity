package longevity.integration.subdomain

import longevity.context._
import longevity.subdomain._
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.CustomGenerator

/** covers a root with a simple shorthand constraint */
package object withComplexConstraint {

  val emailShorthand = Shorthand[Email, String]

  implicit val shorthandPool = ShorthandPool.empty + emailShorthand

  object context {
    val entityTypes = EntityTypePool() + WithComplexConstraint
    val subdomain = Subdomain("With Simple Constraint", entityTypes)

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
  }

}
