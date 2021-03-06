package siosio

import com.intellij.execution.filters.*
import com.intellij.execution.filters.Filter.Result
import com.intellij.execution.impl.ConsoleInputListener
import com.intellij.ide.browsers.*

class ConsoleLinkFilter : Filter {

  override fun applyFilter(textLine: String, endPoint: Int): Filter.Result? {
    val startPoint = endPoint - textLine.length
    val result = REPLACE_PATTERNS.map {
      it.findAll(textLine).map {
        it.groups[1]?.let {
          Result(
              startPoint + it.range.first,
              startPoint + it.range.last + 1,
              OpenUrlHyperlinkInfo(it.value))
        }
      }.toList()
    }.flatten()
    ConsoleInputListener {  }
    /* if (result.size == 0) {
      val tmp = listOf(Result(startPoint, if (endPoint > startPoint+10) endPoint - 10 else startPoint, null));
      return Result(tmp);
    }*/
    return Result(result)
  }

  companion object {
    private const val URI_PATTERN = "((https?://|file:/{1,3}|/)[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#\\[\\]]+)"
    private val REPLACE_PATTERNS = arrayOf(
        Regex("^" + URI_PATTERN),
        Regex("\\s" + URI_PATTERN)
    )
  }
}
