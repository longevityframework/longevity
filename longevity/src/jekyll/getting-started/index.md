---
title: getting started with longevity
layout: page
---

This guide walks through the basic steps needed to get started building a real-life application with
[longevity](..). The application we will be looking at here is a sample
blogging application, built with longevity on the back end, and using [Akka
HTTP](http://doc.akka.io/docs/akka-http/current/scala.html) for a REST API that could be used by a
web client. You can find the source code here:

[https://github.com/longevityframework/simbl](https://github.com/longevityframework/simbl)

We've also [ported](https://github.com/longevityframework/simbl-play) the Akka HTTP tutorial into
[Play](https://www.playframework.com/). The tutorials and this guide cover roughly the same
material, with this guide following the Akka HTTP version.

We will only have the chance to cover a portion of the blogging application code, so please feel
free to explore the codebase further on your own. You can also look to the [user manual](../manual)
for more information.

Here's a table of contents for the getting started guide:

1. [Modelling our Domain](modelling.html)
1. [Declaring the Domain Model](building.html)
1. [Building the User Aggregate](user.html)
1. [The User Profile](user-profile.html)
1. [Username and Email](keyvals.html)
1. [Building the Longevity Context](context.html)
1. [The Akka HTTP Routes](routes.html)
1. [The User Service](service.html)
1. [User Service Implementation](service-impl.html)
1. [UserServiceImpl.createUser](create-user.html)
1. [UserServiceImpl.retrieveUser](retrieve-user.html)
1. [UserServiceImpl.updateUser](update-user.html)
1. [Exercising the API](api.html)
1. [Testing CRUD Operations](testing.html)
1. [Exercises for the Reader](exercises.html)

{% assign upTitle = "longevity site" %}
{% assign upLink = ".." %}
{% assign nextTitle = "modelling our domain" %}
{% assign nextLink = "modelling.html" %}
{% include navigate.html %}
