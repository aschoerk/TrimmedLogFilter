package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.helpers.Symbol
import org.apache.commons.codec.digest.DigestUtils
import java.io.StringReader
import java.security.MessageDigest

class LogFilter {
    private val md5gen = MessageDigest.getInstance("MD5")

    private val backinglines = mutableMapOf<Int,Line>()

    public val lines
        get() = backinglines


    class Line(val no: Int, val symbols: MutableList<Symbol>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Line

            if (no != other.no) return false

            return true
        }

        override fun hashCode(): Int {
            return no
        }

        override fun toString(): String {
            return "Line(no=$no, symbols=$symbols)"
        }

        val md5: List<Byte>
            get() {
                var res = mutableListOf<Byte>()
                symbols.forEach {
                    res.addAll(it.md5().asList())
                }
                return res
            }

    }




    fun learn(s: String) {

        val scanner = net.oneandone.loganalyzer.LogAnalyzerLexer(StringReader(s))

        var res: Symbol?
        var currentLine: Line? = null
        do {
            res = scanner.yylex();
            if (res == null) {
                break;
            }
            if (currentLine == null || currentLine.no != res.line) {
                currentLine = Line(res.line, mutableListOf(res))
                lines[currentLine.no] = currentLine
            } else {
                currentLine.symbols.add(res)
            }
        } while (true)


    }
}