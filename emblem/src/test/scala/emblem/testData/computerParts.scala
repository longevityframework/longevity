package emblem.testData

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemPool
import emblem.emblematic.Emblematic

/** for type map happy cases */
object computerParts {

  sealed trait ComputerPart
  case class Memory(gb: Int) extends ComputerPart
  case class CPU(mhz: Double) extends ComputerPart
  case class Display(resolution: Int) extends ComputerPart

  case class Computer(memory: Memory, cpu: CPU, display: Display)
  type ComputerPartIdentity[P <: ComputerPart] = P

  val emblematic = Emblematic(emblems = EmblemPool(
    Emblem[Memory],
    Emblem[CPU],
    Emblem[Display],
    Emblem[Computer]))
  
}
