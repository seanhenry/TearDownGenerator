import XCTest

class PlaceTearDownAfterSetUp: XCTestCase {

    var prop1: String?
    let prop2: String? = ""
    var prop3: Double!
    var prop4: Int = 0
    override func setUp() {
        super.setUp()
        prop1 = ""
        prop3 = 3.22
    }

    override func tearDown() {
        prop1 = nil
        prop3 = nil
        super.tearDown()
    }
}
