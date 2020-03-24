package codes.seanhenry.util;

import com.jetbrains.swift.codeinsight.resolve.logical.SwiftLogicalBinaryExpression;
import com.jetbrains.swift.codeinsight.resolve.logical.SwiftLogicalExpression;
import com.jetbrains.swift.psi.SwiftComplexOperatorExpression;
import com.jetbrains.swift.psi.SwiftPsiElement;
import com.jetbrains.swift.psi.SwiftVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GetNilAssignmentVisitor extends SwiftVisitor {

  private String name;

  @Nullable
  public static String getName(SwiftPsiElement element) {
    GetNilAssignmentVisitor visitor = new GetNilAssignmentVisitor();
    element.accept(visitor);
    return visitor.name;
  }

  @Override
  public void visitComplexOperatorExpression(@NotNull SwiftComplexOperatorExpression expression) {
    SwiftLogicalExpression logicalExpression = expression.resolveLogicalExpression();
    if (logicalExpression instanceof SwiftLogicalBinaryExpression) {
      SwiftLogicalBinaryExpression binaryExpression = ((SwiftLogicalBinaryExpression) logicalExpression);
      if (isNilAssignment(binaryExpression)) {
        name = GetReferenceElementNameVisitor.getName(binaryExpression.getLeft().toPsi());
      }
    }
  }

  private boolean isNilAssignment(SwiftLogicalBinaryExpression binaryExpression) {
    return IsNilLiteralVisitor.isNilLiteral(binaryExpression.getRight().toPsi())
        && IsAssignmentOperatorVisitor.isAssignmentOperator(binaryExpression.getOperator());
  }
}
