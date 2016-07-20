---
title: the akka http routes
layout: page
---

Let's skip ahead to look at the Akka HTTP routes. In a moment, we'll
come back to our service class to see how these routes are hooked up
to the back-end repositories.

These routes define an application API that might be used by a
JavaScript application front-end. We haven't had the time to actually
write a front end yet. If you would like to give it a shot, we would
happily consider any pull requests!

`UserRoute.scala` defines the Simple Blogging API for users and user
profiles. The following routes are defined:

- `POST /users` - creates a new user
- `GET /users` - retrieves all the users
- `GET /users/$username` - retrieves a single user
- `PUT /users/$username` - updates an existing user
- `DELETE /users/$username` - deletes an existing user
- `GET /users/$username/profile` - retrieves a user profile
- `PUT /users/$username/profile` - creates or updates a user profile
- `DELETE /users/$username/profile` - deletes a user profile

<i>Please note that the `GET /users` endpoint will not work with a
Cassandra persistence strategy, because Cassandra does not support
unfiltered queries.</i>

These routes are defined in the standard idiom of [Akka
HTTP](http://doc.akka.io/docs/akka/2.4.8/scala/http/), and we will not
go into the details here. Please take a look at the source code if you
are interested. If you do not already have the [Simple Blogging
project](https://github.com/longevityframework/simbl) checked out, you
can browse the source [on
GitHub](https://github.com/longevityframework/simbl/blob/master/src/main/scala/simbl/api/UserRoute.scala).

For our purposes, the important thing to note is that the work for
each of these endpoints is delegated to one of the methods in
`UserService.scala`, which we will turn to next.

{% assign prevTitle = "building the longevity context" %}
{% assign prevLink = "context.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle="the user service" %}
{% assign nextLink="service.html" %}
{% include navigate.html %}
