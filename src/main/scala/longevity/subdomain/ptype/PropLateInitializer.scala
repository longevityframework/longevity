package longevity.subdomain.ptype

import emblem.Emblematic
import longevity.subdomain.persistent.Persistent

/** coordinates between `Subdomain`, `PType` and `Prop` to initialize the prop
 * path during subdomain construction.
 * 
 * unfortunately, at present, the props cannot be fully initialized until during
 * subdomain construction. this is due to the fact that in order to construct
 * the prop path, we need to know about all the other entities, including any
 * polymorphic relationships between them. to change this behavior would require
 * a method to generate `EmblematicPropPaths` without an `Emblematic`. a real
 * fix would involve separating out `EntityType` into two parts: the first part
 * to establish the types to be used, and the relationships between them; the
 * second part to declare the props and the indexes and keys.
 */
private[ptype] class PropLateInitializer[P <: Persistent] {

  private var emblematicOpt: Option[Emblematic] = None
  private var uninitializedProps: Set[Prop[P, _]] = Set.empty

  /** assures that the prop will be initialized once the `Emblematic` has been
   * provided. if the emblematic has already been provided, then the prop is
   * initialized immediately.
   */
  def registerProp(prop: Prop[P, _]): Unit = this.synchronized {
    emblematicOpt match {
      case Some(emblematic) => prop.initializePropPath(emblematic)
      case None => uninitializedProps += prop
    }
  }

  /** uses the provided emblematic to initialize all the props that have already
   * been registered, as well as all the props that have not yet been
   * registered. throws exception if called more than once.
   */
  def registerEmblematic(emblematic: Emblematic): Unit = this.synchronized {
    assert(emblematicOpt.isEmpty)
    emblematicOpt = Some(emblematic)
    uninitializedProps.foreach(_.initializePropPath(emblematic))
    uninitializedProps = Set.empty
  }

}
