---
title: enforcing constraints
layout: page
---

The test data generator described in the [previous section](test-data.html) will handle most any
persistent types you put in your domain. One thing we cannot handle out of the box is when
exceptions are thrown in the constructors of your [persistent](../model/persistents.html) and
[component](../model/components.html) objects.

A class constructor is a great place to enforce domain constraints, such as requiring that an email
has an at sign (`'@'`):

```scala
import longevity.model.annotations.component

@component[DomainModel]
case class Email(email: String) {
  if (!email.contains('@'))
    throw new ConstraintValidationException("no '@' in email")
}
```

The test data generator uses [ScalaCheck](https://www.scalacheck.org/)'s `Arbitrary` and `Gen`,
[scalacheck-shapeless](https://github.com/alexarchambault/scalacheck-shapeless) to generate your
test data. An `Arbitrary` is produced for you when the `@persistent`, `@polyPersistent`, or
`@derivedPersistent` macro annotations are expanded. To override all or parts of the provided
`Arbitrary`, you need to provide your own implicit `Arbitrary` in the same package as your
persistent or component. For example, we can provide the following implicits in your `package.scala`
for constructing `Emails` that match our constraint:

```scala
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary

def genEmail = for {
  lhs <- arbitrary[String]
  rhs <- arbitrary[String]
} yield Email(s"$lhs@$rhs.com")

implicit val arbitraryEmail = Arbitrary(genEmail)
```

Extending this example, we have a persistent object that has both a primary email, and a set of all
emails. Our constraint is that the primary email occurs in the set:

```scala
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class ComplexConstraint(
  id: ComplexConstraintId,
  primaryEmail: Email,
  emails: Set[Email]) {

  if (!emails.contains(primaryEmail))
    throw new ConstraintValidationException("primary email is not in emails")
}
```

In the corresponding `package.scala`, we provide an `Arbitrary[ComplexConstraint]` that produces a
set of emails that includes the primary email:

```scala
import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary

def genComplexConstraint = for {
  id <- arbitrary[ComplexConstraintId]
  primary <- arbitrary[Email]
  secondaries <- arbitrary[Set[Email]]
} yield ComplexConstraint(id, primary, secondaries + primary)

implicit val arbitraryComplexConstraint = Arbitrary(genComplexConstraint)
```

{% assign prevTitle = "generating test data" %}
{% assign prevLink = "test-data.html" %}
{% assign upTitle = "testing your domain model" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo crud spec" %}
{% assign nextLink = "repo-crud-spec.html" %}
{% include navigate.html %}

