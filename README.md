Config Validator
===
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

Config
====
Basic key-value data structure.
Theoretically it allows hierarchical structures, 
but the validation itself is based on a flat structure.

Config Loader
====
Abstraction layer to load any kind of config format.

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

Config Specification
====
To be able to validate a config, you need a way to specify your expectations to it.
The config specification module provides a DSL backed by Groovy to express those.

For the sake of a higher flexibility an abstraction allows to plug in another DSL easily.

Config Validator
====
The config validator uses the config loader and config specification modules
and validates the loaded config against the loaded specification for it.

Config Validator Application
====
The application provides a command line interface to run your validation easily,
i.e. as part of your build chain of your project, at configuration management 
tools like Puppet or Chef as a test for all your instances' configuration.

TODOs
===
* Support for
 * XML
 * Typesafe Config ``*.conf`` files
* Make all modules separately accessible via Gradle multi-project setup
* Where to deploy the fatJar to (aka. shadowJar)
* Google Guice vs Spring Core for DI? Support for both?

License
===
This project is licensed under the terms of the 
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
