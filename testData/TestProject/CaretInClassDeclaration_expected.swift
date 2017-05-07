import XCTest

class CaretInClassDeclaration<caret>: XCTestCase {

    override func tearDown() {
        variable = nil
        otherVariable = nil
        super.tearDown()
    }
    var variable: String?
    var otherVariable: String?
}
