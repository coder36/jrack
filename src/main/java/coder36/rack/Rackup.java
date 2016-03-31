package coder36.rack;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import coder36.rack.middleware.*;

public class Rackup {

    public static class RackServer  {

        public int port = 8080;

        public RackServer() {
        }

        public RackServer( int port ) {
            this.port = port;
        }

        String path = "";

        Map<String, RackServlet> apps = new HashMap<>();

        public RackServer map(String path)  {
            this.path = path;
            return this;
        }

        public void onto(Rack rack) {
            apps.put(path, new RackServlet(rack, path));
        }

        public RackServer start() {

            try {
                ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                context.setContextPath("/");
                Server server = new Server(port);
                server.setHandler(context);
                apps.forEach( (path,rackServlet) -> {
                    context.addServlet(new ServletHolder(rackServlet), (path + "/*").replaceAll("//", "/"));
                });

                System.out.println("\u001B[34mRack started... listening for HTTP on /0.0.0.0:" + port);
                server.start();
                return this;
            }
            catch(Exception e ) {
                throw new RuntimeException(e);
            }
    }


    class RackServlet extends HttpServlet {

        private Rack rack;
        private String path;

        public RackServlet(Rack rack, String path) {
            this.rack = rack;
            this.path = path;
        }


        @Override public void service(HttpServletRequest request, HttpServletResponse response) {
            try {
                Map<String, Object> env = new HashMap<>();
                env.put("base_path", path);
                Triple<Integer, Map<String, String>, String> res = new RackBase(rack, request, response).call(env);
                int status = res._1;
                Map<String, String> headers = res._2;
                String body = res._3;
                response.setStatus(status);
                headers.forEach((h,v) -> { response.addHeader(h, v); });
                if (body != null) response.getWriter().write(body);
            }
            catch(Exception e ) {
                throw new RuntimeException(e);
            }
    }
}}


}
