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
import longevity.subdomain.Assoc
import longevity.subdomain.shorthandPoolToExtractorPool
import longevity.subdomain.persistent.Persistent

// TODO pt #110726688 produce roots with PersistedAssoc instead of UnpersistedAssoc

/** mixin trait for test data generation */
private[longevity] trait TestDataGeneration {

  protected val longevityContext: LongevityContext
  protected val testDataGenerator = new TestDataGenerator(
    longevityContext.subdomain.entityEmblemPool,
    shorthandPoolToExtractorPool(longevityContext.subdomain.shorthandPool),
    longevityContext.customGeneratorPool + assocGenerator)

  private def assocGenerator: CustomGenerator[Assoc[_ <: Persistent]] =
    new CustomGenerator[Assoc[_ <: Persistent]] {
      def apply[B <: Assoc[_ <: Persistent] : TypeKey](generator: Generator): B = {
        val pTypeKey: TypeKey[_ <: Persistent] =
          typeKey[B].typeArgs.head.castToUpperBound[Persistent].get
        def genAssoc[Associatee <: Persistent : TypeKey] =
          Assoc[Associatee](generator.generate[Associatee])
        genAssoc(pTypeKey).asInstanceOf[B]
      }
    }

}
