package codes.seanhenry.util;

import com.jetbrains.swift.psi.SwiftPsiElement;
import com.jetbrains.swift.psi.SwiftReferenceExpression;
import com.jetbrains.swift.psi.SwiftVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GetReferenceElementNameVisitor extends SwiftVisitor {

  private String name;

  @Nullable
  public static String getName(SwiftPsiElement element) {
    GetReferenceElementNameVisitor visitor = new GetReferenceElementNameVisitor();
    element.accept(visitor);
    return visitor.name;
  }

  @Override
  public void visitReferenceExpression(@NotNull SwiftReferenceExpression expression) {
    name = expression.getName();
  }
}
