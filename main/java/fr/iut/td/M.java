package fr.iut.td;

import java.io.Reader;
import java.io.StringReader;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class M {

  public static void main(String[] args) {
    try {
      String javaScriptExpression = "sayHello(name);";
      Reader javaScriptFile = new StringReader(
          "function sayHello(name) {\n"
              + "    println('Hello, '+name+'!');\n" + "}");

      ScriptEngineManager factory = new ScriptEngineManager();
      ScriptEngine engine = factory.getEngineByName("JavaScript");
      ScriptContext context = engine.getContext();
      context.setAttribute("name", "JavaScript", ScriptContext.ENGINE_SCOPE);

      engine.eval(javaScriptFile);
      engine.eval(javaScriptExpression);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
