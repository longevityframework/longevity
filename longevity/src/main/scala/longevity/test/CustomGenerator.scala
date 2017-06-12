package longevity.test

import typekey.TypeKey

private[test] class CustomGenerator[A](
  val genTypeKey: TypeKey[A],
  val f: (TestDataGenerator) => A)
