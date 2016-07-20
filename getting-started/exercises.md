---
title: exercises for the reader
layout: page
---

While Simple Blogging is a working application, it has been
developed for the purposes of this tutorial, and consequently,
it is incomplete in a number of ways. As an exercise, you
might try to enhance the application to fill in the gaps. We
will be happy to consider any pull requests you make that fill
in missing features. Here are some ideas for experimentations
you might try:

- Add a `Comment` aggregate to the subdomain.
- Put in service methods and routes for `BlogPost` and `Blog`.
- Write unit tests for the Akka HTTP routes.
- Write unit tests for `UserServiceImpl`, preferably using a mock
object for the user repository.
- Write a simple UI that uses the backing API.

Thank you very much for working through this guide! We hope you enjoy
longevity as much as we do. If you would like to investigate further,
please take a look at our [user
manual](http://longevityframework.github.io/longevity/manual/). Also,
please write to our [discussion
forum](https://groups.google.com/forum/#!forum/longevity-users) to
tell us about about your experience with longevity, or to ask any
questions.

Happy coding!

{% assign prevTitle = "testing crud operations" %}
{% assign prevLink = "testing.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% include navigate.html %}
