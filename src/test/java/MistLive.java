import me.ekita.mist.CompletionHelper;
import me.ekita.mist.analysis.ParserX;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Token;

import java.util.List;
import java.util.Scanner;

public class MistLive {
  public static void main(String[] args) {
    final Evaluator evaluator = new Evaluator();
    final CompletionHelper.Callback callback = new CompletionHelper.Callback() {
      @Override
      public void onReady(List<Token> tokens) {
        Object result = evaluator.statements(new ParserX(tokens).parse());
        if (result != null) {
          System.out.println(result);
        }
      }

      @Override
      public void onLexError(String error) {
        System.err.println(error);
      }
    };
    CompletionHelper helper = new CompletionHelper();

    Scanner scanner = new Scanner(System.in);
    System.out.print("> ");
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      helper.addLine(callback, line);
      System.out.print("> ");
    }
  }
}
