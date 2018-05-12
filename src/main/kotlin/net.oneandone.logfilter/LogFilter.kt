package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.helpers.Symbol
import java.io.StringReader

class LogFilter {


    fun learn(s: String) {

        val scanner = net.oneandone.loganalyzer.LogAnalyzerLexer(StringReader(s))

        var res: Symbol?
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
        }
        val lines = HashSet<Line>()
        var currentLine: Line? = null
        do {
            res = scanner.yylex();
            if (res == null) {
                break;
            }
            if (currentLine == null || currentLine.no != res.line) {
                if (currentLine != null) {
                    lines.add(currentLine);
                }
                currentLine = Line(res.line, mutableListOf(res))
            } else {
                currentLine.symbols.add(res)
            }
        } while (true)


    }
}