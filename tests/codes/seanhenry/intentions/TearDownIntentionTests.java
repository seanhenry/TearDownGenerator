/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codes.seanhenry.intentions;

import codes.seanhenry.helpers.MyHeavyIdeaTestFixtureImpl;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.PlatformTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TearDownIntentionTests extends PlatformTestCase {

  private final String dataPath = "/Users/sean/source/plugins/community/TearDown/testData/TestProject";
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

    VfsRootAccess.allowRootAccess("/Applications/Xcode-8.3.2.app/Contents");

    TempDirTestFixtureImpl tempDirTestFixture = new TempDirTestFixtureImpl();
    String projectDir = tempDirTestFixture.getTempDirPath();
    copyFolder(new File(dataPath), new File(projectDir));

    String name = getClass().getName() + "." + getName();
    myFixture = new CodeInsightTestFixtureImpl(new MyHeavyIdeaTestFixtureImpl(name, projectDir + "/TestProject.xcodeproj"), tempDirTestFixture);

    myFixture.setUp();
    myFixture.setTestDataPath(dataPath);
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
    Assert.assertFalse(runIsAvailableTest("NotAvailableNoXCTestCaseSubclass"));
  }

  private void runInvokeTests() throws Exception {
    String[] fileNames = {
      "SimpleTearDown",
      "ReplaceTearDown",
      "ReplaceTearDownNoSuper",
      "ReplaceTearDownClassMethodEdgeCase",
      "CaretInStatement",
      "CaretInClassDeclaration",
      "CaretBetweenStatements",
      "CaretAtEndOfClassDeclaration",
      "FaultyTearDown",
    };

    for (String fileName : fileNames) {
      runTest(fileName);
    }
  }

  private void runTest(String fileName) throws Exception {

    String expectedFileName = fileName + "_expected.swift";
    PsiFile psiFile = configureFile(fileName);
    IntentionAction action = myFixture.findSingleIntention("Generate tear down");

    WriteCommandAction.runWriteCommandAction(getActiveProject(), () -> action.invoke(getActiveProject(), myFixture.getEditor(), psiFile));
    myFixture.checkResultByFile(expectedFileName, true);
  }

  private boolean runIsAvailableTest(String fileName) throws Exception {

    configureFile(fileName);
    return myFixture.getAvailableIntention("Generate tear down") != null;
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

  private void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
    //Check if sourceFolder is a directory or file
    //If sourceFolder is file; then copy the file directly to new location
    if (sourceFolder.isDirectory())
    {
      //Verify if destinationFolder is already present; If not then create it
      if (!destinationFolder.exists())
      {
        destinationFolder.mkdir();
        //System.out.println("Directory created :: " + destinationFolder);
      }

      //Get all files from source directory
      String files[] = sourceFolder.list();

      //Iterate over all files and copy them to destinationFolder one by one
      for (String file : files)
      {
        File srcFile = new File(sourceFolder, file);
        File destFile = new File(destinationFolder, file);

        //Recursive function call
        copyFolder(srcFile, destFile);
      }
    }
    else
    {
      //Copy the file content from one place to another
      Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
      //System.out.println("File copied :: " + destinationFolder);
    }
  }
}
