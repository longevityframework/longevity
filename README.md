[![Build
Status](https://travis-ci.org/longevityframework/longevity.svg?branch=master)](https://travis-ci.org/longevityframework/longevity)
[![License](http://img.shields.io/:license-Apache%202-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Join the chat at https://gitter.im/longevityframework/longevity](https://badges.gitter.im/longevityframework/longevity.svg)](https://gitter.im/longevityframework/longevity?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# longevity - A Persistence Framework for Scala and NoSQL.

## Focus your development on your domain model, not your database model.

Longevity translates your domain into a natural, easy to understand database model. Your persistent
objects are stored as JSON, and we add any other database schema needed to keep performance fast.

You no longer need to:

- design a database schema
- build and maintain a translation layer between your domain model and database model
- build and maintain integration tests for your translation layer
- make compromises in your domain model to satisfy your database model or translation layer

With longevity, persistence concerns and operations are abstracted behind an elegant persistence
API. We provide you with fully featured repositories for [Cassandra](http://cassandra.apache.org/),
[MongoDB](https://www.mongodb.org/), and [SQLite](https://sqlite.org/).

For more information, please visit the [longevity website](http://longevityframework.org/).
