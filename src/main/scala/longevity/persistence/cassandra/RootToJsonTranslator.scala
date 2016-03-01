package longevity.persistence.cassandra

import emblem.imports._
import emblem.traversors.sync.EmblemToJsonTranslator
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.subdomain._
import org.json4s.JsonAST._

private[cassandra] class RootToJsonTranslator(
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool)
extends EmblemToJsonTranslator {

  override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

  def assocTraversor = new CustomTraversor[AssocAny] {
    def apply[B <: Assoc[_ <: Root] : TypeKey](input: B): JValue = {
      if (!input.isPersisted) {
        throw new AssocIsUnpersistedException(input)
      }
      JString(input.asInstanceOf[CassandraId[_ <: Root]].uuid.toString)
    }
  }

}
