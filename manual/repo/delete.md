---
title: repo.delete
layout: page
---

Use `Repo.delete` to remove an aggregate from the database:

    val userState: PState[User] = getUserState()
    val deleteResult: Future[Deleted[User]] = userRepo.delete(userState)

The delete is complete when the future completes successfully.
For now, this is a hard delete, but see [PT
#106710604](https://www.pivotaltracker.com/story/show/106710604) for
support for soft deletes.

You cannot do much with the `Deleted`, but you can have at the
aggregate or the `Assoc` for old times sake:

    deleteResult map { deleted =>
      val deletedAggregate: User = deleted.root
      val deletedAssoc: Assoc[User] = deleted.assoc
    }

Of course, neither of these values will be particularly useful, since
the aggregate no longer exists.

<div class = "blue-side-bar">

We would like to support in-database deletes in the future, possibly
with something like <code>Repo.deleteByQuery</code>.

</div>

{% assign prevTitle = "repo.update" %}
{% assign prevLink = "repo-update.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "testing your subdomain" %}
{% assign nextLink = "../testing" %}
{% include navigate.html %}
