package longevity.subdomain.ptype

import emblem.Emblematic
import longevity.subdomain.persistent.Persistent

// TODO: choose names for:
// - PropInit
// - registerProp
// - provideEmblematic
// - initializePropPath

private[ptype] class PropInit[P <: Persistent] {

  private var emblematicOpt: Option[Emblematic] = None
  private var uninitializedProps: Set[Prop[P, _]] = Set.empty

  def registerProp(prop: Prop[P, _]): Unit = this.synchronized {
    emblematicOpt match {
      case Some(emblematic) => prop.initializePropPath(emblematic)
      case None => uninitializedProps += prop
    }
  }

  def provideEmblematic(emblematic: Emblematic): Unit = this.synchronized {
    emblematicOpt = Some(emblematic)
    uninitializedProps.foreach(_.initializePropPath(emblematic))
  }

}
