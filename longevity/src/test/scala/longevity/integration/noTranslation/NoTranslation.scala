package longevity.integration.noTranslation

/** a type that should throw [[NotInDomainModelTranslationException]]
 * when encountered by [[DomainModelToBsonTranslator]]
 */
case class NoTranslation(name: String)
