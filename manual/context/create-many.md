---
title: creating many aggregates at once
layout: page
---

When creating networks of aggregates together, you can use
`Assoc.apply` to build associations between them:

{% gist sullivan-/c2d126c9d2a83e76cfbc %}

We call these kinds of associations "unpersisted". If you try to use
`Repo.create` to persist an aggregate with unpersisted assoc, the
operation will fail:

    // fails with an AssocIsUnpersistedException:
    blogRepo.create(blog)

Instead, we can use `RepoPool.createMany` to create all our aggregates
at once:

    val createManyResult: Future[Seq[PState[_ <: Root]]] =
      repoPool.createMany(john, frank, blog, johnsPost, franksPost)

All the unpersisted associations will be recursively persisted. It
doesn't matter what order you pass them in; longevity will assure that
associated aggregates always get persisted first.

Be careful, something like this is likely to fail with a duplicate key
error:

    userRepo.create(john)
    repoPool.createMany(frank, blog, johnsPost, franksPost)

Here, `createMany` with find an unpersisted assoc to `john` from both
`blog` and `johnsPost`, and will attempt to persist `john` a second
time. It always treats unpersisted assocs as aggregates to create. But
feel free to mix in persisted assocs as well. For example, if for
some odd reason we simply must create `john` first, we might do it
like this:

{% gist sullivan-/4560f563eaffd1259108 %}

{% assign prevTitle = "repo.create" %}
{% assign prevLink = "repo-create.html" %}
{% assign upTitle = "the longevity context" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.retrieve" %}
{% assign nextLink = "repo-retrieve.html" %}
{% include navigate.html %}
