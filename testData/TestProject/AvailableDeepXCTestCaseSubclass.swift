import XCTest

class DeeperTests: XCTestCase {

}

class DeepTests: DeeperTests {

}

class ShallowTests: DeepTests {
    var variable: String?
    <caret>
}
