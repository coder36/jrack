package coder36.rack.middleware;

import coder36.rack.Rack;
import coder36.rack.RackResult;

import java.util.HashMap;
import java.util.Map;

import static coder36.rack.RackResult.rackResult;

public class RackNotFound implements Rack {

    public RackResult call(Map<String, Object> env) {
        return rackResult(404, new HashMap<String,String>(), "Not found");
    }
}