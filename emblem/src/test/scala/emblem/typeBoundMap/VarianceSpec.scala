package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** tests [[TypeBoundMap]] operations in the face of covariant and contravariant types */
class VarianceSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeBoundMaps in the face of someone trying to break through my bulletpoof type signatures"

  it should "thwart the attack with a compile time error of unspecified opacity" in {
    import emblem.testData.pets._
    val dog = Dog("dog")
    val anotherDog = Dog("anotherDog")
    val hound = Hound("hound")
    var petToPetMap = TypeBoundMap[Pet, PetIdentity, PetIdentity]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetMap += dog -> anotherDog
    petToPetMap(dog) should equal (anotherDog)

    // you are not allowed to map a hound to a dog. otherwise you
    // could then call "petToPetMap(hound)" and get a
    // ClassCastException.
    "petToPetMap += hound -> dog" shouldNot compile

    // you are allowed to map a dog to a hound. in this case
    // petToPetMap(dog) will return something of type PetIdentity[Dog]
    // (which =:= Dog), and the something returned is hound. nothing
    // weird, no ClassCastException.
    petToPetMap = TypeBoundMap[Pet, PetIdentity, PetIdentity]()
    petToPetMap += dog -> hound
    petToPetMap(dog) should equal (hound)

    var petToPetBoxInvarMap = TypeBoundMap[Pet, PetIdentity, PetBoxInvar]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetBoxInvarMap += dog -> new PetBoxInvar(dog)
    petToPetBoxInvarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxInvar[Dog].
    // otherwise you could then call "petToPetBoxInvarMap(hound)" and
    // get a ClassCastException.
    "petToPetBoxInvarMap += hound -> new PetBoxInvar(dog)" shouldNot compile

    // you are not allowed to map a dog to a PetBoxInvar[Hound].
    // otherwise you could then call "petToPetBoxInvarMap(dog)" and
    // get a ClassCastException.
    "petToPetBoxInvarMap += dog -> new PetBoxInvar(hound)" shouldNot compile

    var petToPetBoxCovarMap = TypeBoundMap[Pet, PetIdentity, PetBoxCovar]()

    // standard usage is to map dogs to dogs and hounds to hounds
    petToPetBoxCovarMap += dog -> new PetBoxCovar(dog)
    petToPetBoxCovarMap[Pet](dog).p should equal (dog)

    // you are not allowed to map a hound to a PetBoxCovar[Dog].
    // otherwise you could then call "petToPetBoxCovarMap(hound)" and
    // get a ClassCastException.
    "petToPetBoxCovarMap += hound -> new PetBoxCovar(dog)" shouldNot compile

    // you are allowed to map a dog to a PetBoxCovar[Hound]. in this
    // case petToPetMap(dog) return something of type
    // PetBoxCovar[Dog], which is >:> PetBoxCovar[Hound]
    petToPetBoxCovarMap = TypeBoundMap[Pet, PetIdentity, PetBoxCovar]()
    petToPetBoxCovarMap += dog -> new PetBoxCovar(hound)
    petToPetBoxCovarMap(dog).p should equal (hound)

    val dogContravar = new PetBoxContravar
    val houndContravar = new PetBoxContravar
    var petToPetBoxContravarMap = TypeBoundMap[Pet, PetIdentity, PetBoxContravar]()

    "petToPetBoxContravarMap += dog -> dogContravar" should compile

    petToPetBoxContravarMap += dog -> dogContravar
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(dog)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(dog)" should compile
    petToPetBoxContravarMap(dog) should be theSameInstanceAs dogContravar

    // you are allowed to map a hound to a PetBoxContravar[Dog].  in
    // this case petToPetMap(hound) will return something of type
    // PetBoxContravar[Hound], which is >:> PetBoxContravar[Dog]
    petToPetBoxContravarMap = TypeBoundMap[Pet, PetIdentity, PetBoxContravar]()
    petToPetBoxContravarMap += hound -> dogContravar
    "val contravar: PetBoxContravar[Dog] = petToPetBoxContravarMap(hound)" should compile
    "val contravar: PetBoxContravar[Hound] = petToPetBoxContravarMap(hound)" should compile
    petToPetBoxContravarMap(hound) should be theSameInstanceAs dogContravar

    // you are not allowed to map a dog to a PetBoxContravar[Hound].
    // otherwise you could then call "petToPetBoxContravarMap(dog)"
    // and get a ClassCastException.
    "petToPetBoxContravarMap += dog -> new PetBoxContravar[Hound]" shouldNot compile    
  }
 
}
