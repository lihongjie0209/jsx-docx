// Runtime for JSX to Word
// Defines the environment for the compiled JSX to run in.

// 1. Define Component Types as constants so <Document /> works
// Capitalized names are treated as identifiers by JSX compiler.
const Document = 'document';
const Section = 'section';
const Paragraph = 'paragraph';
const Text = 'text';
const Heading = 'heading';
const PageBreak = 'pagebreak';
const Link = 'link';
const Table = 'table';
const Row = 'row';
const Cell = 'cell';
const Image = 'image';
const BulletedList = 'bulletedlist';
const NumberedList = 'numberedlist';
const ListItem = 'listitem';
const Header = 'header';
const Footer = 'footer';
const PageNumber = 'pagenumber';
const Br = 'br';
const Tab = 'tab';
const Styles = 'styles';
const Style = 'style';
const Toc = 'toc';
const Include = 'include';

// 2. JSX runtime implementations
// Classic: React.createElement(...)
const React = {
    createElement: function(type, props, ...children) {
        const flatChildren = [];
        const add = (c) => {
            if (Array.isArray(c)) c.forEach(add);
            else if (c !== null && c !== undefined && c !== false && c !== true) flatChildren.push(c);
        };
        children.forEach(add);
        return { type: type, props: props || {}, children: flatChildren };
    },
    Fragment: Symbol('Fragment')
};

// Automatic: jsx / jsxs
function normalizeChildrenFromProps(props) {
    if (!props) return [];
    const c = props.children;
    if (c === undefined || c === null) return [];
    
    const flatChildren = [];
    const add = (child) => {
        if (Array.isArray(child)) {
            child.forEach(add); // Recursively flatten
        } else if (child !== null && child !== undefined && child !== false && child !== true) {
            flatChildren.push(child);
        }
    };
    
    if (Array.isArray(c)) {
        c.forEach(add);
    } else {
        add(c);
    }
    
    return flatChildren;
}

function jsx(type, props, key) {
    const { children, ...rest } = props || {};
    return { type, props: rest, children: normalizeChildrenFromProps(props) };
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

// 4. Export for safety (though we read __RESULT__ directly)
globalThis.React = React; // for classic runtime and backward compatibility
globalThis.jsx = jsx;     // for automatic runtime single child
globalThis.jsxs = jsxs;   // for automatic runtime multiple children
globalThis.Fragment = React.Fragment; // alias for fragment factory when needed
globalThis.render = render;
