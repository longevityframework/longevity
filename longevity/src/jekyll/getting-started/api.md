---
title: exercising the api
layout: page
---

Of course, this API actually works. Feel free to play around with it
with the tool of your choice. You could use a UNIX tool such as
[curl](https://github.com/curl/curl), or perhaps a Chrome plugin such
as
[Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop)
or [Advanced REST
client](https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo). We
have a slight preference towards the Advanced REST client at the
moment. It is a little less quirky than Postman.

If you choose to use the Advanced REST client, we've exported our
sample requests to
[arc-simbl-export.json](https://raw.githubusercontent.com/longevityframework/simbl/master/tutorial/arc-simbl-export.json). You
can use this as a starting point. (You won't be able to view this file
within Typesafe Activator, so we've provided a link to the raw file in
GitHub.)

{% assign prevTitle = "UserServiceImpl.updateUser" %}
{% assign prevLink = "update-user.html" %}
{% assign upTitle = "getting started guide" %}
{% assign upLink = "." %}
{% assign nextTitle = "testing crud operations" %}
{% assign nextLink = "testing.html" %}
{% include navigate.html %}
