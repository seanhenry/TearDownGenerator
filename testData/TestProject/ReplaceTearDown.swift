import XCTest

class ReplaceTearDown: XCTestCase {
<caret>
    var variable: Int?
    var alreadyNilled: String?
    var alreadyNilledNoSpaces: String?

    override func tearDown() {
        alreadyNilled = nil
        alreadyNilledNoSpaces=nil
        super.tearDown()
    }
}
