package longevity.unit.subdomain

import org.scalatest._

object SubdomainSpec {

  // copied from https://gist.github.com/sullivan-/1bf6e826ce266588ecde
  // used in http://sullivan-.github.io/longevity/manual/subdomain/
  object emptySubdomain {
    import longevity.subdomain._
    val subdomain = Subdomain("blogging", EntityTypePool.empty)

    // you can also use these synonyms freely:
    val coreDomain: CoreDomain = CoreDomain("blogging", EntityTypePool.empty)
    val supportingSubdomain: SupportingSubdomain = SupportingSubdomain("accounts", EntityTypePool.empty)
    val genericSubdomain: GenericSubdomain = GenericSubdomain("searches", EntityTypePool.empty)
  }

  // copied from https://gist.github.com/sullivan-/db1226b4d31a0526ac8c
  // copied from https://gist.github.com/sullivan-/6a68ac5f6f6331274e21
  // used in http://sullivan-.github.io/longevity/manual/subdomain/roots.html
  object roots {

    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

}

/** exercises code samples found in the subdomain section of the user manual. the samples themselves are
 * in [[SubdomainSpec]] companion object. we include them in the tests here to force the initialization of the
 * subdomains, and to perform some basic sanity checks on the results.
 *
 * @see http://sullivan-.github.io/longevity/manual/subdomain/
 */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  import SubdomainSpec._
  import longevity.subdomain._

  "user manual example code" should "produce correct subdomains" in {

    // emptySubdomain:

    def emptySubdomainShould(subdomain: Subdomain, name: String): Unit = {
      subdomain.name should equal (name)
      subdomain.entityTypePool should be ('empty)
      subdomain.shorthandPool should be ('empty)
      subdomain.rootEntityTypePool should be ('empty)
    }

    emptySubdomainShould(emptySubdomain.subdomain, "blogging")
    emptySubdomainShould(emptySubdomain.coreDomain, "blogging")
    emptySubdomainShould(emptySubdomain.supportingSubdomain, "accounts")
    emptySubdomainShould(emptySubdomain.genericSubdomain, "searches")

    // roots:

    roots.subdomain.name should equal ("blogging")
    roots.subdomain.entityTypePool.size should equal (1)
    roots.subdomain.entityTypePool.values.head should equal (roots.User)
    roots.subdomain.shorthandPool should be ('empty)
    roots.subdomain.rootEntityTypePool.size should equal (1)
    roots.subdomain.entityTypePool.values.head should equal (roots.User)
    roots.subdomain.entityEmblemPool.size should equal (1)

  }

}
