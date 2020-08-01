package at.ssw.visualizer.texteditor.model;

import java.util.Iterator;

/**
 *
 * @author ChristianWimmer
 */
public class HoverParser implements Iterator<String> {

    private static String HOVER_START = "<@";
    private static String HOVER_SEP = "|@";
    private static String HOVER_END = ">@";
    private String text;
    private int curPos;
    private String curText;
    private String curHover;
    private boolean curNewLine;

    public static String firstLine(String text) {
        if (text == null) {
            return "";
        }
        HoverParser p = new HoverParser(text);
        StringBuilder result = new StringBuilder(text.length());
        while (p.hasNext()) {
            String part = p.next();
            if (p.isNewLine()) {
                break;
            }
            result.append(part);
        }
        return result.toString();
    }

    public HoverParser(String text) {
        this.text = text;
    }

    private void advance() {
        int lineStart = text.indexOf('\n', curPos);
        int nextStart = text.indexOf(HOVER_START, curPos);

        if (lineStart == curPos) {
            curText = "\n";
            curHover = null;
            curPos = lineStart + 1;
            curNewLine = true;
            while (curPos < text.length() && text.charAt(curPos) <= ' ') {
                curPos++;
            }
            return;
        }
        curNewLine = false;
        if (lineStart != -1 && (nextStart == -1 || lineStart < nextStart)) {
            curText = text.substring(curPos, lineStart);
            curHover = null;
            curPos = lineStart;
            return;
        }

        if (nextStart == curPos) {
            int nextSep = text.indexOf(HOVER_SEP, nextStart);
            if (nextSep != -1) {
                int nextEnd = text.indexOf(HOVER_END, nextSep);
                if (nextEnd != -1) {
                    curText = text.substring(nextStart + HOVER_START.length(), nextSep);
                    curHover = text.substring(nextSep + HOVER_SEP.length(), nextEnd);
                    while (curHover.endsWith("\n")) {
                        curHover = curHover.substring(0, curHover.length() - 1);
                    }
                    curPos = nextEnd + HOVER_END.length();
                    return;
                }
            }
        }

        if (nextStart == curPos) {
            // Incomplete hover sequence. Make sure we make progress by just advancing to the next chararter.
            nextStart++;
        }

        if (nextStart != -1) {
            curText = text.substring(curPos, nextStart);
            curHover = null;
            curPos = nextStart;
        } else if (curPos < text.length()) {
            curText = text.substring(curPos);
            curHover = null;
            curPos = text.length();
        } else {
            curText = null;
            curHover = null;
        }
    }

    public boolean hasNext() {
        return curPos < text.length();
    }

    public String next() {
        advance();
        return curText;
    }

    public String getHover() {
        return curHover;
    }

    public boolean isNewLine() {
        return curNewLine;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
