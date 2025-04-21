package me.ekita.mist.modules.definitions;

import me.ekita.mist.expr.Expr;
import me.ekita.mist.modules.ModFunction;
import me.ekita.mist.modules.ModPropSet;
import me.ekita.mist.modules.Module;
import me.ekita.mist.modules.ModPropGet;
import me.ekita.mist.runtime.Evaluator;
import me.ekita.mist.runtime.structs.RList;
import me.ekita.mist.runtime.structs.RNumber;
import me.ekita.mist.syntax.Token;

import java.awt.*;
import java.util.List;

import static me.ekita.mist.modules.ModuleHelper.asList;

public class ColorModule extends Module {
  public ColorModule() {
    final String[] colorsNames = new String[]{
        "black", "white", "red", "pink",
        "orange", "yellow", "green",
        "cyan", "blue", "magenta",
        "lightGray", "gray", "darkGray"};
    final int[] colors = new int[]{
        -16777216, -1, -65536,
        -20561, -14336, -256,
        -16711936, -16711681, -16776961,
        -65281, -4144960, -8355712,
        -12566464
    };
    for (int i = 0, l = colorsNames.length; i < l; i++) {
      final int rgb = colors[i];
      definePropGet(colorsNames[i], new ModPropGet() {
        @Override
        public Object get() {
          return rgb;
        }
      });
    }

    definePropSet("someProp", new ModPropSet() {
      @Override
      public void set(Object value) {
        System.out.println("[Debug] Color Got Set " + value);
      }
    });

    defineFunc("make", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        Expr listExpr = args.get(0);
        List<Object> values = asList(token, listExpr);
        if (values.size() < 3)
          return token.error("Expected at least three arguments (r, g, b)");

        int r = (int) ((RNumber) values.get(0)).longValue();
        int g = (int) ((RNumber) values.get(1)).longValue();
        int b = (int) ((RNumber) values.get(2)).longValue();
        int a = values.size() > 3 ? (int) values.get(3) : 255;

        return new RNumber((a << 24) | (r << 16) | (g << 8) | b);
      }
    });

    defineFunc("split", 1, new ModFunction() {
      @Override
      public Object call(Token token, Evaluator runtime, List<Expr> args) {
        int color = (int) runtime.numericExpr(args.get(0)).longValue();
        List<Object> values = new RList();
        values.add((color >> 24) & 0xFF);
        values.add((color >> 16) & 0xFF);
        values.add((color >> 8) & 0xFF);
        values.add(color & 0xFF);
        return values;
      }
    });
  }
}
