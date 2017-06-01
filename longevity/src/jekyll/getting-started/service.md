---
title: the user service
layout: page
---

Before getting into the implementation of these service methods, let's
take a look at the user service API, found in `UserService.scala`. We
find eight service methods here that mirror the eight [user
routes](routes.html). Here is a shortened version with the three
service methods we will look at in this tutorial:

```scala
package simbl.service

import scala.concurrent.Future
import simbl.api.UserInfo

/** service methods to back the [[UserRoute user routes]] */
trait UserService {

  /** creates and persists a new [[User]] from the [[UserInfo]], returning a
   * `UserInfo` reflecting the persisted `User`.
   *
   * @throws DuplicateUsernameException if a user with the supplied username
   * already exists
   * 
   * @throws DuplicateEmailException if a user with the supplied email already
   * exists
   */
  def createUser(info: UserInfo): Future[UserInfo]

  /** retrieves a [[User]] by username, returning a [[UserInfo]] reflecting the
   * persisted `User`. returns `None` if no such user by that username.
   */
  def retrieveUser(username: String): Future[Option[UserInfo]]

  /** updates a [[User]] by username, returning a [[UserInfo]] reflecting the
   * persisted `User`. returns `None` if no such user by that username.
   *
   * @throws DuplicateUsernameException if a user with the supplied username
   * already exists
   * 
   * @throws DuplicateEmailException if a user with the supplied email already
   * exists
   */
  def updateUser(username: String, info: UserInfo): Future[Option[UserInfo]]

}
```

The most important thing to note here is that each of the service
methods is defined in terms of API classes `UserInfo`, and _not_ in
terms of the domain entities themselves. This is probably not
necessary for such a simple application as this, but it's a good
practice, because the UI typically speaks in a slightly different
language than the domain model. As a simple example, some user
information, such as email or street address, should largely be
considered private, and should be left out of most UI views.

As you can see, `UserInfo` is a simple case class that should convert
in and out of JSON cleanly. It also contains a couple of methods for
conversions between the API objects and the domain objects:

```scala
package simbl.api

import simbl.domain.Email
import simbl.domain.User
import simbl.domain.Username

case class UserInfo(
  username: String,
  email: String,
  fullname: String) {

  def toUser = User(Username(username), Email(email), fullname, None)

  /** updates a [[User]] according to the information in this [[UserInfo]] */
  def mapUser(user: User) = user.copy(
    username = Username(username),
    email = Email(email),
    fullname = fullname)

}

object UserInfo {

  def apply(user: User): UserInfo =
    UserInfo(user.username.username, user.email.email, user.fullname)

}
```

{% assign prevTitle = "the akka http routes" %}
{% assign prevLink = "routes.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="user service implementation" %}
{% assign nextLink="service-impl.html" %}
{% include navigate.html %}
