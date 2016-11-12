package longevity.json

import emblem.TypeKey
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import longevity.subdomain.Subdomain
import org.json4s.JsonAST.JValue

/** translates from JSON into objects from your subdomain.
 * translates from [[https://github.com/json4s/json4s json4s AST]].
 * 
 * expects JSON for persistent components with a single member to be inlined in
 * the JSON. expects [[longevity.subdomain.PolyCType PolyCTypes]] to not be
 * inlined.
 */
class JsonUnmarshaller(subdomain: Subdomain) {

  private val translator = new JsonToEmblematicTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  /** unmarshalls a subdomain object from json4s AST */
  def unmarshall[A : TypeKey](input: JValue): A = translator.translate[A](input)

}
