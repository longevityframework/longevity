package emblem.traversors.sync

import emblem.TypeKey
import emblem.testData.exhaustive
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** exercises translations of [[JsonToEmblemTranslator]] and
 * [[EmblemToJsonTranslator]] by running data through both, and seeing if we
 * get the original result.
 *
 * this test attempts to be exhaustive of emblematic features using
 * [[emblem.testData.exhaustive]].
 */
class JsonTranslationSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val emblemTranslator = new EmblemToJsonTranslator {
    override protected val emblematic = exhaustive.emblematic
  }

  private val jsonTranslator = new JsonToEmblemTranslator {
    override protected val emblematic = exhaustive.emblematic
  }

  behavior of "a pipeline of the JSON producer and consumer translators"

  it should "produce the original output for basic types" in {
    pipelineReproducesInput(exhaustive.basics.boolean)
    pipelineReproducesInput(exhaustive.basics.char)
    pipelineReproducesInput(exhaustive.basics.dateTime)
    pipelineReproducesInput(exhaustive.basics.double)
    pipelineReproducesInput(exhaustive.basics.float)
    pipelineReproducesInput(exhaustive.basics.int)
    pipelineReproducesInput(exhaustive.basics.long)
    pipelineReproducesInput(exhaustive.basics.string)    
  }

  it should "produce the original output for extractors" in {
    pipelineReproducesInput(exhaustive.extractors.email)
    pipelineReproducesInput(exhaustive.extractors.markdown)
    pipelineReproducesInput(exhaustive.extractors.uri)
  }

  // TODO: emblems
  // TODO: unions
  // TODO: collections

  private def pipelineReproducesInput[A : TypeKey](a: A): Unit = {
    jsonTranslator.traverse(emblemTranslator.traverse[A](a)) should equal (a)
  }

}
