package musette.repo

import emblem.TypeKey
import emblem.traversors.Generator.emptyCustomGenerators
import emblem.generators.CustomGenerator.simpleGenerator
import longevity.testUtil.RepoSpec
import longevity.domain.Entity
import musette.domain.Email
import musette.domain.Uri

/** Fills in `RepoSpec` requirements that are common across the project */
abstract class MusetteRepoSpec[E <: Entity : TypeKey] extends RepoSpec[E] {

  def domainConfig = musette.domain.domainConfig

  override def customGenerators = emptyCustomGenerators +
    simpleGenerator((generator) => Uri("http://localhost/" + generator.string)) +
    simpleGenerator((generator) => Email(generator.string + "@jsmscs.net"))

}
