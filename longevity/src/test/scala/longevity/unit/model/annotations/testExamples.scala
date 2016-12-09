package longevity.unit.model.annotations.testExamples

import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.persistent
import longevity.model.annotations.polyPersistent

// normally i would put these test examples directly in the test. or, if they
// needed to be stable, i would put them in the companion object for the test.
// unfortunately, the macro mechanism seems to choke for all my @persistent
// macros when they are used within a companion object. they are only working
// top-level for me. problem is, the annotation is on the definition of the type
// that i need to reflect on to generate the mprops. the mprops macro can pull
// up the type just fine when things are declared top level, but not within a
// stable object.

@persistent(keySet = emptyKeySet)
class PNoCompanion()

@persistent(keySet = emptyKeySet)
class PWithCompanion

object PWithCompanion { val y = 7 }

@persistent(keySet = emptyKeySet)
case class PCaseClass()

@persistent(keySet = emptyKeySet)
case class PCaseClassWithDefaults(x: Int = 7)

@polyPersistent(keySet = emptyKeySet)
trait PolyPNoCompanion

@polyPersistent(keySet = emptyKeySet)
trait PolyPWithCompanion

object PolyPWithCompanion { val y = 7 }

@polyPersistent(keySet = emptyKeySet)
trait Poly

@derivedPersistent[Poly](keySet = emptyKeySet)
class DerivedPNoCompanion extends Poly

@derivedPersistent[Poly](keySet = emptyKeySet)
class DerivedPWithCompanion extends Poly

object DerivedPWithCompanion { val y = 7 }

@derivedPersistent[Poly](keySet = emptyKeySet)
case class DerivedPCaseClass() extends Poly

object DerivedPCaseClass { val y = 7 }
