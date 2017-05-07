import XCTest

class ReplaceTearDownClassMethodEdgeCase: XCTestCase {
<caret>
    var variable: String?

    override class func tearDown() {
        super.tearDown()
    }
}
