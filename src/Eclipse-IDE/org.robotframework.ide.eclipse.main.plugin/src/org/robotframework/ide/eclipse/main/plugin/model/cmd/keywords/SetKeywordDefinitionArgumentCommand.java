/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model.cmd.keywords;

import java.util.ArrayList;
import java.util.List;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.presenter.update.IExecutablesTableModelUpdater;
import org.rf.ide.core.testdata.model.table.keywords.UserKeyword;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.robotframework.ide.eclipse.main.plugin.model.RobotDefinitionSetting;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModelEvents;
import org.robotframework.ide.eclipse.main.plugin.model.cmd.SetKeywordCallArgumentCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.EditorCommand;
import org.robotframework.services.event.RedEventBroker;

public class SetKeywordDefinitionArgumentCommand extends EditorCommand {

    private final RobotKeywordDefinition definition;

    private final int index;

    private final String value;

    public SetKeywordDefinitionArgumentCommand(final RobotKeywordDefinition definition, final int index,
            final String value) {
        this.definition = definition;
        this.index = index;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws CommandExecutionException {
        RobotDefinitionSetting setting = definition.getArgumentsSetting();

        final List<String> arguments = SetKeywordCallArgumentCommand.prepareArgumentsList(setting, value, index);
        final boolean areAllEmpty = areAllEmpty(arguments);

        if (setting == null && areAllEmpty) {
            // there is no setting and we have no arguments to set
            return;

        } else if (setting != null && areAllEmpty) {
            // there is a setting, but we don't have arguments
            definition.getChildren().remove(setting);
            definition.getLinkedElement()
                    .removeUnitSettings((AModelElement<UserKeyword>) setting.getLinkedElement());

            RedEventBroker.using(eventBroker)
                    .additionallyBinding(RobotModelEvents.ADDITIONAL_DATA)
                    .to(setting)
                    .send(RobotModelEvents.ROBOT_KEYWORD_CALL_REMOVED, definition);

        } else if (setting == null) {
            // there is no setting, but we have arguments to set
            setting = definition.createSetting(0, RobotTokenType.KEYWORD_SETTING_ARGUMENTS.getRepresentation().get(0),
                    new ArrayList<String>(), "");
            updateModel(setting, arguments);

            RedEventBroker.using(eventBroker)
                    .additionallyBinding(RobotModelEvents.ADDITIONAL_DATA)
                    .to(setting)
                    .send(RobotModelEvents.ROBOT_KEYWORD_CALL_ADDED, definition);

        } else if (!arguments.equals(setting.getArguments())) {
            // there is a setting and we have arguments which are different than current
            updateModel(setting, arguments);
            setting.resetStored();

            eventBroker.send(RobotModelEvents.ROBOT_KEYWORD_DEFINITION_ARGUMENT_CHANGE, definition);
        }
    }

    private boolean areAllEmpty(final List<String> arguments) {
        for (final String argument : arguments) {
            if (argument != null && !argument.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void updateModel(final RobotDefinitionSetting argumentsSetting, final List<String> arguments) {
        final IExecutablesTableModelUpdater<UserKeyword> updater = definition.getModelUpdater();
        if (value != null) {
            for (int i = arguments.size() - 1; i >= 0; i--) {
                updater.updateArgument(argumentsSetting.getLinkedElement(), i, arguments.get(i));
            }
        } else {
            updater.updateArgument(argumentsSetting.getLinkedElement(), index, value);
        }
    }
}
