import XCTest

class DeeperTests: XCTestCase {

}

class DeepTests: DeeperTests {

}

class <weak_warning descr="Not all properties are set to nil in the tear down.">ShallowTests<caret></weak_warning>: DeepTests {
    var variable: String?
}
