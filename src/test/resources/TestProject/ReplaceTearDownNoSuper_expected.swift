import XCTest

class ReplaceTearDownNoSuper: XCTestCase {

    var variable: Int?

    override func tearDown() {
        variable = nil
        super.tearDown()
    }
}
