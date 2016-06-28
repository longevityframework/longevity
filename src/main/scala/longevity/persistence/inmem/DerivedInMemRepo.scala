package longevity.persistence.inmem

import longevity.persistence.PersistedAssoc
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.RealizedKey

private[inmem] trait DerivedInMemRepo[P <: Persistent, Poly >: P <: Persistent] extends InMemRepo[P] {

  protected val polyRepo: InMemRepo[Poly]

  override protected[inmem] def nextId: Int = polyRepo.nextId

  override protected[inmem] val keys: Seq[RealizedKey[_ >: P <: Persistent, _ <: KeyVal[_ >: P <: Persistent]]] =
    polyRepo.keys ++ myKeys

  override protected[inmem] def registerPStateByAssoc(
    assoc: PersistedAssoc[_ <: Persistent],
    state: PState[P])
  : Unit =
    polyRepo.registerPStateByAssoc(assoc, state.widen[Poly])

  override protected[inmem] def unregisterPStateByAssoc(assoc: PersistedAssoc[_ <: Persistent]): Unit =
    polyRepo.unregisterPStateByAssoc(assoc)

  override protected[inmem] def registerPStateByKeyVal(
    keyVal: KeyVal[_ <: Persistent],
    state: PState[P])
  : Unit =
    polyRepo.registerPStateByKeyVal(keyVal, state.widen[Poly])

  override protected[inmem] def lookupPStateByKeyVal(
    keyVal: KeyVal[_ <: Persistent])
  : Option[PState[P]] =
    polyRepo.lookupPStateByKeyVal(keyVal).asInstanceOf[Option[PState[P]]]

  override protected[inmem] def unregisterKeyVal(keyVal: KeyVal[_ <: Persistent]): Unit =
    polyRepo.unregisterKeyVal(keyVal)

  override protected[inmem] def allPStates: Seq[PState[P]] = {
    def hasTypeP(state: PState[_ >: P]): Boolean = {
      state.get.getClass.getSimpleName == pType.pTypeKey.name
    }
    polyRepo.allPStates.filter(hasTypeP).asInstanceOf[Seq[PState[P]]]
  }

}
