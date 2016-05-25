#! /bin/bash

startdir=`pwd`

cd /Users/jsmscs/ws/longevity
sbt doc

cd /Users/jsmscs/ws/longevity-docs/scaladocs

git rm -r emblem-latest longevity-latest

cp -rf /Users/jsmscs/ws/longevity/target/scala-2.11/api longevity-latest
cp -rf /Users/jsmscs/ws/longevity/emblem/target/scala-2.11/api emblem-latest

git add emblem-latest longevity-latest

git commit -m "update scaladocs to latest version"

git push

cd $startdir

