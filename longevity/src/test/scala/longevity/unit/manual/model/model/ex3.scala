package longevity.unit.manual.model.model.ex3

package myPackage

import longevity.model.ModelEv
import longevity.model.ModelType

trait MyDomainModel

object MyDomainModel {

  private[myPackage] implicit object modelEv extends ModelEv[MyDomainModel]

  implicit object modelType extends ModelType[MyDomainModel](
    List(User, Blog, BlogPost),
    List(UserProfile),
    List(Username, BlogUri, BlogPostUri))
}
