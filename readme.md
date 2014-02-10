# kadai

## General Scala utilities

kadai is a collection of general utility classes that are helpful when starting
a new Scala project. These have been derived from a number of projects that were
repeating essentially the same code.

## Architecture

The code is separated into fairly specific purpose modules, each with as few dependencies as possible.

The modules are:

### core

basic data structures for disjunction result types with Invalids holding errors or error messages
on the left and and utilities for turning exceptions into Invalid results

### concurrent

commonly useful concurrency primitives

### config

simple Scala interface to the typesafe-config library with a Reader monad for composing 
configurable things and injecting configuration files.

### cmdopts

simple and type-safe Command Line Option parser

### logging

lightweight Scala friendly logging library, currently uses log4j2 under the covers

### hash

Tools for hashing bytes

# Download

To use this library from SBT you need to add the following resolver:

    resolvers += "atlassian-public" at "https://maven.atlassian.com/content/repositories/public/"

It has the groupId: `io.kadai`

There is an artifactId: `kadai` that combines all modules or you can depend on one directly with `kadai-logging`, `kadai-cmdopts` etc. Note:, there is a scala-version qualifier.