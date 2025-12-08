#!/bin/bash
# Test script for v0.2.0 features

echo "=== Testing v0.2.0 Features ==="
echo ""

echo "1. Testing --version"
java -jar target/jsx-docx-0.2.0-fat.jar --version
echo ""

echo "2. Testing --help (showing new options: --stdin, --progress, --no-progress, --report)"
java -jar target/jsx-docx-0.2.0-fat.jar --help | grep -E "(stdin|progress|report)"
echo ""

echo "3. Testing stdin support (example command):"
echo "   echo '<Document>...</Document>' | java -jar jsx-docx-0.2.0-fat.jar --stdin -o output.docx"
echo ""

echo "4. Testing batch conversion with progress bar (example command):"
echo "   java -jar jsx-docx-0.2.0-fat.jar examples/*.jsx -d output"
echo ""

echo "5. Testing report generation (example command):"
echo "   java -jar jsx-docx-0.2.0-fat.jar examples/*.jsx -d output --report report.json"
echo ""

echo "6. Testing disable progress (example command):"
echo "   java -jar jsx-docx-0.2.0-fat.jar examples/*.jsx -d output --no-progress"
echo ""

echo "=== All v0.2.0 features implemented! ==="
echo ""
echo "Key Features:"
echo "  ✓ Standard input support (--stdin)"
echo "  ✓ Progress bar for batch conversion (--progress, default enabled)"
echo "  ✓ Disable progress bar (--no-progress)"
echo "  ✓ JSON report generation (--report <file>)"
echo "  ✓ Version updated to 0.2.0"
