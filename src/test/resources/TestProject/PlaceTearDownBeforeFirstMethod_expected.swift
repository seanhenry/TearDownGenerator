import XCTest

class PlaceTearDownBeforeFirstMethod: XCTestCase {

    var prop1: String?
    let prop2: String? = ""
    var prop3: Double!
    var prop4: Int = 0

    override func tearDown() {
        prop1 = nil
        prop3 = nil
        lowerProp = nil
        super.tearDown()
    }

    func test_method1() {
        prop4 += 1
    }
    func test_method2() {
        prop4 += 1
    }
    var lowerProp: String?
}
