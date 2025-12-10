# DOCX to JSX Conversion Example

This example demonstrates the new image export functionality when converting DOCX files to JSX.

## Feature Overview

When converting a DOCX document to JSX, you can now:
- Export images to a specified folder instead of embedding them as base64
- Reference images by file path in the generated JSX
- Automatically generate unique filenames for images (image-1.png, image-2.jpg, etc.)

## Usage Examples

### Example 1: Default Behavior (Base64 Embedding)

```java
// Images are embedded as base64 data URIs (backward compatible)
String jsx = DocxToJsx.convert("document-with-images.docx");

// Generated JSX will contain:
// <Image src="data:image/png;base64,iVBORw0KGgoAAAANS..." width={200} height={150} />
```

### Example 2: Export Images to Folder

```java
import java.nio.file.Paths;

// Export images to a folder
Path imageExportDir = Paths.get("exported-images");
String jsx = DocxToJsx.convert("document-with-images.docx", imageExportDir);

// Generated JSX will contain:
// <Image src="/path/to/exported-images/image-1.png" width={200} height={150} />
// <Image src="/path/to/exported-images/image-2.jpg" width={300} height={200} />
```

### Example 3: Convert to File with Image Export

```java
// Convert and save JSX to file, exporting images
DocxToJsx.convertToFile(
    "template.docx",           // Input DOCX
    "template.jsx",            // Output JSX
    Paths.get("images")        // Image export directory
);
```

### Example 4: MCP Tool Usage

When using the MCP `docx_to_jsx` tool, you can specify the `imageExportDir` parameter:

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "docx_to_jsx",
    "arguments": {
      "docxPath": "/path/to/document.docx",
      "imageExportDir": "/path/to/export/images"
    }
  }
}
```

## Benefits

1. **Smaller JSX Files**: File paths are much shorter than base64-encoded image data
2. **Easier Version Control**: Separate image files are easier to track in git
3. **Reusable Images**: Exported images can be reused in other documents
4. **Better Organization**: Images are organized in a dedicated folder
5. **Flexible Paths**: Can use relative or absolute paths depending on your needs

## Image Naming Convention

Images are automatically named based on their order in the document:
- First image: `image-1.png` (or .jpg, .gif, etc.)
- Second image: `image-2.png`
- Third image: `image-3.png`
- And so on...

The file extension is determined by the image's MIME type:
- PNG images → `.png`
- JPEG images → `.jpg`
- GIF images → `.gif`
- BMP images → `.bmp`
- Other formats → appropriate extension

## Use Cases

### Use Case 1: Template Modification
```java
// 1. Export a corporate template to JSX with separate images
DocxToJsx.convertToFile(
    "corporate-template.docx",
    "template.jsx",
    Paths.get("template-images")
);

// 2. Modify the JSX (change text, update layout)
// 3. Generate new document using the same images

Compiler compiler = new Compiler();
JsRuntime runtime = new JsRuntime();
Renderer renderer = new Renderer();

String modifiedJsx = Files.readString(Path.of("template.jsx"));
String jsCode = compiler.compile(modifiedJsx);
VNode vdom = runtime.run(jsCode, null);
renderer.renderToDocx(vdom, "new-document.docx", Paths.get("template-images"), null);
```

### Use Case 2: Batch Document Generation
```java
// Convert template once, reuse images for multiple documents
Path imageDir = Paths.get("shared-images");
String templateJsx = DocxToJsx.convert("template.docx", imageDir);

// Generate multiple documents using the same template and images
for (Map<String, Object> data : dataSets) {
    String jsCode = compiler.compile(templateJsx);
    VNode vdom = runtime.run(jsCode, data);
    renderer.renderToDocx(vdom, "document-" + data.get("id") + ".docx", imageDir, data);
}
```

### Use Case 3: Documentation Migration
```java
// Convert existing documentation to JSX format
// Images are organized in a separate folder for easy management
DocxToJsx.convertToFile(
    "old-documentation.docx",
    "documentation.jsx",
    Paths.get("doc-images")
);

// Now you can:
// - Version control the JSX in git
// - Optimize images separately
// - Reuse images across multiple documents
// - Update text without re-embedding images
```

## Notes

- The export directory is created automatically if it doesn't exist
- Existing files with the same name are overwritten
- Image paths in JSX are absolute paths (full filesystem paths)
- For relative paths, you may need to adjust the paths manually after conversion
- Base64 embedding (default) is still recommended for simple documents or when portability is important
