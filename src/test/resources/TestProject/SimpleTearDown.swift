import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">SimpleTearDown<caret></weak_warning>: XCTestCase {

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
}
