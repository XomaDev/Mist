package me.ekita.mist.utils;

/*
 * Note: The CsvParser class was adapted from
 * java/com/google/appinventor/components/runtime/util/CsvUtil.java, which in turn was
 * adapted from: java/com/google/devtools/ode/server/util/CsvParser.java, which in turn was
 * copied from: java/com/google/collaboration/tables/util/CsvParser.java
 */

import me.ekita.mist.runtime.structs.RList;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.regex.Pattern;

public class CsvParser implements Iterator<RList> {
    /**
     * Escaped quotes in quoted cells are doubled.
     */
    private final Pattern ESCAPED_QUOTE_PATTERN = Pattern.compile("\"\"");

    /**
     * Character buffer for cell parsing. The size limits the largest parsable
     * cell. Specifically, if an unquoted cell and its trailing delimiter exceed
     * this limit, the cell will be split at the limit. Moreover, a quoted large
     * cell will cause a syntax error as when we reach end-of-file without
     * reading a closing quote.
     */
    private final char[] buf = new char[10240];

    private final Reader in;

    /**
     * The beginning of the currently parsed cell in {@code buf}. Everything
     * before it is discarded during compaction. The beginning includes the
     * quote for a quoted cell.
     */
    private int pos;

    /**
     * The end of valid content in {@code buf}.
     */
    private int limit;

    /**
     * Indicates whether more content might be in the reader.
     */
    private boolean opened = true;

    /**
     * Length of a successfully parsed cell. For a quoted cell this includes the
     * closing quote. Set whenever parsing of a cell succeeds. The value should
     * be ignored when cell parsing fails, but is set to -1 to help debugging.
     */
    private int cellLength = -1;

    /**
     * Length of a successfully parsed cell including its trailing delimiter.
     * Set whenever parsing of a cell with trailing delimiter succeeds. The
     * value should be ignored when cell parsing fails, but is set to -1 to help
     * debugging.
     */
    private int delimitedCellLength = -1;

    /**
     * Last exception encountered. Saved here to properly implement {@code
     * Iterator}.
     */
    private String lastException;

    private long previouslyRead;

    public CsvParser(Reader in) {
      this.in = in;
    }

    public void skip(long charPosition) throws IOException {
      while (charPosition > 0) {
        int n = in.read(buf, 0, Math.min((int) charPosition, buf.length));
        if (n < 0) {
          break;
        }
        previouslyRead += n;
        charPosition -= n;
      }
    }

    public boolean hasNext() {
      if (limit == 0) {
        fill();
      }
      return (pos < limit || indexAfterCompactionAndFilling(pos) < limit) && lookingAtCell();
    }

    public RList next() {
      RList result = new RList();
      boolean trailingComma;
      boolean haveMoreData;
      do {
        // Invariant: pos < limit && lookingAtCell() from hasNext() or previous
        // iteration
        if (buf[pos] != '"') {
          // trim the string tokens we pull from the CSV entries, since it's common to include
          // leading an trailing spaces here
          result.add(new String(buf, pos, cellLength).trim());
        } else {
          String cell = new String(buf, pos + 1, cellLength - 2);
          result.add(ESCAPED_QUOTE_PATTERN.matcher(cell).replaceAll("\"").trim());
        }
        trailingComma = delimitedCellLength > 0 && buf[pos + delimitedCellLength - 1] == ',';
        pos += delimitedCellLength;
        delimitedCellLength = cellLength = -1;
        int oldLimit = limit;
        haveMoreData = pos < limit || indexAfterCompactionAndFilling(pos) < limit;
        // If the CSV data ends with a trailing comma, haveMoreData will be false even though there
        // is one (empty) cell remaining. The cell "appears" if a trailing carriage return is added
        // to the input CSV. The following code handles this edge case by checking that we have no
        // more data and if the last character in the buffer was a comma. If so, it appends the
        // empty cell that would otherwise be dropped.
        if (!haveMoreData && buf[oldLimit - 1] == ',') {
          result.add("");
        }
      } while (trailingComma && haveMoreData && lookingAtCell());
      return result;
    }

