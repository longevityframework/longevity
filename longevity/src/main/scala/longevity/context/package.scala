package longevity

import emblem.imports._
import longevity.shorthands._
import longevity.persistence.Repo
import longevity.subdomain.RootEntity

/** contains the [[LongevityContext]] plus supporting types and classes */
package object context {

  /** a function producing a specialized version of a repository
   * @tparam RE the root entity type for the repository
   */
  type SpecializedRepoFactory[RE <: RootEntity] = (EmblemPool, ShorthandPool) => Repo[RE]

  /** a pool of [[SpecializedRepoFactory specialized repo factories, type-mapped on the root entity type */
  type SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]

  object SpecializedRepoFactoryPool {
    
    /** an empty [[SpecializedRepoFactoryPool specialized repo factory pool]]. used to grow larger factory pools
     * via the `+` operation
     */
    val empty: SpecializedRepoFactoryPool = TypeKeyMap[RootEntity, SpecializedRepoFactory]
  }

}
