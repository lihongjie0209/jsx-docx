# AI Coding Agent Instructions for jsx-docx

## Project Overview
**jsx-docx** converts JSX document definitions into DOCX files. It's a Java 17 Maven project using a three-stage pipeline: JSX → compiled JavaScript → VNode tree → DOCX rendering via Apache POI.

### Architecture
1. **Compiler** (`Compiler.java`): Transpiles JSX to JavaScript using SWC4J (Rust-based, supports modern syntax)
2. **JsRuntime** (`JsRuntime.java`): Executes compiled JS in GraalVM Polyglot context; requires `runtime.js` (React-like polyfill)
3. **Renderer** (`Renderer.java`): Converts VNode tree to XWPFDocument; handles all Word formatting via Apache POI XML APIs
4. **VNode** (`model/VNode.java`): Intermediate representation (type + props + children) bridging JS execution and Java rendering

**Data Flow**: JSX file → Compiler → JS string → JsRuntime context → VNode tree (detached from context) → Renderer → DOCX file

## Critical Developer Workflows

### Build & Package
```powershell
mvn clean compile      # Verify code compiles
mvn package           # Creates target/jsx-docx-1.0-SNAPSHOT-fat.jar (all dependencies included)
```

### Test Patterns
- **End-to-end tests** (`src/test/java/**/*Test.java`): Generate actual DOCX files to temp directories
- All tests use `@TempDir Path tempDir` (JUnit 5) for isolated output
- **Never** parse DOCX XML for exact values—POI API limitations mean formatting is in numbering definitions, not easily accessible
- Validation pattern: Generate → Count elements → Verify document structure, not exact formatting
- Example: `CustomIndentTest.java` validates document generation success + list item count (3 items per test)

### Run Examples
```powershell
# Single file
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test.jsx -o output.docx

# Batch conversion
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/*.jsx -d output --verbose
```

## Project-Specific Patterns & Conventions

### 1. VNode Rendering Switch Pattern
`Renderer.java` uses massive `switch(type)` statement (cases: document, section, paragraph, etc.). New component types require:
- Add case in `renderNode()` method
- Extract props: `node.getProps().get("propertyName")`
- Use `toInt(value, defaultValue)` helper for type-safe prop parsing
- Children handled via `renderChildren()` recursive call
- Ref: Lines 150–600 in `Renderer.java`

### 2. Props Extraction & Type Conversion
Standard pattern for component props (used in 40+ test cases):
```java
int indentLeft = toInt(node.getProps().get("indentLeft"), 420);  // int with default
String bulletChar = String.valueOf(node.getProps().get("bulletChar")); // string
boolean header = "true".equals(String.valueOf(node.getProps().get("header"))); // boolean
```
The `toInt()` helper safely handles null, String, and numeric types.

### 3. Twips Unit Conversion
Word indentation/sizing uses **twips** (1/20th of a point):
- `1 inch = 1440 twips`
- Common defaults: `420 twips ≈ 0.29"`, `360 twips = 0.25"`
- Formula for nested indents: `indentLeft + level × indentIncrement` (all in twips)
- Apache POI uses `BigInteger.valueOf()` for CTInd fields
- Ref: `CustomIndentTest.java`, `BulletedList` spec section

### 4. Apache POI XML Internals
Don't use XWPFDocument high-level APIs for advanced formatting—directly manipulate XML:
- Access: `para.getCTP()` → `getPPr()` → `getInd()` for paragraph indentation
- Numbering: Create `CTAbstractNum` → 9 levels with `CTLvl` objects → set `CTInd` per level
- Colors: Convert `#RRGGBB` → remove `#` → pass to POI methods expecting hex strings
- Example: `createBulletNumbering()` method (lines 843–863) shows numbering definition setup

