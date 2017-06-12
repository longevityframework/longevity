package longevity.emblem.testData

import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.EmblemPool
import longevity.emblem.emblematic.Emblematic

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
