package longevity.json

import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import longevity.subdomain.Subdomain
import org.json4s.JsonAST.JValue

/** translates from your subdomain objects into JSON.
 * translates objects into [[https://github.com/json4s/json4s json4s AST]].
 * 
 * [[longevity.subdomain.Embeddable Embeddables]] with a single
 * member will be inlined in the JSON. Does not inline
 * [[longevity.subdomain.embeddable.PolyEType PolyETypes]].
 */
class JsonMarshaller(subdomain: Subdomain) {

  private val translator = new EmblematicToJsonTranslator {
    override protected val emblematic = subdomain.emblematic
  }

  /** marshalls a subdomain object into json4s AST */
  def marshall[A : TypeKey](input: A): JValue = translator.translate[A](input)

}
