import XCTest

class ReplaceTearDown: XCTestCase {

    var variable: Int?
    var alreadyNilled: String?
    var alreadyNilledNoSpaces: String?
    var alreadyNilledUsingSelf: Int?

    override func tearDown() {
        alreadyNilled = nil
        alreadyNilledNoSpaces=nil
        self.alreadyNilledUsingSelf = nil
        variable = nil
        super.tearDown()
    }
}
