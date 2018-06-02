package codes.seanhenry.inspections;

import codes.seanhenry.util.MySwiftPsiUtil;
import codes.seanhenry.util.TearDownUtil;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.jetbrains.swift.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TearDownInspection extends LocalInspectionTool implements CleanupLocalInspectionTool {

  public static final String DESCRIPTION = "Not all properties are set to nil in the tear down.";

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new TearDownClassVisitor(holder);
  }

  @Nullable
  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    if (Objects.equals(file.getVirtualFile().getExtension(), "swift")) {
      return super.checkFile(file, manager, isOnTheFly);
    }
    return new ProblemDescriptor[0];
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return "Tear Down";
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

    private static int getNumberOfProblems(SwiftClassDeclaration declaration) {
      SwiftFunctionDeclaration tearDown = TearDownUtil.getTearDownMethod(declaration);
      List<String> variableNames = TearDownUtil.getWritableVariableNames(declaration);
      if (tearDown == null || tearDown.getCodeBlock() == null) {
        return variableNames.size();
      }
      return TearDownUtil.removeExistingNilledVariables(variableNames, tearDown.getCodeBlock()).size();
    }
  }
}
