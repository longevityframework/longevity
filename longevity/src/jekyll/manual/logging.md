---
title: managing logging
layout: page
---

Longevity current produces two kinds of logging output. First, all
exceptions thrown by longevity that are intended to be non-recoverable
are logged at the WARN level. Second, all [repo API](repo) calls, and
all calls to the underlying database, are logged at the DEBUG level.

The underlying database drivers also generate a lot of logging
output. They tend to be quite chatty, and produce a lot of logs at the
INFO level.

Longevity and the underlying drivers all use the [SLF4J
API](http://www.slf4j.org/), which means you can configure your
logging by providing any SLF4J implementation. You may want to turn
down logging for the underlying drivers to the WARN level.

As an example, a simple way to do this is to use `slf4j-simple`:

```scala
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
```

You can then provide a `simplelogger.properties` file in your
`src/main/resources` directory containing the following line:

```
org.slf4j.simpleLogger.defaultLogLevel=warn
```

This will quell all logs at the INFO level or lower.

{% assign prevTitle = "sqlite translation" %}
{% assign prevLink  = "translation/sqlite.html" %}
{% assign upTitle   = "user manual" %}
{% assign upLink    = "." %}
{% include navigate.html %}

