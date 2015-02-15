package emblem.traversors

// TODO this one is next

import emblem._
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

  /** TODO
   * @throws emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def diff[A : TypeKey](lhs: A, rhs: A): Diffs = traversor.traverse(DifferInput(lhs, rhs, ""))

  private case class DifferInput[A](lhs: A, rhs: A, path: String)

  private val traversor = new Traversor {

    type TraverseInput[A] = DifferInput[A]
    type TraverseResult[A] = Diffs

    override protected val shorthandPool: ShorthandPool = Differ.this.shorthandPool
    override protected val emblemPool: EmblemPool = Differ.this.emblemPool

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

    protected def stageTraverseEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: DifferInput[A])
    : Iterator[TraverseEmblemPropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) =
        (prop, DifferInput(prop.get(input.lhs), prop.get(input.rhs), input.path + "." + prop.name))
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageTraverseEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: DifferInput[A],
      result: Iterator[TraverseEmblemPropResult[A, _]])
    : Diffs =
      result.map(_._2).foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }

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

    override protected def unstageTraverseOptionValue[A : TypeKey](
      input: DifferInput[Option[A]],
      optionValueResult: Option[Diffs])
    : Diffs =
      if (input.lhs.size == input.rhs.size)
        optionValueResult.get
      else
        Seq(Diff(input.path + ".size", input.lhs.size, input.rhs.size))

    // we kind of have to bail on traversing sets, since there is no obvious way to pull out matching pairs
    // of elements from the lhs and rhs sets
    protected def stageTraverseSetElements[A : TypeKey](input: DifferInput[Set[A]]): Iterator[DifferInput[A]] =
      Iterator.empty

    protected def unstageTraverseSetElements[A : TypeKey](
      setInput: DifferInput[Set[A]],
      setElementsResult: Iterator[Diffs])
    : Diffs =
      if (setInput.lhs.size != setInput.rhs.size) {
        Seq(Diff(setInput.path + ".size", setInput.lhs.size, setInput.rhs.size))
      } else if (setInput.lhs != setInput.rhs) {
        Seq(Diff(setInput.path, setInput.lhs, setInput.rhs))
      } else {
        Seq()
      }

    protected def stageTraverseListElements[A : TypeKey](input: DifferInput[List[A]]): Iterator[DifferInput[A]] =
      if (input.lhs.size == input.rhs.size) {
        (0 until input.lhs.size).iterator map { i =>
          DifferInput[A](
            input.lhs(i),
            input.rhs(i),
            input.path + "(" + i + ")")
        }
      }
      else Iterator.empty

    protected def unstageTraverseListElements[A : TypeKey](
      input: DifferInput[List[A]],
      result: Iterator[Diffs]): Diffs =
      if (input.lhs.size == input.rhs.size) {
        result.foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }
      } else {
        Seq(Diff(input.path + ".size", input.lhs.size, input.rhs.size))
      }

  }

}
