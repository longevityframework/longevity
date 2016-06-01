package longevity.test

import emblem.TypeKey
import emblem.emblematic.traversors.sync.CustomGenerator
import emblem.emblematic.traversors.sync.Generator
import emblem.emblematic.traversors.sync.TestDataGenerator
import emblem.typeKey
import longevity.context.LongevityContext
import longevity.subdomain.Assoc
import longevity.subdomain.persistent.Persistent

// TODO pt #110726688 produce roots with PersistedAssoc instead of UnpersistedAssoc

/** mixin trait for test data generation */
private[longevity] trait TestDataGeneration {

  protected val longevityContext: LongevityContext
  protected val testDataGenerator = new TestDataGenerator(
    longevityContext.subdomain.emblematic,
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
