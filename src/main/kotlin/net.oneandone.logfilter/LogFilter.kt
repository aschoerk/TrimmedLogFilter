package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.helpers.Symbol
import org.apache.commons.codec.digest.DigestUtils
import java.io.StringReader
import java.security.MessageDigest

class LogFilter {
    private val backinglines = mutableMapOf<Int,Line>()
    private val linesByHash = mutableMapOf<Int, MutableList<Line>>()

    public val lines
        get() = backinglines


    class Line(val no: Int, val symbols: MutableList<Symbol>) {

        override fun toString(): String {
            return "Line(no=$no, symbols=$symbols)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Line

            if (no != other.no) return false
            if (symbols != other.symbols) return false

            return true
        }

        override fun hashCode(): Int {
            var result = no
            result = 31 * result + symbols.hashCode()
            return result
        }

        val hash: Int
            get() {
                var result = 0
                symbols.forEach {
                    result = 31 * result + it.hash()
                }
                return result
            }



    }


    fun addLine(l: Line?) {
        if (l != null) {
            lines[l.no] = l
            if (!linesByHash.containsKey(l.hash)) {
                linesByHash[l.hash] = mutableListOf<Line>()
            }
            val list = linesByHash[l.hash]!!
            list.add(l);
        }
    }


    fun learn(s: String) {
        return handle(s, { it, scanner -> addLine(it) } )
    }

    fun handle(s: String, f: (Line?, LogAnalyzerLexer) -> Unit) {

        val scanner = net.oneandone.loganalyzer.LogAnalyzerLexer(StringReader(s))

        var res: Symbol?
        var currentLine: Line? = null
        do {
            res = scanner.yylex();
            if (res == null) {
                f(currentLine, scanner)
                break;
                }
            if (currentLine == null || currentLine.no != res.line) {
                f(currentLine, scanner)
                currentLine = Line(res.line, mutableListOf(res))
            } else {
                currentLine.symbols.add(res)
            }
        } while (true)

    }

    fun filter(s: String): MutableList<String> {
        val result = mutableListOf<String>()
        handle(s, { it, scanner ->
            if (it != null) {
                if (linesByHash.containsKey(it.hash)) {
                    println("filtered: ${scanner.lastLine}")
                } else {
                    result.add(scanner.lastLine)
                }
            }
        })
        return result;
    }
}