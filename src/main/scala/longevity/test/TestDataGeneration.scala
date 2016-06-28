package longevity.test

import emblem.emblematic.traversors.sync.TestDataGenerator
import longevity.context.LongevityContext

/** mixin trait for test data generation */
private[longevity] trait TestDataGeneration {

  protected val longevityContext: LongevityContext
  protected val testDataGenerator = new TestDataGenerator(
    longevityContext.subdomain.emblematic,
    longevityContext.customGeneratorPool)

}
