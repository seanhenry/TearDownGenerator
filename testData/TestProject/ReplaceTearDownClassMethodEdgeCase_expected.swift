import XCTest

class ReplaceTearDownClassMethodEdgeCase: XCTestCase {

    var variable: String?

    override func tearDown() {
        variable = nil
        super.tearDown()
    }

    override class func tearDown() {
        super.tearDown()
    }
}
