package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.mozilla.javascript.*;

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
        Context cx = Context.enter();
        try {
            // Set optimization level (-1 = interpreted mode, more compatible)
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_ES6);
            // Enable ES6 features including arrow functions
            cx.getWrapFactory().setJavaPrimitiveWrap(false);
            
            Scriptable scope = cx.initStandardObjects();
            
            // 1. Load Runtime (React polyfill)
            try (Reader reader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/runtime.js")),
                    StandardCharsets.UTF_8)) {
                cx.evaluateReader(scope, reader, "runtime.js", 1, null);
            }

            // 1.5. Expose data context if provided
            if (data != null) {
                Object jsData = mapToJsValue(cx, scope, data);
                scope.put("data", scope, jsData);
            }

            // 2. Run the compiled user code
            Object evalResult = cx.evaluateString(scope, compiledJs, "compiled.js", 1, null);

            // 3. Get the result - check both __RESULT__ (from render()) and eval result
            Object result = scope.get("__RESULT__", scope);
            
            // If no render() was called, use the last expression result
            if (result == null || result == Scriptable.NOT_FOUND || result == Undefined.instance) {
                if (evalResult != null && evalResult != Undefined.instance && evalResult instanceof Scriptable) {
                    result = evalResult;
                } else {
                    throw new RuntimeException("No document returned. JSX should evaluate to a VNode object or call render(<Document ... />).");
                }
            }

            // 4. Convert to a detached Java object tree
            return toVNode(result);
        } finally {
            Context.exit();
        }
    }

    /**
     * Execute compiled JSX without data context (backward compatible)
     */
    public VNode run(String compiledJs) throws Exception {
        return run(compiledJs, null);
    }

    /**
     * Convert Java Map/List/primitives to Rhino JS object
     */
    private Object mapToJsValue(Context cx, Scriptable scope, Object obj) {
        if (obj == null) return null;
        if (obj instanceof String || obj instanceof Boolean || obj instanceof Number) {
            return obj;
        }
        
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Scriptable jsObj = cx.newObject(scope);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsObj.put(entry.getKey(), jsObj, mapToJsValue(cx, scope, entry.getValue()));
            }
            return jsObj;
        }
        
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            Object[] array = new Object[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = mapToJsValue(cx, scope, list.get(i));
            }
            return cx.newArray(scope, array);
        }
        
        return obj.toString();
    }

    private VNode toVNode(Object value) {
        if (!(value instanceof Scriptable)) {
            throw new RuntimeException("Expected VNode object, got: " + value);
        }
        
        Scriptable obj = (Scriptable) value;
        VNode node = new VNode();
        
        // Get type
        Object typeVal = obj.get("type", obj);
        if (typeVal != Scriptable.NOT_FOUND && typeVal != null) {
            node.setType(typeVal.toString());
        }

        // Get props
        Map<String, Object> props = new HashMap<>();
        Object propsVal = obj.get("props", obj);
        if (propsVal instanceof Scriptable) {
            Scriptable propsObj = (Scriptable) propsVal;
            Object[] propIds = propsObj.getIds();
            for (Object propId : propIds) {
                String key = propId.toString();
                Object v = propsObj.get(key, propsObj);
                props.put(key, toJavaAny(v));
            }
        }
        node.setProps(props);

        // Get children
        List<Object> children = new ArrayList<>();
        Object childrenVal = obj.get("children", obj);
        if (childrenVal instanceof NativeArray) {
            NativeArray childrenArr = (NativeArray) childrenVal;
            for (int i = 0; i < childrenArr.getLength(); i++) {
                Object c = childrenArr.get(i, childrenArr);
                if (c == Scriptable.NOT_FOUND || c == Undefined.instance) {
                    continue;
                }
                if (c instanceof String) {
                    children.add(c);
                } else if (c instanceof Number) {
                    children.add(String.valueOf(c));
                } else if (c instanceof Boolean) {
                    children.add(String.valueOf(c));
                } else if (c instanceof Scriptable) {
                    Scriptable scriptable = (Scriptable) c;
                    // Check if it's a VNode (has type, props, children)
                    if (scriptable.has("type", scriptable)) {
                        children.add(toVNode(c));
                    } else {
                        // Fallback: convert to string
                        children.add(c.toString());
                    }
                } else if (c != null) {
                    children.add(c.toString());
                }
            }
        }
        node.setChildren(children);

        return node;
    }

    private Object toJavaAny(Object v) {
        if (v == null || v == Undefined.instance || v == Scriptable.NOT_FOUND) {
            return null;
        }
        if (v instanceof String || v instanceof Boolean || v instanceof Number) {
            return v;
        }
        if (v instanceof NativeArray) {
            NativeArray arr = (NativeArray) v;
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < arr.getLength(); i++) {
                Object item = arr.get(i, arr);
                if (item != Scriptable.NOT_FOUND) {
                    list.add(toJavaAny(item));
                }
            }
            return list;
        }
        if (v instanceof Scriptable) {
            Scriptable obj = (Scriptable) v;
            Map<String, Object> map = new HashMap<>();
            Object[] ids = obj.getIds();
            for (Object id : ids) {
                String key = id.toString();
                Object val = obj.get(key, obj);
                if (val != Scriptable.NOT_FOUND) {
                    map.put(key, toJavaAny(val));
                }
            }
            return map;
        }
        return v.toString();
    }
}
