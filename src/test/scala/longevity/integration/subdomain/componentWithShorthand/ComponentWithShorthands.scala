package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType

case class ComponentShorthands(uri: Uri) extends Entity

object ComponentShorthands extends EntityType[ComponentShorthands]
