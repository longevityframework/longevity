package longevity.persistence.inmem

import longevity.persistence.PersistedAssoc
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.Query

private[inmem] trait DerivedInMemRepo[P <: Persistent, Poly >: P <: Persistent] extends InMemRepo[P] {

  protected val polyRepo: InMemRepo[Poly]

  override protected[inmem] def nextId: Int = polyRepo.nextId

  override protected[inmem] val keys: Seq[Key[_ >: P <: Persistent]] =
    polyRepo.keys ++ pType.keySet.toSeq

  override protected[inmem] def registerEntityById(
    assoc: PersistedAssoc[_ <: Persistent],
    state: PState[P])
  : Unit =
    polyRepo.registerEntityById(assoc, state.widen[Poly])

  override protected[inmem] def registerEntityByKeyVal(
    keyVal: KeyVal[_ <: Persistent],
    state: PState[P])
  : Unit =
    polyRepo.registerEntityByKeyVal(keyVal, state.widen[Poly])

  override protected[inmem] def lookupEntityById(
    assoc: PersistedAssoc[_ <: Persistent])
  : Option[PState[P]] =
    polyRepo.lookupEntityById(assoc).asInstanceOf[Option[PState[P]]]

  override protected[inmem] def lookupEntityByKeyVal(
    keyVal: KeyVal[_ <: Persistent])
  : Option[PState[P]] =
    polyRepo.lookupEntityByKeyVal(keyVal).asInstanceOf[Option[PState[P]]]

  override protected[inmem] def allPStates: Seq[PState[P]] = {
    def hasTypeP(state: PState[_ >: P]): Boolean = {
      state.get.getClass.getSimpleName == pType.pTypeKey.name
    }
    polyRepo.allPStates.filter(hasTypeP).asInstanceOf[Seq[PState[P]]]
  }

  override protected[inmem] def unregisterEntityById(assoc: PersistedAssoc[_ <: Persistent]): Unit =
    polyRepo.unregisterEntityById(assoc)

}
