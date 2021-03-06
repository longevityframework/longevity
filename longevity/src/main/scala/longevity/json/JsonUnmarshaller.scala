package longevity.json

import typekey.TypeKey
import longevity.emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import longevity.model.ModelType
import org.json4s.JsonAST.JValue

/** translates from JSON into objects from your domain model. translates from
 * [[https://github.com/json4s/json4s json4s AST]].
 * 
 * expects JSON for persistent components with a single member to be inlined in the JSON. expects
 * [[longevity.model.PolyCType PolyCTypes]] to not be inlined.
 */
class JsonUnmarshaller(modelType: ModelType[_]) {

  private val translator = new JsonToEmblematicTranslator {
    override protected val emblematic = modelType.emblematic
  }

  /** unmarshalls a modelType object from json4s AST */
  def unmarshall[A : TypeKey](input: JValue): A = translator.translate[A](input)

}
