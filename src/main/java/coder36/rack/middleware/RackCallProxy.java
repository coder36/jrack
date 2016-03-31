package coder36.rack.middleware;


import coder36.rack.*;

import java.util.Map;
import java.util.function.Function;

public class RackCallProxy implements Rack {

    private Function<Map<String, Object>, RackResult> delegate;
    public RackCallProxy(Function<Map<String, Object>, RackResult> delegate) {
        this.delegate = delegate;
    }

    public RackResult call(Map<String, Object> env) {
        return delegate.apply(env);
    }
}
