package me.ekita.mist;

import me.ekita.mist.analysis.Parser;
import me.ekita.mist.expr.Statements;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.syntax.Lexer;
import me.ekita.mist.syntax.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Mist {
  public static void main(String[] args) {
    String filePath = "/var/home/kumaraswamy/IdeaProjects/Mist/examples/csv_texts.m";
    try (FileInputStream fis = new FileInputStream(filePath)) {
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      String content = new String(bytes);

      List<Token> tokens = new Lexer(content).tokens;
      System.out.println(tokens);
      Statements statements = new Parser(tokens).parse();

      long start = System.currentTimeMillis();
      new Evaluator().statements(statements);
      long end = System.currentTimeMillis();
      System.out.println(end - start);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
