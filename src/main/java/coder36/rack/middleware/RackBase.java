package coder36.rack.middleware;

import coder36.rack.Rack;
import coder36.rack.RackResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RackBase implements Rack {

    private Rack rack;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public RackBase(Rack rack, HttpServletRequest request, HttpServletResponse response) {
        this.rack = rack;
        this.request = request;
        this.response = response;
    }

    private String safe(String str) {
        return str != null ? str : "";
    }

    public RackResult call(Map<String, Object> env) {
        Map<String, String> headers = headers(request);
        env.put("headers", headers);
        env.put("request", request);
        env.put("response", response);
        env.put("request_method", safe(request.getMethod()));
        env.put("request_path", safe(request.getRequestURI()));
        env.put("query_string", request.getQueryString());
        env.put("body", body(request));
        env.put("content_type", safe(headers.get("Content-Type")));
        String basePath = env.get("base_path").toString();
        env.put("path", request.getRequestURI().replaceFirst(basePath, "/").replaceAll("//", "/"));
        return rack.call(env);
    }

    public String body(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> headers(HttpServletRequest request) {

        Map<String, String> hdrs = new HashMap<>();
        Collections.list(request.getHeaderNames()).forEach(h -> {
            hdrs.put(h, request.getHeader(h));
        });
        return hdrs;
    }
}