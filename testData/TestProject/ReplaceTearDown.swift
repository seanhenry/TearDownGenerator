import XCTest

class ReplaceTearDown: XCTestCase {
<caret>
    var variable: Int?
    var alreadyNilled: String?
    var alreadyNilledNoSpaces: String?
    var alreadyNilledUsingSelf: Int?

    override func tearDown() {
        alreadyNilled = nil
        alreadyNilledNoSpaces=nil
        self.alreadyNilledUsingSelf = nil
        super.tearDown()
    }
}
