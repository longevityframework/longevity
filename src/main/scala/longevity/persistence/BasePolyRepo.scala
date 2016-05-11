package longevity.persistence

import emblem.TypeKey
import emblem.emblematic.Union
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

private[persistence] trait BasePolyRepo[P <: Persistent] extends BaseRepo[P] {

  private val union: Union[P] = subdomain.emblematic.unions(pTypeKey)

  override def create(p: P)(implicit context: ExecutionContext) = {
    def createDerived[D <: P : TypeKey] = repoPool[D].create(p.asInstanceOf[D])(context)
    implicit val derivedTypeKey: TypeKey[_ <: P] = union.typeKeyForInstance(p).getOrElse {
      throw new NotInSubdomainTranslationException(p.getClass.getSimpleName)
    }
    createDerived.map(_.widen[P])
  }

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] = {
    def updateDerived[D <: P : TypeKey] = repoPool[D].update(state.asInstanceOf[PState[D]])(context)
    implicit val derivedTypeKey: TypeKey[_ <: P] = union.typeKeyForInstance(state.get).getOrElse {
      throw new NotInSubdomainTranslationException(state.get.getClass.getSimpleName)
    }
    updateDerived.map(_.widen[P])
  }

}
