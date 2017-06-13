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
package codes.seanhenry.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.swift.psi.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TearDownUtil {

  public static List<String> getWritableVariableNames(SwiftClassDeclaration classDeclaration) {

    return classDeclaration.getStatementList().stream()
      .filter(s -> s instanceof SwiftVariableDeclaration)
      .map(s -> (SwiftVariableDeclaration) s)
      .filter(v -> !v.isConstant())
      .flatMap(v -> v.getPatternInitializerList().stream())
      .filter(TearDownUtil::isNilable)
      .filter(TearDownUtil::isWritable)
      .map(p -> PsiTreeUtil.findChildOfType(p, SwiftIdentifierPattern.class))
      .filter(Objects::nonNull)
      .map(PsiNamedElement::getName)
      .collect(Collectors.toList());
  }

  public static List<String> removeExistingNilledVariables(List<String> variableNames, SwiftCodeBlock codeBlock) {
    Set<String> statementHash = codeBlock.getStatementList()
      .stream()
      .map(s -> s.getText().replaceAll("\\s|self\\.", ""))
      .collect(Collectors.toSet());
    variableNames = variableNames.stream().filter(v -> !statementHash.contains(v + "=nil")).collect(Collectors.toList());
    return variableNames;
  }

  public static SwiftFunctionDeclaration getTearDownMethod(SwiftClassDeclaration classDeclaration) {
    for (PsiElement statement : classDeclaration.getStatementList()) {
      if (statement instanceof SwiftFunctionDeclaration) {
        SwiftFunctionDeclaration function = (SwiftFunctionDeclaration)statement;
        if ("tearDown".equals(function.getName()) && !function.isStatic()) {
          return function;
        }
      }
    }
    return null;
  }

  private static boolean isNilable(SwiftPatternInitializer pattern) {
    return PsiTreeUtil.findChildOfAnyType(pattern, SwiftOptionalTypeElement.class, SwiftImplicitlyUnwrappedOptionalTypeElement.class) != null;
  }

  private static boolean isWritable(SwiftPatternInitializer pattern) {
    boolean isComputed = pattern.isComputed();
    if (isComputed) {
      return PsiTreeUtil.findChildOfType(pattern, SwiftSetterClause.class) != null;
    } else {
      return true;
    }
  }
}
