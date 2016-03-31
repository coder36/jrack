package coder36.rack.middleware;

import coder36.rack.Rack;
import coder36.rack.RackResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RackFormParser implements Rack {

    private Rack rack;

    public RackFormParser(Rack rack ) {
        this.rack = rack;
    }

    public RackResult call(Map<String, Object> env) {
        Map<String, String> formParams = new HashMap<>();
        env.put("form_params", formParams);
        if (env.get("content_type").toString().equals("application/x-www-form-urlencoded")) {
            String body = env.get("body").toString();
            Arrays.stream(body.split("&")).forEach(p -> {
                String[] a = p.split("=");
                try {
                    formParams.put(java.net.URLDecoder.decode(a[0], "UTF-8"), java.net.URLDecoder.decode(a[1], "UTF-8"));
                }
                catch( Exception e ) {
                    throw new RuntimeException(e);
                }

            });
        }

        return rack.call(env);
    }
}
