# Rack for Java

A bare bones implementation of Rack and Sinatra for java.

Compile time dependencies:

* JDK8
* [Jetty](http://www.eclipse.org/jetty/)
* [Guava](https://github.com/google/guava)
* [scalate](https://github.com/scalate/scalate)

### Background
 
When writing a web apps in java, I was always dependent on a heavy weight MVC framework and equally heavy app server.
Having worked with other languages and frameworks, I realised that life becomes a lot simpler when using
 light touch frameworks such as Rack and Sinatra in Ruby.  So I've copied them... JRack is kind of port of Rack from Ruby
  to java, but including only the features I found useful and intuitive, hence the bare bones.

JDK8 and lambdas, now makes it possible to write a nice API.  Clever styling of the java language makes
it even nicer (though probably breaks every java checkstyle out there!).

# Building and running demo app

```
    ./gradlew fatJar
     java -jar build/libs/javarack-1.0-SNAPSHOT.jar
```

Navigate to:

[http://localhost:8080](http://localhost:8080)

[http://localhost:8080/hello/mark](http://localhost:8080/hello/mark)

[http://localhost:8080/hellorack](http://localhost:8080/hellorack)

[http://localhost:8080/assets/demo.html](http://localhost:8080/assets/demo.html)



# Rack

Rack itself is an interface:

```
public interface Rack {
    RackResult call(Map<String,Object> env);
}
```

A hello world implementation would be:

```
class HelloRack implements Rack {
    public RackResult call(Map<String, Object> env) {
        return rackResult(200, new HashMap<String,String>(), "Hello world");
    }
}
```

The `env` map passed into `call()` contains:


| Property        | Type                  |
| ----------------|-----------------------|
| headers         | Map<String,String>    |
| request         | HttpServletRequest    |
| response        | HttpServletResponse   |
| request_method  | String                |
| request_path    | String                |
| query_string    | String                |
| body            | String                |
| content_type    | String                |
| path            | String                |



A `RackResult` contains

```
    1) Http Status Code
    2) Map of headers to return in the response
    3) Body
```

This is rack at its most basic level.  What makes it really powerful is that you can
chain Rack apps together.

```
Rack rack = new RackFormParse(new HelloRack());
```

This example will parse `x-www-form-urlencoded` post requests, adding `form_params = Map<String,String>` as an `env` property.


# Rackup

To run Rack applications, you need a RackServer.

```
import coder36.rack.*;

class MyApp {

    public static void main(String [] args ) {

        class HelloRack implements Rack {
            public RackResult call(Map<String, Object> env) {
                return rackResult(200, new HashMap<String,String>(), "Hello world");
            }
        }

        class MyServer extends RackServer {{
            port = 8081;
            map("/").onto(new HelloSinatra());
        }}

        new MyServer().start()
    }

}
```

Navigate to [http://localhost:8081](http://localhost:8081)


# Web frameworks built on top of Rack

## Assets

Assets is a really basic web server which serves up static content.  By default
 it serves content from the resources/public folder.

```
    map("/").onto(new Assets());
```

## Sinatra

Sinatra is a lightweight web framework developed on top of Rack.  It also has a built in
static asset server (based on `Assets`).

```
    static class HelloSinatra extends Sinatra {{
        get("/hello",       (c) -> "Hello from sinatra" );
        get("/hello/:name", (c) -> "Hello " + c.params.get("name"));

        get("/boom", (c) -> {
            return "" +
             "<html>               "+
             "   <body>            "+
             "     <h1>Boom!</h1>  "+
             "   </body>           "+
              "</html>             "
        });
    }}

    ...
    map("/").onto(new HelloSinatra());

```

### Context

The (context) object provides a way of interacting with sinatra:


| Property        | Type                  | Description
| ----------------|-----------------------|------------------------------------------
| status          | int                   | http status code to send back to browser
| respHeaders     | Map<String,String>    | Response headers to send back to browser
| params          | Map<String,String>    | Parameters from Post request
| env             | Map<String,Object>    | Rack env



#### Http status

The HTTP status can be set with:
```
    c.status = 204
```

#### Response Headers

`c.respHeaders.put("Content-Type", "application/json")`


#### Parameterized paths

eg.  `get("/hello/:name", (c) -> "Hello " + c.params.get("name"));`


#### Scala server pages

Rack supports ssp template rendering:

```
get("/hello/:name", (c) -> {
  ssp("welcome", Map("name" -> c.params("name")) )
})
```

`ssp(...)` looks in the `resources/views` for `welcome.ssp` and renders it:

```
<%@ val name: String %>
<html>
    <body>
        <h1> Hi <%=name%>, welcome to Sinatra</h1>
    </body>
</html>
```



## License

Licensed under the [MIT license](https://raw.githubusercontent.com/coder36/jrack/master/LICENSE).
