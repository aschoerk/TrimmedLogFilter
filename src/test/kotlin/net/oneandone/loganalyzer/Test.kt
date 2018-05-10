package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.LogAnalyzerLexer
import net.oneandone.loganalyzer.helpers.Symbol
import org.junit.Test
import java.io.StringReader
import java.nio.charset.Charset

class Test {
    @Test
    fun test1() {
        val testres = this.javaClass.getResource("/test.testlog").readText(Charset.defaultCharset());
        // val myString = "10021111 a1257 1234567 some input)\n"
        val myScanner = LogAnalyzerLexer(StringReader(testres))
        var res: Symbol?;
        do {
            res = myScanner.yylex()
            if (res != null) {
                println("\n" + res.line + ": " + res.s.name + ": " + res.text )
            }
        } while (res != null);

    }
}
