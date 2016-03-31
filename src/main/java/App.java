import coder36.rack.Assets;
import coder36.rack.Rack;
import coder36.rack.RackResult;
import coder36.rack.Sinatra;

import static coder36.rack.RackResult.*;

import java.util.HashMap;
import java.util.Map;
import static coder36.rack.Rackup.*;

public class App {

    static class HelloRack implements Rack {

        public RackResult call(Map<String, Object> env) {
            return rackResult(200, new HashMap<String,String>(), "Hello world");
        }
    }


    static class HelloSinatra extends Sinatra {{
        get("/hello",       (c) -> "Hello from sinatra" );
        get("/ssp",         (c) -> ssp("demo"));
        get("/hello/:name", (c) -> "Hello " + c.params.get("name"));
    }}


    static class Server1 extends RackServer {{
        map("/").onto(new HelloSinatra());
        map("/assets").onto( new Assets());
    }}


    static class Server2 extends RackServer {{
        port = 8081;
        map("/").onto(new HelloRack());
        map("/a").onto( (env) -> rackResult(200, new HashMap<String,String>(), "Hello world") );
    }}


    public static void main(String[] args) {
        new Server1().start();
        new Server2().start();
    }
}
