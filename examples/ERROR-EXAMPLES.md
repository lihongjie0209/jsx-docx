# Error Handling Examples

This directory contains example JSX files that intentionally produce errors to demonstrate the improved error messages feature.

## Overview

jsx-docx now provides comprehensive error messages for three types of errors:

1. **JSX Syntax Errors** - Errors during JSX compilation
2. **Runtime Errors** - Errors during JavaScript execution
3. **Component Validation Errors** - Errors with component properties or structure

## Example Files

### 1. Syntax Error Example (`test-error-syntax.jsx`)

This file contains a missing closing tag to demonstrate syntax error messages.

**Run:**
```bash
java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-syntax.jsx
```

**Expected Output:**
```
JSX Syntax Error: Expected '</', got 'jsx text'
  at line 11, column 17

   9 | <Paragraph>
  10 |   <Text>This text is missing a closing tag
→ 11 | </Paragraph>
         ^

Common fixes:
  • Check for missing or extra closing tags
  • Ensure all JSX elements are properly closed
  • Verify that JSX expressions use valid JavaScript syntax
  • Make sure component names start with uppercase letters

Documentation: https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md
```

### 2. Runtime Error Example (`test-error-runtime.jsx`)

This file references an undefined variable to demonstrate runtime error messages.

**Run:**
```bash
java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-runtime.jsx
```

**Expected Output:**
```
JSX Runtime Error: undefinedVariable is not defined

💡 Suggested fix:
  Variable 'undefinedVariable' is not defined. 
  Check spelling, or pass it via --data option if it's external data.

Documentation: https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md
```

### 3. Component Validation Error Example (`test-error-component.jsx`)

This file uses `<Section>` as the root element instead of `<Document>`.

**Run:**
```bash
java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-component.jsx
```

**Expected Output:**
```
Component Validation Error in <Root>
  Error: Root element must be <Document>, but found <section>

💡 How to fix:
  • Document must be the root element
  • Contains Section or direct Paragraph children
  • Example: <Document><Section>...</Section></Document>

Component documentation: https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md#root
```

## Features

### JSX Syntax Errors
- ✅ Shows line and column number
- ✅ Displays code snippet with context (2 lines before, 1 line after)
- ✅ Points to exact error location with arrow indicator
- ✅ Provides common fix suggestions
- ✅ Links to documentation

### Runtime Errors
- ✅ Identifies the type of runtime error
- ✅ Provides context-specific fix suggestions
- ✅ Suggests using `--data` option for external data
- ✅ Links to documentation

### Component Validation Errors
- ✅ Identifies the component with the error
- ✅ Specifies which property is incorrect (if applicable)
- ✅ Shows expected vs. actual values
- ✅ Provides component-specific fix suggestions
- ✅ Links to component documentation

## Implementation

The error handling improvements are implemented through three custom exception classes:

- `JsxSyntaxException` - For compilation errors
- `JsxRuntimeException` - For execution errors  
- `ComponentValidationException` - For validation errors

These exceptions format error messages with:
- Clear error descriptions
- Contextual information (line numbers, code snippets)
- Actionable fix suggestions
- Documentation links

## Testing

Run the error handling tests:
```bash
mvn test -Dtest=ErrorHandlingTest
```

All tests should pass, verifying that:
- Syntax errors are properly caught and formatted
- Runtime errors include helpful suggestions
- Component errors specify the problem clearly
- Valid JSX still compiles and runs correctly
