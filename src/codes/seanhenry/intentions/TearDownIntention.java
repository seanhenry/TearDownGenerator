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
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.swift.psi.*;
import com.jetbrains.swift.psi.types.SwiftOptionalType;
import com.jetbrains.swift.psi.types.SwiftTypeVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TearDownIntention extends PsiElementBaseIntentionAction implements IntentionAction, ProjectComponent {

  private final String NEWLINE = "\n";
  private SwiftClassDeclaration classDeclaration;
  private SwiftPsiElementFactory elementFactory;

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

    elementFactory = SwiftPsiElementFactory.getInstance(element);
    classDeclaration = PsiTreeUtil.getParentOfType(element, SwiftClassDeclaration.class);
    assert classDeclaration != null;
    createTearDownMethod(getWritableVariableNames());
  }

  public class WritableVariableVistor extends PsiElementVisitor {

    private List<SwiftVariableDeclaration> variables = new ArrayList<>();

    @Override
    public void visitElement(PsiElement element) {
      super.visitElement(element);
      if (element instanceof SwiftVariableDeclaration) {
        SwiftVariableDeclaration variable = (SwiftVariableDeclaration)element;
        SwiftOptionalTypeElement optional = PsiTreeUtil.findChildOfType(element, SwiftOptionalTypeElement.class);
        if (optional != null) {
          variables.add(variable);
        }
      }
    }

    public List<SwiftVariableDeclaration> getVariables() {
      return variables;
    }
  }

  private List<String> getWritableVariableNames() {

    //List<SwiftVariableDeclaration> variables = new ArrayList<>();
    //for (PsiElement element : classDeclaration.getChildren()) {
    //  if (element instanceof SwiftVariableDeclaration) {
    //    SwiftVariableDeclaration variable = (SwiftVariableDeclaration)element;
    //    boolean isOptional = PsiTreeUtil.findChildOfType(element, SwiftOptionalTypeElement.class) != null;
    //    boolean isConstant = variable.isConstant();
    //    boolean isComputed = variable.getPatternInitializerList().get(0).isComputed();
    //    if (isOptional && !isConstant && !isComputed) {
    //      variables.add(variable);
    //    }
    //  }
    //}
    //WritableVariableVistor vistor = new WritableVariableVistor();
    //classDeclaration.getFirstChild().accept(vistor);

    return Arrays.asList(classDeclaration.getChildren())
      .stream()
      .filter(e -> e instanceof SwiftVariableDeclaration)
      .map(e -> (SwiftVariableDeclaration) e)
      .filter(v -> !v.isConstant())
      .filter(this::isNilable)
      .filter(this::isWritable)
      .map(v -> PsiTreeUtil.findChildOfType(v, SwiftIdentifierPattern.class))
      .map(PsiNamedElement::getName)
      .collect(Collectors.toList());
  }

  private boolean isNilable(SwiftVariableDeclaration variable) {
    return PsiTreeUtil.findChildOfAnyType(variable, SwiftOptionalTypeElement.class, SwiftImplicitlyUnwrappedOptionalTypeElement.class) != null;
  }

  private boolean isWritable(SwiftVariableDeclaration variable) {
    if (variable.getPatternInitializerList().isEmpty()) {
      return false;
    }
    boolean isComputed = variable.getPatternInitializerList().get(0).isComputed();
    if (isComputed) {
      return PsiTreeUtil.findChildOfType(variable, SwiftSetterClause.class) != null;
    } else {
      return true;
    }
  }

  private void createTearDownMethod(List<String> variableNames) {
    SwiftFunctionDeclaration tearDown = elementFactory
      .createFunction("override func tearDown() { " + NEWLINE +
                      variableNames.stream().map(v -> v + " = nil").collect(Collectors.joining(NEWLINE)) + NEWLINE +
                      "super.tearDown() " + NEWLINE +
                      "}");
    classDeclaration.addBefore(tearDown, classDeclaration.getLastChild());
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

    SwiftClassDeclaration classDeclaration = PsiTreeUtil.getParentOfType(element, SwiftClassDeclaration.class);
    return MySwiftPsiUtil.isSubclassOf(classDeclaration, "XCTestCase");
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
