package emblem.traversors.sync

import Differ.Diff
import Differ.Diffs
import emblem.Emblem
import emblem.Emblematic
import emblem.EmblemPool
import emblem.EmblemProp
import emblem.Extractor
import emblem.ExtractorPool
import emblem.HasEmblem
import emblem.TypeKey
import emblem.Union
import emblem.typeKey
import org.joda.time.DateTime
import scala.reflect.runtime.universe.typeOf

/** recursively computes a sequence of [[Differ.Diff diffs]] between two different values of the same type.
 * 
 * we kind of have to bail on traversing sets, since there is no obvious way to pull out matching pairs
 * of elements from the lhs and rhs sets. if the sets have differing sizes, then we report the difference in
 * size. if the sets are otherwise different, then we report the sets as different.
 *
 * @param emblematic the emblematic types to use in the traversal
 */
class Differ(
  private val emblematic: Emblematic = Emblematic.empty) {

  /** computes the diffs between the left- and right-hand sides
   * @param lhs the left-hand side
   * @param rhs the right-hand side
   * @throws emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def diff[A : TypeKey](lhs: A, rhs: A): Diffs = traversor.traverse(DifferInput(lhs, rhs, ""))

  private case class DifferInput[A](lhs: A, rhs: A, path: String)

  private val traversor = new Traversor {

    type TraverseInput[A] = DifferInput[A]
    type TraverseResult[A] = Diffs

    override protected val emblematic = Differ.this.emblematic

    protected def traverseBoolean(input: DifferInput[Boolean]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseChar(input: DifferInput[Char]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseDateTime(input: DifferInput[DateTime]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseDouble(input: DifferInput[Double]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseFloat(input: DifferInput[Float]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseInt(input: DifferInput[Int]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    protected def traverseLong(input: DifferInput[Long]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    override protected def traverseString(input: DifferInput[String]): Diffs = {
      if (input.lhs == input.rhs) Diffs() else Seq(Diff(input.path, input.lhs, input.rhs))
    }

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: DifferInput[A])
    : TypeKey[_ <: A] =
      union.typeKeyForInstance(input.lhs).get

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: DifferInput[A])
    : Iterable[DifferInput[B]] = {
      val lhsTypeKey = typeKey[A]
      val rhsTypeKey = union.typeKeyForInstance(input.rhs).get
      if (lhsTypeKey == rhsTypeKey) {
        Seq(input.asInstanceOf[DifferInput[B]])
      }
      else {
        Seq()
      }
    }

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: DifferInput[A],
      result: Iterable[Diffs])
    : Diffs =
      result.headOption.getOrElse(Seq(
        Diff(s"${input.path}.type", typeKey[A].name, typeKey[B].name)))

    override protected def stageEmblemProps[A <: HasEmblem : TypeKey](emblem: Emblem[A], input: DifferInput[A])
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) =
        (prop, DifferInput(prop.get(input.lhs), prop.get(input.rhs), input.path + "." + prop.name))
      emblem.props.map(propInput(_))
    }

    override protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      result: Iterable[PropResult[A, _]])
    : Diffs =
      result.map(_._2).foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }

    override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      input: DifferInput[Domain])
    : DifferInput[Range] =
      input.copy(
        lhs = extractor.apply(input.lhs),
        rhs = extractor.apply(input.rhs),
        path = input.path + ".inverse")

    override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      result: Diffs)
    : Diffs = result

    override protected def stageOptionValue[A : TypeKey](input: DifferInput[Option[A]]): Iterable[DifferInput[A]] =
      (input.lhs, input.rhs) match {
        case (Some(lhso), Some(rhso)) => Seq(DifferInput[A](lhso, rhso, input.path + ".value"))
        case _ => Seq()
      }

    override protected def unstageOptionValue[A : TypeKey](
      input: DifferInput[Option[A]],
      optionValueResult: Iterable[Diffs])
    : Diffs =
      if (input.lhs.size == input.rhs.size)
        optionValueResult.headOption.getOrElse(Diffs())
      else
        Seq(Diff(input.path + ".size", input.lhs.size, input.rhs.size))

    override protected def stageSetElements[A : TypeKey](input: DifferInput[Set[A]]): Iterable[DifferInput[A]] =
      Iterable.empty

    override protected def unstageSetElements[A : TypeKey](
      setInput: DifferInput[Set[A]],
      setElementsResult: Iterable[Diffs])
    : Diffs =
      if (setInput.lhs.size != setInput.rhs.size) {
        Seq(Diff(setInput.path + ".size", setInput.lhs.size, setInput.rhs.size))
      } else if (setInput.lhs != setInput.rhs) {
        Seq(Diff(setInput.path, setInput.lhs, setInput.rhs))
      } else {
        Diffs()
      }

    override protected def stageListElements[A : TypeKey](input: DifferInput[List[A]]): Iterable[DifferInput[A]] =
      if (input.lhs.size == input.rhs.size) {
        (0 until input.lhs.size) map { i =>
          DifferInput[A](
            input.lhs(i),
            input.rhs(i),
            input.path + "(" + i + ")")
        }
      }
      else Iterable.empty

    override protected def unstageListElements[A : TypeKey](
      input: DifferInput[List[A]],
      result: Iterable[Diffs]): Diffs =
      if (input.lhs.size == input.rhs.size) {
        result.foldLeft(Seq[Diff]()) { (a: Diffs, b: Diffs) => a ++ b }
      } else {
        Seq(Diff(input.path + ".size", input.lhs.size, input.rhs.size))
      }

  }

}

object Differ {

  /** a diff encountered by the [[Differ]].
   * @param path the path from the root where the diff was encountered
   * @param lhs the left-hand side value
   * @param rhs the right-hand side value
   */
  case class Diff(path: String, lhs: Any, rhs: Any)

  /** a sequence of [[Diff diffs]] */
  type Diffs = Seq[Diff]

  /** generator methods for Diffs */
  object Diffs {

    /** creates and returns a set of diffs */
    def apply(diffs: Diff*): Diffs = Seq[Diff](diffs: _*)
  }

  /** a textual explanation of a [[Diffs sequence of diffs]].
   * @param diffs the diffs
   * @param goryDetails when true, the explanation is expanded by including the left- and right-hand side
   * values of the diffs encountered
   * @return the textual explanation
   */
  def explainDiffs(diffs: Diffs, goryDetails: Boolean = false): String = {
    if (diffs.isEmpty)
      "lhs and rhs have no diffs"
    else {
      val builder = new StringBuilder
      builder.append("lhs and rhs have the following diffs:\n")
      diffs.foreach { diff =>
        builder.append(s" - lhs${diff.path} != rhs${diff.path}\n")
        if (goryDetails) {
          builder.append(s"     lhs${diff.path} = ${diff.lhs}\n")
          builder.append(s"     rhs${diff.path} = ${diff.rhs}\n")
        }
      }
      builder.toString
    }
  }

}
