import XCTest

class ReplaceTearDown: XCTestCase {

    var variable: Int?
    var alreadyNilled: String?
    var alreadyNilledNoSpaces: String?

    override func tearDown() {
        alreadyNilled = nil
        alreadyNilledNoSpaces=nil
        variable = nil
        super.tearDown()
    }
}
