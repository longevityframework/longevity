---
title: running your migration
layout: page
---

Now that we have [built our migration](migration.html), we can run it like so:

```
sbt migrateSchema 0_1_to_0_2
```

Here, the migration process will look for a `longevity.migrations.Migration[_, _]` instance in a
field named `0_1_to_0_2` in the `migrationsPackage` setting we configured when we [set up the
plugin](setup.html).

The `migrateSchema` task will perform a series of checks before running the migration. First, it
checks that the migration is valid, as described towards the bottom of the [previous
section](migration.html). It also asks the user for a couple confirmations.

We ask you to confirm that you have backed up your database before running the migration. As with
any migration process, things can go wrong along the way, leaving your database in a corrupted
state. One common example of something that can go wrong is this: You define a new
[key](../ptype/keys.html) for your persistent type, and two existing objects have the same value for
the new key. Depending on your back end, the type of key, and whether or not your database is
partitioned, an error like this can result in different outcomes. It could cause the migration
process to halt with an error, it could silently overwrite, or it could allow the dupicate key
values in, violating your integrity constraint. This kind of lack of strict integrity is fairly
typical when working with NoSQL databases. With the SQLite or JDBC back ends, the migration will
definitely halt with an error in this scenario.

We also ask you to confirm that you have no applications running against the database while the
migration is running. Any concurrent writes to the initial version of the model may or may not be
lost by the migration process. Any concurrent reads from the initial version of the model will
produce correct results, but note that one of the last steps of the migration process is to tear
down the initial version, so any read-only clients will probably error out towards the end of the
migration process.

We have considered ways to allow you to keep running your application while the migration is
running. In particular, see these three items on our "migrations wishlist":

- [Migration flag to maintain the old
  schema](https://github.com/longevityframework/longevity/wiki/Longevity-Migrations---Ideas-for-Future-Directions#migration-flag-to-maintain-the-old-schema)
- [Soft stop
  migrations](https://github.com/longevityframework/longevity/wiki/Longevity-Migrations---Ideas-for-Future-Directions#soft-stop-migrations)
- [No stop migrations](https://github.com/longevityframework/longevity/wiki/Longevity-Migrations---Ideas-for-Future-Directions#no-stop-migrations)

If you are interested in taking on one of these enhancements, please let us know! We would be more
than happy to help.

You can avoid these interactive confirmation queries by supplying a `--nonInteractive` flag to the
SBT task invocation:

```
sbt migrateSchema 0_1_to_0_2 --nonInteractive
```

This will come in handy if you want to run this task from within a script.

Please note that due to the interactive nature of the `migrateSchema` rule, you may encounter
difficulties running the rule if your build forks runs, for instance like so:

```scala
fork in run := true
```

In such situations, you may wish to run `migrateSchema` with the `--nonInteractive` flag. Or you
could add the following settings to your build to allow forked runs to share the STDIN and STDOUT
streams of the SBT process:

```scala
outputStrategy := Some(StdoutOutput)
connectInput in run := true
```

The SBT task will let you know once the migration is complete. This migration process has been
designed to be restartable, so if it does get interrupted mid-way through, you do not have to
restore from a backup and start over. Just re-run the SBT `migrateSchema` command.

After the migration, you will need to make a change to your [longevity
configuration](../context/config.html) to indicate that you are running against a new model version.
In our example, we specified "version_0_2" as the final version of our migration, so we would update
our `application.conf` file as follows:

```
longevity.modelVersion = version_0_2
```

{% assign prevTitle = "defining your migration" %}
{% assign prevLink  = "migration.html" %}
{% assign upTitle   = "migrating to a new version of your domain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "integration points" %}
{% assign nextLink  = "../integrations" %}
{% include navigate.html %}
