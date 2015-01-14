package musette.domain

import emblem._
import longevity.domain._

/** a website. */
case class Site(
  val uri: Uri
)
extends Entity

object Site extends EntityType[Site] {

  private lazy val uriProp = new EmblemProp[Site, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))
  lazy val emblem = new Emblem[Site](
    "musette.domain",
    "Site",
    Seq(uriProp),
    EmblemPropToValueMap(),
    { map => Site(map.get(uriProp)) }
  )

}
