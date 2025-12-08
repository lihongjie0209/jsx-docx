package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

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
        // Create V8 runtime (V8 engine supports all modern JavaScript features)
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Use proxy converter for seamless Java-JS interop
            v8Runtime.setConverter(new JavetProxyConverter());
            
            // Optional: Enable console logging
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());
            
            // 1. Load Runtime (React polyfill)
            try (Reader reader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/runtime.js")),
                    StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                char[] buffer = new char[8192];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    sb.append(buffer, 0, read);
                }
                v8Runtime.getExecutor(sb.toString()).executeVoid();
            }

            // 1.5. Expose data context if provided
            if (data != null) {
                v8Runtime.getGlobalObject().set("data", data);
            }

            // 2. Run the compiled user code
            V8Value evalResult = v8Runtime.getExecutor(compiledJs).execute();

            // 3. Get the result - check both __RESULT__ (from render()) and eval result
            V8Value result = v8Runtime.getGlobalObject().get("__RESULT__");
            
            // If no render() was called, use the last expression result
            boolean resultIsNullOrUndefined = (result instanceof V8ValueNull || result instanceof V8ValueUndefined);
            if (resultIsNullOrUndefined) {
                boolean evalIsNullOrUndefined = (evalResult == null || evalResult instanceof V8ValueNull || evalResult instanceof V8ValueUndefined);
                if (!evalIsNullOrUndefined) {
                    result = evalResult;
                } else {
                    throw new RuntimeException("No document returned. JSX should evaluate to a VNode object or call render(<Document ... />).");
                }
            }

            // 4. Convert to a detached Java object tree so we can safely close the runtime
            VNode vnode = toVNode(result);
            
            // Clean up
            if (result != evalResult) {
                result.close();
            }
            if (evalResult != null) {
                evalResult.close();
            }
            consoleInterceptor.unregister(v8Runtime.getGlobalObject());
            
            return vnode;
        }
    }

    /**
     * Execute compiled JSX without data context (backward compatible)
     */
    public VNode run(String compiledJs) throws Exception {
        return run(compiledJs, null);
    }

    private VNode toVNode(V8Value value) throws Exception {
        if (!(value instanceof V8ValueObject)) {
            throw new RuntimeException("Expected VNode object, got: " + value);
        }
        
        V8ValueObject obj = (V8ValueObject) value;
        VNode node = new VNode();
        
        // Get type
        try (V8Value typeVal = obj.get("type")) {
            if (!(typeVal instanceof V8ValueNull || typeVal instanceof V8ValueUndefined)) {
                node.setType(typeVal.toString());
            }
        }

        // Get props
        Map<String, Object> props = new HashMap<>();
        try (V8Value propsVal = obj.get("props")) {
            if (propsVal instanceof V8ValueObject) {
                V8ValueObject propsObj = (V8ValueObject) propsVal;
                try (V8ValueArray propKeys = (V8ValueArray) propsObj.getOwnPropertyNames()) {
                    int len = propKeys.getLength();
                    for (int i = 0; i < len; i++) {
                        try (V8Value keyVal = propKeys.get(i)) {
                            String key = keyVal.toString();
                            try (V8Value v = propsObj.get(key)) {
                                props.put(key, toJavaAny(v));
                            }
                        }
                    }
                }
            }
        }
        node.setProps(props);

        // Get children
        List<Object> children = new ArrayList<>();
        try (V8Value childrenVal = obj.get("children")) {
            if (childrenVal instanceof V8ValueArray) {
                V8ValueArray childrenArr = (V8ValueArray) childrenVal;
                int len = childrenArr.getLength();
                for (int i = 0; i < len; i++) {
                    try (V8Value c = childrenArr.get(i)) {
                        if (c instanceof V8ValueNull || c instanceof V8ValueUndefined) {
                            continue;
                        }
                        if (c instanceof V8ValueString) {
                            children.add(c.toString());
                        } else if (c instanceof V8ValueNumber || c instanceof V8ValueInteger || c instanceof V8ValueLong || c instanceof V8ValueDouble) {
                            children.add(String.valueOf(c.toString()));
                        } else if (c instanceof V8ValueBoolean) {
                            children.add(String.valueOf(((V8ValueBoolean)c).getValue()));
                        } else if (c instanceof V8ValueObject) {
                            V8ValueObject scriptable = (V8ValueObject) c;
                            // Check if it's a VNode (has type, props, children)
                            if (scriptable.has("type")) {
                                children.add(toVNode(c));
                            } else {
                                // Fallback: convert to string
                                children.add(c.toString());
                            }
                        } else {
                            children.add(c.toString());
                        }
                    }
                }
            }
        }
        node.setChildren(children);

        return node;
    }

    private Object toJavaAny(V8Value v) throws Exception {
        if (v instanceof V8ValueNull || v instanceof V8ValueUndefined) {
            return null;
        }
        if (v instanceof V8ValueString) {
            return v.toString();
        }
        if (v instanceof V8ValueBoolean) {
            return ((V8ValueBoolean)v).getValue();
        }
        if (v instanceof V8ValueInteger) {
            return ((V8ValueInteger)v).getValue();
        }
        if (v instanceof V8ValueLong) {
            return ((V8ValueLong)v).getValue();
        }
        if (v instanceof V8ValueDouble) {
            return ((V8ValueDouble)v).getValue();
        }
        if (v instanceof V8ValueNumber) {
            // Generic number type
            return ((V8ValueNumber)v).getValue();
        }
        if (v instanceof V8ValueArray) {
            V8ValueArray arr = (V8ValueArray) v;
            List<Object> list = new ArrayList<>();
            int len = arr.getLength();
            for (int i = 0; i < len; i++) {
                try (V8Value item = arr.get(i)) {
                    list.add(toJavaAny(item));
                }
            }
            return list;
        }
        if (v instanceof V8ValueObject) {
            V8ValueObject obj = (V8ValueObject) v;
            Map<String, Object> map = new HashMap<>();
            try (V8ValueArray keys = (V8ValueArray) obj.getOwnPropertyNames()) {
                int len = keys.getLength();
                for (int i = 0; i < len; i++) {
                    try (V8Value keyVal = keys.get(i)) {
                        String key = keyVal.toString();
                        try (V8Value val = obj.get(key)) {
                            map.put(key, toJavaAny(val));
                        }
                    }
                }
            }
            return map;
        }
        return v.toString();
    }
}
