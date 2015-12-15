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

  private def assocGenerator: CustomGenerator[Assoc[_ <: Root]] =
    new CustomGenerator[Assoc[_ <: Root]] {
      def apply[B <: Assoc[_ <: Root] : TypeKey](generator: Generator): B = {
        val entityTypeKey: TypeKey[_ <: Root] =
          typeKey[B].typeArgs.head.castToUpperBound[Root].get
        def genAssoc[Associatee <: Root : TypeKey] =
            Assoc[Associatee](generator.generate[Associatee])
        genAssoc(entityTypeKey).asInstanceOf[B]
      }
    }

}
