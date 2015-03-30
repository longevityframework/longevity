package longevity.persistence

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem._
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Traversor
import longevity.subdomain._
import longevity.exceptions.CouldNotTranslateException

/** translates [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList
 * casbah MongoDBObjects]] into [[Entity entities]].
 *
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param extractorPool a complete set of the extractors used by the bounded context
 * @param repoPool a pool of the repositories for this persistence context
 */
private[persistence] class CasbahToEntityTranslator(
  emblemPool: EmblemPool,
  extractorPool: ExtractorPool,
  private val repoPool: RepoPool) {

  /** translates a `MongoDBList` into an [[Entity]] */
  def translate[E <: Entity : TypeKey](casbah: MongoDBObject): E = try {
    traversor.traverse[E](casbah)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTranslateException(typeKey[E], e)
  }

  private val traversor = new Traversor {

    type TraverseInput[A] = Any
    type TraverseResult[A] = A

    override protected val emblemPool: EmblemPool = CasbahToEntityTranslator.this.emblemPool
    override protected val extractorPool: ExtractorPool = CasbahToEntityTranslator.this.extractorPool
    override protected val customTraversors: CustomTraversors = CustomTraversors.empty + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: RootEntity] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {

        // this asInstanceOf is because of emblem shortfall
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: RootEntity]]
 
        // this asInstanceOf is because we dont type param our RepoPool with PersistenceStrategy
        def associateeRepo[Associatee <: RootEntity : TypeKey] =
          repoPool(typeKey[Associatee]).asInstanceOf[MongoRepo[Associatee]]

        // first asInstanceOf because casbah gives us Any
        // second asInstanceOf is basically the same emblem shortfall as before
        associateeRepo(associateeTypeKey).MongoId(input.asInstanceOf[ObjectId]).asInstanceOf[B]
      }
    }

    protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean] =
      input.asInstanceOf[Boolean]

    protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char] =
      input.asInstanceOf[String](0)

    protected def traverseDouble(input: TraverseInput[Double]): TraverseResult[Double] =
      input.asInstanceOf[Double]

    protected def traverseFloat(input: TraverseInput[Float]): TraverseResult[Float] =
      input.asInstanceOf[Double].toFloat

    protected def traverseInt(input: TraverseInput[Int]): TraverseResult[Int] =
      input.asInstanceOf[Int]

    protected def traverseLong(input: TraverseInput[Long]): TraverseResult[Long] =
      input.asInstanceOf[Long]

    protected def traverseString(input: TraverseInput[String]): TraverseResult[String] =
      input.asInstanceOf[String]

    protected def stageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: TraverseInput[A])
    : Iterator[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = prop -> input.asInstanceOf[MongoDBObject](prop.name)
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: TraverseInput[A],
      result: Iterator[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageExtractor[Domain, Range](
      extractor: Extractor[Domain, Range],
      input: TraverseInput[Range])
    : TraverseInput[Domain] =
      input

    protected def unstageExtractor[Domain, Range](
      extractor: Extractor[Domain, Range],
      domainResult: TraverseResult[Domain])
    : TraverseResult[Range] =
      extractor.apply(domainResult)

    protected def stageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]])
    : Option[TraverseInput[A]] =
      input.asInstanceOf[Option[TraverseInput[A]]]

    protected def unstageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]],
      result: Option[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result

    protected def stageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]])
    : Iterator[TraverseInput[A]] = {
      val list: MongoDBList = input.asInstanceOf[BasicDBList]
      list.iterator
    }

    protected def unstageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]],
      result: Iterator[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: TraverseInput[List[A]])
    : Iterator[TraverseInput[A]] = {
      val list: MongoDBList = input.asInstanceOf[BasicDBList]
      list.iterator
    }

    protected def unstageListElements[A : TypeKey](
      input: TraverseInput[List[A]],
      result: Iterator[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
