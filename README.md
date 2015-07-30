#longevity

[![Build
Status](https://travis-ci.org/sullivan-/longevity.svg?branch=master)](https://travis-ci.org/sullivan-/longevity.svg)

A Domain Driven Design persistence framework for Scala and MongoDB.

This project is still in early development. A [minimum marketable
product
(MMP)](http://www.romanpichler.com/blog/minimum-viable-product-and-minimal-marketable-product/)
\- that is, the earliest version suitable for your use - is currently
projected for release in mid June. Please be aware that this is only
my best estimate. If you want to track progress, take a look at [my
story board](https://www.pivotaltracker.com/n/projects/1231978). The
longevity 0.2 release is my MMP.

longevity currently contains three subprojects:

- **emblem** - a metaprogramming library for managing types and reflecting case classes
- **longevity** - a DDD persistence frameowkr for Scala and MongoDB
- **musette** - a content resource management system

emblem is a utility library used by longevity, and you can think of
musette as a flagship project that makes use of longevity.
