package longevity.migrations

import longevity.config.LongevityConfig
import longevity.emblem.reflectionUtil.lookupFieldInPackage
import longevity.context.LongevityContext
import cats.effect.IO
import cats.implicits._

object Unversioner extends App {

  if (args.length != 2) {
    throw new RuntimeException("expects exactly two arguments: <model-name> <version>")
  }

  val modelName = args(0)
  val version = args(1)

  val (packageName, fieldName) = {
    val i = modelName.lastIndexOf('.')
    (modelName.substring(0, i), modelName.substring(i + 1))
  }

  val config = LongevityConfig.fromTypesafeConfig().copy(modelVersion = Some(version))

}
