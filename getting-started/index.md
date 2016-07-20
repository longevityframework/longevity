---
title: getting started with longevity
layout: page
---

This guide walks through the basic steps needed to get started
building a real-life application with
[longevity](http://longevityframework.github.io/longevity/). The
application we will be looking at here is a sample blogging
application, built with longevity on the back end, and using [Akka
HTTP]("http://doc.akka.io/docs/akka/2.4.7/scala/http/") for a REST API
that could be used by a web client. You can find the source code here:

[https://github.com/longevityframework/simbl](https://github.com/longevityframework/simbl)

The simbl project (short for "Simple Blogging") also doubles as a
[Lightbend Activator tutorial](../activator.html). The tutorial and
this guide cover roughly the same material. We created this guide for
people who couldn't be bothered to install and learn [Lightbend
Activator](https://www.lightbend.com/community/core-tools/activator-and-sbt).

We will only have the chance to cover a portion of the blogging
application code, so please feel free to explore the codebase further
on your own. You can also look to the [user manual](../manual) for
more information.

Here's a table of contents for the getting started guide:

- [Modelling our Subdomain](modelling.html)
- [Building the User Aggregate](user.html)
- [The User Profile](user-profile.html)
- [Username and Email](keyvals.html)
- [Building the Subdomain](building.html)
- [Building the Longevity Context](context.html)
- [The Akka HTTP Routes](routes.html)
- [The User Service](service.html)
- [User Service Implementation](service-impl.html)
- [UserServiceImpl.createUser](create-user.html)
- [UserServiceImpl.retrieveUser](retrieve-user.html)
- [UserServiceImpl.updateUser](update-user.html)
- [Exercising the API](api.html)
- [Testing CRUD Operations](testing.html)
- [Exercises for the Reader](exercises.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle = "modelling our subdomain" %}
{% assign nextLink = "modelling.html" %}
{% include navigate.html %}
