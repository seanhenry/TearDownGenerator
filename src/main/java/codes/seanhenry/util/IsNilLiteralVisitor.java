package codes.seanhenry.util;

import com.jetbrains.swift.psi.SwiftLiteralExpression;
import com.jetbrains.swift.psi.SwiftPsiElement;
import com.jetbrains.swift.psi.SwiftVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IsNilLiteralVisitor extends SwiftVisitor {

  private boolean isNilLiteral = false;

  public static boolean isNilLiteral(SwiftPsiElement element) {
    IsNilLiteralVisitor visitor = new IsNilLiteralVisitor();
    element.accept(visitor);
    return visitor.isNilLiteral;
  }

  @Override
  public void visitLiteralExpression(@NotNull SwiftLiteralExpression expression) {
    isNilLiteral = Objects.equals(expression.getText(), "nil");
  }
}
