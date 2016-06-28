package longevity.integration.noTranslation

/** a type that should throw [[NotInSubdomainTranslationException]]
 * when encountered by [[PersistentToCasbahTranslator]]
 */
case class NoTranslation(name: String)
