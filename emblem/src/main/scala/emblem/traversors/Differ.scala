package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotTraverseException
import scala.reflect.runtime.universe.typeOf
import Differ._

// TODO: DifferSpec

object Differ {

  case class Diff(path: String, lhs: Any, rhs: Any)

  type Diffs = Seq[Diff]

  def explainDiffs(diffs: Diffs, goryDetails: Boolean = false): String = {
    if (diffs.isEmpty)
      "lhs and rhs have no diffs"
    else {
      val builder = new StringBuilder
      builder.append("lhs and rhs have the following diffs:\n")
      diffs.foreach { diff =>
        builder.append(s" - lhs.${diff.path} != rhs.${diff.path}\n")
        if (goryDetails) {
          builder.append(s"     lhs.${diff.path} = ${diff.lhs}\n")
          builder.append(s"     rhs.${diff.path} = ${diff.rhs}\n")
        }
      }
      builder.toString
    }
  }

}

/** TODO scaladoc */
class Differ(
  private val shorthandPool: ShorthandPool = ShorthandPool(),
  private val emblemPool: EmblemPool = EmblemPool()) {

  case class DifferInput[A](lhs: A, rhs: A, path: String)

  def diff[A : TypeKey](lhs: A, rhs: A): Diffs = traversor.traverse(DifferInput(lhs, rhs, ""))

  val traversor = new Traversor {

    type TraverseInput[A] = DifferInput[A]
    type TraverseEmblemInput[A <: HasEmblem] = DifferInput[A]
    type TraverseResult[A] = Diffs

    override protected val shorthandPool: ShorthandPool = Differ.this.shorthandPool
    override protected val emblemPool: EmblemPool = Differ.this.emblemPool
    override protected val customTraversors: CustomTraversors = emptyCustomTraversor

    protected def traverseBoolean(input: DifferInput[Boolean]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseChar(input: DifferInput[Char]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseDouble(input: DifferInput[Double]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseFloat(input: DifferInput[Float]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseInt(input: DifferInput[Int]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseLong(input: DifferInput[Long]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseString(input: DifferInput[String]): Diffs = {
      if (input.lhs == input.rhs) Seq() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def stageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      input: DifferInput[A])
    : DifferInput[A] = input

    protected def stageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      input: DifferInput[A])
    : DifferInput[B] =
      DifferInput(
        prop.get(input.lhs),
        prop.get(input.rhs),
        input.path + "." + prop.name)

    protected def unstageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      input: DifferInput[A],
      propResult: Diffs)
    : DifferInput[A] =
      input

    protected def unstageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      input: DifferInput[A])
    : Diffs =
      ???

    protected def stageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: DifferInput[Actual])
    : DifferInput[Abbreviated] =
      input.copy(
        lhs = shorthand.abbreviate(input.lhs),
        rhs = shorthand.abbreviate(input.rhs))

    protected def unstageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      result: Diffs)
    : Diffs = result

    protected def stageTraverseOptionValue[A : TypeKey](
      input: DifferInput[Option[A]])
    : Option[DifferInput[A]] =
      (input.lhs, input.rhs) match {
        case (Some(lhso), Some(rhso)) => Some(DifferInput[A](lhso, rhso, input.path + ".value"))
        case _ => None
      }

    protected def unstageTraverseOptionValue[A : TypeKey](result: Option[Diffs]): Diffs =
      result.getOrElse(Seq())

    override protected def unstageTraverseOption[A : TypeKey](
      input: DifferInput[Option[A]],
      optionValueResult: Diffs)
    : Diffs =
      if (input.lhs.size == input.rhs.size)
        optionValueResult
      else
        optionValueResult :+ Diff(input.path + ".size", input.lhs.size, input.rhs.size)

    override protected def unstageTraverseSet[A : TypeKey](
      input: DifferInput[Set[A]],
      setElementsResult: Diffs)
    : Diffs =
      if (input.lhs.size != input.rhs.size) {
        setElementsResult :+ Diff(input.path + ".size", input.lhs.size, input.rhs.size)
      } else if (input.lhs != input.rhs) {
        setElementsResult :+ Diff(input.path, input.lhs, input.rhs)
      } else {
        Seq()
      }

    // we kind of have to bail on traversing sets, since there is no obvious way to pull out matching pairs
    // of elements from the lhs and rhs sets
    protected def stageTraverseSetElements[A : TypeKey](input: DifferInput[Set[A]]): Iterator[DifferInput[A]] =
      Iterator.empty

    protected def unstageTraverseSetElements[A : TypeKey](result: Iterator[Diffs]): Diffs =
      result.foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }

    protected def stageTraverseListElements[A : TypeKey](input: DifferInput[List[A]]): List[DifferInput[A]] =
      if (input.lhs.size == input.rhs.size) {
        (0 until input.lhs.size).toList map { i =>
          DifferInput[A](
            input.lhs(i),
            input.rhs(i),
            input.path + "(" + i + ")")
        }
      }
      else List() 

    protected def unstageTraverseListElements[A : TypeKey](result: List[Diffs]): Diffs =
      result.foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }

    override protected def unstageTraverseList[A : TypeKey](
      input: DifferInput[List[A]],
      listElementsResult: Diffs)
    : Diffs =
      if (input.lhs.size != input.rhs.size) {
        listElementsResult :+ Diff(input.path + ".size", input.lhs.size, input.rhs.size)
      } else {
        listElementsResult
      }

  }

}
