import XCTest

class SimpleTearDown: XCTestCase {

    var opt: String?
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
    var multi: String?, line: String?, statement: String?
    static var staticVar: String?

    override func tearDown() {
        opt = nil
        anotherOptional = nil
        privateOptional = nil
        iuo = nil
        readWriteOptional = nil
        flippedReadWriteOptional = nil
        didSetOptional = nil
        multi = nil
        line = nil
        statement = nil
        super.tearDown()
    }
}
