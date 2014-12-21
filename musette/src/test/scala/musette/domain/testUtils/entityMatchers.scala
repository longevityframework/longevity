package musette.domain
package testUtils

import org.scalatest._
import longevity.domain.Assoc
import longevity.repo.Id

// TODO: find some way to do this kind of stuff generically
package object entityMatchers extends Matchers {

  def persistedBlogShouldMatchUnpersisted(persisted: Blog, unpersisted: Blog): Unit = {
    persisted.uri should equal (unpersisted.uri)
    persisted.site.isPersisted should be (true)
    persistedSiteShouldMatchUnpersisted(persisted.site.retrieve, unpersisted.site.unpersisted)
    userSetsShouldMatch(persisted.authors, unpersisted.authors)

    // constraint: the site of the blog authors and the site of the blog should be the same
    persisted.authors.map(_.retrieve).foreach { user => user.site should equal (persisted.site) }

    persisted.slug should equal (unpersisted.slug)
  }

  def persistedBlogPostShouldMatchUnpersisted(persisted: BlogPost, unpersisted: BlogPost): Unit = {
    persisted.uri should equal (unpersisted.uri)
    persisted.blog.isPersisted should be (true)
    persistedBlogShouldMatchUnpersisted(persisted.blog.retrieve, unpersisted.blog.unpersisted)
    userSetsShouldMatch(persisted.authors, unpersisted.authors)

    // constraint: the site of the blog authors and the site of the blog should be the same
    persisted.authors.map(_.retrieve).foreach { user => user.site should equal (persisted.blog.retrieve.site) }

    persisted.content should equal (unpersisted.content)
    persisted.slug should equal (unpersisted.slug)
  }

  def persistedCommentShouldMatchUnpersisted(persisted: Comment, unpersisted: Comment): Unit = {
    persisted.uri should equal (unpersisted.uri)
    // TODO
  }

  def persistedSiteShouldMatchUnpersisted(persisted: Site, unpersisted: Site): Unit = {
    persisted.uri should equal (unpersisted.uri)
  }

  def persistedUserShouldMatchUnpersisted(persisted: User, unpersisted: User): Unit = {
    persisted.uri should equal (unpersisted.uri)
    persisted.site.isPersisted should be (true)
    persistedSiteShouldMatchUnpersisted(persisted.site.retrieve, unpersisted.site.unpersisted)
    persisted.email should equal (unpersisted.email)
    persisted.handle should equal (unpersisted.handle)
    persisted.slug should equal (unpersisted.slug)
  }

  private def userSetsShouldMatch(persisted: Set[Assoc[User]], unpersisted: Set[Assoc[User]]): Unit = {
    val uriToUnpersistedUserMap = unpersisted.map(_.unpersisted).map(user => (user.uri -> user)).toMap
    persisted.foreach { userAssoc =>
      userAssoc.isPersisted should be (true)
      val user = userAssoc.retrieve
      uriToUnpersistedUserMap should contain key (user.uri)
      persistedUserShouldMatchUnpersisted(user, uriToUnpersistedUserMap(user.uri))
    }
  }

}
