package longevity.persistence.inmem

import longevity.persistence.DatabaseId
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.AnyRealizedKey

private[inmem] trait DerivedInMemRepo[P <: Persistent, Poly >: P <: Persistent] extends InMemRepo[P] {

  protected val polyRepo: InMemRepo[Poly]

  override protected[inmem] def nextId: Int = polyRepo.nextId

  override protected[inmem] val keys: Seq[AnyRealizedKey[_ >: P <: Persistent]] =
    polyRepo.keys ++ myKeys

  override protected[inmem] def registerPStateById(id: DatabaseId[_ <: Persistent], state: PState[P]): Unit =
    polyRepo.registerPStateById(id, state.widen[Poly])

  override protected[inmem] def unregisterPStateById(id: DatabaseId[_ <: Persistent]): Unit =
    polyRepo.unregisterPStateById(id)

  override protected[inmem] def registerPStateByKeyVal(keyVal: Any, state: PState[P]): Unit =
    polyRepo.registerPStateByKeyVal(keyVal, state.widen[Poly])

  override protected[inmem] def lookupPStateByKeyVal(keyVal: Any): Option[PState[P]] =
    polyRepo.lookupPStateByKeyVal(keyVal).asInstanceOf[Option[PState[P]]]

  override protected[inmem] def unregisterKeyVal(keyVal: Any): Unit =
    polyRepo.unregisterKeyVal(keyVal)

  override protected[inmem] def allPStates: Seq[PState[P]] = {
    def hasTypeP(state: PState[_ >: P]): Boolean = {
      state.get.getClass.getSimpleName == pTypeKey.name
    }
    polyRepo.allPStates.filter(hasTypeP).asInstanceOf[Seq[PState[P]]]
  }

}
