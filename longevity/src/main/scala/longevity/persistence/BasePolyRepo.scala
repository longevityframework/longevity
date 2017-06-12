package longevity.persistence

import typekey.TypeKey
import longevity.emblem.emblematic.Union
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

private[persistence] trait BasePolyRepo[M, P] extends PRepo[M, P] {

  private val union: Union[P] = modelType.emblematic.unions(pTypeKey)

  override def create(p: P)(implicit context: ExecutionContext) = {
    def createDerived[D <: P : TypeKey] = repo.pRepoMap[D].create(p.asInstanceOf[D])(context)
    createDerived(derivedTypeKey(p)).map(_.widen[P])
  }

  override def update(state: PState[P])(implicit context: ExecutionContext): Future[PState[P]] = {
    if (union.typeKeyForInstance(state.orig) != union.typeKeyForInstance(state.get)) {
      throw new PStateChangesDerivedPTypeException(
        state.orig.getClass.getSimpleName,
        state.get.getClass.getSimpleName)
    }

    def updateDerived[D <: P : TypeKey] = repo.pRepoMap[D].update(state.asInstanceOf[PState[D]])
    updateDerived(derivedTypeKey(state.get)).map(_.widen[P])
  }

  override def delete(state: PState[P])(implicit context: ExecutionContext): Future[Deleted[P]] = {
    def deleteDerived[D <: P : TypeKey] = repo.pRepoMap[D].delete(state.asInstanceOf[PState[D]])
    deleteDerived(derivedTypeKey(state.get)).map(_.widen[P])
  }

  private def derivedTypeKey(p: P): TypeKey[_ <: P] = union.typeKeyForInstance(p).getOrElse {
    throw new NotInDomainModelTranslationException(p.getClass.getSimpleName)
  }

}
