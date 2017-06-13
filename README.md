# Test Tear Down Inspection for AppCode

![Generating a tear down method](readme/TearDownInpsection.gif "Generating a tear down method")

## Motivation

For each test you write, an `XCTestCase` is allocated but never deallocated.  

The best practice is to set all your instance variables to nil in the tear down method of your test.

This AppCode plugin inspects your tests and finds all places where one or more properties are not set to nil. It will also offer to fix the problem for you.
  
See the usages below on how to fix a single test file and how to apply the fix on all test files.

## Installation

- Open AppCode
- Open AppCode → Preferences `⌘,`
- Go to Plugins
- Click Browse repositories...
- Search for 'Test Tear Down Inspection for AppCode'
- Click Install
- Restart AppCode

## Usages

### Mid-workflow
With a Swift test file open, the class name will be highlighted with a warning if any properties are not set to nil in the tear down.
  
You can then ask AppCode to fix the problem for you.
  
- Move the cursor to the class name
- Press `alt ↩`
- Select 'Set properties to nil in tear down'

### Batch find problems
For when you want to find all the problematic tests in the project.

- Select `Code -> Run inspection by name`
- Start typing 'Properties are not set to nil in tear down'
- Select that inspection

### Batch find and fix problems
For when you want to find and automatically fix all problematic tests in the project.

- Select `Code -> Code Cleanup`
- Select the scope.
- Make sure the inspection is included in the Inspection Profile.
- Click OK.

### Fix problems before a commit
For when you want to automatically fix all problematic tests before a commit.

- Select `VCS -> Commit Changes`
- Stage your changes
- Under 'Before Commit', select 'Cleanup'
- Click Commit