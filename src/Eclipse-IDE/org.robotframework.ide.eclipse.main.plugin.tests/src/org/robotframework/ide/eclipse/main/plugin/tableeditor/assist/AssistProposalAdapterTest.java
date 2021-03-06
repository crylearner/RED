/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.assist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.junit.Test;
import org.robotframework.ide.eclipse.main.plugin.RedImages;
import org.robotframework.ide.eclipse.main.plugin.assist.AssistProposal;
import org.robotframework.red.jface.assist.RedContentProposal.ModificationStrategy;
import org.robotframework.red.jface.assist.RedTextContentAdapter.SubstituteTextModificationStrategy;

public class AssistProposalAdapterTest {

    @Test
    public void contentIsTakenFromAdaptedProposalWithAddition() {
        final AssistProposal proposal = mock(AssistProposal.class);
        when(proposal.getContent()).thenReturn("content");

        assertThat(new AssistProposalAdapter(proposal).getContent()).isEqualTo("content");
        assertThat(new AssistProposalAdapter(proposal, " enhanced").getContent()).isEqualTo("content enhanced");
    }

    @Test
    public void cursorPositionIsAlwaysAtTheEndOfContent() {
        final AssistProposal proposal = mock(AssistProposal.class);
        when(proposal.getContent()).thenReturn("content");

        assertThat(new AssistProposalAdapter(proposal).getCursorPosition()).isEqualTo(7);
        assertThat(new AssistProposalAdapter(proposal, " enhanced").getCursorPosition()).isEqualTo(16);
    }

    @Test
    public void imageIsTakenFromAdaptedProposal() {
        final ImageDescriptor imageDescriptor = RedImages.getRobotImage();
        final AssistProposal proposal = mock(AssistProposal.class);
        when(proposal.getImage()).thenReturn(imageDescriptor);

        assertThat(new AssistProposalAdapter(proposal).getImage()).isSameAs(imageDescriptor);
    }

    @Test
    public void styledLabelIsTakenFromAdaptedProposal() {
        final StyledString label = new StyledString("foo");
        final AssistProposal proposal = mock(AssistProposal.class);
        when(proposal.getStyledLabel()).thenReturn(label);

        assertThat(new AssistProposalAdapter(proposal).getStyledLabel()).isSameAs(label);
    }

    @Test
    public void labelIsTakenFromAdaptedProposal() {
        final AssistProposal proposal = mock(AssistProposal.class);
        when(proposal.getLabel()).thenReturn("label");

        assertThat(new AssistProposalAdapter(proposal).getLabel()).isSameAs("label");
    }

    @Test
    public void hasDescriptionIsTakenFromAdaptedProposal() {
        final AssistProposal proposal1 = mock(AssistProposal.class);
        when(proposal1.hasDescription()).thenReturn(true);

        final AssistProposal proposal2 = mock(AssistProposal.class);
        when(proposal2.hasDescription()).thenReturn(false);

        assertThat(new AssistProposalAdapter(proposal1).hasDescription()).isTrue();
        assertThat(new AssistProposalAdapter(proposal2).hasDescription()).isFalse();
    }

    @Test
    public void descriptionIsTakenFromAdaptedProposal() {
        final AssistProposal proposal1 = mock(AssistProposal.class);
        when(proposal1.getDescription()).thenReturn("desc");

        assertThat(new AssistProposalAdapter(proposal1).getDescription()).isEqualTo("desc");
    }

    @Test
    public void substituteStrategyIsAlwaysProvided_whenNoOtherStrategyWasPassedToConstructor() {
        final AssistProposal proposal = mock(AssistProposal.class);
        assertThat(new AssistProposalAdapter(proposal).getModificationStrategy())
                .isExactlyInstanceOf(SubstituteTextModificationStrategy.class);
        assertThat(new AssistProposalAdapter(proposal, "").getModificationStrategy())
                .isExactlyInstanceOf(SubstituteTextModificationStrategy.class);
        assertThat(new AssistProposalAdapter(proposal, (ModificationStrategy) null).getModificationStrategy())
                .isExactlyInstanceOf(SubstituteTextModificationStrategy.class);
    }

    @Test
    public void modificationStrategyIsProvided_whenPassedToConstructor() {
        final AssistProposal proposal = mock(AssistProposal.class);
        final ModificationStrategy strategy = mock(ModificationStrategy.class);

        assertThat(new AssistProposalAdapter(proposal, strategy).getModificationStrategy()).isSameAs(strategy);
    }
}
