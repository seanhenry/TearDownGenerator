import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">ReplaceTearDownNoSuper<caret></weak_warning>: XCTestCase {

    var variable: Int?

    override func tearDown() {
    }
}
