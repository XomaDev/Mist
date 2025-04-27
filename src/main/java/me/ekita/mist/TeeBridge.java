package me.ekita.mist;

import me.ekita.mist.analysis.ParserX;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Lexer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;


public class TeeBridge {

  private static final Evaluator executor = new Evaluator();

  public static void main(String[] args) {
    provideCode(new MistJs() {
      @Override
      public void provideCode(String source) {
        provideMistCode(source);
      }
    });
    System.out.println("Hola Amigoo!!");
  }

  private static void provideMistCode(String code) {
    Object result = executor.statements(new ParserX(new Lexer(code).tokens).parse());
    mistOutput(String.valueOf(result));
  }

  @JSBody(params = { "result" }, script = "mistOutput(result);")
  public static native void mistOutput(String result);

  @JSBody(params = "mist", script = "main.mist = mist;")
  private static native void provideCode(MistJs input);
}

@SuppressWarnings("unused")
@JSFunctor
interface MistJs extends JSObject {
  void provideCode(String source);
}
