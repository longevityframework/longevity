package musette

import longevity.context._
import longevity.shorthands._
import longevity.subdomain._

package object coredomain {

  val entityTypes = EntityTypePool() +
    BlogType +
    BlogPostType +
    CommentType + 
    SiteType +
    UserType +
    WikiType +
    WikiPageType

  val shorthands = ShorthandPool(
    Shorthand[Email, String],
    Shorthand[Markdown, String],
    Shorthand[Uri, String]
  )

  implicit def stringToEmail(email: String): Email = Email(email)

  implicit def stringToMarkdown(markdown: String): Markdown = Markdown(markdown)

  implicit def stringToUri(uri: String): Uri = Uri(uri)

  val subdomain = Subdomain("Musette", entityTypes)

  val longevityContext = LongevityContext(subdomain, shorthands, Mongo)

}
