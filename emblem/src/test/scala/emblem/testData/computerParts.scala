package emblem.testData

import emblem.HasEmblem

/** for type map happy cases */
object computerParts {
  sealed trait ComputerPart extends HasEmblem
  case class Memory(gb: Int) extends ComputerPart
  case class CPU(mhz: Double) extends ComputerPart
  case class Display(resolution: Int) extends ComputerPart
  case class Computer(memory: Memory, cpu: CPU, display: Display) extends HasEmblem
  type ComputerPartIdentity[P <: ComputerPart] = P
}
