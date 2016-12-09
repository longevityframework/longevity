package longevity.context
import longevity.json.JsonMarshaller
import longevity.json.JsonUnmarshaller

/** the portion of a [[LongevityContext]] that deals with JSON marshalling */
trait JsonContext {

  /** a utility to translate from your domain objects into JSON */
  val jsonMarshaller: JsonMarshaller

  /** a utility to translate from JSON into your domain objects */
  val jsonUnmarshaller: JsonUnmarshaller

}
