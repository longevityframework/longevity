package longevity.integration.noTranslation

/** a type that should throw [[NotInSubdomainTranslationException]]
 * when encountered by [[SubdomainToBsonTranslator]]
 */
case class NoTranslation(name: String)
