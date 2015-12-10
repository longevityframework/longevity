package longevity.integration.noTranslation

/** a type that should throw [[BsonTranslationException]] when encountered by [[EntityToCasbahTranslator]] */
case class NoTranslation(name: String)
