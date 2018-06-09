package longevity.unit.model.annotations

import org.joda.time.DateTime
import longevity.integration.model.component.Component
import longevity.integration.model.component.WithComponent
import longevity.integration.model.component.WithComponentId
import longevity.integration.model.basics.Basics
import longevity.integration.model.basics.BasicsId
import longevity.model.PType
import longevity.model.annotations.mprops
import longevity.model.annotations.pEv
import longevity.model.ptype.Prop
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** stuff to use in the tests below that need to be stable (ie in packages and objects not in classes */
object MPropsSpec {

  trait DomainModel
  object DomainModel {
    implicit val ev = new longevity.model.ModelEv[DomainModel]
  }

  // for use in `extends PType[Foo]`
  case class Foo()

  // help ensure macro application in the face of multiple inheritence
  trait Extra

  object inner {

    case class Foo2()

    // extends PType[Foo2] doesnt work here, for the same reason i have testExamples.scala.
    // @mprops can't work on a type that is declared in the same object

    // help ensure macro application doesnt mess up companion object
    @pEv @mprops class WithCompanion extends PType[DomainModel, Foo]

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
    "@mprops          object Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops          object Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops case     object Foo                                                            " shouldNot compile
    "@mprops case     object Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops case     object Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops          class  Foo                                                            " shouldNot compile
    "@mprops          class  Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops          class  Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops          class  Foo(x: Int)                                                    " shouldNot compile
    "@mprops          class  Foo(x: Int) extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops          class  Foo(x: Int) extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops case     class  Foo()                                                          " shouldNot compile
    "@mprops case     class  Foo()       extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops case     class  Foo()       extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops abstract class  Foo                                                            " shouldNot compile
    "@mprops abstract class  Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops abstract class  Foo         extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile
    "@mprops abstract class  Foo(x: Int)                                                    " shouldNot compile
    "@mprops abstract class  Foo(x: Int) extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo]                      " should compile
    "@mprops abstract class  Foo(x: Int) extends PType[MPropsSpec.DomainModel, MPropsSpec.Foo] with MPropsSpec.Extra" should compile

    // make sure that @mprops on a class doesn't mess up its companion object
    "MPropsSpec.inner.WithCompanion.y: Int" should compile
  }

  it should "create props for members with basic types" in {
    Basics.props.boolean  should equal (new Prop[Basics, Boolean ]("boolean" ))
    Basics.props.char     should equal (new Prop[Basics, Char    ]("char"    ))
    Basics.props.dateTime should equal (new Prop[Basics, DateTime]("dateTime"))
    Basics.props.double   should equal (new Prop[Basics, Double  ]("double"  ))
    Basics.props.float    should equal (new Prop[Basics, Float   ]("float"   ))
    Basics.props.int      should equal (new Prop[Basics, Int     ]("int"     ))
    Basics.props.long     should equal (new Prop[Basics, Long    ]("long"    ))
    Basics.props.string   should equal (new Prop[Basics, String  ]("string"  ))
  }

  it should "create props for members with case-class types" in {
    Basics.props.id               should equal (new Prop[Basics,        BasicsId       ]("id"))
    WithComponent.props.id        should equal (new Prop[WithComponent, WithComponentId]("id"))
    WithComponent.props.component should equal (new Prop[WithComponent, Component      ]("component"))
  }

  it should "create props for nested members" in {
    Basics.props.id.id                should equal (new Prop[Basics,        String]("id.id"))
    WithComponent.props.id.id         should equal (new Prop[WithComponent, String]("id.id"))
    WithComponent.props.component.id  should equal (new Prop[WithComponent, String]("component.id"))
    WithComponent.props.component.tag should equal (new Prop[WithComponent, String]("component.tag"))
  }

  it should "not create props for collections" in {
    import longevity.integration.model.basicLists.BasicLists
    import longevity.integration.model.basicOptions.BasicOptions
    import longevity.integration.model.basicSets.BasicSets
    "BasicLists  .props.boolean"  shouldNot compile
    "BasicLists  .props.char"     shouldNot compile
    "BasicLists  .props.dateTime" shouldNot compile
    "BasicLists  .props.double"   shouldNot compile
    "BasicLists  .props.float"    shouldNot compile
    "BasicLists  .props.int"      shouldNot compile
    "BasicLists  .props.long"     shouldNot compile
    "BasicLists  .props.string"   shouldNot compile
    "BasicOptions.props.boolean"  shouldNot compile
    "BasicOptions.props.char"     shouldNot compile
    "BasicOptions.props.dateTime" shouldNot compile
    "BasicOptions.props.double"   shouldNot compile
    "BasicOptions.props.float"    shouldNot compile
    "BasicOptions.props.int"      shouldNot compile
    "BasicOptions.props.long"     shouldNot compile
    "BasicOptions.props.string"   shouldNot compile
    "BasicSets   .props.boolean"  shouldNot compile
    "BasicSets   .props.char"     shouldNot compile
    "BasicSets   .props.dateTime" shouldNot compile
    "BasicSets   .props.double"   shouldNot compile
    "BasicSets   .props.float"    shouldNot compile
    "BasicSets   .props.int"      shouldNot compile
    "BasicSets   .props.long"     shouldNot compile
    "BasicSets   .props.string"   shouldNot compile
  }

}
