package codes.seanhenry.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.swift.psi.SwiftClassDeclaration;
import com.jetbrains.swift.psi.SwiftReferenceTypeElement;
import com.jetbrains.swift.psi.SwiftTypeInheritanceClause;

import java.util.List;
import java.util.stream.Collectors;

public class MySwiftPsiUtil {

  public static boolean isSubclassOf(PsiElement element, String superClassName) {
    if (element == null || !(element instanceof SwiftClassDeclaration)) {
      return false;
    }
    SwiftClassDeclaration classDeclaration = (SwiftClassDeclaration) element;
    SwiftTypeInheritanceClause clause = classDeclaration.getTypeInheritanceClause();
    if (clause == null) {
      return false;
    }
    List<SwiftReferenceTypeElement> elements = clause.getTypeElementList()
            .stream()
            .filter(e -> e instanceof SwiftReferenceTypeElement)
            .map(e -> (SwiftReferenceTypeElement) e)
            .collect(Collectors.toList());
    if (elements.size() == 0) {
      return false;
    }
    SwiftReferenceTypeElement referenceTypeElement = elements.get(0);
    if (referenceTypeElement.getName().equals(superClassName)) {
      return true;
    }
    return isSubclassOf(referenceTypeElement.resolve(), superClassName);
  }
}
