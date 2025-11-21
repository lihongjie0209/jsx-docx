package cn.lihongjie.jsxdocx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VNode {
    private String type;
    private Map<String, Object> props = new HashMap<>();
    private List<Object> children = new ArrayList<>(); // elements are either String or VNode

    public VNode() {}

    public VNode(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }
}
