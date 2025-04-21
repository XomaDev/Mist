package me.ekita.mist;

import me.ekita.mist.analysis.ParserX;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

import java.util.List;

public class TeeBridge {

  private static final Evaluator executor = new Evaluator();

  private static final CompletionHelper.Callback completionCallback = new CompletionHelper.Callback() {
    @Override
    public void onReady(List<Token> tokens) {
      System.out.println(executor.statements(new ParserX(tokens).parse()));
    }

    @Override
    public void onLexError(String error) {
      System.out.println("Lex error: " + error);
    }
  };
  private static final CompletionHelper helper = new CompletionHelper();

  public static void main(String[] args) {
    provideUserInput(new MistJs() {
      @Override
      public void provideLine(String source) {
        pushNewLine(source);
      }
    });
  }

  private static void pushNewLine(String line) {
    helper.addLine(completionCallback, line);
  }

  @JSBody(params = "eia", script = "main.eia = eia;")
  private static native void provideUserInput(MistJs input);
}

@SuppressWarnings("unused")
@JSFunctor
interface MistJs extends JSObject {
  void provideLine(String source);
}
