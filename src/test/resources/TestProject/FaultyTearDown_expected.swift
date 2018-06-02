import XCTest

class FaultyTearDown: XCTestCase {

    var variable: String?

    override func tearDown() {
        variable = nil
        super.tearDown()
    }
}
