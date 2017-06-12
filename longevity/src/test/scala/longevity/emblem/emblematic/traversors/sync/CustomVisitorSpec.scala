package longevity.emblem.emblematic.traversors.sync

import typekey.TypeKey
import typekey.typeKey
import longevity.emblem.emblematic.traversors.sync.Visitor._
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specs for [[CustomVisitor]] */
class CustomVisitorSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "the example code in the scaladocs for trait CustomVisitor"
  it should "compile and produce the expected results" in {

    // only visit the first five elements of a list
    val listCustomVisitor = new CustomVisitor[List[Any]] {
      def apply[B <: List[_] : TypeKey](visitor: Visitor, list: B): Unit = {
        val elementTypeKey = typeKey[B].typeArgs.head
        def visitFive[C : TypeKey]: Unit =
          list.asInstanceOf[List[C]].take(5).foreach { element => visitor.visit(element) }
        visitFive(elementTypeKey)
      }
    }

    // count int visits
    var visitCount = 0
    val intCustomVisitor = new CustomVisitor[Int] {
      def apply[B <: Int : TypeKey](visitor: Visitor, b: B): Unit = {
        visitCount += 1
      }
    }

    val visitor = new Visitor {
      override protected val customVisitors: CustomVisitorPool =
        CustomVisitorPool.empty + listCustomVisitor + intCustomVisitor
    }

    visitor.visit("dd")
    visitCount should equal (0)
    visitor.visit(5)
    visitCount should equal (1)
    visitor.visit(List(2,3,4))
    visitCount should equal (4)
    visitor.visit(List(2,3,4,5,6,7,8,9,0,1,2,3,4,5,6))
    visitCount should equal (9)
    visitor.visit(List(2,3,4,5,6,7,8,9,0,1,2,3,4,5,6))
    visitCount should equal (14)
  }

  behavior of "the example code in the scaladocs for trait CustomVisitor, modified to use KTBF"
  it should "compile and produce the expected results" in {

    // this is a safe way to encapsulate the type cast in the above example. a little wordy isnt it? :)

    // TODO pt-91207722: investigate this further. this kind of type casting has shown up in a lot of different
    // places now. ideally, emblem users should not have to type cast.

    // this should go in emblem package object if i am going to follow through with this
    type UnitAny[_] = Unit

    trait KeyedTypeBoundFunction[TypeBound, Arg[_ <: TypeBound], ReturnVal[_ <: TypeBound]] {
      def apply[TypeParam <: TypeBound : TypeKey](value: Arg[TypeParam]): ReturnVal[TypeParam]
    }

    def applyKeyed[
      TypeBound,
      Arg[_ <: TypeBound],
      ReturnVal[_ <: TypeBound],
      RefinedArg <: Arg[_ <: TypeBound] : TypeKey](
      arg: RefinedArg,
      f: KeyedTypeBoundFunction[TypeBound, Arg, ReturnVal])
    : ReturnVal[_ <: TypeBound] = {
      val typeParamTypeKey = typeKey[RefinedArg].typeArgs.head.asInstanceOf[TypeKey[_ <: TypeBound]]
      def applyWithTypeParam[TypeParam <: TypeBound : TypeKey]: ReturnVal[TypeParam] =
        f(arg.asInstanceOf[Arg[TypeParam]]) // typecast is safe, but we still want to hide it from users
      applyWithTypeParam(typeParamTypeKey)
    }

    // only visit the first five elements of a list
    val listCustomVisitor = new CustomVisitor[List[Any]] {
      def apply[B <: List[_] : TypeKey](visitor: Visitor, list: B): Unit = {
        val visitFiveNoCast = new KeyedTypeBoundFunction[Any, List, UnitAny] {
          def apply[C : TypeKey](clist: List[C]): Unit =
            clist.take(5).foreach { element => visitor.visit(element) }
        }
        applyKeyed[Any, List, UnitAny, B](list, visitFiveNoCast)
      }
    }

    val visitor = new Visitor {
      override protected val customVisitors: CustomVisitorPool = CustomVisitorPool.empty + listCustomVisitor
    }

  }

}
