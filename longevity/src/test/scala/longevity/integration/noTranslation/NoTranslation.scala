package longevity.integration.noTranslation

/** a type that should throw [[CouldNotTranslateException]] when encountered by [[EntityToCasbahTranslator]] */
case class NoTranslation(name: String)
