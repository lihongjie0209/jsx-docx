# Feature Summary: Image Export for DOCX to JSX Conversion

## Overview

This feature enables exporting images to a specified folder when converting DOCX files to JSX, with images referenced by file path instead of base64 data URIs.

## Problem Solved

Previously, when converting DOCX files to JSX:
- Images were always embedded as base64 data URIs
- This resulted in very large JSX files
- Images couldn't be reused across multiple documents
- Harder to version control and manage images separately

## Solution

Added optional `imageExportDir` parameter that:
- Exports images to a specified folder
- References images by file path in generated JSX
- Maintains backward compatibility (base64 is still the default)

## Technical Implementation

### Core Changes

1. **DocxToJsx.java**
   - Added `imageExportDir` field and constructor parameter
   - Modified `convertImage()` method to support both modes:
     - Base64 embedding (default, when `imageExportDir` is null)
     - File export (when `imageExportDir` is provided)
   - Implemented automatic filename generation: `image-1.png`, `image-2.jpg`, etc.
   - Added `getExtensionFromMimeType()` helper for proper file extensions
   - Auto-creates export directory if it doesn't exist

2. **McpServer.java**
   - Added `imageExportDir` optional parameter to `docx_to_jsx` tool
   - Updated tool description and schema
   - Modified `handleDocxToJsx()` to pass parameter through

3. **DocxToJsxImageExportTest.java** (New)
   - 9 comprehensive tests covering all scenarios
   - Tests default behavior (backward compatibility)
   - Tests image export functionality
   - Tests edge cases and round-trip conversion

### API Examples

**Default Behavior (Base64):**
```java
// Images embedded as base64 (backward compatible)
String jsx = DocxToJsx.convert("document.docx");
// Result: <Image src="data:image/png;base64,iVBORw0KG..." />
```

**Export Images to Folder:**
```java
// Export images to folder
Path imageDir = Paths.get("images");
String jsx = DocxToJsx.convert("document.docx", imageDir);
// Result: <Image src="/path/to/images/image-1.png" />

// Convert to file with image export
DocxToJsx.convertToFile("in.docx", "out.jsx", imageDir);
```

**MCP Tool:**
```json
{
  "name": "docx_to_jsx",
  "arguments": {
    "docxPath": "/path/to/document.docx",
    "imageExportDir": "/path/to/images"
  }
}
```

## Benefits

1. **Smaller JSX Files**: File paths are much shorter than base64 data
2. **Easier Version Control**: Images tracked separately in git
3. **Reusable Images**: Can reuse exported images in other documents
4. **Better Organization**: Images in dedicated folder
5. **Flexible**: Can choose between embedding or exporting
6. **Backward Compatible**: Existing code continues to work

## Testing

### Test Coverage
- ✅ Default base64 behavior (backward compatibility)
- ✅ Image export to folder
- ✅ Auto-creation of export directory
- ✅ Multiple images with unique filenames
- ✅ Round-trip conversion (DOCX → JSX → DOCX)
- ✅ File extensions from MIME types
- ✅ convertToFile() with image export
- ✅ Backward compatibility verification

### Test Results
- **167 tests total**, 0 failures, 0 errors
- All new tests pass
- All existing tests pass (backward compatibility confirmed)
- No security vulnerabilities detected (CodeQL clean)

## Documentation

### Updated Files
1. **README.md**
   - Added "DOCX 转 JSX" section with examples
   - Included Java API usage

2. **docs/mcp.md**
   - Updated `docx_to_jsx` tool documentation
   - Added parameter descriptions and examples

3. **CHANGELOG.md**
   - Added feature entry with examples

4. **examples/docx-to-jsx-example.md** (New)
   - Comprehensive usage guide
   - Multiple use cases
   - Best practices

## Use Cases

### Use Case 1: Template Modification
```java
// Export template with separate images
DocxToJsx.convertToFile("template.docx", "template.jsx", Paths.get("images"));

// Modify JSX, regenerate with same images
// ... modify JSX ...
renderer.renderToDocx(vdom, "new-doc.docx", Paths.get("images"), null);
```

### Use Case 2: Batch Document Generation
```java
// Convert template once
Path images = Paths.get("shared-images");
String templateJsx = DocxToJsx.convert("template.docx", images);

// Generate multiple documents using same images
for (Map<String, Object> data : dataSets) {
    String jsCode = compiler.compile(templateJsx);
    VNode vdom = runtime.run(jsCode, data);
    renderer.renderToDocx(vdom, "doc-" + data.get("id") + ".docx", images, data);
}
```

### Use Case 3: Documentation Migration
```java
// Convert documentation with organized images
DocxToJsx.convertToFile(
    "old-docs.docx",
    "docs.jsx",
    Paths.get("doc-images")
);

// Now: version control JSX + images separately
// Optimize images independently
// Reuse images across multiple documents
```

## Image Naming Convention

Images are automatically named based on their order in the document:
- First image: `image-1.png` (or .jpg, .gif, etc.)
- Second image: `image-2.png`
- Third image: `image-3.png`

File extension determined by MIME type:
- `image/png` → `.png`
- `image/jpeg` → `.jpg`
- `image/gif` → `.gif`
- `image/bmp` → `.bmp`
- Other formats → appropriate extension

## Commits

1. **Initial plan** (1cd0211)
   - Outlined implementation approach

2. **Implement image export folder option** (6f0be90)
   - Core implementation in DocxToJsx.java
   - MCP server integration
   - Comprehensive test suite

3. **Update documentation** (00cec31)
   - README, MCP docs, CHANGELOG
   - Usage examples

4. **Fix code review issues** (364be0d)
   - Removed unnecessary path escaping
   - Deduplicated test data

## Security

- ✅ No security vulnerabilities detected (CodeQL)
- ✅ Proper path handling
- ✅ No SQL injection risks
- ✅ No XSS vulnerabilities
- ✅ Safe file operations with proper directory creation

## Backward Compatibility

✅ **100% Backward Compatible**
- Default behavior unchanged (base64 embedding)
- All existing tests pass
- Optional parameter (doesn't break existing code)
- No breaking API changes

## Future Enhancements

Potential improvements:
1. Support relative paths (currently absolute)
2. Configurable filename pattern
3. Image optimization options
4. Deduplication of identical images
5. Support for embedded fonts and other resources

## Conclusion

This feature successfully adds flexible image handling to DOCX-to-JSX conversion while maintaining full backward compatibility. The implementation is well-tested, documented, and production-ready.

**Status**: ✅ Complete and ready for merge
