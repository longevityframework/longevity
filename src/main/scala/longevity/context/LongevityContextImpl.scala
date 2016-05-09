package longevity.context

import com.typesafe.config.Config
import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.persistence.buildRepoPool
import longevity.subdomain.Subdomain

private[context] final class LongevityContextImpl (
  val subdomain: Subdomain,
  val persistenceStrategy: PersistenceStrategy,
  val customGeneratorPool: CustomGeneratorPool,
  val config: Config)
extends LongevityContext {

  private lazy val longevityConfig = config.getConfig("longevity")
  private lazy val longevityTestConfig = config.getConfig("longevity.test")

  lazy val repoPool = buildRepoPool(subdomain, persistenceStrategy, longevityConfig)
  lazy val testRepoPool = buildRepoPool(subdomain, persistenceStrategy, longevityTestConfig)
  lazy val inMemTestRepoPool = buildRepoPool(subdomain, InMem, longevityTestConfig)

}
