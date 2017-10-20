---
title: tagging a version of your domain model
layout: page
---

To migrate your data, you will first need to tag two versions of your domain model. We call the
first version the _initial_ version. That's the version that matches the current state of the
database. The second is the _final_ version. That's the version that matches the desired state of
the database. In a typical scenario, the _initial_ version is what is running on production, and the
_final_ version is the version that you wish to release to production.

To tag the current version of your domain model as "versionX", run the following command in SBT:

```scala
createVersionTag versionX
```

This will make a copy of your domain model code into a new package `versionX`. This new package will
be a sub-package of the `migrationsPackage` we set up in SBT in the [last section](setup.html). It's
not a strict copy; all of the `package` and `import` statements that reference your domain model
package will be adjusted to reference the new `versionX` package. At this point, you will want to
run `sbt compile` to make sure the tagging process went smoothly.

Ideally, you would have tagged the initial version of your model right around the time that you did
your last production release. But if not, that's okay. You simply need to find the correct version
from within source control, tag there, and bring the tagged version up to the version control head.
With a modern source control system such as [Git](https://git-scm.com/), this can be accomplished
like so:

1. Identify a commit along your master branch that has the correct model version. Perhaps you put a
version control tag there?
2. Check out that commit, and create a new branch from there.
3. Create the model version tag on that branch, and commit the changes.
4. Check out your master branch again.
5. Merge in the branch you just created.

If you already have a release branch, you could just use that branch. But take care to make sure
that everything else on the release branch is merged into master. Otherwise, merging your release
branch to master may bring in some unwanted changes. Another possibility for accomplishing this is
to [cherry pick](https://git-scm.com/docs/git-cherry-pick) the single commit where you created
your version tag.

{% assign prevTitle = "project setup for longevity migrations" %}
{% assign prevLink  = "setup.html" %}
{% assign upTitle   = "migrating to a new version of your domain" %}
{% assign upLink    = "." %}
{% assign nextTitle = "defining your migration" %}
{% assign nextLink  = "migration.html" %}
{% include navigate.html %}
