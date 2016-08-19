---
title: what is longevity?
layout: page
---

At it's heart, longevity is a persistence framework. Our aim is to do
all the persistence work for you, and to present persistence via a
clean API that helps you isolate persistence concerns from other parts
of your application. This is a big win for you, as building and
maintaining a persistence layer is typically a major effort when
writing database applications.

To accomplish this, we ask you to build your _domain objects_ - those
data that you want to persist - according to certain rules. We hope
you agree that these rules are not overly restrictive. And we ask you
to tell longevity about your domain objects by constructing a
longevity [subdomain](subdomain.html). The first half of this user
manual, more or less, describes how to build your `Subdomain`.

Once you build your `Subdomain`, you pass it back to longevity to get
your `LongevityContext`. The main thing we provide to you with the
[longevity context](context) is a [pool](context/repo-pools.html) of
[repositories](repo) for you to use. These repositories present with a
complete persistence API, including CRUD operations
(create/retrieve/update/delete), and queries that return multiple
results.

The longevity context provides you with other tools that we generally
consider part of the persistence layer. We provide you with a set of
repositories that persist to a test database, as well as fully
functional in-memory repositories, that you can use for testing. We
provide you with integration tests that will exercise all of the
repository CRUD operations. And we provide you with a framework for
testing any queries that are important to your application.

We borrow "subdomain" and "context" terminology from [Domain Driven
Design](http://dddcommunity.org/), and we support DDD concepts and
terminology in many other places. But it's important to note that
longevity is primarily a persistence framework, and you don't have to
be doing DDD to use it. The key principle that we take from DDD is
that our domain classes should express our domain model as closely as
possible, and should be isolated from application-level
concerns. Whether or not you are doing DDD, we think you will find
that this kind of separation of concerns will help you build solid,
long-lasting applications.

{% assign prevTitle = "user manual" %}
{% assign prevLink = "./" %}
{% assign upTitle = "user manual" %}
{% assign upLink = "./" %}
{% assign nextTitle = "project setup" %}
{% assign nextLink = "project-setup.html" %}
{% include navigate.html %}
