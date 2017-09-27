package longevity.unit.model.annotations

import longevity.model.ModelType
import longevity.model.annotations.domainModel
import org.scalatest.FlatSpec
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@domainModel` macro annotation]] */
class DomainModelSpec extends FlatSpec with Matchers {

  behavior of "@domainModel"

  it should "cause a compiler error when applied to anything other than a class or trait" in {
    "@domainModel val x = 7"           shouldNot compile
    "@domainModel type X = Int"        shouldNot compile
    "@domainModel def foo = 7"         shouldNot compile
    "def foo(@domainModel x: Int) = 7" shouldNot compile
    "@domainModel object Foo"          shouldNot compile
  }

  it should "extend the object with `ModelType(currentPackage)`" in {
    type M = domainModelExample.DomainModel
    val mt = domainModelExample.DomainModel.modelType
    mt.isInstanceOf[ModelType[_]] should be (true)
    mt.asInstanceOf[ModelType[M]].pTypePool.size should equal (1)
    mt.asInstanceOf[ModelType[M]].pTypePool.values.head should equal (domainModelExample.User)
    mt.asInstanceOf[ModelType[M]].cTypePool.size should equal (0)
  }

}
