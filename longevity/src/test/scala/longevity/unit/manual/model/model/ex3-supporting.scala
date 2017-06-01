package longevity.unit.manual.model.model.ex3

package myPackage

import longevity.model.annotations.component
import longevity.model.annotations.persistent

@persistent[MyDomainModel](keySet = emptyKeySet) case class User()
@persistent[MyDomainModel](keySet = emptyKeySet) case class Blog()
@persistent[MyDomainModel](keySet = emptyKeySet) case class BlogPost()
@component case class UserProfile()
