package longevity.persistence.cassandra

import emblem.emblematic.Emblematic
import emblem.TypeKey
import emblem.exceptions.CouldNotTraverseException
import emblem.emblematic.traversors.sync.JsonToEmblematicTranslator
import emblem.typeKey
import java.util.UUID
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.persistent.Persistent
import org.json4s.JsonAST.JString

private[cassandra] class JsonToPersistentTranslator(
  override protected val emblematic: Emblematic)
extends JsonToEmblematicTranslator {

  override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

  def assocTraversor = new CustomTraversor[AssocAny] {
    def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: WrappedInput): B = {
      def assocFromString(s: String) = {
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: Persistent]]
        CassandraId(UUID.fromString(s))
      }
      input.value match {
        case JString(s) => assocFromString(s).asInstanceOf[B]
        case _ => throw new CouldNotTraverseException(typeKey[B])
      }
    }
  }

}
