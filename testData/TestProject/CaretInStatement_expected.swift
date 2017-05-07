import XCTest

class CaretInStatement: XCTestCase {

    var variable<caret>: String?
    override func tearDown() {
        variable = nil
        super.tearDown()
    }
}
