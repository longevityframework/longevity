package longevity.persistence.cassandra

import emblem.Emblematic
import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.sync.JsonToEmblemTranslator
import emblem.typeKey
import java.util.UUID
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.persistent.Persistent
import org.json4s.JsonAST.JString
import org.json4s.JsonAST.JValue

private[cassandra] class JsonToPersistentTranslator(
  override protected val emblematic: Emblematic)
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
