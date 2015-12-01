[![Build Status](https://travis-ci.org/pjungermann/config-validator.svg?branch=master)](https://travis-ci.org/pjungermann/config-validator)
[![Coverage Status](https://codecov.io/github/pjungermann/config-validator/coverage.svg?branch=master)](https://codecov.io/github/pjungermann/config-validator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pjungermann.config/config-validator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pjungermann.config/config-validator)
[![Dependency Status](https://www.versioneye.com/user/projects/56583c14ff016c003a001ad3/badge.svg)](https://www.versioneye.com/user/projects/56583c14ff016c003a001ad3)

# Config Validator
Configuration is an elementary part of most applications. 
It allows to apply to different use cases and environments. 
Therefore, applications have certain expectations onto the 
values handed in. Some of them might be optional, some have
to be within a certain set of values, etc. The possible 
constraints to apply to are endless and can be based on very 
specific application internal conditions and rules.

The goal of this library is to provide an extensible and polyglot framework
for config validation with support for any kind of config format.

This gets supported by the following modules:

* Config
* Config Loader
* Config Specification
* Config Validator
* Config Validator Application

## Config
Basic key-value data structure.
Theoretically it allows hierarchical structures, 
but the validation itself is based on a flat structure.

### Data Types and Data Structures
In general, configs can contain the following data types:

* key-value type
* collection type
* value type
 * primitive values (boolean, numbers, strings, null, undefined / undef, ...)
 * complex types (key-value, collection, any other complex object)

The root type, the config itself is a value as well as a key-value data structure 
even though there are certain config types which can start with a different structure, 
e.g. Array at JSON. For those, the (virtual) key ``"__config__"`` will be used to create a 
key-value structure as wrapper.

### Flat Config
In order to flatten the hierarchical config structure, the key-value and collection typed
data needs to be flattened. The root config which is a key-value structure itself, is excluded
from that rule.

#### Key-Value Data
Any key-value typed data can be resolved by using the higher level key as key path / prefix
for any key of it using a key separator between the different hierarchical levels in order
to be able reconstruct the hierarchy on a later stage.

Example:

    key1:
        key11: value11
        key12: value12

will get resolved to

    key1.key11: value11
    key1.key12: value12

#### Collection Data
For collections, capabilities are needed in order to

* provide specification for the collection itself like e.g. size
* provide specification for each item

In certain special cases, there might be the need to specify expectations
for a single item only, maybe additionally to the one applied to all items.

To achieve those goals, a way is needed to reference the collection, all of its
items as well a concrete item of it.

For creating those references, keys like the following can be used:

* ``collection_key`` as reference for the collection itself
* ``collection_key.[*]`` as reference for all items
* ``collection_key.[index]`` as reference for a single item
* ``collection_key.[from..to]`` as reference for a range of items

To flatten a collection, either the collection could get resolved so that the 
flat config contains only flat keys like the example above or the collection 
could get flattened recursively by flattening all its items.

The first approach provides a truly flat structure, but looses the information 
about the collection's properties. Also applying a specification for each item 
might be harder to achieve, esp. if the size of the collection is unknown. This 
could get circumvented by providing collection metadata, e.g. for the collection 
key itself or any other way.

The second approach leave the collection fully functional and only provides
a mechanism to define a specification for its content.

The second approach is the one used here.

## Config Loader
is an abstraction layer to load any kind of config format.

It also provides implementations for the following formats:

* Groovy config files (``ConfigObject``)
* INI files
* JSON files
* Properties files
* Yaml files

Further implementations can be added and / or plugged in.

Planned for future iterations:

* XML files
* [Typesafe Config (``*.conf``)](https://github.com/typesafehub/config)

## Config Specification
To be able to validate a config, you need a way to specify your expectations to it.
The config specification module provides a DSL backed by Groovy to express those.

For the sake of a higher flexibility an abstraction allows to plug in another DSL easily.

## Config Validator
The config validator uses the config loader and config specification modules
and validates the loaded config against the loaded specification for it.

## Config Validator Application
The application provides a command line interface to run your validation easily,
i.e. as part of your build chain of your project, at configuration management 
tools like Puppet or Chef as a test for all your instances' configuration.

# TODOs
* Support for
 * XML
 * Typesafe Config ``*.conf`` files
* Make all modules separately accessible via Gradle multi-project setup
* Where to deploy the fatJar to (aka. shadowJar)
* Google Guice vs Spring Core for DI? Support for both?

# License
This project is licensed under the terms of the 
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
