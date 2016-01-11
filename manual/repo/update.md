---
title: repo.update
layout: page
---

placeholder

- example
- may not call the database if the state is clean
  - only Repo method that does not guarrantee a database call
- we can continue to manipulate the result and pass to update or
  delete
- it is possible to update a keyval. so it is possible to create a dup
  key. see bottom or repo-create.html
- the pstate result may be different from the input, as the
  aggregate's revision counter may have been updated.
  - re-using (or continuing to use) the input pstate could result in an
    optimistic locking failure
  - link to the appropriate issue

{% assign prevTitle = "retrieval by query" %}
{% assign prevLink = "repo-query.html" %}
{% assign upTitle = "repositories" %}
{% assign upLink = "." %}
{% assign nextTitle = "repo.delete" %}
{% assign nextLink = "delete.html" %}
{% include navigate.html %}
