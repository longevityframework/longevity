package musette.repo.mongo

import scala.reflect.runtime.universe.TypeTag
import longevity.domain.Entity
import longevity.domain.EntityType
import longevity.repo._

class MusetteMongoRepo[E <: Entity : TypeTag](
  override val entityType: EntityType[E]
)(
  implicit repoPool: OldRepoPool
)
extends MongoRepo[E](entityType, musette.domain.shorthands)

