package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.LogAnalyzerLexer
import net.oneandone.loganalyzer.helpers.Symbol
import java.io.StringReader
import java.nio.charset.Charset
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun test2() {
        val filter = LogFilter()
        filter.learn("2018 sentence 1\nanother sentence\n2018-01-01 sentence 2\n")
        val res = filter.filter("2018 sentence 1\nanother sentenceX\n2018-01-01 sentence 2\n")
        assertEquals(1, res.size)
        assertEquals("another sentenceX\n", res[0]);

    }
}
