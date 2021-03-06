package longevity.json

import typekey.TypeKey
import longevity.emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import longevity.model.ModelType
import org.json4s.JsonAST.JValue

/** translates from your domain objects into JSON. translates objects into
 * [[https://github.com/json4s/json4s json4s AST]].
 * 
 * persistent components with a single member will be inlined in the JSON. does
 * not inline [[longevity.model.PolyCType PolyCTypes]].
 */
class JsonMarshaller(modelType: ModelType[_]) {

  private val translator = new EmblematicToJsonTranslator {
    override protected val emblematic = modelType.emblematic
  }

  /** marshalls a modelType object into json4s AST */
  def marshall[A : TypeKey](input: A): JValue = translator.translate[A](input)

}
