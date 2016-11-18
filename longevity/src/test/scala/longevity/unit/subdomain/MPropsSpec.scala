package longevity.unit.subdomain

import longevity.subdomain.mprops
import longevity.subdomain.PType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object MPropsSpec {

  // for use in `extends PType[Foo]`
  case class Foo()

  // help ensure macro application in the face of multiple inheritence
  trait Extra

  object inner {

    case class Foo2()

    // TODO: extends PType[Foo2] doesnt work here. document, if nothing else
    // help ensure macro application doesnt mess up companion object
    @mprops class WithCompanion extends PType[Foo]

    object WithCompanion { val y = 7 }
  }

}

/** unit tests for the proper behavior of [[mprops `@mprops` macro annotation]] */
class MPropsSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "@mprops"

  it should "cause a compiler error when annotating something other than a class definition" in {
    "@mprops val x = 7"           shouldNot compile
    "@mprops type X = Int"        shouldNot compile
    "@mprops def foo = 7"         shouldNot compile
    "def foo(@mprops x: Int) = 7" shouldNot compile
  }

  it should "cause a compiler error when annotating a class that is not a PType" in {
    "@mprops          object Foo                                                            " shouldNot compile
    "@mprops          object Foo         extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops          object Foo         extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops case     object Foo                                                            " shouldNot compile
    "@mprops case     object Foo         extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops case     object Foo         extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops          class  Foo                                                            " shouldNot compile
    "@mprops          class  Foo         extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops          class  Foo         extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops          class  Foo(x: Int)                                                    " shouldNot compile
    "@mprops          class  Foo(x: Int) extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops          class  Foo(x: Int) extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops case     class  Foo()                                                          " shouldNot compile
    "@mprops case     class  Foo()       extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops case     class  Foo()       extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops abstract class  Foo                                                            " shouldNot compile
    "@mprops abstract class  Foo         extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops abstract class  Foo         extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops abstract class  Foo(x: Int)                                                    " shouldNot compile
    "@mprops abstract class  Foo(x: Int) extends PType[MPropsSpec.Foo]                      " should compile
    "@mprops abstract class  Foo(x: Int) extends PType[MPropsSpec.Foo] with MPropsSpec.Extra" should compile

    // make sure that @mprops on a class doesn't mess up its companion object
    "MPropsSpec.inner.WithCompanion.y: Int" should compile
  }

}
