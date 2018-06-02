import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">PlaceTearDownAfterSetUp<caret></weak_warning>: XCTestCase {

    var prop1: String?
    let prop2: String? = ""
    var prop3: Double!
    var prop4: Int = 0
    override func setUp() {
        super.setUp()
        prop1 = ""
        prop3 = 3.22
    }
}
