---
title: declaring a domain model
layout: page
---

The first step in building out our domain model is to define a trait that describes our model. This
trait never needs to be instantiated. It is used as a type parameter to identify the elements of our
model, and the tools that longevity provides for you, as being specific to this model. This is done
to provide a strong degree of type safety.

We annotate this domain model trait with the `longevity.model.annotations.domainModel` annotation:

```scala
package myPackage

import longevity.model.annotations.domainModel

@domainModel trait MyDomainModel
```

The details of what the `@domainModel` annotation should not be important to the casual user. We
present them here for the sake of completeness, for interested readers, and in case you end up
needing to troubleshoot a problem. If you do find yourself troubleshooting, be sure to give us a
shout on [Gitter](https://gitter.im/longevityframework/longevity) or the [user
group](https://groups.google.com/forum/#!forum/longevity-users). We are eager to help!

Like all the other model annotations in longevity, it is possible to write out the equivalent code
yourself. We don't recommend that you do this. It is a lot of boilerplate.

The `@domainModel` annotation creates or extends the companion object of your trait, adding in a
couple of implicit type classes, like so:

```scala
package myPackage

import longevity.model.CType
import longevity.model.KVType
import longevity.model.ModelEv
import longevity.model.ModelType
import longevity.model.PType
import longevity.model.annotations.packscanToList

trait MyDomainModel

object MyDomainModel {

  private[myPackage] implicit object modelEv extends ModelEv[MyDomainModel]

  implicit object modelType extends ModelType[MyDomainModel](
    packscanToList[PType[MyDomainModel, _]],
    packscanToList[CType[MyDomainModel, _]],
    packscanToList[KVType[MyDomainModel, _, _]])
}
```

The `ModelEv[MyDomainModel]` is evidence of the model. The compiler needs to be able to locate this
evidence when constructing a [persistent](persistents.html), [component](components.html), or a [key
value](key-values.html). All your persistent classes need to be found in the same package as, or a
subpackage of, the package that you define your domain model. Scoping the implicit model evidence
this way makes it findable by implicit resolution in exactly the right places.

The `ModelType[MyDomainModel]` collects information about all of your persistents, components, and
key values. This `ModelType` is used by the [longevity context](../context) to provide you with
tools specific to your model. It will be found automatically by implicit resolution when
constructing your context like so: `longevity.context.LongevityContext[MyDomainModel]`.

`packscanToList` is a def macro that scans the current package, and all sub-packages, for objects
that match the provided types. For instance, if you had declared persistent classes `User`, `Blog`,
and `BlogPost`, a component `UserProfile`, and key values `Username`, `BlogUri`, and `BlogPostUri`,
then the above code would expand as follows:

```scala
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
```

{% assign prevTitle = "the domain model" %}
{% assign prevLink  = "." %}
{% assign upTitle   = "the domain model" %}
{% assign upLink    = "." %}
{% assign nextTitle = "persistent objects" %}
{% assign nextLink  = "persistents.html" %}
{% include navigate.html %}
