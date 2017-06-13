import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">ReplaceTearDown<caret></weak_warning>: XCTestCase {

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
