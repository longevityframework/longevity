package longevity.persistence.cassandra

import emblem.exceptions.CouldNotTraverseException
import emblem.imports._
import emblem.traversors.sync.JsonToEmblemTranslator
import java.util.UUID
import longevity.subdomain._
import org.json4s.JsonAST._

private[cassandra] class JsonToRootTranslator(
  override protected val emblemPool: EmblemPool,
  override protected val extractorPool: ExtractorPool)
extends JsonToEmblemTranslator {

  override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

  def assocTraversor = new CustomTraversor[AssocAny] {
    def apply[B <: Assoc[_ <: Root] : TypeKey](input: JValue): B = {
      // TODO fix emblem to get rid of the asInstanceOf shortfalls
      def assocFromString(s: String) = {
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: Root]]
        CassandraId(UUID.fromString(s), associateeTypeKey)
      }
      input match {
        case JString(s) => assocFromString(s).asInstanceOf[B]
        case _ => throw new CouldNotTraverseException(typeKey[B])
      }
    }
  }

}
