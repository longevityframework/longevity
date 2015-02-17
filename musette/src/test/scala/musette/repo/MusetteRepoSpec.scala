package musette.repo

import emblem.TypeKey
import emblem.traversors.CustomGenerator.simpleGenerator
import emblem.traversors.Generator.emptyCustomGenerators
import longevity.domain.Entity
import longevity.testUtil.RepoSpec
import musette.domain.Email
import musette.domain.Uri

/** Fills in `RepoSpec` requirements that are common across the project */
abstract class MusetteRepoSpec[E <: Entity : TypeKey] extends RepoSpec[E] {

  def domainConfig = musette.domain.domainConfig

  override def customGenerators = emptyCustomGenerators +
    simpleGenerator((generator) => Uri("http://localhost/" + generator.generate[String])) +
    simpleGenerator((generator) => Email(generator.generate[String] + "@jsmscs.net"))

}
