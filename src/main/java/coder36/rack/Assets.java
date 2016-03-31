package coder36.rack;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import coder36.rack.middleware.*;

public class Assets implements Rack {

    private String root = "public";

    public Assets() {
    }

    public Assets(String root) {
        this.root = root;
    }

    public RackResult call(Map<String, Object> env) {
        try {
            String path = "/" + root + env.get("path");
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null ) {
                return new RackNotFound().call(env);
            }

            HttpServletResponse res = (HttpServletResponse) env.get("response");

            byte[] buffer = new byte[1024];
            int s = 0;
            while ( (s = is.read(buffer, 0, 1024)) !=-1){
                res.getOutputStream().write(buffer, 0, s);
            }
            is.close();
        }
        catch( IOException e ) {
            throw new RuntimeException(e);
        }
        return RackResult.rackResult(200,new HashMap<String, String>(),null);
    }

}