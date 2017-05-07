import XCTest

class SimpleTearDown: XCTestCase {
<caret>
    var optional: String?
    let constantOptional: String? = ""
    var anotherOptional: String?
    var readOnlyOptional: String? {
        return ""
    }
    private var privateOptional: String?
    var iuo: Int!
    let constantIUO: Int! = 1
    var readOnlyIUO: String? {
        return ""
    }
    var nonOptional = 1
    var readWriteOptional: UInt? {
        set {  }
        get { return 0 }
    }
    var flippedReadWriteOptional: UInt? {
        get { return 0 }
        set {  }
    }
    var didSetOptional: UInt? {
        willSet {}
        didSet {}
    }
}
