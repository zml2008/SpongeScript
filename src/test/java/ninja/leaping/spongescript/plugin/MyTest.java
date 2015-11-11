package ninja.leaping.spongescript.plugin;

import org.junit.Test;

import static org.junit.Assert.*;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MyTest {
    @Test
    public void exampleTest() throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        System.out.println(engine.eval("var x = 2;"));
        System.out.println(engine.eval("var q = {'key': 'a', 'something': 'b', 'val': x}"));
        System.out.println(engine.eval("q"));
        System.out.println(engine.getFactory().getMethodCallSyntax("String", "startsWith", "aoeu"));
    }
}
