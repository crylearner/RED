/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCallConditions.properlySetParent;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.junit.Before;
import org.junit.Test;
import org.rf.ide.core.testdata.model.table.RobotExecutableRow;
import org.rf.ide.core.testdata.model.table.keywords.UserKeyword;
import org.rf.ide.core.testdata.model.table.testcases.TestCase;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;
import org.robotframework.ide.eclipse.main.plugin.mockeclipse.ContextInjector;
import org.robotframework.ide.eclipse.main.plugin.mockmodel.RobotSuiteFileCreator;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCase;
import org.robotframework.ide.eclipse.main.plugin.model.RobotCasesSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotDefinitionSetting;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.model.RobotKeywordsSection;
import org.robotframework.ide.eclipse.main.plugin.model.RobotModelEvents;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;

import com.google.common.collect.ImmutableMap;

public class ConvertCommentToSettingTest {

    private IEventBroker eventBroker;

    @Before
    public void beforeTest() {
        eventBroker = mock(IEventBroker.class);
    }

    @Test
    public void testCaseExecutableRowIsProperlyUncommented() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Test Cases ***")
                .appendLine("case")
                .appendLine("  # [Teardown]  Catenate  1  2  #comment")
                .build();
        final RobotCase testCase = model.findSection(RobotCasesSection.class).get().getChildren().get(0);
        final RobotKeywordCall keywordCall = testCase.getChildren().get(0);
        @SuppressWarnings("unchecked")
        final RobotExecutableRow<TestCase> oldLinked = (RobotExecutableRow<TestCase>) keywordCall.getLinkedElement();

        ContextInjector.prepareContext()
                .inWhich(eventBroker)
                .isInjectedInto(new ConvertCommentToSetting(eventBroker, keywordCall, "[Teardown]"))
                .execute();

        assertThat(testCase.getChildren().size()).isEqualTo(1);
        final RobotKeywordCall result = testCase.getChildren().get(0);
        assertThat(result).isExactlyInstanceOf(RobotDefinitionSetting.class);
        assertThat(result.getLinkedElement().getDeclaration().getTypes())
                .containsExactly(RobotTokenType.TEST_CASE_SETTING_TEARDOWN);
        assertThat(result.getName()).isEqualTo("Teardown");
        assertThat(result.getArguments()).containsExactly("Catenate", "1", "2");
        assertThat(result.getComment()).isEqualTo("#comment");
        assertThat(result).has(properlySetParent());
        assertThat(testCase.getLinkedElement().getTestExecutionRows()).doesNotContain(oldLinked);
        assertThat(testCase.getLinkedElement().getTestExecutionRows()).isEmpty();

        verify(eventBroker, times(1)).send(eq(RobotModelEvents.ROBOT_KEYWORD_CALL_COMMENT_CHANGE), eq(ImmutableMap
                .of(IEventBroker.DATA, testCase, RobotModelEvents.ADDITIONAL_DATA, result)));
        verifyNoMoreInteractions(eventBroker);
    }

    @Test
    public void keywordExecutableRowIsProperlyUncommented() {
        final RobotSuiteFile model = new RobotSuiteFileCreator().appendLine("*** Keywords ***")
                .appendLine("kw")
                .appendLine("  # [Teardown]  Catenate  1  2  #comment")
                .build();
        final RobotKeywordDefinition keyword = model.findSection(RobotKeywordsSection.class).get().getChildren().get(0);
        final RobotKeywordCall keywordCall = keyword.getChildren().get(0);
        @SuppressWarnings("unchecked")
        final RobotExecutableRow<UserKeyword> oldLinked = (RobotExecutableRow<UserKeyword>) keywordCall
                .getLinkedElement();

        ContextInjector.prepareContext()
                .inWhich(eventBroker)
                .isInjectedInto(new ConvertCommentToSetting(eventBroker, keywordCall, "[Teardown]"))
                .execute();

        assertThat(keyword.getChildren().size()).isEqualTo(1);
        final RobotKeywordCall result = keyword.getChildren().get(0);
        assertThat(result).isExactlyInstanceOf(RobotDefinitionSetting.class);
        assertThat(result.getLinkedElement().getDeclaration().getTypes())
                .containsExactly(RobotTokenType.KEYWORD_SETTING_TEARDOWN);
        assertThat(result.getName()).isEqualTo("Teardown");
        assertThat(result.getArguments()).containsExactly("Catenate", "1", "2");
        assertThat(result.getComment()).isEqualTo("#comment");
        assertThat(result).has(properlySetParent());
        assertThat(keyword.getLinkedElement().getKeywordExecutionRows()).doesNotContain(oldLinked);
        assertThat(keyword.getLinkedElement().getKeywordExecutionRows()).isEmpty();

        verify(eventBroker, times(1)).send(eq(RobotModelEvents.ROBOT_KEYWORD_CALL_COMMENT_CHANGE), eq(ImmutableMap
                .of(IEventBroker.DATA, keyword, RobotModelEvents.ADDITIONAL_DATA, result)));
        verifyNoMoreInteractions(eventBroker);
    }
}
