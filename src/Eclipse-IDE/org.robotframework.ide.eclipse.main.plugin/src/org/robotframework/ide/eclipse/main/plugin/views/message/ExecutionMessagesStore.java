/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.views.message;

import org.eclipse.ui.services.IDisposable;

import com.google.common.base.Preconditions;

class ExecutionMessagesStore implements IDisposable {

    private final StringBuilder message = new StringBuilder();

    private boolean isOpen = true;
    private boolean isDirty = false;

    void append(final String msg) {
        // can't change store state when store is closed
        Preconditions.checkState(isOpen);

        message.append(msg);
        isDirty = true;
    }

    String getMessage() {
        return message.toString();
    }

    boolean isOpen() {
        return isOpen;
    }

    void close() {
        this.isOpen = false;
    }

    @Override
    public void dispose() {
        message.setLength(0);
    }

    boolean checkDirtyAndReset() {
        final boolean wasDirty = isDirty;
        isDirty = false;
        return wasDirty;
    }
}