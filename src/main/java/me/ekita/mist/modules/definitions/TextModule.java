package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.ModMethod;
import me.ekita.mist.modules.Module;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RDictionary;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.util.*;
import java.util.regex.Pattern;

import static me.ekita.mist.modules.ModuleHelper.*;

public class TextModule extends Module {
  public TextModule() {
    defineFunc("isText", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        return args.get(0).accept(runtime) instanceof String;
      }
    });

    defineMethod("len", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new RNumber(asString(token, object).length());
      }
    });
    defineMethod("isEmpty", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asString(token, object).isEmpty();
      }
    });
    defineMethod("trim", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asString(token, object).trim();
      }
    });
    defineMethod("upcase", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asString(token, object).toUpperCase();
      }
    });
    defineMethod("downcase", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asString(token, object).toLowerCase();
      }
    });
    defineMethod("startsWith", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr pieceExpr = args.get(0);
        String piece = asString(pieceExpr.token, pieceExpr.accept(runtime));
        String text = asString(token, object);
        return text.startsWith(piece);
      }
    });
    defineMethod("contains", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr pieceExpr = args.get(0);
        String piece = asString(pieceExpr.token, pieceExpr.accept(runtime));
        String text = asString(token, object);
        return text.contains(piece);
      }
    });
    defineMethod("containsAny", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr piecesExpr = args.get(0);
        List<Object> pieces = asList(piecesExpr.token, piecesExpr.accept(runtime));
        String text = asString(token, object);

        for (Object piece : pieces)
          if (text.contains(piece.toString())) return true;
        return false;
      }
    });
    defineMethod("containsAll", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr piecesExpr = args.get(0);
        List<Object> pieces = asList(piecesExpr.token, piecesExpr.accept(runtime));
        String text = asString(token, object);

        for (Object piece : pieces)
          if (!text.contains(piece.toString())) return false;
        return true;
      }
    });
    defineMethod("split", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr delimExpr = args.get(0);
        String delimiter = asString(delimExpr.token, delimExpr.accept(runtime));
        String text = asString(token, object);
        return arrayToList(text.split(delimiter));
      }
    });
    defineMethod("splitAtFirst", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr delimExpr = args.get(0);
        String delimiter = asString(delimExpr.token, delimExpr.accept(runtime));
        String text = asString(token, object);

        int index = text.indexOf(delimiter);
        if (index == -1) return makeFromArray(text);
        String first = text.substring(0, index);
        String last = text.substring(index + delimiter.length());
        return arrayToList(new String[]{first, last});
      }
    });
    defineMethod("splitAtAny", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr piecesExpr = args.get(0);
        List<Object> pieces = asList(piecesExpr.token, piecesExpr.accept(runtime));
        String text = asString(token, object);

        StringBuilder combinedRegex = new StringBuilder();
        for (Object piece : pieces) {
          combinedRegex
              .append(Pattern.quote(String.valueOf(piece)))
              .append("|");
        }
        if (combinedRegex.length() > 0)
          combinedRegex.setLength(combinedRegex.length() - 1);
        return arrayToList(text.split(combinedRegex.toString()));
      }
    });
    defineMethod("splitAtFirstOfAny", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr piecesExpr = args.get(0);
        List<Object> pieces = asList(piecesExpr.token, piecesExpr.accept(runtime));
        String text = asString(token, object);

        int leastIndex = -1, pieceLength = -1;
        for (Object piece : pieces) {
          String asString = piece.toString();
          int index = text.indexOf(asString);
          if (index == -1) continue;

          if (leastIndex == -1 || index < leastIndex) {
            leastIndex = index;
            pieceLength = asString.length();
          }
        }
        if (leastIndex == -1) return makeFromArray(text);
        String first = text.substring(0, leastIndex);
        String last = text.substring(leastIndex + pieceLength);
        return arrayToList(new String[]{first, last});
      }
    });
    defineMethod("splitAtSpaces", 1, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return asString(token, object).split("\\s+");
      }
    });
    defineMethod("segment", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        int start = (int) runtime.numericExpr(args.get(0)).longValue() - 1;
        int length = (int) runtime.numericExpr(args.get(1)).longValue();
        String text = asString(token, object);
        return text.substring(start, start + length);
      }
    });
    defineMethod("replaceAll", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        Expr segmentExpr = args.get(0), replacementExpr = args.get(1);
        String segment = asString(segmentExpr.token, segmentExpr.accept(runtime));
        String replacement = asString(replacementExpr.token, replacementExpr.accept(runtime));
        String text = asString(token, object);
        return text.replaceAll(Pattern.quote(segment), replacement);
      }
    });
    defineMethod("reverse", 0, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        return new StringBuilder(asString(token, object)).reverse().toString();
      }
    });
    defineMethod("replaceFromDict", 2, new ModMethod() {
      @Override
      public Object call(Token token, Evaluator runtime, Object object, List<Expr> args) {
        String text = asString(token, object);
        Map<Object, Object> replacements = asMap(token, args.get(0).accept(runtime));
        boolean defaultOrder = runtime.boolExpr(args.get(1));

        List<String> keys = new ArrayList<>();
        for (Object key: replacements.keySet()) keys.add(String.valueOf(key));
        if (!defaultOrder) {
          // Longest string first!
          Collections.sort(keys, new Comparator<Object>() {
            @Override
            public int compare(Object a, Object b) {
              return Integer.compare(String.valueOf(b).length(), String.valueOf(a).length());
            }
          });
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0, l = text.length(); i < l; ) {
          boolean matched = false;
          for (String key: keys) {
            if (text.startsWith(key, i)) {
              result.append(replacements.get(key));
              i += key.length();
              matched = true;
              break;
            }
          }
          if (!matched) result.append(text.charAt(i++));
        }
        return result.toString();
      }
    });
  }


}
