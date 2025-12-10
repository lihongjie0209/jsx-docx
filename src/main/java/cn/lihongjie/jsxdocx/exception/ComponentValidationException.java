package cn.lihongjie.jsxdocx.exception;

/**
 * Exception for component validation errors.
 * Specifies which component and which property is incorrect.
 */
public class ComponentValidationException extends Exception {
    private final String componentName;
    private final String propertyName;
    private final String expectedValue;
    private final String actualValue;

    public ComponentValidationException(String componentName, String message) {
        this(componentName, null, message, null, null);
    }

    public ComponentValidationException(String componentName, String propertyName, String message) {
        this(componentName, propertyName, message, null, null);
    }

    public ComponentValidationException(String componentName, String propertyName, 
                                       String message, String expectedValue, String actualValue) {
        super(formatErrorMessage(componentName, propertyName, message, expectedValue, actualValue));
        this.componentName = componentName;
        this.propertyName = propertyName;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }

    private static String formatErrorMessage(String componentName, String propertyName, 
                                            String message, String expectedValue, String actualValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("Component Validation Error in <").append(componentName).append(">\n");
        
        if (propertyName != null) {
            sb.append("  Property: '").append(propertyName).append("'\n");
        }
        
        sb.append("  Error: ").append(message).append("\n");
        
        if (actualValue != null) {
            sb.append("  Received: ").append(actualValue).append("\n");
        }
        
        if (expectedValue != null) {
            sb.append("  Expected: ").append(expectedValue).append("\n");
        }
        
        sb.append("\n💡 How to fix:\n");
        sb.append(getComponentSpecificSuggestion(componentName, propertyName)).append("\n");
        
        sb.append("\nComponent documentation: ");
        sb.append("https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md#");
        sb.append(componentName.toLowerCase());
        
        return sb.toString();
    }

    private static String getComponentSpecificSuggestion(String componentName, String propertyName) {
        // Provide specific suggestions based on component and property
        if (propertyName != null) {
            switch (propertyName) {
                case "path":
                    return "  • Ensure the 'path' property points to an existing file\n" +
                           "  • Use relative paths from the current JSX file location\n" +
                           "  • Check file permissions and accessibility";
                case "src":
                    return "  • Verify the image file path is correct\n" +
                           "  • Support formats: PNG, JPG, JPEG, GIF\n" +
                           "  • Use base64 data URI or file path";
                case "width":
                case "height":
                    return "  • Use numeric values for dimensions (in pixels)\n" +
                           "  • Consider using maxWidth/maxHeight for responsive sizing";
                case "color":
                    return "  • Use hex color format: #RRGGBB (e.g., #FF0000)\n" +
                           "  • Ensure the # symbol is included\n" +
                           "  • Only use 6-digit hex codes";
                case "fontSize":
                    return "  • Use numeric values for font size (in half-points)\n" +
                           "  • Common sizes: 16 (8pt), 22 (11pt), 24 (12pt)";
                case "styleId":
                    return "  • Ensure the style ID is defined in <Styles> section\n" +
                           "  • Built-in styles: Normal, Heading1, Heading2, etc.";
                default:
                    break;
            }
        }
        
        // Component-specific suggestions
        switch (componentName.toLowerCase()) {
            case "document":
                return "  • Document must be the root element\n" +
                       "  • Contains Section or direct Paragraph children\n" +
                       "  • Example: <Document><Section>...</Section></Document>";
            case "section":
                return "  • Section must be inside Document\n" +
                       "  • Contains Paragraph, Table, Image, etc.\n" +
                       "  • Use pageSize, margins, orientation properties";
            case "paragraph":
                return "  • Paragraph contains Text, Link, Image, etc.\n" +
                       "  • Use alignment, spacing, indent properties\n" +
                       "  • Example: <Paragraph alignment=\"center\">text</Paragraph>";
            case "text":
                return "  • Text must be inside Paragraph or similar container\n" +
                       "  • Use bold, italic, fontSize, color properties\n" +
                       "  • Example: <Text bold fontSize={24}>content</Text>";
            case "table":
                return "  • Table contains Row elements\n" +
                       "  • Row contains Cell elements\n" +
                       "  • Use borders, width, layout properties";
            case "image":
                return "  • Image requires 'src' property (path or base64)\n" +
                       "  • Supported formats: PNG, JPG, JPEG, GIF\n" +
                       "  • Use width/height or maxWidth/maxHeight";
            case "include":
                return "  • Include requires 'path' property\n" +
                       "  • Path is relative to current JSX file\n" +
                       "  • Avoid circular includes";
            default:
                return "  • Check the component name spelling\n" +
                       "  • Verify all required properties are provided\n" +
                       "  • Review property types and values\n" +
                       "  • Consult the documentation for valid properties";
        }
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }
}
