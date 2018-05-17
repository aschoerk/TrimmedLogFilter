package net.oneandone.logfilter

import com.intellij.execution.filters.ConsoleInputFilterProvider
import com.intellij.execution.filters.InputFilter
import com.intellij.execution.ui.ConsoleViewContentType

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import net.oneandone.logfilter.LearningConsoleInputFilterProvider.ProjectConsoleInformation.Mode.*

/**
 * @author aschoerk
 */
class LearningConsoleInputFilterProvider : ConsoleInputFilterProvider {
    override fun getDefaultFilters(p0: Project): Array<InputFilter> {
        val existingInfo = consoleInformation.projectMap[p0.name]

        if (existingInfo == null) {
            consoleInformation.projectMap[p0.name] = ProjectConsoleInformation(p0.name)
        } else {
            existingInfo.mode = FILTERING
        }
        return arrayOf(LearningConsoleInputFilter(p0))
    }

    class LearningConsoleInputFilter constructor (val project: Project) : InputFilter {
        override fun applyFilter(line: String, type: ConsoleViewContentType): MutableList<Pair<String, ConsoleViewContentType>> {
            if (type != ConsoleViewContentType.SYSTEM_OUTPUT) {
                val projectConsoleInformation = consoleInformation.projectMap[project.name]
                if (projectConsoleInformation != null) {
                    if (projectConsoleInformation.mode == COLLECTING) {
                        projectConsoleInformation!!.content.append(line)
                    } else {

                    }
                }
            }
            return mutableListOf<Pair<String, ConsoleViewContentType>>(Pair(line, type));
        }


    }
    class ProjectConsoleInformation(val projectName: String) {
        enum class Mode {
            COLLECTING, FILTERING
        }
        val content = StringBuilder()
        val currentLine = StringBuilder()
        var mode = COLLECTING
    }

    companion object consoleInformation {
        val projectMap = mutableMapOf<String, ProjectConsoleInformation>()
    }

}


