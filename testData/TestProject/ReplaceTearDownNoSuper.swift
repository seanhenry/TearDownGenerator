import XCTest

class ReplaceTearDownNoSuper: XCTestCase {
<caret>
    var variable: Int?

    override func tearDown() {
    }
}
