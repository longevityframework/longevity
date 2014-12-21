package musette.domain
package testUtils

// TODO: find some way to do this kind of stuff generically
package object testEntityGen {

  val rootUri: Uri = "http://localhost:5000"

  def blog(): Blog = blog(site())

  def blog(site: Site): Blog = Blog(uri(site.uri), site, Set(user(site), user(site)), markdown())

  def blogPost(): BlogPost = blogPost(blog())

  def blogPost(blog: Blog): BlogPost = BlogPost(uri(blog.uri), blog, blog.authors, markdown(), markdown())

  def comment(): Comment = comment(blogPost())

  def comment(subject: BlogPost): Comment = {
    val site = subject.blog.get.site.get
    Comment(uri(subject.uri), subject, user(site), markdown())
  }

  def site() = Site(uri())

  def user(): User = user(site())

  def user(site: Site): User = User(uri(site.uri), site, email(), string(), markdown())

  def wiki(): Wiki = wiki(site())

  def wiki(site: Site): Wiki = Wiki(uri(site.uri), site, Set(user(site), user(site)), markdown())

  def wikiPage(): WikiPage = wikiPage(wiki())

  def wikiPage(wiki: Wiki): WikiPage = WikiPage(uri(wiki.uri), wiki.authors, wiki, markdown(), markdown())

  @inline def email(): Email = string()

  @inline def markdown(): Markdown = string()

  @inline def uri(): Uri = uri(rootUri)

  @inline def uri(base: Uri): Uri = base + "/" + string()

  @inline def string(): String = string(8)

  @inline def string(len: Int): String = (1 to len map { i => char() }).mkString

  @inline def char(): Char = (math.abs(util.Random.nextInt % 26) + 'a').toChar

}
