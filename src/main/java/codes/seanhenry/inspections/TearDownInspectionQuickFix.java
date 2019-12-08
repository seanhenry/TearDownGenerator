package codes.seanhenry.inspections;

import codes.seanhenry.analytics.GoogleAnalyticsTracker;
import codes.seanhenry.analytics.Tracker;
import codes.seanhenry.util.TearDownUtil;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.swift.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TearDownInspectionQuickFix extends LocalQuickFixOnPsiElement {

  public static Tracker tracker = new GoogleAnalyticsTracker();
  private static final String NEWLINE = "\n";
  public static final String NAME = "Set properties to nil in tear down";
  private SwiftPsiElementFactory elementFactory;

  public TearDownInspectionQuickFix(SwiftClassDeclaration classDeclaration) {
    super(classDeclaration);
  }

  @NotNull
  @Override
  public String getText() {
    return NAME;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getName();
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement element, @NotNull PsiElement element1) {
    tracker.track("inspection", "teardown", "0");
    elementFactory = SwiftPsiElementFactory.getInstance(getClassDeclaration());
    List<String> writableNames = TearDownUtil.getWritableVariableNames(getClassDeclaration());
    SwiftFunctionDeclaration tearDownMethod = TearDownUtil.getTearDownMethod(getClassDeclaration());
    if (tearDownMethod != null) {
      replaceTearDownMethod(tearDownMethod, writableNames);
    } else {
      createTearDown(writableNames);
    }
  }

  private void createTearDown(List<String> variableNames) {
    SetUpMethodVisitor visitor = new SetUpMethodVisitor();
    for (SwiftMemberDeclaration declaration : getClassDeclaration().getDeclarations()) {
      declaration.accept(visitor);
    }
    SwiftFunctionDeclaration tearDown = elementFactory
      .createFunction("override func tearDown() { " + NEWLINE +
                        variableNames.stream().map(v -> v + " = nil").collect(Collectors.joining(NEWLINE)) + NEWLINE +
                        "super.tearDown() " + NEWLINE +
                      "}");
    visitor.placeTearDown(tearDown);
  }

  private class SetUpMethodVisitor extends SwiftVisitor {

    private PsiElement setUpMethod;
    private List<PsiElement> methods = new ArrayList<>();

    @Override
    public void visitFunctionDeclaration(@NotNull SwiftFunctionDeclaration declaration) {
      if (Objects.equals(declaration.getName(), "setUp")
          && !declaration.isStatic()) {
        setUpMethod = declaration;
      }
      methods.add(declaration);
    }

    public void placeTearDown(SwiftStatement tearDown) {
      if (setUpMethod != null) {
        getClassDeclaration().addAfter(tearDown, setUpMethod);
        return;
      }
      if (methods.isEmpty()) {
        getClassDeclaration().addBefore(tearDown, getClassDeclaration().getLastChild());
      } else {
        getClassDeclaration().addBefore(tearDown, methods.get(0));
      }
    }
  }

  private void replaceTearDownMethod(SwiftFunctionDeclaration tearDownMethod, List<String> variableNames) {
    SwiftCodeBlock codeBlock = tearDownMethod.getCodeBlock();
    if (codeBlock == null) {
      SwiftFunctionDeclaration newMethod = elementFactory.createFunction(tearDownMethod.getText() + "{}");
      tearDownMethod.replace(newMethod);
      replaceTearDownMethod(TearDownUtil.getTearDownMethod(getClassDeclaration()), variableNames);
      return;
    }
    PsiElement superExpression = findSuperExpression(codeBlock);
    if (superExpression == null) {
      addSuperCall(codeBlock);
      replaceTearDownMethod(tearDownMethod, variableNames);
      return;
    }
    variableNames = TearDownUtil.removeExistingNilledVariables(variableNames, codeBlock);
    addVariableNames(variableNames, codeBlock);
  }

  private static PsiElement findSuperExpression(SwiftCodeBlock codeBlock) {
    for (SwiftStatement element : codeBlock.getStatements()) {
      if (element.getText().equals("super.tearDown()")) {
        return element;
      }
    }
    return null;
  }

  private void addSuperCall(SwiftCodeBlock codeBlock) {
    PsiElement superExpression;
    superExpression = elementFactory.createExpression("super.tearDown()", null);
    addStatementToCodeBlock(superExpression, codeBlock);
  }

  private void addVariableNames(List<String> variableNames, SwiftCodeBlock codeBlock) {
    for (String name : variableNames) {
      SwiftExpression expression = elementFactory.createExpression(name + " = nil", null);
      addStatementToCodeBlock(expression, codeBlock);
    }
  }

  private SwiftClassDeclaration getClassDeclaration() {
    return (SwiftClassDeclaration)getStartElement();
  }

  private void addStatementToCodeBlock(PsiElement statement, SwiftCodeBlock codeBlock) {
    if (codeBlock.getStatements().isEmpty()) {
      codeBlock.addBefore(statement, codeBlock.getLastChild());
    } else {
      codeBlock.addStatementAfter(statement, codeBlock.getStatements().get(codeBlock.getStatements().size() - 1));
    }
  }
}
