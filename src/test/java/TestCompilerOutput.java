import cn.lihongjie.jsxdocx.Compiler;

public class TestCompilerOutput {
    public static void main(String[] args) throws Exception {
        Compiler c = new Compiler();
        String jsx = "const items = [1,2,3]; items.map(x => <Text>{x}</Text>)";
        String compiled = c.compile(jsx);
        System.out.println("Compiled:");
        System.out.println(compiled);
    }
}
