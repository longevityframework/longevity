---
title: using idea and longevity together
layout: page
---

If you are using [IntelliJ IDEA](https://www.jetbrains.com/idea/) with your longevity project, you will
want to install the [longevity IDEA
plugin](https://github.com/longevityframework/longevity-idea-plugin). The plugin is available from
the [JetBrains Plugins Repository](https://plugins.jetbrains.com/plugin/9896-longevity), so you can
install it just like any other IDEA plugin. Just search for "longevity" under Plugins in the 
[Settings / Preferences Dialog](https://www.jetbrains.com/help/idea/settings-preferences-dialog.html).

Without the plugin, IDEA doesn't know how to expand the longevity macro annotations, and
consequently shows error messages for `primaryKey` and `props` in an example like this:

```scala
@persistent[DomainModel]
case class User(username: Username, email: Email, fullName: FullName)

object User {
  implicit val usernameKey = primaryKey(props.username)
}
```

The plugin helps IDEA understand what the annotation macro is doing, and makes error messages like
this go away.

It seems that JetBrains is wisely skipping full-blown support for Scala macros in favor of
supporting Scala.meta. See for example [this blog
entry](https://blog.jetbrains.com/scala/2016/11/11/intellij-idea-2016-3-rc-scala-js-scala-meta-and-more/).
We plan on migrating longevity from Scala macros to Scala meta as soon as possible. But the
Scala.meta feature set is not quite developed enough for our needs as of yet. We're tracking
progress on this front in [this GitHub
issue](https://github.com/longevityframework/longevity/issues/37).

{% assign prevTitle = "managing logging" %}
{% assign prevLink  = "logging.html" %}
{% assign upTitle   = "integration points" %}
{% assign upLink    = "." %}

{% include navigate.html %}

