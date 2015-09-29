package longevity.context

import emblem.traversors.sync.Generator.CustomGeneratorPool
import longevity.persistence.buildRepoPool
import longevity.subdomain._

private[context] final class LongevityContextImpl (
  val subdomain: Subdomain,
  val persistenceStrategy: PersistenceStrategy,
  val customGeneratorPool: CustomGeneratorPool)
extends LongevityContext {

  lazy val repoPool = buildRepoPool(subdomain, persistenceStrategy)

  lazy val inMemRepoPool = println(s"building in mem repo pool $persistenceStrategy")

}
