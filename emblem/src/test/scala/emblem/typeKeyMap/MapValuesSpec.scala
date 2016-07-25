package emblem.typeKeyMap

import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundFunction
import emblem.typeBound.WideningTypeBoundFunction
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.mapValues]] and [[TypeKeyMap.mapValuesWiden]] */
class MapValuesSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.mapValues"

  it should "return the TypeKeyMap obtained by applying the TypeBoundFunction to each of the values" in {
    import emblem.testData.computerParts._
    val toHeadOption = new TypeBoundFunction[ComputerPart, List, Option] {
      def apply[P <: ComputerPart](parts: List[P]): Option[P] = parts.headOption
    }
    var partLists = TypeKeyMap[ComputerPart, List]()

    // make sure the the method returns the right type
    val partOptions: TypeKeyMap[ComputerPart, Option] = partLists.mapValues[Option](toHeadOption)

    partLists.mapValues[Option](toHeadOption) should be (TypeKeyMap[ComputerPart, Option]())
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil

    partLists += memoryList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption)

    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    partLists += cpuList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption + cpuList.headOption)

    val displayList = Display(720) :: Display(1080) :: Nil
    partLists += displayList
    partLists.mapValues[Option](toHeadOption) should be (
      TypeKeyMap[ComputerPart, Option]() + memoryList.headOption + cpuList.headOption + displayList.headOption)
  }  

  // a good example of the utility of mapValuesWiden is in longevity.subdomain.Subdomain
  behavior of "TypeKeyMap.mapValuesWiden"

  it should "return a TypeKeyMap with a wider type bound than the original" in {
    import emblem.testData.pets._
    val toSponsor = new WideningTypeBoundFunction[Dog, Pet, DogKennel, PetStore] {
      def apply[D <: Dog](kennel: DogKennel[D]): PetStore[D] = kennel.sponsor
    }

    var kennels = TypeKeyMap[Dog, DogKennel]

    // make sure the the method returns the right type
    val stores: TypeKeyMap[Pet, PetStore] = kennels.mapValuesWiden[Pet, PetStore](toSponsor)

    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (TypeKeyMap[Pet, PetStore])

    val houndKennel = new DogKennel[Hound](new PetStore[Hound])
    kennels += houndKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor)

    val shepherdKennel = new DogKennel[Shepherd](new PetStore[Shepherd])
    kennels += shepherdKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor + shepherdKennel.sponsor)

    val germanShepherdKennel = new DogKennel[GermanShepherd](new PetStore[GermanShepherd])
    kennels += germanShepherdKennel
    kennels.mapValuesWiden[Pet, PetStore](toSponsor) should be (
      TypeKeyMap[Pet, PetStore] + houndKennel.sponsor + shepherdKennel.sponsor + germanShepherdKennel.sponsor)
  }  
 
}