### 5. JSX Runtime Behavior
**Key constraint**: Compiled code runs in isolated GraalVM context; VNode must be detached before context closes.
- `runtime.js` defines component type constants (`Document='document'`, etc.) and React-like API
- Supports classic JSX runtime: `React.createElement(type, props, ...children)`
- User code can use `render()` function **or** direct expression—both work
- Children array auto-flattens (maps produce arrays that are flattened into parent's children)
- Ref: `runtime.js` lines 1–55, `JsRuntime.toVNode()` method

### 6. Component Specification as Source of Truth
`docs/spec.md` defines all components, props, and behaviors. When implementing features:
1. **Update spec first** with new component/props definition
2. **Reference spec section** in code comments
3. **Test against spec requirements** (not implementation assumptions)
4. Spec includes indentation math, color format, unit conversions, error handling
- Examples: `BulletedList` indentation formula (lines 89–108), `<Image>` fit modes (lines 345–365)

## Integration Points & Dependencies

### External Libraries
- **SWC4J 1.8.0**: Transpiles JSX to ES5-compatible JavaScript; platform-specific native bindings (Windows x86_64 + macOS ARM64/x86_64 included in pom.xml)
- **GraalVM Polyglot 24.1.1**: JS execution; requires `/runtime.js` resource for component definitions
- **Apache POI 5.2.5**: DOCX generation; use XWPFDocument, XWPFNumbering, CTAbstractNum, CTLvl, CTInd for low-level control
- **Picocli 4.7.5**: CLI argument parsing (single/batch file, output dir, verbose flag)

### Resource Files
- `src/main/resources/runtime.js`: **Critical**—defines React polyfill + component type constants. Loaded in `JsRuntime.run()` before user code execution

### Build Tool
- **Maven 3.9+** with shade plugin: Creates fat JAR with all dependencies + native bindings
- `maven-compiler-plugin:3.13.0` (Java 17), `maven-surefire-plugin:3.2.5` (JUnit 5)

## Test Organization
- **Component tests** (40+ files): Each tests single component (e.g., `CustomBulletTest.java`, `NestedBulletListTest.java`)
- **Pattern**: JSX string → Compiler → JsRuntime → Renderer → assert document structure
- **Validation approach**: Avoid POI XML parsing for exact values; count elements, verify numIds match, check list item counts
- **Recent additions**: `CustomIndentTest.java` (5 tests, configurable bullet indentation), `NestedBulletListTest.java` (4 tests, multi-level lists)

## Key Conventions Differing from Common Practice
1. **No high-level POI API** for complex formatting—direct XML manipulation required for indentation, numbering
2. **VNode intermediate representation** instead of direct POI generation—enables JSX → VDOM pattern but requires context detachment before rendering
3. **Props extraction with defaults**—all component props read from `node.getProps()` with type coercion and defaults in single place
4. **Test-driven spec updates**—new features tested immediately via comprehensive examples before merging

## Development Workflow: Spec → Implementation → Tests → Examples

This project follows a **spec-driven development** pattern. Always work in this order:

### Phase 1: Update Specification (docs/spec.md)
Define the feature **before** implementing it:
- Add component definition or property under appropriate section
- Document all props with types and defaults (e.g., `indentLeft: number (default 420)`)
- Include behavior description and examples
- Document units/formats (e.g., twips, hex colors, percentages)
- Specify error handling and validation rules
- Example: `BulletedList` spec (lines 89–108) shows required detail level

### Phase 2: Implement in Renderer.java
Once spec is complete:
- Add/modify case in `switch(type)` statement in `renderNode()` method
- Extract props using pattern: `toInt(node.getProps().get("propName"), defaultValue)`
- Reference spec line numbers in code comments
- Use POI XML APIs directly for complex formatting (not high-level APIs)
- Validate against spec requirements, not assumptions

### Phase 3: Write Comprehensive Unit Tests
Create `src/test/java/**/*YourFeatureTest.java`:
- Use `@TempDir Path tempDir` pattern for isolated DOCX output
- Test default behavior, custom values, edge cases
- Validate document structure: element count, nested levels, numIds match
- **Don't** parse POI XML for exact formatting values (stored in numbering definitions)
- Pattern example: `CustomIndentTest.java` (5 tests covering all indent parameters)

### Phase 4: Create JSX Examples
Add demo file `examples/test-your-feature.jsx`:
- Show practical use cases and combinations
- Include comments explaining each section
- Generate DOCX with: `java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test-your-feature.jsx -o output.docx`
- Test visually in Word/WPS Office
- Example: `examples/test-custom-indent.jsx` demonstrates 10 indent configuration scenarios

### Validation Checklist
- [ ] Spec section written and detailed
- [ ] `mvn compile` succeeds (syntax correct)
- [ ] `mvn clean test` passes (all tests including new ones)
- [ ] Example JSX runs without errors
- [ ] Visual output matches spec behavior description
- [ ] No regressions: existing tests still pass
