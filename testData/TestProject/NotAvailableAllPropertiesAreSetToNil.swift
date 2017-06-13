import XCTest

class NotAvailableAllPropertiesAreSetToNil<caret>: XCTestCase {

    var prop1: String?
    var prop2: String!
    var prop3: Int = 0
    let prop4: String? = "let"

    override func tearDown() {
        prop1 = nil
        prop2 = nil
        super.tearDown()
    }
}
