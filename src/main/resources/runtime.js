// Runtime for JSX to Word (ES5-compatible for Rhino)
// Defines the environment for the compiled JSX to run in.

// 1. Define Component Types as constants so <Document /> works
// Capitalized names are treated as identifiers by JSX compiler.
var Document = 'document';
var Section = 'section';
var Paragraph = 'paragraph';
var Text = 'text';
var Heading = 'heading';
var PageBreak = 'pagebreak';
var Link = 'link';
var Table = 'table';
var Row = 'row';
var Cell = 'cell';
var Image = 'image';
var BulletedList = 'bulletedlist';
var NumberedList = 'numberedlist';
var ListItem = 'listitem';
var Header = 'header';
var Footer = 'footer';
var PageNumber = 'pagenumber';
var Br = 'br';
var Tab = 'tab';
var Styles = 'styles';
var Style = 'style';
var Toc = 'toc';
var Include = 'include';
var Chart = 'chart';
var Watermark = 'watermark';
var Comment = 'comment';
var Footnote = 'footnote';
var Endnote = 'endnote';
var Bookmark = 'bookmark';
var BookmarkRef = 'bookmarkref';

// Helper to assign properties (ES5 Object.assign polyfill)
function assignProps(target, source) {
    if (source == null) return target;
    for (var key in source) {
        if (source.hasOwnProperty(key)) {
            target[key] = source[key];
        }
    }
    return target;
}

// 2. JSX runtime implementations
// Classic: React.createElement(...)
var React = {
    createElement: function(type, props) {
        // Get children from arguments[2] onwards (rest parameters not available in ES5)
        var children = [];
        for (var i = 2; i < arguments.length; i++) {
            children.push(arguments[i]);
        }
        
        var flatChildren = [];
        function add(c) {
            if (Array.isArray(c)) {
                for (var j = 0; j < c.length; j++) {
                    add(c[j]);
                }
            } else if (c !== null && c !== undefined && c !== false && c !== true) {
                // Convert numbers to strings for children
                if (typeof c === 'number') {
                    flatChildren.push(String(c));
                } else {
                    flatChildren.push(c);
                }
            }
        }
        
        for (var k = 0; k < children.length; k++) {
            add(children[k]);
        }
        
        // Handle function components (custom components)
        if (typeof type === 'function') {
            var allProps = assignProps({}, props || {});
            allProps.children = flatChildren;
            var result = type(allProps);
            return result;
        }
        
        return { type: type, props: props || {}, children: flatChildren };
    },
    Fragment: '__Fragment__' // Use string instead of Symbol (not available in Rhino)
};

// Automatic: jsx / jsxs
function normalizeChildrenFromProps(props) {
    if (!props) return [];
    var c = props.children;
    if (c === undefined || c === null) return [];
    
    var flatChildren = [];
    function add(child) {
        if (Array.isArray(child)) {
            for (var i = 0; i < child.length; i++) {
                add(child[i]); // Recursively flatten
            }
        } else if (child !== null && child !== undefined && child !== false && child !== true) {
            flatChildren.push(child);
        }
    }
    
    if (Array.isArray(c)) {
        for (var i = 0; i < c.length; i++) {
            add(c[i]);
        }
    } else {
        add(c);
    }
    
    return flatChildren;
}

function jsx(type, props, key) {
    var normalizedChildren = normalizeChildrenFromProps(props);
    var rest = {};
    
    // Copy all properties except children (no destructuring in ES5)
    if (props) {
        for (var k in props) {
            if (props.hasOwnProperty(k) && k !== 'children') {
                rest[k] = props[k];
            }
        }
    }
    
    // Handle function components (custom components)
    if (typeof type === 'function') {
        var allProps = assignProps({}, rest);
        allProps.children = normalizedChildren;
        return type(allProps);
    }
    
    return { type: type, props: rest, children: normalizedChildren };
}

function jsxs(type, props, key) {
    return jsx(type, props, key);
}

// 3. Global render function to capture the output (optional)
var __RESULT__ = null;
function render(element) {
    __RESULT__ = element;
    return element; // Allow chaining or use as expression
}
