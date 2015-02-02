package musette

import scala.language.implicitConversions
import emblem._
import longevity.domain._

package object domain {

  val entityTypes = EntityTypePool() +
    BlogType + BlogPostType + CommentType + 
    (typeKey[Site] -> SiteType) +
    UserType + WikiType + WikiPageType

  val shorthands = emblem.ShorthandPool(
    shorthandFor[Email, String],
    shorthandFor[Markdown, String],
    shorthandFor[Uri, String]
  )

  implicit def stringToEmail(email: String): Email = Email(email)

  implicit def stringToMarkdown(markdown: String): Markdown = Markdown(markdown)

  implicit def stringToUri(uri: String): Uri = Uri(uri)

  val domainConfig = DomainConfig(entityTypes, shorthands)

}
