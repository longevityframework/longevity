package longevity.unit.manual.model.model.ex3

package myPackage

import longevity.model.annotations.component
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@persistent[MyDomainModel] case class User()
@persistent[MyDomainModel] case class Blog()
@persistent[MyDomainModel] case class BlogPost()
@component[MyDomainModel] case class UserProfile()
@keyVal[MyDomainModel, User] case class Username()
@keyVal[MyDomainModel, Blog] case class BlogUri()
@keyVal[MyDomainModel, BlogUri] case class BlogPostUri()
