package emblem.testData

/** for type map happy cases */
object pets {

  trait Pet
  type PetIdentity[P <: Pet] = P

  class Cat protected (val name: String) extends Pet
  object Cat {
    private case class SullCat(override val name: String) extends Cat(name)
    def apply(name: String): Cat = SullCat(name)
  }

  class Dog protected (val name: String) extends Pet
  object Dog {
    private case class SullDog(override val name: String) extends Dog(name)
    def apply(name: String): Dog = SullDog(name)
  }

  case class Hound(override val name: String) extends Dog(name)

  class Shepherd protected (name: String) extends Dog(name)
  object Shepherd {
    private case class SullShepherd(override val name: String) extends Shepherd(name)
    def apply(name: String): Shepherd = SullShepherd(name)
  }

  case class GermanShepherd(override val name: String) extends Shepherd(name)

  class PetStore[P <: Pet]
  class DogKennel[D <: Dog](val sponsor: PetStore[D])

  class PetBoxInvar[P <: Pet](var p: P)
  class PetBoxCovar[+P <: Pet](val p: P)
  class PetBoxContravar[-P <: Pet] {
    def p_=(p: P): Unit = {}
  }

}
