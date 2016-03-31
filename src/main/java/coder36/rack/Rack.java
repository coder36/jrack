package coder36.rack;

import java.util.Map;


public interface Rack {
    RackResult call(Map<String,Object> env);
}
