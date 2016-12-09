package longevity.unit.model.annotations

import longevity.model.Subdomain
import longevity.model.annotations.domainModel
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@domainModel` macro annotation]] */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "@domainModel"

  it should "cause a compiler error when applied to anything other than an object" in {
    "@domainModel val x = 7"           shouldNot compile
    "@domainModel type X = Int"        shouldNot compile
    "@domainModel def foo = 7"         shouldNot compile
    "def foo(@domainModel x: Int) = 7" shouldNot compile
    "@domainModel trait Foo"           shouldNot compile
    "@domainModel class Foo"           shouldNot compile
  }

  it should "extend the object with `Subdomain(currentPackage)`" in {
    subdomainExample.subdomain.isInstanceOf[Subdomain] should be (true)
    subdomainExample.subdomain.asInstanceOf[Subdomain].pTypePool.size should equal (1)
    subdomainExample.subdomain.asInstanceOf[Subdomain].pTypePool.values.head should equal (subdomainExample.User)
    subdomainExample.subdomain.asInstanceOf[Subdomain].cTypePool.size should equal (0)
  }

}
