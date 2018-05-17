package siosio;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.execution.filters.ConsoleInputFilterProvider;
import com.intellij.execution.filters.InputFilter;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;

/**
 * @author aschoerk
 */
public class ConsoleInputFilterProviderImpl implements ConsoleInputFilterProvider {
    @NotNull
    @Override
    public InputFilter[] getDefaultFilters(@NotNull final Project project) {
        return new InputFilter[] {new InputFilter() {
            Project filteredProject = project;
            @Nullable
            @Override
            public List<Pair<String, ConsoleViewContentType>> applyFilter(@NotNull final String s, @NotNull final ConsoleViewContentType consoleViewContentType) {
                ArrayList<Pair<String, ConsoleViewContentType>> res = new ArrayList<Pair<String, ConsoleViewContentType>>();
                res.add(new Pair<String, ConsoleViewContentType>("test", ConsoleViewContentType.ERROR_OUTPUT));
                return res;
            }
        }};
    }
}
