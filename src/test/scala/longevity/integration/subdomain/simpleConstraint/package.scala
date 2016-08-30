package longevity.integration.subdomain

import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.ValueType
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a simple shorthand constraint */
package object simpleConstraint {

  val subdomain = Subdomain(
    "Simple Constraint",
    PTypePool(SimpleConstraint),
    ETypePool(ValueType[Email]))

  val emailGenerator = CustomGenerator.simpleGenerator[Email] { generator =>
    Email(s"{generator.generate[String]}@{generate.generate[String]")
  }
  val generators = CustomGeneratorPool.empty + emailGenerator

  val mongoContext = LongevityContext(subdomain, Mongo, customGeneratorPool = generators)
  val cassandraContext = LongevityContext(subdomain, Cassandra, customGeneratorPool = generators)

}
