package de.jplag.cli.picocli;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import picocli.CommandLine;

/**
 * Custom implementation for the help page, including the custom {@link ParameterLabelRenderer}.
 */
public class CustomHelp extends CommandLine.Help {
    private final IParamLabelRenderer paramLabelRenderer;

    /**
     * Created a custom help text.
     * @param command The {@link picocli.CommandLine.Model.CommandSpec} to build the help for
     * @param colorScheme The {@link picocli.CommandLine.Help.ColorScheme} for the help page
     */
    public CustomHelp(CommandLine.Model.CommandSpec command, ColorScheme colorScheme) {
        super(command, colorScheme);

        this.paramLabelRenderer = new ParameterLabelRenderer(super.parameterLabelRenderer());
    }

    @Override
    public IParamLabelRenderer parameterLabelRenderer() {
        return this.paramLabelRenderer;
    }

    @Override
    public String parameterList() {
        if (!this.isSubcommand()) { // Language parameters cannot be positional, so for subcommands they aren't printed. Otherwise, global positional
                                    // parameters would be printed for subcommands.
            return super.parameterList();
        } else {
            return "";
        }
    }

    @Override
    public String optionList(Layout layout, Comparator<CommandLine.Model.OptionSpec> optionSort, IParamLabelRenderer valueLabelRenderer) {
        List<CommandLine.Model.OptionSpec> visibleOptionsNotInGroups = filterOptions(listAllOptions());
        if (!this.isSubcommand()) { // Subcommands don't make use of groups in JPlag, so they are suppressed
            return optionListExcludingGroups(visibleOptionsNotInGroups, layout, optionSort, valueLabelRenderer) + optionListGroupSections();
        } else {
            return optionListExcludingGroups(visibleOptionsNotInGroups, layout, optionSort, valueLabelRenderer);
        }
    }

    private List<CommandLine.Model.OptionSpec> listAllOptions() {
        return this.commandSpec().options();
    }

    /**
     * Removes all options that should not be printed. If this is a subcommand global options are suppressed.
     * @param all The list of all options
     * @return The filtered list
     */
    private List<CommandLine.Model.OptionSpec> filterOptions(List<CommandLine.Model.OptionSpec> all) {
        List<CommandLine.Model.OptionSpec> result = new ArrayList<>(all);
        for (CommandLine.Model.ArgGroupSpec group : optionSectionGroups()) {
            result.removeAll(group.allOptionsNested());
        }
        for (Iterator<CommandLine.Model.OptionSpec> iter = result.iterator(); iter.hasNext();) {
            CommandLine.Model.OptionSpec spec = iter.next();
            if (spec.hidden() || (spec.userObject() instanceof Field && this.isSubcommand())) { // if the suerObject is a field, the parameter hasn't
                                                                                                // been created through the api and is assumed to be a
                                                                                                // global parameter.
                iter.remove();
            }
        }
        return result;
    }

    /**
     * Checks if the help text is printed for a subcommand. This works by checking the userObject from picocli. It will be
     * null for subcommands, since they are generated using the api.
     * @return true, if the help is printed for a subcommand
     */
    private boolean isSubcommand() {
        return this.commandSpec().userObject() == null;
    }
}
