package net.oneandone.loganalyzer.helpers;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Symbol {
    private final Sym s;
    private final int line;
    private final int column;
    private final String text;


    public static enum Sym {
        DATE, TIME, TIMESTAMP, GUID,
        LEVEL, SENTENCE, EXCEPTION, WORD,
        PATH,
        BIN_INTEGER_LITERAL,
        DEC_INTEGER_LITERAL,
        HEX_INTEGER_LITERAL
    }

    public Symbol(Sym s, int line, int column, String text) {
        this.s = s;
        this.line = line;
        this.column = column;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public int hash() {
        switch (s) {
            case PATH:
            case SENTENCE: return text.hashCode();
            default:
                return s.ordinal();
        }
    }

    @Override
    public String toString() {
        return "Symbol{" +
               "s=" + s +
               ", line=" + line +
               ", column=" + column +
                ", hash=" + hash() +
                ", text='" + text + '\'' +
               '}';
    }
}