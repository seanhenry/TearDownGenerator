import XCTest

class DeeperTests: XCTestCase {

}

class DeepTests: DeeperTests {

}

class ShallowTests: DeepTests {
    <caret>
}
