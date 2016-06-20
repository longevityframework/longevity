package longevity.persistence.cassandra

import emblem.emblematic.Emblematic
import emblem.TypeKey
import emblem.emblematic.traversors.sync.EmblematicToJsonTranslator
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.persistent.Persistent
import org.json4s.JsonAST.JValue
import org.json4s.JsonAST.JString

private[cassandra] class PersistentToJsonTranslator(
  override protected val emblematic: Emblematic)
extends EmblematicToJsonTranslator {

  override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

  def assocTraversor = new CustomTraversor[AssocAny] {
    def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: WrappedInput[B]): JValue = {
      if (!input.value.isPersisted) {
        throw new AssocIsUnpersistedException(input.value)
      }
      JString(input.value.asInstanceOf[CassandraId[_ <: Persistent]].uuid.toString)
    }
  }

}
