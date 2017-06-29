package codes.seanhenry.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
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
    List<SwiftStatement> statements = codeBlock.getStatementList();
    Set<String> nilledProperties = getNilledPropertyNames(statements);
    Set<String> resolvedNilledProperties = getResolvedNilledPropertyNames(statements);
    nilledProperties.addAll(resolvedNilledProperties);
    variableNames = variableNames
      .stream()
      .filter(v -> !nilledProperties.contains(v))
      .collect(Collectors.toList());
    return variableNames;
  }

  private static Set<String> getResolvedNilledPropertyNames(List<SwiftStatement> statements) {
    return statements.stream()
      .filter(s -> s instanceof SwiftCallExpression)
      .map(s -> (SwiftCallExpression) s)
      .map(PsiReference::resolve)
      .filter(e -> e instanceof SwiftFunctionDeclaration)
      .map(e -> (SwiftFunctionDeclaration) e)
      .filter(f -> f.getCodeBlock() != null)
      .map(SwiftFunctionDeclaration::getCodeBlock)
      .flatMap(b -> getNilledPropertyNames(b.getStatementList()).stream())
      .collect(Collectors.toSet());
  }

  private static Set<String> getNilledPropertyNames(List<SwiftStatement> statements) {
    return statements.stream()
      .filter(s -> s instanceof SwiftBinaryExpression)
      .map(TearDownUtil::removeWhitespaceAndSelf)
      .filter(TearDownUtil::isNilledStatement)
      .map(TearDownUtil::removeNilledStatement)
      .collect(Collectors.toSet());
  }

  private static String removeWhitespaceAndSelf(SwiftStatement statement) {
    return statement.getText().replaceAll("\\s|self\\.", "");
  }

  private static boolean isNilledStatement(String statement) {
    return statement.endsWith("=nil");
  }

  private static String removeNilledStatement(String statement) {
    return statement.replaceAll("=nil", "");
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
