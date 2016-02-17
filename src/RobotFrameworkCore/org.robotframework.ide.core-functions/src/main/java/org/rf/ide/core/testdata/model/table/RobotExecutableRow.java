/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.table.exec.descs.ExecutableRowDescriptorBuilder;
import org.rf.ide.core.testdata.model.table.exec.descs.IExecutableRowDescriptor;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.rf.ide.core.testdata.text.read.separators.TokenSeparatorBuilder.FileFormat;

public class RobotExecutableRow<T> extends AModelElement<T> {

    private final static Pattern TSV_COMMENT = Pattern.compile("(\\s)*\"(\\s)*[#].*\"(\\s)*$");

    private RobotToken action;

    private final List<RobotToken> arguments = new ArrayList<>();

    private final List<RobotToken> comments = new ArrayList<>();

    public RobotExecutableRow() {
        this.action = new RobotToken();
    }

    public RobotToken getAction() {
        return action;
    }

    public void setAction(final RobotToken action) {
        this.action = action;
    }

    public List<RobotToken> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void addArgument(final RobotToken argument) {
        arguments.add(argument);
    }

    public List<RobotToken> getComment() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(final RobotToken commentWord) {
        this.comments.add(commentWord);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public RobotToken getDeclaration() {
        return action;
    }

    @Override
    public ModelType getModelType() {
        ModelType type = ModelType.UNKNOWN;

        final List<IRobotTokenType> types = getAction().getTypes();
        if (types.contains(RobotTokenType.TEST_CASE_ACTION_NAME)) {
            type = ModelType.TEST_CASE_EXECUTABLE_ROW;
        } else if (types.contains(RobotTokenType.KEYWORD_ACTION_NAME)) {
            type = ModelType.USER_KEYWORD_EXECUTABLE_ROW;
        }

        return type;
    }

    @Override
    public FilePosition getBeginPosition() {
        return getAction().getFilePosition();
    }

    @Override
    public List<RobotToken> getElementTokens() {
        final List<RobotToken> tokens = new ArrayList<>();
        tokens.add(getAction());
        tokens.addAll(getArguments());
        tokens.addAll(getComment());

        return tokens;
    }

    public boolean isExecutable() {
        boolean result = false;
        if (action != null && !action.getFilePosition().isNotSet()) {
            if (getParent() instanceof IExecutableStepsHolder) {
                @SuppressWarnings("unchecked")
                IExecutableStepsHolder<AModelElement<? extends ARobotSectionTable>> parent = (IExecutableStepsHolder<AModelElement<? extends ARobotSectionTable>>) getParent();
                FileFormat fileFormat = parent.getHolder().getParent().getParent().getParent().getFileFormat();

                if (!action.getTypes().contains(RobotTokenType.START_HASH_COMMENT)) {
                    String raw = action.getRaw().trim();
                    List<RobotToken> elementTokens = getElementTokens();
                    if (raw.equals("\\")) {
                        if (elementTokens.size() > 1) {
                            if (!elementTokens.get(1).getTypes().contains(RobotTokenType.START_HASH_COMMENT)) {
                                result = true;
                            }
                        }
                    } else if ("".equals(raw)) {
                        if (fileFormat == FileFormat.TSV) {
                            if (elementTokens.size() > 1) {
                                if (!elementTokens.get(1).getTypes().contains(RobotTokenType.START_HASH_COMMENT)) {
                                    result = true;
                                }
                            }
                        } else {
                            result = true;
                        }
                    } else {
                        result = true;
                    }
                }
            } else {
                result = !action.getTypes().contains(RobotTokenType.START_HASH_COMMENT);
            }
        }

        return result;
    }

    public static boolean isTsvComment(final String raw, final FileFormat format) {
        return (format == FileFormat.TSV && TSV_COMMENT.matcher(raw).matches());
    }

    public IExecutableRowDescriptor<T> buildLineDescription() {
        return new ExecutableRowDescriptorBuilder().buildLineDescriptor(this);
    }
}
