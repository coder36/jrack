import coder36.rack.Assets;
import coder36.rack.Rack;
import coder36.rack.RackResult;
import coder36.rack.Sinatra;
import com.google.common.collect.ImmutableMap;

import static coder36.rack.RackResult.*;

import java.util.HashMap;
import java.util.Map;
import static coder36.rack.Rackup.*;

public class App {

    public static void main(String [] args ) {
        class HelloRack implements Rack {

            public RackResult call(Map<String, Object> env) {
                return rackResult(200, new HashMap<String,String>(), "Hello world");
            }
        }

        class CrossOrigin implements Rack {

            private Rack rack;

            public CrossOrigin(Rack rack ) {
                this.rack = rack;
            }

            public RackResult call(Map<String, Object> env) {
                RackResult res = rack.call(env);
                res._2.put("Access-Control-Allow-Origin", "http://*");
                return rackResult(res._1, res._2 , res._3);
            }
        }

        class SinatraApp1 extends Sinatra {{
            get("/", (c) -> {
                return ""+
                "<html>"+
                "  <body>"+
                "    <h1>App1</h1>"+
                "    <form action='/hiddenredirect' method='POST'>"+
                "      <input name='test' value='something'/>"+
                "      <input name='test2' value='blah something+asd'/>"+
                "      <input name='test3' value='whatever'/>"+
                "      <button>Sign in</button>"+
                "    </form>"+
                "  </body>"+
                "</html>";
            });

            post("/hiddenredirect", (c) -> {
                return ""+
                "<html>"+
                "  <body>"+
                "    <h1>App1" + c.params.get("test") +"</h1>"+
                "    <h1>hidden redirect to App2</h1>"+
                "    <form id='myform' action='http://localhost:8081/'>"+
                "    </form>"+
                "    <script>"+
                "       setTimeout( function() {"+
                "         document.getElementById('myform').submit()"+
                "       },1000 )"+
                "    </script>"+
                "  </body>"+
                "</html>";
            });

            get("/redirect",(c) -> {
                c.status = 302;
                c.respHeader.put("Location","http://localhost:8081");
                return "";
            });

            get("/hello/:name", (c) -> ssp("welcome", ImmutableMap.of("name", c.params.get("name")) ));

        }}


        class SinatraApp2 extends Sinatra {{
            get("/", (c) -> {
                return "" +
                "<html>"+
                "  <body>"+
                "    <h1>App 2</h1>"+
                "  </body>"+
                "</html>";
            });
        }}


        class Server1 extends RackServer {{
            map("/").onto(new CrossOrigin(new SinatraApp1()));
            map("/assets").onto( new Assets());
            map("/hellorack").onto( new HelloRack());
        }}


        class Server2 extends RackServer {{
            port = 8081;
            map("/").onto(new SinatraApp2());
        }}


        new Server1().start();
        new Server2().start();
    }
}
