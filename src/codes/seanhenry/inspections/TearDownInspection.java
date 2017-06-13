package codes.seanhenry.inspections;

import codes.seanhenry.util.MySwiftPsiUtil;
import codes.seanhenry.util.TearDownUtil;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.swift.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TearDownInspection extends LocalInspectionTool {

  public static final String DESCRIPTION = "Not all properties are set to nil in the tear down.";

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new TearDownClassVisitor(holder);
  }

  private static class TearDownClassVisitor extends SwiftVisitor {

    private final ProblemsHolder holder;

    public TearDownClassVisitor(ProblemsHolder holder) {
      this.holder = holder;
    }

    @Override
    public void visitClassDeclaration(@NotNull SwiftClassDeclaration declaration) {
      if (MySwiftPsiUtil.isSubclassOf(declaration, "XCTestCase")
          && declaration.getNameIdentifier() != null
          && getNumberOfProblems(declaration) > 0) {
        holder.registerProblem(declaration.getNameIdentifier(), DESCRIPTION, ProblemHighlightType.WEAK_WARNING, new TearDownInspectionQuickFix(declaration));
      }
      super.visitClassDeclaration(declaration);
    }

    private int getNumberOfProblems(SwiftClassDeclaration declaration) {
      SwiftFunctionDeclaration tearDown = TearDownUtil.getTearDownMethod(declaration);
      List<String> variableNames = TearDownUtil.getWritableVariableNames(declaration);
      if (tearDown == null || tearDown.getCodeBlock() == null) {
        return variableNames.size();
      }
      return TearDownUtil.removeExistingNilledVariables(variableNames, tearDown.getCodeBlock()).size();
    }
  }
}
