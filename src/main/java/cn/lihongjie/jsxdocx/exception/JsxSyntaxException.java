package cn.lihongjie.jsxdocx.exception;

/**
 * Exception for JSX syntax errors during compilation.
 * Includes line number, column number, and code snippet context.
 */
public class JsxSyntaxException extends Exception {
    private final int line;
    private final int column;
    private final String sourceCode;
    private final String errorMessage;

    public JsxSyntaxException(String errorMessage, int line, int column, String sourceCode) {
        super(formatErrorMessage(errorMessage, line, column, sourceCode));
        this.errorMessage = errorMessage;
        this.line = line;
        this.column = column;
        this.sourceCode = sourceCode;
    }

    public JsxSyntaxException(String errorMessage, String sourceCode) {
        this(errorMessage, -1, -1, sourceCode);
    }

    private static String formatErrorMessage(String errorMessage, int line, int column, String sourceCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("JSX Syntax Error: ").append(errorMessage).append("\n");
        
        if (line > 0) {
            sb.append("  at line ").append(line);
            if (column > 0) {
                sb.append(", column ").append(column);
            }
            sb.append("\n");
            
            // Show code snippet with context
            if (sourceCode != null && !sourceCode.isEmpty()) {
                String[] lines = sourceCode.split("\n");
                int startLine = Math.max(0, line - 3);
                int endLine = Math.min(lines.length - 1, line + 1);
                
                sb.append("\n");
                for (int i = startLine; i <= endLine; i++) {
                    boolean isErrorLine = (i == line - 1);
                    sb.append(isErrorLine ? "→ " : "  ");
                    sb.append(String.format("%4d | ", i + 1));
                    sb.append(lines[i]).append("\n");
                    
                    // Show pointer to column if available
                    if (isErrorLine && column > 0) {
                        sb.append("       ");
                        for (int j = 0; j < column - 1; j++) {
                            sb.append(" ");
                        }
                        sb.append("^\n");
                    }
                }
                sb.append("\n");
            }
        }
        
        sb.append("\nCommon fixes:\n");
        sb.append("  • Check for missing or extra closing tags\n");
        sb.append("  • Ensure all JSX elements are properly closed\n");
        sb.append("  • Verify that JSX expressions use valid JavaScript syntax\n");
        sb.append("  • Make sure component names start with uppercase letters\n");
        sb.append("\nDocumentation: https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md");
        
        return sb.toString();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
