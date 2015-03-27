package longevity.persistence

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem._
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Traversor
import longevity.context._
import longevity.subdomain._
import longevity.exceptions.CouldNotTranslateException

/** translates [[Entity entities]] into
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList casbah MongoDBObjects]].
 *
 * @param longevityContext the longevity context that contains the entity types and shorthands to use in the
 * translation
 */
private[persistence] class EntityToCasbahTranslator(longevityContext: LongevityContext) {

  /** translates an [[Entity]] into a `MongoDBList` */
  def translate[E <: Entity : TypeKey](e: E): MongoDBObject = try {
    traversor.traverse[E](e).asInstanceOf[BasicDBObject]
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTranslateException(e.typeKey, e)
  }

  private val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = Any

    override protected val emblemPool: EmblemPool = longevityContext.entityEmblemPool
    override protected val shorthandPool: ShorthandPool = longevityContext.shorthandPool
    override protected val customTraversors: CustomTraversors = emptyCustomTraversor + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: RootEntity] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: RootEntity]]
 
        // TODO pt 91220826: get rid of asInstanceOf by tightening type on repo pools and repo layers
        val associateeRepo = longevityContext.repoPool(associateeTypeKey).asInstanceOf[MongoRepo[_]]

        input.asInstanceOf[associateeRepo.MongoId].objectId
      }
    }

    protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean] = input

    protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char] = input

    protected def traverseDouble(input: TraverseInput[Double]): TraverseResult[Double] = input

    protected def traverseFloat(input: TraverseInput[Float]): TraverseResult[Float] = input

    protected def traverseInt(input: TraverseInput[Int]): TraverseResult[Int] = input

    protected def traverseLong(input: TraverseInput[Long]): TraverseResult[Long] = input

    protected def traverseString(input: TraverseInput[String]): TraverseResult[String] = input

    protected def stageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: TraverseInput[A])
    : Iterator[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = prop -> prop.get(input)
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: TraverseInput[A],
      result: Iterator[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = new MongoDBObjectBuilder()
      result.foreach {
        case (prop, propResult) =>
          def pair[B : TypeKey] = prop.name -> propResult.asInstanceOf[B]
          builder += pair(prop.typeKey)
      }
      builder.result()
    }

    protected def stageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: TraverseInput[Actual])
    : TraverseInput[Abbreviated] = {
      shorthand.abbreviate(input)
    }

    protected def unstageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviatedResult: TraverseResult[Abbreviated])
    : TraverseResult[Actual] =
      abbreviatedResult

    protected def stageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]])
    : Option[TraverseInput[A]] =
      input

    protected def unstageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]],
      result: Option[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result

    protected def stageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]])
    : Iterator[TraverseInput[A]] =
      input.iterator

    protected def unstageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]],
      result: Iterator[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: TraverseInput[List[A]])
    : Iterator[TraverseInput[A]] =
      input.iterator

    protected def unstageListElements[A : TypeKey](
      input: TraverseInput[List[A]],
      result: Iterator[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
