package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Chart component support
 * Verifies bar, pie, line, area, and column charts can be rendered
 */
public class ChartTest {

    private Compiler compiler;
    private JsRuntime runtime;
    private Renderer renderer;

    @BeforeEach
    public void setUp() {
        compiler = new Compiler();
        runtime = new JsRuntime();
        renderer = new Renderer();
    }

    @Test
    public void simpleBarChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Paragraph><Text bold={true} size={16}>Simple Bar Chart</Text></Paragraph>
                    <Chart 
                      type="bar"
                      title="Sales by Quarter"
                      width={500}
                      height={300}
                      data={[
                        { label: "Q1", value: 100 },
                        { label: "Q2", value: 150 },
                        { label: "Q3", value: 200 },
                        { label: "Q4", value: 180 }
                      ]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        assertEquals("document", vnode.getType());
        
        Path output = tempDir.resolve("simple-bar-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        assertTrue(output.toFile().length() > 0);
        
        // Verify chart exists in document
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFChart> charts = doc.getCharts();
            assertEquals(1, charts.size(), "Should have 1 chart");
        }
        
        System.out.println("Bar chart test passed: " + output);
    }

    @Test
    public void simplePieChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Paragraph><Text bold={true} size={16}>Pie Chart</Text></Paragraph>
                    <Chart 
                      type="pie"
                      title="Market Share"
                      width={400}
                      height={400}
                      data={[
                        { label: "Product A", value: 35 },
                        { label: "Product B", value: 25 },
                        { label: "Product C", value: 20 },
                        { label: "Product D", value: 20 }
                      ]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("pie-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            assertEquals(1, doc.getCharts().size(), "Should have 1 chart");
        }
        
        System.out.println("Pie chart test passed: " + output);
    }

    @Test
    public void lineChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Paragraph><Text bold={true} size={16}>Line Chart</Text></Paragraph>
                    <Chart 
                      type="line"
                      title="Monthly Trend"
                      width={600}
                      height={300}
                      data={[
                        { label: "Jan", value: 30 },
                        { label: "Feb", value: 45 },
                        { label: "Mar", value: 35 },
                        { label: "Apr", value: 50 },
                        { label: "May", value: 60 },
                        { label: "Jun", value: 55 }
                      ]}
                      colors={["#FF6384"]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("line-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Line chart test passed: " + output);
    }

    @Test
    public void areaChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Chart 
                      type="area"
                      title="Revenue Growth"
                      width={500}
                      height={300}
                      data={[
                        { label: "2020", value: 100 },
                        { label: "2021", value: 150 },
                        { label: "2022", value: 200 },
                        { label: "2023", value: 280 }
                      ]}
                      colors={["#36A2EB"]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("area-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Area chart test passed: " + output);
    }

    @Test
    public void columnChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Chart 
                      type="column"
                      title="Vertical Bars"
                      width={500}
                      height={300}
                      data={[
                        { label: "A", value: 40 },
                        { label: "B", value: 65 },
                        { label: "C", value: 55 },
                        { label: "D", value: 80 }
                      ]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("column-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Column chart test passed: " + output);
    }

    @Test
    public void multiSeriesBarChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Paragraph><Text bold={true} size={16}>Multi-Series Bar Chart</Text></Paragraph>
                    <Chart 
                      type="bar"
                      title="Sales Comparison"
                      width={600}
                      height={350}
                      categories={["Q1", "Q2", "Q3", "Q4"]}
                      series={[
                        { name: "2022", values: [100, 120, 140, 160] },
                        { name: "2023", values: [110, 150, 180, 200] }
                      ]}
                      colors={["#FF6384", "#36A2EB"]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("multi-series-bar.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Multi-series bar chart test passed: " + output);
    }

    @Test
    public void multiSeriesLineChart(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Chart 
                      type="line"
                      title="Product Performance"
                      width={600}
                      height={300}
                      categories={["Jan", "Feb", "Mar", "Apr", "May", "Jun"]}
                      series={[
                        { name: "Product A", values: [30, 40, 45, 50, 55, 60] },
                        { name: "Product B", values: [20, 35, 40, 45, 55, 70] },
                        { name: "Product C", values: [10, 25, 30, 40, 50, 55] }
                      ]}
                      colors={["#FF6384", "#36A2EB", "#FFCE56"]}
                      legendPosition="right"
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("multi-series-line.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Multi-series line chart test passed: " + output);
    }

    @Test
    public void chartWithCustomColors(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Chart 
                      type="bar"
                      title="Custom Colors"
                      width={500}
                      height={300}
                      data={[
                        { label: "Red", value: 30 },
                        { label: "Green", value: 50 },
                        { label: "Blue", value: 40 }
                      ]}
                      colors={["#E74C3C"]}
                      legend={false}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("custom-colors-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        System.out.println("Custom colors chart test passed: " + output);
    }

    @Test
    public void multipleChartsInDocument(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                <Document>
                  <Section>
                    <Paragraph><Text bold={true} size={20}>Sales Report</Text></Paragraph>
                    
                    <Paragraph><Text>Quarterly Sales:</Text></Paragraph>
                    <Chart 
                      type="bar"
                      title="Quarterly Revenue"
                      width={500}
                      height={250}
                      data={[
                        { label: "Q1", value: 100 },
                        { label: "Q2", value: 150 },
                        { label: "Q3", value: 200 },
                        { label: "Q4", value: 180 }
                      ]}
                    />
                    
                    <Paragraph><Text>Market Distribution:</Text></Paragraph>
                    <Chart 
                      type="pie"
                      title="Market Share"
                      width={400}
                      height={300}
                      data={[
                        { label: "Region A", value: 40 },
                        { label: "Region B", value: 30 },
                        { label: "Region C", value: 30 }
                      ]}
                    />
                    
                    <Paragraph><Text>Monthly Trend:</Text></Paragraph>
                    <Chart 
                      type="line"
                      title="Growth Trend"
                      width={500}
                      height={250}
                      data={[
                        { label: "Jan", value: 20 },
                        { label: "Feb", value: 35 },
                        { label: "Mar", value: 45 },
                        { label: "Apr", value: 60 }
                      ]}
                    />
                  </Section>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("multiple-charts.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFChart> charts = doc.getCharts();
            assertEquals(3, charts.size(), "Should have 3 charts");
        }
        
        System.out.println("Multiple charts test passed: " + output);
    }

    @Test
    public void chartWithDynamicData(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const salesData = [
                  { label: "January", value: 1200 },
                  { label: "February", value: 1500 },
                  { label: "March", value: 1800 },
                  { label: "April", value: 2100 },
                  { label: "May", value: 2400 }
                ];
                
                const ChartSection = ({ title, data, type }) => (
                  <Section>
                    <Paragraph><Text bold={true} size={16}>{title}</Text></Paragraph>
                    <Chart 
                      type={type}
                      title={title}
                      width={500}
                      height={300}
                      data={data}
                    />
                  </Section>
                );
                
                <Document>
                  <ChartSection title="Monthly Sales" data={salesData} type="bar" />
                  <ChartSection title="Sales Trend" data={salesData} type="line" />
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        Path output = tempDir.resolve("dynamic-chart.docx");
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(output.toFile().exists());
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            assertEquals(2, doc.getCharts().size(), "Should have 2 charts");
        }
        
        System.out.println("Dynamic chart test passed: " + output);
    }
}
