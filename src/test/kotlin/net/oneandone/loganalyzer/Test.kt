package net.oneandone.loganalyzer

import net.oneandone.loganalyzer.LogAnalyzerLexer
import net.oneandone.loganalyzer.helpers.Symbol
import java.io.Reader
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

    class FillableReader : Reader() {

        var buffers = mutableListOf<StringReader>()

        fun addString(s: String) {
            buffers.add(StringReader(s))
        }
        /**
         * Closes the stream and releases any system resources associated with
         * it.  Once the stream has been closed, further read(), ready(),
         * mark(), reset(), or skip() invocations will throw an IOException.
         * Closing a previously closed stream has no effect.
         *
         * @exception  IOException  If an I/O error occurs
         */
        override fun close() {

        }

        /**
         * Reads characters into a portion of an array.  This method will block
         * until some input is available, an I/O error occurs, or the end of the
         * stream is reached.
         *
         * @param      cbuf  Destination buffer
         * @param      off   Offset at which to start storing characters
         * @param      len   Maximum number of characters to read
         *
         * @return     The number of characters read, or -1 if the end of the
         * stream has been reached
         *
         * @exception  IOException  If an I/O error occurs
         */
        override fun read(cbuf: CharArray?, off: Int, len: Int): Int {
            while (buffers.size > 0) {
                val res = buffers[0].read(cbuf, off, len)
                if (res == -1) {
                    buffers.removeAt(0)
                } else {
                    return res;
                }
            }
            return -1;
        }

    }

    @Test
    fun testPartCompile() {
        val reader = FillableReader()
        reader.addString("2018 sentence 1\nanother sentence\n  at com.x.x(aaa.java:100)\n  at com.x.y(aaa.java:101)\n")
        var myScanner = LogAnalyzerLexer(reader)
        var res: Symbol?;
        do {
            res = myScanner.yylex()
            if (res != null) {
                println("\n" + res.line + ": " + res.s.name + ": " + res.text )
            } else {
                val lastState = myScanner.yystate()
                if (lastState == 0)
                    break;
                reader.addString("  at com.x.x(aaa.java:100)\n263165612 32627\n  ")
                myScanner = LogAnalyzerLexer(reader)
                myScanner.yybegin(lastState)
                continue
            }
        } while (true);
    }


    @Test
    fun test2() {
        val filter = LogFilter()
        filter.learn("2018 sentence 1\nanother sentence\n2018-01-01 sentence 2\n")
        val res = filter.filter("2018 sentence 1\nanother sentenceX\n2018-01-01 sentence 2\n")
        assertEquals(1, res.size)
        assertEquals("another sentenceX", res[0]);

    }
}
