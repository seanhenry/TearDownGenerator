import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">ReplaceTearDownClassMethodEdgeCase<caret></weak_warning>: XCTestCase {

    var variable: String?

    override class func tearDown() {
        super.tearDown()
    }
}
