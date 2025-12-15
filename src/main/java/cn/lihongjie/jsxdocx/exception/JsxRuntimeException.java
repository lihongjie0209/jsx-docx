package cn.lihongjie.jsxdocx.exception;

/**
 * Exception for runtime errors during JSX execution.
 * Provides fix suggestions and documentation links.
 */
public class JsxRuntimeException extends Exception {
    private final String suggestion;
    private final String documentationLink;

    public JsxRuntimeException(String message) {
        this(message, null, null);
    }

    public JsxRuntimeException(String message, String suggestion) {
        this(message, suggestion, null);
    }

    public JsxRuntimeException(String message, String suggestion, String documentationLink) {
        super(formatErrorMessage(message, suggestion, documentationLink));
        this.suggestion = suggestion;
        this.documentationLink = documentationLink != null ? documentationLink : 
            "https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md";
    }

    private static String formatErrorMessage(String message, String suggestion, String documentationLink) {
        StringBuilder sb = new StringBuilder();
        sb.append("JSX Runtime Error: ").append(message).append("\n");
        
        if (suggestion != null && !suggestion.isEmpty()) {
            sb.append("\n💡 Suggested fix:\n");
            sb.append("  ").append(suggestion).append("\n");
        } else {
            // Provide generic suggestions based on common error patterns
            sb.append("\n💡 Common fixes:\n");
            if (message.contains("undefined")) {
                sb.append("  • Check that all variables are defined before use\n");
                sb.append("  • Verify data context properties are correctly passed\n");
            } else if (message.contains("null")) {
                sb.append("  • Ensure objects are initialized before accessing properties\n");
                sb.append("  • Add null checks for optional data\n");
            } else if (message.contains("is not a function")) {
                sb.append("  • Check that function names are spelled correctly\n");
                sb.append("  • Verify the function is defined in scope\n");
            } else {
                sb.append("  • Review the JavaScript logic in your JSX file\n");
                sb.append("  • Check console output for additional error details\n");
                sb.append("  • Verify all referenced variables and functions exist\n");
            }
        }
        
        String docLink = documentationLink != null ? documentationLink : 
            "https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md";
        sb.append("\nDocumentation: ").append(docLink);
        
        return sb.toString();
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getDocumentationLink() {
        return documentationLink;
    }
}
