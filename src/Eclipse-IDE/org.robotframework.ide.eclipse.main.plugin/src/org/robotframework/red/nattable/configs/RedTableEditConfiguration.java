/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.red.nattable.configs;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.validate.DefaultDataValidator;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.robotframework.ide.eclipse.main.plugin.model.RobotElement;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.red.nattable.AddingElementLabelAccumulator;
import org.robotframework.red.nattable.NewElementsCreator;
import org.robotframework.red.nattable.edit.AlwaysDeactivatingCellEditor;
import org.robotframework.red.nattable.edit.RedTextCellEditor;


/**
 * @author Michal Anglart
 *
 */
public class RedTableEditConfiguration<T extends RobotElement> extends DefaultEditConfiguration {

    private final IEditableRule editableRule;

    private final NewElementsCreator<T> creator;

    private boolean wrapCellContent;

    public RedTableEditConfiguration(final RobotSuiteFile fileModel, final NewElementsCreator<T> creator,
            final boolean wrapCellContent) {
        this(creator, SuiteModelEditableRule.createEditableRule(fileModel), wrapCellContent);
    }
    
    public RedTableEditConfiguration(final NewElementsCreator<T> creator, final IEditableRule editableRule,
            final boolean wrapCellContent) {
        this.editableRule = editableRule;
        this.creator = creator;
        this.wrapCellContent = wrapCellContent;
    }

    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editableRule);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new AlwaysDeactivatingCellEditor(creator), DisplayMode.NORMAL,
                AddingElementLabelAccumulator.ELEMENT_ADDER_ROW_CONFIG_LABEL);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
                new AlwaysDeactivatingCellEditor(creator), DisplayMode.NORMAL,
                AddingElementLabelAccumulator.ELEMENT_ADDER_ROW_NESTED_CONFIG_LABEL);
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RedTextCellEditor(wrapCellContent),
                DisplayMode.NORMAL, GridRegion.BODY);
        configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, new DefaultDataValidator());
        configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
                DisplayMode.EDIT, GridRegion.BODY);
        configRegistry.registerConfigAttribute(EditConfigAttributes.ACTIVATE_EDITOR_ON_TRAVERSAL, Boolean.TRUE,
                DisplayMode.EDIT, GridRegion.BODY);
    }
}
