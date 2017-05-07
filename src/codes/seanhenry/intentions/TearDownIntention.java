/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codes.seanhenry.intentions;

import codes.seanhenry.util.MySwiftPsiUtil;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.swift.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TearDownIntention extends PsiElementBaseIntentionAction implements IntentionAction, ProjectComponent {

  private final String NEWLINE = "\n";
  private SwiftClassDeclaration classDeclaration;
  private SwiftPsiElementFactory elementFactory;

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

    SwiftClassDeclaration classDeclaration = PsiTreeUtil.getParentOfType(element, SwiftClassDeclaration.class);
    if (classDeclaration == null) {
      return false;
    }
    return MySwiftPsiUtil.isSubclassOf(classDeclaration, "XCTestCase")
           && (findTopElementUnderCursor(element, classDeclaration) != null
           || getTearDownMethod() != null);
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

    elementFactory = SwiftPsiElementFactory.getInstance(element);
    classDeclaration = PsiTreeUtil.getParentOfType(element, SwiftClassDeclaration.class);
    assert classDeclaration != null;
    List<String> writableNames = getWritableVariableNames();
    SwiftFunctionDeclaration tearDownMethod = getTearDownMethod();
    if (tearDownMethod != null) {
      replaceTearDownMethod(tearDownMethod, writableNames);
    } else {
      PsiElement caretElement = findTopElementUnderCursor(element, classDeclaration);
      createTearDownMethod(writableNames, caretElement);
    }
  }

  private PsiElement findTopElementUnderCursor(PsiElement element, SwiftClassDeclaration classDeclaration) {
    if (PsiTreeUtil.getParentOfType(element, SwiftClassDeclaration.class) == null) {
      return null;
    }
    while (element.getParent() != classDeclaration) {
      element = element.getParent();
    }
    if (element instanceof SwiftStatement) {
      return element;
    }
    do {
      element = element.getPrevSibling();
    } while (element != null && !(element instanceof SwiftStatement));
    if (element instanceof SwiftStatement) {
      return element;
    }
    if (classDeclaration.getStatementList().isEmpty()) {
      return null;
    }
    return classDeclaration.getStatementList().get(0).getPrevSibling();
  }

  private SwiftFunctionDeclaration getTearDownMethod() {
    for (PsiElement child : classDeclaration.getChildren()) {
      if (child instanceof SwiftFunctionDeclaration) {
        SwiftFunctionDeclaration function = (SwiftFunctionDeclaration)child;
        if ("tearDown".equals(function.getName()) && !function.isStatic()) {
          return function;
        }
      }
    }
    return null;
  }

  private List<String> getWritableVariableNames() {

    return Arrays.stream(classDeclaration.getChildren())
      .filter(e -> e instanceof SwiftVariableDeclaration)
      .map(e -> (SwiftVariableDeclaration) e)
      .filter(v -> !v.isConstant())
      .flatMap(v -> v.getPatternInitializerList().stream())
      .filter(this::isNilable)
      .filter(this::isWritable)
      .map(p -> PsiTreeUtil.findChildOfType(p, SwiftIdentifierPattern.class))
      .filter(Objects::nonNull)
      .map(PsiNamedElement::getName)
      .collect(Collectors.toList());
  }

  private boolean isNilable(SwiftPatternInitializer pattern) {
    return PsiTreeUtil.findChildOfAnyType(pattern, SwiftOptionalTypeElement.class, SwiftImplicitlyUnwrappedOptionalTypeElement.class) != null;
  }

  private boolean isWritable(SwiftPatternInitializer pattern) {
    boolean isComputed = pattern.isComputed();
    if (isComputed) {
      return PsiTreeUtil.findChildOfType(pattern, SwiftSetterClause.class) != null;
    } else {
      return true;
    }
  }

  private void createTearDownMethod(List<String> variableNames, PsiElement caretElement) {
    SwiftFunctionDeclaration tearDown = elementFactory
      .createFunction("override func tearDown() { " + NEWLINE +
                      variableNames.stream().map(v -> v + " = nil").collect(Collectors.joining(NEWLINE)) + NEWLINE +
                      "super.tearDown() " + NEWLINE +
                      "}");
    classDeclaration.addAfter(tearDown, caretElement);
  }

  private void replaceTearDownMethod(SwiftFunctionDeclaration tearDownMethod, List<String> variableNames) {
    SwiftCodeBlock codeBlock = tearDownMethod.getCodeBlock();
    if (codeBlock == null) {
      return; // TODO: error
    }
    PsiElement superExpression = findSuperExpression(codeBlock);
    if (superExpression == null) {
      addSuperCall(codeBlock);
      replaceTearDownMethod(tearDownMethod, variableNames);
      return;
    }
    variableNames = removeExistingNilledVariables(variableNames, codeBlock);
    addVariableNames(variableNames, codeBlock, superExpression);
  }

  private PsiElement findSuperExpression(SwiftCodeBlock codeBlock) {
    for (PsiElement element : codeBlock.getChildren()) {
      if (element.getText().equals("super.tearDown()")) {
        return element;
      }
    }
    return null;
  }

  private void addSuperCall(SwiftCodeBlock codeBlock) {
    PsiElement superExpression;
    superExpression = elementFactory.createExpression("super.tearDown()", null);
    codeBlock.addBefore(superExpression, codeBlock.getLastChild());
  }

  private void addVariableNames(List<String> variableNames, SwiftCodeBlock codeBlock, PsiElement superExpression) {
    for (String name : variableNames) {
      SwiftExpression expression = elementFactory.createExpression(name + " = nil", null);
      codeBlock.addBefore(expression, superExpression);
    }
  }

  private List<String> removeExistingNilledVariables(List<String> variableNames, SwiftCodeBlock codeBlock) {
    Set<String> statementHash = Arrays.stream(codeBlock.getChildren()).map(c -> c.getText().replaceAll("\\s", "")).collect(Collectors.toSet());
    variableNames = variableNames.stream().filter(v -> !statementHash.contains(v + "=nil")).collect(Collectors.toList());
    return variableNames;
  }

  @NotNull
  @Override
  public String getText() {
    return "Generate tear down";
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getText();
  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {

  }

  @Override
  public void initComponent() {

  }

  @Override
  public void disposeComponent() {

  }

  @NotNull
  @Override
  public String getComponentName() {
    return "Tear down generator";
  }
}
