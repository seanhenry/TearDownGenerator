import XCTest

class FaultyTearDown: XCTestCase {
<caret>
    var variable: String?

    override func tearDown()
}
