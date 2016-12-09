package longevity.unit.subdomain.annotations

import longevity.model.Subdomain
import longevity.model.annotations.subdomain
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@subdomain` macro annotation]] */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "@subdomain"

  it should "cause a compiler error when applied to anything other than an object" in {
    "@subdomain val x = 7"           shouldNot compile
    "@subdomain type X = Int"        shouldNot compile
    "@subdomain def foo = 7"         shouldNot compile
    "def foo(@subdomain x: Int) = 7" shouldNot compile
    "@subdomain trait Foo"           shouldNot compile
    "@subdomain class Foo"           shouldNot compile
  }

  it should "extend the object with `Subdomain(currentPackage)`" in {
    subdomainExample.subdomain.isInstanceOf[Subdomain] should be (true)
    subdomainExample.subdomain.asInstanceOf[Subdomain].pTypePool.size should equal (1)
    subdomainExample.subdomain.asInstanceOf[Subdomain].pTypePool.values.head should equal (subdomainExample.User)
    subdomainExample.subdomain.asInstanceOf[Subdomain].cTypePool.size should equal (0)
  }

}
