---
title: using idea and longevity together
layout: page
---

[IntelliJ IDEA](https://www.jetbrains.com/idea/) can't expand the longevity macro annotations, and
consequently shows error messages for `primaryKey` and `props` in an example like this:

```scala
@persistent[DomainModel]
case class User(username: Username, email: Email, fullName: FullName)

object User {
  implicit val usernameKey = primaryKey(props.username)
}
```

For references to `User` in other source files, while implicit resolution of the generated `PEv`
instances (persistent evidence, as described in the section on
[repo.create](http://longevityframework.org/manual/repo/create.html)) and the
[keys](http://longevityframework.org/manual/ptype/keys.html) seems to work fine, we still get
compiler errors on things like `User.props` and `User.queryDsl`.

It looks like the right way to handle this is to use the [IntelliJ API to build scala macros
support](https://blog.jetbrains.com/scala/2015/10/14/intellij-api-to-build-scala-macros-support/).
We have an [open ticket on GitHub](https://github.com/longevityframework/longevity/issues/38) for
this issue. If you are interested in taking this on, that would be excellent! Just mention it on the
[Gitter channel](https://gitter.im/longevityframework/longevity), to make sure you are not
duplicating work.

It seems that JetBrains is wisely skipping full-blown support for Scala macros in favor of
supporting Scala.meta. See for example [this blog
entry](https://blog.jetbrains.com/scala/2016/11/11/intellij-idea-2016-3-rc-scala-js-scala-meta-and-more/).
We plan on migrating longevity from Scala macros to Scala meta as soon as possible. But the
Scala.meta feature set is not quite developed enough for our needs as of yet. We're tracking
progress on this front in [this GitHub
issue](https://github.com/longevityframework/longevity/issues/37).

It's worth noting that it is legal and equivalent to write this instead:

```scala
@persistent[DomainModel]
case class User(username: Username, email: Email, fullName: FullName)

object User extends longevity.model.PType[Domain, User] {
  implicit val usernameKey = primaryKey(props.username)
}
```

This will remove many of the false errors that IDEA reports. Errors about `User.props`, however,
will still be present.

{% assign prevTitle = "managing logging" %}
{% assign prevLink  = "logging.html" %}
{% assign upTitle   = "integration points" %}
{% assign upLink    = "." %}

{% include navigate.html %}

