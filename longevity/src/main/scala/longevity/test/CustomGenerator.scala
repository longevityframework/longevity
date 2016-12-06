package longevity.test

import emblem.TypeKey

private[test] class CustomGenerator[A](
  val genTypeKey: TypeKey[A],
  val f: (TestDataGenerator) => A)
