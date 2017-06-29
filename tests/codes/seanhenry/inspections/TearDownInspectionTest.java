package codes.seanhenry.inspections;

import codes.seanhenry.helpers.ImportProjectTestFixture;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.PlatformTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import org.junit.Assert;

public class TearDownInspectionTest extends PlatformTestCase {

  private static final String dataPath = "/Users/sean/source/plugins/community/TearDown/testData/TestProject";
  private CodeInsightTestFixture myFixture;

  @Override
  protected void tearDown() throws Exception {
    try {
      myFixture.tearDown();
    } catch (Throwable ignored) {
    } finally {
      myFixture = null;
      try {
        super.tearDown();
      } catch (Throwable ignored) {}
    }
  }

  @Override
  protected void setUpProject() throws Exception {
    super.setUpProject();

    TempDirTestFixtureImpl tempDirTestFixture = new TempDirTestFixtureImpl();
    myFixture = IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(new ImportProjectTestFixture(dataPath, "TestProject.xcodeproj", tempDirTestFixture), tempDirTestFixture);
    try {
      myFixture.setUp();
    } catch (Exception ignored) {}
    myFixture.setTestDataPath(dataPath);
    myFixture.enableInspections(new TearDownInspection());
  }

  public Project getActiveProject() {
    return myFixture.getProject();
  }

  public void testAll() throws Exception {
    runAvailableTests();
    runUnavailableTests();
    runInvokeTests();
  }

  private void runAvailableTests() throws Exception {
    String[] fileNames = {
      "AvailableXCTestCaseSubclass",
      "AvailableDeepXCTestCaseSubclass",
    };

    for (String fileName: fileNames) {
      Assert.assertTrue(runIsAvailableTest(fileName));
    }
  }

  private void runUnavailableTests() throws Exception {
    String[] fileNames = {
      "NotAvailableNoXCTestCaseSubclass",
      "NotAvailableAllPropertiesAreSetToNil",
      "NotAvailableNilInAnotherMethod",
    };

    for (String fileName: fileNames) {
      Assert.assertFalse(runIsAvailableTest(fileName));
    }
  }

  private void runInvokeTests() throws Exception {
    String[] fileNames = {
      "SimpleTearDown",
      "ReplaceTearDown",
      "ReplaceTearDownNoSuper",
      "ReplaceTearDownClassMethodEdgeCase",
      "FaultyTearDown",
      "PlaceTearDownAfterProperties",
      "PlaceTearDownAfterSetUp",
      "PlaceTearDownBeforeFirstMethod",
      "NilInAnotherMethod",
    };

    for (String fileName : fileNames) {
      runTest(fileName);
    }
  }

  private void runTest(String fileName) throws Exception {
    String expectedFileName = fileName + "_expected.swift";
    PsiFile psiFile = configureFile(fileName);
    myFixture.checkHighlighting(false, false, true);
    IntentionAction action = myFixture.findSingleIntention(TearDownInspectionQuickFix.NAME);

    WriteCommandAction.runWriteCommandAction(getActiveProject(), () -> action.invoke(getActiveProject(), myFixture.getEditor(), psiFile));
    myFixture.checkResultByFile(expectedFileName, true);
  }

  private boolean runIsAvailableTest(String fileName) throws Exception {
    configureFile(fileName);
    myFixture.checkHighlighting(false, false, true);
    return myFixture.getAvailableIntention(TearDownInspectionQuickFix.NAME) != null;
  }

  private PsiFile configureFile(String fileName) throws Exception {
    String testFileName = fileName + ".swift";
    System.out.println("Running test for " + fileName);
    PsiFile[] files = FilenameIndex.getFilesByName(getActiveProject(), testFileName, GlobalSearchScope.projectScope(getActiveProject()));
    PsiFile psiFile = files[0];
    VirtualFile file = psiFile.getVirtualFile();
    myFixture.configureFromExistingVirtualFile(file);
    return psiFile;
  }
}
