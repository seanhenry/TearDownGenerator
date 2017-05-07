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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.swift.psi.SwiftClassDeclaration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class TearDownIntention extends PsiElementBaseIntentionAction implements IntentionAction, ProjectComponent {

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {


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
    return "FAMILY NAME";
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
    return "COMPONENT NAME";
  }
}