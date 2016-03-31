package coder36.rack;

import java.util.Map;

public class RackResult extends Triple<Integer, Map<String,String>, String>{
    public RackResult(Integer q ,Map<String,String> w, String e) {
        super(q,w,e);
    }

    static public RackResult rackResult(Integer q ,Map<String,String> w, String e) {
        return new RackResult(q,w,e);
    }
}
