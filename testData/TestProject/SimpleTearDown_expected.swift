import XCTest

class SimpleTearDown: XCTestCase {

    var variable: String?
    override func tearDown() {
        variable = nil
        super.tearDown()
    }
}
