import XCTest

class TrackerTearDown: XCTestCase {

    var opt: String?

    override func tearDown() {
        opt = nil
        super.tearDown()
    }
}
