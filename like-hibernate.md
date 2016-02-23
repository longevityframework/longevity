---
title: longevity is like hibernate
layout: page
---

Longevity is like Hibernate, except that it's not. The two projects
share one major goal: provide persistence support for your domain
entities in your enterprise application. But they are more different
than same.

While Hibernate is for Java and relational databases, Longevity is for
Scala and document databases. Hibernate is also an Object-Relational
Mapper (ORM), and longevity is not. Let's break that down.

The "object" in ORM is the domain entity instances in an
Object-Oriented programming language, such as Java. In Scala, our
entity instances are still objects, but they are immutable objects. In
OO, objects are mutable by default. So the "O" in ORM is talking about
mutable objects. Indeed, in Hibernate, you cannot create an entity with
immutable set or list properties. In contrast, in a Functional/OO
hybrid such as Scala, we always prefer to use immutable objects,
because of the elegance of programming with them in a functional
style.

The "relational" in ORM is the relational database the domain objects
are stored in. We are thinking in Entity-Relationship modelling here,
where the domain is a loosely structured graph of related entities. In
DDD, we focus on a higher structure of the entities and relationships
in our model by composing them into larger units called
aggregates. And document databases do the same thing, except there,
they call them documents.

The "mapper" in ORM is about mapping in between the two different
worlds of "O" and "R": from rows in a relational database, into OO
objects, and back again. Hibernate gives you tremendous power to
customize your database schema by enscripting the specifics of your
physical model into annotations on your entity classes. Longevity
takes an alternate approach, encouraging you to design your domain
classes freely, trusting that the translation in and out of the
database will work out well. After all documents and aggregates are
a great match for each other.

Longevity encapsulates persistence concerns behind a persistence API
that is elegant and powerful. Your domain classes remain
persistence-free, so that you can use them easily throughout your
application. Functional techniques such as higher ordered functions
allow us to abstract persistence concerns in a way where it is still
convenient to perform operations on the entities themselves, similar
to the way futures encapsulate an asynchronous process.

Longevity is designed to make it easy for you to do Domain Driven
Design. The terminology of the framework is largely borrowed from the
terminology of DDD. While DDD practices cover a wide breadth of
activities, domain modeling is at its core. In DDD, the best model of
your domain is the one in your software. It's the reference
model. Every feature of longevity is designed with doing good DDD
modelling in mind.
