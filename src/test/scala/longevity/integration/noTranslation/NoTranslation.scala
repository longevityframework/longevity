package longevity.integration.noTranslation

/** a type that should throw [[BsonTranslationException]] when encountered by [[PersistentToCasbahTranslator]] */
case class NoTranslation(name: String)
