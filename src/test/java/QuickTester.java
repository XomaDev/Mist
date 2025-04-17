import me.ekita.mist.analysis.ParserX;
import me.ekita.mist.expr.Statements;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Lexer;
import me.ekita.mist.syntax.Token;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class QuickTester {
  public static void main(String[] args) {
    String filePath = "/var/home/kumaraswamy/Downloads/Mist/temp_img.png";

    try (Socket socket = new Socket("localhost", 9090);
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      // Send the file path followed by newline
      writer.write(filePath);
      writer.newLine();
      writer.flush();

      // Read the response
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line).append("\n");
      }
      String code = builder.toString().trim();
      System.out.println(code);
      System.out.println();


      List<Token> tokens = new Lexer(code).tokens;
      System.out.println(tokens);
      Statements statements = new ParserX(tokens).parse();
      Object value = new Evaluator().statements(statements);
      if (value != null) {
        System.out.println(value);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
