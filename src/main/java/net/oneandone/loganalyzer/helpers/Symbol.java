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
        LEVEL, SENTENCE, WORD,
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

    byte[] ordinalAsByteArray(Sym s) {
        int val = s.ordinal();
        byte[] res = new byte[4];
        res[0] = (byte)(val & 0xFF);
        res[1] = (byte)(val >> 8 & 0xFF);
        res[2] = (byte)(val >> 16 & 0xFF);
        res[3] = (byte)(val >> 24 & 0xFF);
        return res;
    }

    public byte[] md5() {
        switch (s) {
            case PATH:
            case SENTENCE: return DigestUtils.md5(text);
            default:
                return ordinalAsByteArray(s);

        }
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