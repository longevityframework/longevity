---
title: retrieval by key value
layout: page
---

You can look up any persistent entities from the database using the
[keys](../ptype/keys.html) you defined in your `PType`. You just have
to get a `KeyVal` out of the `Key`, which you can do by supplying
values for each of the properties of the key, in turn. For example:

```scala
import longevity.subdomain.Assoc
import longevity.subdomain.ptype.RootType
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.KeyVal

object User extends RootType[User] {
  object props {
    val username = prop[String]("username")
  }
  object keys {
    val username = key(props.username)
  }
  object indexes {
  }
}

val usernameKey: Key[User] = User.keys.username
val username: String = "smithy"
val usernameKeyVal: KeyVal[User] = usernameKey(username)

object BlogPost extends RootType[BlogPost] {
  object props {
    val blog = prop[Assoc[Blog]]("blog")
    val suffix = prop[String]("uriPathSuffix")
  }
  object keys {
    val uri = key(props.blog, props.suffix)
  }
  object indexes {
  }
}

val blogAssoc: Assoc[Blog] = blogState.assoc
val uriPathSuffix: String = "suffix"
val blogPostKeyVal: KeyVal[BlogPost] = BlogPost.keys.uri(blogAssoc, uriPathSuffix)
```

`Key.apply` with throw a `KeyValException` if the types of the
supplied arguments do not match the types of the properties of the
key. Of course, we [would prefer this to be better
typed](https://www.pivotaltracker.com/story/show/109682804), so as to
fail here at compile time.

You can also pull a `KeyVal` out of an existing `Persistent` if you so
desire:

```scala
val user: User = parseUserFromJson(json)
val userKeyVal: KeyVal[User] = User.keys.username.keyValForP(user)
```

Once you have your `KeyVal`, you can look up the persistent in the
database. You get an `Option` back, as the `KeyVal` does not
necessarily match an existing entity.

```scala
val userRetrieveResult: Future[Option[PState[User]]] =
  userRepo.retrieve(userKeyVal)

val blogRetrieveResult: Future[Option[PState[Blog]]] =
  blogRepo.retreieve(blogKeyVal)
```

Once you get back your `PState`, you can of course use it to examine
the persistent itself with `PState.get`. You can modify it with
`PState.map`, and you can pass the state on to
[`Repo.update`](repo-update.html) or
[`Repo.delete`](repo-delete.html).

`Repo.retrieve` will always result in a database call. Longevity will
not cache versions for you and pull them from the cache. Not caching
provides a guarrantee that the retrieved aggregate is up to date with
the latest state of the database, and reduces the chances of a write
collision. We may revisit this in the future, but we do not consider
it an excessive burden on the longevity user to employ their own
cache, if need be.

{% assign prevTitle = "retrieval by assoc" %}
{% assign prevLink = "retrieve-assoc.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "retrieval by query" %}
{% assign nextLink = "query.html" %}
{% include navigate.html %}
