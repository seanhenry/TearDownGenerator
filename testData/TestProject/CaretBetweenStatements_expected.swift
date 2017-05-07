import XCTest

class CaretBetweenStatements: XCTestCase {

    var variable: String?
    override func tearDown() {
        variable = nil
        otherVariable = nil
        super.tearDown()
    }

    var otherVariable: String?
}
