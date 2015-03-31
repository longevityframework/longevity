package longevity.context

import emblem.traversors.Generator.CustomGeneratorPool
import longevity.persistence.buildRepoPool
import longevity.shorthands.ShorthandPool
import longevity.subdomain.Subdomain

private[context] final class LongevityContextImpl (
  val subdomain: Subdomain,
  val shorthandPool: ShorthandPool,
  val persistenceStrategy: PersistenceStrategy,
  val specializedRepoFactoryPool: SpecializedRepoFactoryPool,
  val customGeneratorPool: CustomGeneratorPool)
extends LongevityContext {

  lazy val repoPool = buildRepoPool(subdomain, shorthandPool, persistenceStrategy, specializedRepoFactoryPool)

  lazy val inMemRepoPool = buildRepoPool(subdomain, shorthandPool, InMem)

}
