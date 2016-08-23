package longevity.persistence.inmem

import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.AnyRealizedKey

private[inmem] trait DerivedInMemRepo[P <: Persistent, Poly >: P <: Persistent] extends InMemRepo[P] {

  protected val polyRepo: InMemRepo[Poly]

  override protected[inmem] def nextId: Int = polyRepo.nextId

  override protected[inmem] val keys: Seq[AnyRealizedKey[_ >: P <: Persistent]] =
    polyRepo.keys ++ myKeys

  override protected[inmem] def assertNoWriteConflict(state: PState[P]) =
    polyRepo.assertNoWriteConflict(state.widen[Poly])

  override protected[inmem] def registerById(state: PState[P]): Unit =
    polyRepo.registerById(state.widen[Poly])

  override protected[inmem] def unregisterById(state: PState[P]): Unit =
    polyRepo.unregisterById(state.widen[Poly])

  override protected[inmem] def registerByKeyVal(keyVal: AnyKeyValAtAll, state: PState[P]): Unit =
    polyRepo.registerByKeyVal(keyVal, state.widen[Poly])

  override protected[inmem] def lookupPStateByKeyVal(keyVal: AnyKeyValAtAll): Option[PState[P]] =
    polyRepo.lookupPStateByKeyVal(keyVal).asInstanceOf[Option[PState[P]]]

  override protected[inmem] def unregisterByKeyVal(keyVal: AnyKeyValAtAll): Unit =
    polyRepo.unregisterByKeyVal(keyVal)

  override protected[inmem] def allPStates: Seq[PState[P]] = {
    def hasTypeP(state: PState[_ >: P]): Boolean = {
      state.get.getClass.getSimpleName == pTypeKey.name
    }
    polyRepo.allPStates.filter(hasTypeP).asInstanceOf[Seq[PState[P]]]
  }

}
