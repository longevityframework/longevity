---
title: migrating to a new version of your domain
layout: page
---

The traditional approach to migrating your database schema and data to keep up with changes to your
application involves maintaining a sequence on migration scripts - typically written in a database
query language like SQL or CQL. A number of systems have been developed for managing the appropriate
application of these scripts. This approach is contrary to the design goals of longevity, where the
user develops their application code in terms of their domain model, and leaves persistence concerns
in the hands of longevity. While we are transparent about how we [translate your
domain](../translation) into different back ends, longevity users should be able to define their
migrations purely in terms of their domains. The longevity migrations framework described here
serves this purpose.

In longevity, we describe our schema migrations in terms of our _domain model_. Because the domain
model is undergoing changes, we save checkpoints of our model off to the side, in another package.
We define a `longevity.migrations.Migration`, that maps from persistent objects in the initial
model, to persistent objects in the final model, using _Scala functions_. And we hand this migration
off to longevity, which applies the changes. Your database is now ready to run against the more
recent version of your Scala code.

The longevity migrations framework is still in early stages of development, and many potentially
useful features are not in place yet. This user manual only covers existing functionality. To get a
sense of potential future directions, check out our [migrations
wishlist](https://github.com/longevityframework/longevity/wiki/Longevity-Migrations---Ideas-for-Future-Directions)
on GitHub.

- [Project Setup for Longevity Migrations](setup.html)
- [Tagging a Version of Your Domain Model](tagging.html)
- [Defining Your Migration](migration.html)
- [Running Your Migration](running.html)

(To be sure, you are free to user more traditional methods to migrate schema. But you should be aware
that this may be difficult or impossible, due to the way the various longevity back ends persist
your data. At present, every back end stores your persistent objects in JSON. Back ends such as
SQLite and Cassandra store the JSON is regular text columns. You may well have a very difficult time
applying the necessary changes to these JSON columns using traditional techniques. The MongoDB back
end stores your persistent objects as BSON, and MongoDB offers update commands that allow you to
modify your BSON documents dynamically, so a more traditional approach may work for you. It's also
possible that we will be adding column-based back ends for
[SQLite](https://github.com/longevityframework/longevity/issues/47) and
[Cassandra](https://github.com/longevityframework/longevity/issues/46) in the future, which will
also be amenable to more traditional approaches.)

{% assign prevTitle = "sqlite translation" %}
{% assign prevLink  = "../translation/sqlite.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = ".." %}
{% assign nextTitle = "project setup for longevity migrations" %}
{% assign nextLink  = "setup.html" %}
{% include navigate.html %}
