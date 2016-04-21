package emblem.testData



/** for type map happy cases */
object computerParts {
  sealed trait ComputerPart
  case class Memory(gb: Int) extends ComputerPart
  case class CPU(mhz: Double) extends ComputerPart
  case class Display(resolution: Int) extends ComputerPart
  case class Computer(memory: Memory, cpu: CPU, display: Display)
  type ComputerPartIdentity[P <: ComputerPart] = P
}
