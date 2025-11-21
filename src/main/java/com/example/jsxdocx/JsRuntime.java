package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsRuntime {

    /**
     * Execute compiled JSX with optional data context
     * @param compiledJs compiled JavaScript from JSX
     * @param data optional Map to expose as global 'data' object in JSX context
     * @return VNode tree
     */
    public VNode run(String compiledJs, Map<String, Object> data) throws Exception {
        try (Context context = Context.newBuilder("js")
            .option("engine.WarnInterpreterOnly", "false")
            .build()) {
            // 1. Load Runtime (React polyfill)
            try (Reader reader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/runtime.js")),
                    StandardCharsets.UTF_8)) {
                context.eval(Source.newBuilder("js", reader, "runtime.js").build());
            }

            // 1.5. Expose data context if provided
            if (data != null) {
                Value dataValue = mapToJsValue(context, data);
                context.getBindings("js").putMember("data", dataValue);
            }

            // 2. Run the compiled user code
            Value evalResult = context.eval("js", compiledJs);

            // 3. Get the result - check both __RESULT__ (from render()) and eval result
            Value result = context.getBindings("js").getMember("__RESULT__");
            
            // If no render() was called, use the last expression result
            if (result == null || result.isNull()) {
                if (evalResult != null && !evalResult.isNull() && evalResult.hasMembers()) {
                    result = evalResult;
                } else {
                    throw new RuntimeException("No document returned. JSX should evaluate to a VNode object or call render(<Document ... />).");
                }
            }

            // 4. Convert to a detached Java object tree so we can safely close the context
            return toVNode(result);
        }
    }

    /**
     * Execute compiled JSX without data context (backward compatible)
     */
    public VNode run(String compiledJs) throws Exception {
        return run(compiledJs, null);
    }

    /**
     * Convert Java Map to GraalVM JS object
     */
    private Value mapToJsValue(Context context, Object obj) throws Exception {
        if (obj == null) return context.eval("js", "null");
        if (obj instanceof String) return context.eval("js", "'" + escapeJsString((String) obj) + "'");
        if (obj instanceof Boolean) return context.eval("js", obj.toString());
        if (obj instanceof Number) return context.eval("js", obj.toString());
        
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            StringBuilder sb = new StringBuilder("({");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(", ");
                first = false;
                sb.append(entry.getKey()).append(": ");
                
                Object value = entry.getValue();
                if (value instanceof String) {
                    sb.append("'").append(escapeJsString((String) value)).append("'");
                } else if (value instanceof Map) {
                    // Nested objects will be handled recursively
                    sb.append(mapToJsString((Map<String, Object>) value));
                } else if (value instanceof List) {
                    sb.append(listToJsString((List<?>) value));
                } else if (value instanceof Boolean || value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append("'").append(escapeJsString(value.toString())).append("'");
                }
            }
            sb.append("})");
            return context.eval("js", sb.toString());
        }
        
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(", ");
                first = false;
                
                if (item instanceof String) {
                    sb.append("'").append(escapeJsString((String) item)).append("'");
                } else if (item instanceof Map) {
                    sb.append(mapToJsString((Map<String, Object>) item));
                } else if (item instanceof List) {
                    sb.append(listToJsString((List<?>) item));
                } else if (item instanceof Boolean || item instanceof Number) {
                    sb.append(item);
                } else {
                    sb.append("'").append(escapeJsString(item.toString())).append("'");
                }
            }
            sb.append("]");
            return context.eval("js", sb.toString());
        }
        
        return context.eval("js", "'" + escapeJsString(obj.toString()) + "'");
    }

    private String mapToJsString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("({");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(entry.getKey()).append(": ");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("'").append(escapeJsString((String) value)).append("'");
            } else if (value instanceof Map) {
                sb.append(mapToJsString((Map<String, Object>) value));
            } else if (value instanceof List) {
                sb.append(listToJsString((List<?>) value));
            } else if (value instanceof Boolean || value instanceof Number) {
                sb.append(value);
            } else {
                sb.append("'").append(escapeJsString(value.toString())).append("'");
            }
        }
        sb.append("})");
        return sb.toString();
    }

    private String listToJsString(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(", ");
            first = false;
            
            if (item instanceof String) {
                sb.append("'").append(escapeJsString((String) item)).append("'");
            } else if (item instanceof Map) {
                sb.append(mapToJsString((Map<String, Object>) item));
            } else if (item instanceof List) {
                sb.append(listToJsString((List<?>) item));
            } else if (item instanceof Boolean || item instanceof Number) {
                sb.append(item);
            } else {
                sb.append("'").append(escapeJsString(item.toString())).append("'");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJsString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("'", "\\'")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private VNode toVNode(Value value) {
        VNode node = new VNode();
        node.setType(value.getMember("type").asString());

        Map<String, Object> props = new HashMap<>();
        Value propsVal = value.getMember("props");
        if (propsVal != null && propsVal.hasMembers()) {
            for (String key : propsVal.getMemberKeys()) {
                Value v = propsVal.getMember(key);
                props.put(key, toJavaAny(v));
            }
        }
        node.setProps(props);

        List<Object> children = new ArrayList<>();
        Value childrenVal = value.getMember("children");
        if (childrenVal != null && childrenVal.hasArrayElements()) {
            long len = childrenVal.getArraySize();
            for (int i = 0; i < len; i++) {
                Value c = childrenVal.getArrayElement(i);
                if (c.isString()) {
                    children.add(c.asString());
                } else if (c.hasMembers()) {
                    children.add(toVNode(c));
                } else if (c.isNumber()) {
                    children.add(c.asString());
                }
            }
        }
        node.setChildren(children);

        return node;
    }

    private Object toJavaPrimitive(Value v) {
        if (v == null || v.isNull()) return null;
        if (v.isBoolean()) return v.asBoolean();
        if (v.isNumber()) {
            try {
                if (v.fitsInInt()) return v.asInt();
                if (v.fitsInLong()) return v.asLong();
            } catch (Throwable ignored) {}
            return v.asDouble();
        }
        if (v.isString()) return v.asString();
        return v.toString();
    }

    private Object toJavaAny(Value v) {
        if (v == null || v.isNull()) return null;
        if (v.isBoolean() || v.isNumber() || v.isString()) return toJavaPrimitive(v);
        if (v.hasArrayElements()) {
            List<Object> list = new ArrayList<>();
            long len = v.getArraySize();
            for (int i = 0; i < len; i++) {
                list.add(toJavaAny(v.getArrayElement(i)));
            }
            return list;
        }
        if (v.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : v.getMemberKeys()) {
                map.put(key, toJavaAny(v.getMember(key)));
            }
            return map;
        }
        return v.toString();
    }
}
