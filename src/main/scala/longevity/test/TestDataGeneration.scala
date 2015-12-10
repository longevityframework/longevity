package longevity.test

import longevity.context.LongevityContext
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import emblem.TypeBoundPair
import emblem.traversors.sync.CustomGeneratorPool
import emblem.traversors.sync.CustomGenerator
import emblem.traversors.sync.Differ
import emblem.traversors.sync.Generator
import emblem.traversors.sync.TestDataGenerator
import longevity.subdomain._

/** mixin trait for test data generation */
private[longevity] trait TestDataGeneration {

  protected val longevityContext: LongevityContext
  protected val testDataGenerator = new TestDataGenerator(
    longevityContext.subdomain.entityEmblemPool,
    shorthandPoolToExtractorPool(longevityContext.subdomain.shorthandPool),
    longevityContext.customGeneratorPool + assocGenerator)

  private def assocGenerator: CustomGenerator[Assoc[_ <: RootEntity]] =
    new CustomGenerator[Assoc[_ <: RootEntity]] {
      def apply[B <: Assoc[_ <: RootEntity] : TypeKey](generator: Generator): B = {
        val entityTypeKey: TypeKey[_ <: RootEntity] =
          typeKey[B].typeArgs.head.castToUpperBound[RootEntity].get
        def genAssoc[Associatee <: RootEntity : TypeKey] =
            Assoc[Associatee](generator.generate[Associatee])
        genAssoc(entityTypeKey).asInstanceOf[B]
      }
    }

}
