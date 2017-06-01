package longevity.unit.manual.model.model.ex2

package myPackage

import longevity.model.CType
import longevity.model.ModelEv
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.annotations.packscanToList

trait MyDomainModel

object MyDomainModel {

  private[myPackage] implicit object modelEv extends ModelEv[MyDomainModel]

  implicit object modelType extends ModelType[MyDomainModel](
    packscanToList[PType[MyDomainModel, _]],
    packscanToList[CType[_]])
}
