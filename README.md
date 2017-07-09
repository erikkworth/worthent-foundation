# worthent-foundation
Provides core utilities for the Worth Enterprises suite of software components.

These utilities are provided with a very minimal set of dependencies on third party components to make it easy for others to adopt without having to pull in and manage a bunch of transient dependencies.

The utilities require **Java 1.8** or above.

**Download from Maven Central:**
```xml
<dependency>
    <groupId>com.worthent.foundation</groupId>
    <artifactId>worthent-foundation</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**
```
compile 'com.worthent.foundation:worthent-foundation:1.0.0'
```
The foundation distribution includes these utilities:

* [State Table Utility](doc/state/state.md): this utility makes it easy to implement state machines that do work as they transition between discrete states based on events.
