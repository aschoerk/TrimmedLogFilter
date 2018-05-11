package net.oneandone.loganalyzer.helpers;

public class Symbol {
    private final Sym s;
    private final int line;
    private final int column;
    private final Object value;
    private final String text;

    public static enum Sym {
        DATE, TIME, TIMESTAMP, GUID,
        LEVEL, SENTENCE, WORD,
        PATH,
        BIN_INTEGER_LITERAL,
        DEC_INTEGER_LITERAL,
        HEX_INTEGER_LITERAL
    }

    public Symbol(Sym s, int line, int column, String text, Object value) {
        this.s = s;
        this.line = line;
        this.column = column;
        this.text = text;
        this.value = value;
    }

    public Symbol(Sym s, int line, int column, String text) {
        this(s,line,column, text, null);
    }

    public Sym getS() {
        return s;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Symbol{" +
               "s=" + s +
               ", line=" + line +
               ", column=" + column +
               ", value=" + value +
               ", text='" + text + '\'' +
               '}';
    }
}