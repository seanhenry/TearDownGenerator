package codes.seanhenry.inspections;

import codes.seanhenry.analytics.TrackerSpy;
import codes.seanhenry.testhelpers.ImportProjectTestCase;
import com.intellij.psi.PsiFile;
import org.junit.Assert;

public class TearDownInspectionTest extends ImportProjectTestCase {

  private TrackerSpy trackerSpy;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setTrackerSpy();
    getFixture().enableInspections(new TearDownInspection());
  }

  private void setTrackerSpy() {
    trackerSpy = new TrackerSpy();
    TearDownInspection.tracker = trackerSpy;
  }

  @Override
  protected String getDataPath() {
    return "src/test/resources/TestProject";
  }

  @Override
  protected String getProjectFileName() {
    return "TestProject.xcodeproj";
  }

  public void testAll() throws Exception {
    runAvailableTests();
    runUnavailableTests();
    runInvokeTests();
    runTrackerTests();
  }

  private void runTrackerTests() throws Exception {
    setTrackerSpy();
    runTest("TrackerTearDown");
    assertEquals(1, trackerSpy.invocationCount);
    assertEquals("inspection", trackerSpy.invokedCategory);
    assertEquals("teardown", trackerSpy.invokedAction);
    assertEquals("0", trackerSpy.invokedValue);
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
    PsiFile targetFile = configureFile(fileName);
    getFixture().checkHighlighting(false, false, true, true);
    invokeIntention(TearDownInspectionQuickFix.NAME, targetFile);
    assertFilesEqual(expectedFileName, fileName);
  }

  private boolean runIsAvailableTest(String fileName) throws Exception {
    configureFile(fileName);
    getFixture().checkHighlighting(false, false, true, true);
    return getFixture().getAvailableIntention(TearDownInspectionQuickFix.NAME) != null;
  }

  private PsiFile configureFile(String fileName) throws Exception {
    String testFileName = fileName + ".swift";
    System.out.println("Running test for " + fileName);
    return getFixture().configureByFile(testFileName);
  }
}
