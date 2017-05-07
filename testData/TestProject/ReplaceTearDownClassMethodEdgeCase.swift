import XCTest

class ReplaceTearDownClassMethodEdgeCase: XCTestCase {

    var variable: String?

    override class func tearDown() {
        super.tearDown()<caret>
    }
}
