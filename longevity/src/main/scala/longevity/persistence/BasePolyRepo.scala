package longevity.persistence

import typekey.TypeKey
import longevity.emblem.emblematic.Union
import longevity.exceptions.persistence.PStateChangesDerivedPTypeException
import longevity.exceptions.persistence.NotInDomainModelTranslationException

private[persistence] trait BasePolyRepo[F[_], M, P] extends PRepo[F, M, P] {

  private val union: Union[P] = modelType.emblematic.unions(pTypeKey)

  override def create(p: P) = {
    def createDerived[D <: P : TypeKey] = repo.pRepoMap[D].create(p.asInstanceOf[D])
    effect.map(createDerived(derivedTypeKey(p)))(_.widen[P])
  }

  override def update(state: PState[P]): F[PState[P]] = {
    if (union.typeKeyForInstance(state.orig) != union.typeKeyForInstance(state.get)) {
      throw new PStateChangesDerivedPTypeException(
        state.orig.getClass.getSimpleName,
        state.get.getClass.getSimpleName)
    }

    def updateDerived[D <: P : TypeKey] = repo.pRepoMap[D].update(state.asInstanceOf[PState[D]])
    effect.map(updateDerived(derivedTypeKey(state.get)))(_.widen[P])
  }

  override def delete(state: PState[P]): F[Deleted[P]] = {
    def deleteDerived[D <: P : TypeKey] = repo.pRepoMap[D].delete(state.asInstanceOf[PState[D]])
    effect.map(deleteDerived(derivedTypeKey(state.get)))(_.widen[P])
  }

  private def derivedTypeKey(p: P): TypeKey[_ <: P] = union.typeKeyForInstance(p).getOrElse {
    throw new NotInDomainModelTranslationException(p.getClass.getSimpleName)
  }

}
