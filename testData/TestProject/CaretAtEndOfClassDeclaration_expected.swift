import XCTest

class CaretAtEndOfClassDeclaration: XCTestCase {

    var variable: String?
    var otherVariable: String?
    override func tearDown() {
        variable = nil
        otherVariable = nil
        super.tearDown()
    }

}
