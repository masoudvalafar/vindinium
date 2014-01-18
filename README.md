Java starter pack for the Vindinium AI challenge.

# Build

Project is compliant with [Maven Layout](http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

## Maven

```
mvn package
```

## SBT

```
sbt package
```

# Run

With Maven:

```
mvn exec:java -Dexec.arguments='http://server/url'
```

With SBT:

```
sbt# run http://server/url
```

# Test

Tests can be written using [JUnit](http://junit.org) or [Specs2](http://etorreborre.github.io/specs2/) (even if code is vanilla Java).

They can be executed using `mvn test` (with Maven) or `sbt test` (with SBT).

# Get started

Working with I/O reader from URL:

```java
import java.io.BufferedReader;
import java.net.URL;
import vindinium.UnaryFunction;
import vindinium.IO;

// Provided UnaryFunction<BufferedReader,A> function ...
A result = IO.fromUrl(new URL("http://url", "UTF-8", function);
```

Working I/O reader from HTTP POST response:

```java
import java.io.BufferedReader;
import java.util.HashMap;
import java.net.URL;
import vindinium.UnaryFunction;
import vindinium.IO;

// Prepare POST parameters
HashMap<String,String> ps = new HashMap<String,String>();
ps.put("name", "value");

// Provided UnaryFunction<BufferedReader,A> function ...
A result = IO.fromPost(ps, "UTF-8", 
                       new URL("http://post/url"), "UTF-8",
                       function);
```

Parsing Vindinium information from JSON reader:

```java
import com.google.gson.stream.JsonReader;
import vindinium.State;
import vindinium.JSON;

// Provided java.io.Reader r ...
JsonReader json = new JsonReader(r);
State vindinium = Json.next(json, Json.stateReader);
```