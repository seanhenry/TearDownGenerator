package codes.seanhenry.util;

import com.jetbrains.swift.psi.SwiftBinaryOperator;
import com.jetbrains.swift.psi.SwiftPsiElement;
import com.jetbrains.swift.psi.SwiftVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IsAssignmentOperatorVisitor extends SwiftVisitor {

  private boolean isAssignmentOperator = false;

  public static boolean isAssignmentOperator(SwiftPsiElement element) {
    IsAssignmentOperatorVisitor visitor = new IsAssignmentOperatorVisitor();
    element.accept(visitor);
    return visitor.isAssignmentOperator;
  }

  @Override
  public void visitBinaryOperator(@NotNull SwiftBinaryOperator operator) {
    isAssignmentOperator = Objects.equals(operator.getText(), "=");
  }
}
