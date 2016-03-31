package coder36.rack;


import org.fusesource.scalate.japi.TemplateEngineFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import coder36.rack.middleware.*;

public class Sinatra implements Rack {

    public static class Context {
        public int status = 200;
        public Map<String, String> respHeader = new HashMap<>();
        public Map<String, String> params = new HashMap<>();
        public Map<String, Object> env = new HashMap<>();
    }

    private TemplateEngineFacade engine = new TemplateEngineFacade();
    private Assets assets = new Assets();

    private Map<String, Map<String, Function<Context, String>>> methodHandlers = new HashMap<>();

    {
        methodHandlers.put("GET", new HashMap<>());
        methodHandlers.put("POST", new HashMap<>());
        methodHandlers.put("PUT", new HashMap<>());
    }

    public void get(String path, Function<Context, String> handler) {
        methodHandlers.get("GET").put(path, handler);
    }

    public void post(String path, Function<Context, String> handler) {
        methodHandlers.get("POST").put(path, handler);
    }

    public void put(String path, Function<Context, String> handler) {
        methodHandlers.get("PUT").put(path, handler);
    }

    public String ssp(String name, Map<String, Object> params) {
        return engine.layout("views/" + name + ".ssp", params);
    }

    public String ssp(String name) {
        return ssp(name, new HashMap<String, Object>());
    }


    public RackResult call(Map<String, Object> env) {
        return (new RackFormParser(new RackCallProxy(this::_call)).call(env));
    }

    private RackResult _call(Map<String, Object> env) {
        Context c = new Context();
        c.env = env;
        HashMap<String, String> formParams = (HashMap<String, String>) env.get("form_params");
        formParams.forEach((k, v) -> c.params.put(k, v));
        Function<Context, String> handler = getHandler(c);
        if (handler != null) {
            return new RackResult(c.status, c.respHeader, handler.apply(c));
        }
        return assets.call(env);
    }

    private Function<Context, String> getHandler(Context c) {
        String path = c.env.get("path").toString();
        String method = c.env.get("request_method").toString();

        for (String handlerPath : methodHandlers.get(method).keySet()) {
            Function<Context, String> handler = methodHandlers.get(method).get(handlerPath);
            List<String> tokens = Arrays.asList(handlerPath.split("/")).stream().filter(a -> !a.isEmpty()).filter(a -> a.startsWith(":")).map((a) -> a.substring(1)).collect(Collectors.toList());
            String regex = "^" + tokens.stream().reduce(handlerPath, (t, p) -> t.replace(":" + p, "(?<" + p + ">\\w+)")) + "$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(path);

            if (matcher.find()) {
                tokens.stream().forEach((t) -> c.params.put(t, matcher.group(t)));
                return handler;
            }
        }

        return null;
    }
}