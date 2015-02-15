package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotGenerateException
import emblem.traversors.Visitor._
import org.scalatest._

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

    val visitor = new Visitor {
      override protected val customVisitors: CustomVisitors = emptyCustomVisitors + listCustomVisitor
    }

  }

}
