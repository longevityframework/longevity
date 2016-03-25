package longevity.persistence.cassandra

import emblem.exceptions.CouldNotTraverseException
import emblem.imports._
import emblem.traversors.sync.JsonToEmblemTranslator
import java.util.UUID
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.persistent.Persistent
import org.json4s.JsonAST._

private[cassandra] class JsonToPersistentTranslator(
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool)
extends JsonToEmblemTranslator {

  override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

  def assocTraversor = new CustomTraversor[AssocAny] {
    def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: JValue): B = {
      def assocFromString(s: String) = {
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: Persistent]]
        CassandraId(UUID.fromString(s))
      }
      input match {
        case JString(s) => assocFromString(s).asInstanceOf[B]
        case _ => throw new CouldNotTraverseException(typeKey[B])
      }
    }
  }

}