    public long getCharPosition() {
      return previouslyRead + pos;
    }

    /**
     * Compacts and fills the buffer. Returns the possibly shifted index for the
     * given index.
     */
    private int indexAfterCompactionAndFilling(int i) {
      if (pos > 0) {
        i = compact(i);
      }
      fill();
      return i;
    }

    /**
     * Moves the contents between {@code pos} and {@code limit} to the beginning
     * of {@code buf}. Returns the new position of the given index.
     */
    private int compact(int i) {
      int oldPos = pos;
      pos = 0;
      int toMove = limit - oldPos;
      if (toMove > 0) {
        System.arraycopy(buf, oldPos, buf, 0, toMove);
      }
      limit -= oldPos;
      previouslyRead += oldPos;
      return i - oldPos;
    }

    /**
     * Fills {@code buf} from the reader.
     */
    private void fill() {
      int toFill = buf.length - limit;
      while (opened && toFill > 0) {
        try {
          int n = in.read(buf, limit, toFill);
          if (n == -1) {
            opened = false;
          } else {
            limit += n;
            toFill -= n;
          }
        } catch (IOException e) {
          lastException = e.getClass().getSimpleName() + " " + e.getMessage();
          opened = false;
        }
      }
    }

    private boolean lookingAtCell() {
      return (buf[pos] == '"' ? findUnescapedEndQuote(pos + 1) : findUnquotedCellEnd(pos));
    }

    private boolean findUnescapedEndQuote(int i) {
      for (; i < limit || (i = indexAfterCompactionAndFilling(i)) < limit; i++) {
        if (buf[i] == '"') {
          i = checkedIndex(i + 1);
          if (i == limit || buf[i] != '"') {
            cellLength = i - pos;
            return findDelimOrEnd(i);
          }
        }
      }
      lastException = "Syntax Error. unclosed quoted cell";
      return false;
    }

    /**
     * Determines that we are looking at the end of a cell, tolerating some
     * whitespace. Called after consuming the end quote of a quoted cell.
     */
    private boolean findDelimOrEnd(int i) {
      for (; i < limit || (i = indexAfterCompactionAndFilling(i)) < limit; i++) {
        switch (buf[i]) {
          case ' ':
          case '\t':
            // whitespace after closing quote
            continue;
          case '\r':
            // In standard CSV \r\n terminates a cell. However, Macintosh uses
            // one \r instead of \n.
            int j = checkedIndex(i + 1);
            delimitedCellLength = (buf[j] == '\n' ? checkedIndex(j + 1) : j) - pos;
            return true;
          case ',':
          case '\n':
            delimitedCellLength = (checkedIndex(i + 1) - pos);
            return true;
          default:
            lastException = "Syntax Error: non-whitespace between closing quote and delimiter or end";
            return false;
        }
      }
      delimitedCellLength = (limit - pos);
      return true;
    }

    /**
     * Returns the given index, after trying to read its corresponding buffered
     * character. The resulting index will be shifted if compaction was
     * triggered.
     */
    private int checkedIndex(int i) {
      return i < limit ? i : indexAfterCompactionAndFilling(i);
    }

    private boolean findUnquotedCellEnd(int i) {
      for (; i < limit || (i = indexAfterCompactionAndFilling(i)) < limit; i++) {
        switch (buf[i]) {
          case ',':
          case '\n':
            cellLength = i - pos;
            delimitedCellLength = cellLength + 1;
            return true;
          case '\r':
            // In standard CSV \r\n terminates a cell. However, Macintosh uses
            // one \r instead of \n.
            cellLength = i - pos;
            int j = checkedIndex(i + 1);
            delimitedCellLength = (buf[j] == '\n' ? checkedIndex(j + 1) : j) - pos;
            return true;
          case '"':
            lastException = "Syntax Error: quote in unquoted cell";
            return false;
        }
      }
      delimitedCellLength = cellLength = (limit - pos);
      return true;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public String anyErrorMessage() {
      return lastException;
    }
  }