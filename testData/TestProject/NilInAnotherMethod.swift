import XCTest

protocol AProtocol: class {
    var alreadyNilledInProtocolExtension: String? { get set }
    func setToNilInProtocolExtension()
}

extension AProtocol where Self: XCTestCase {

    func setToNilInProtocolExtension() {
        alreadyNilledInProtocolExtension = nil
    }
}

class <weak_warning descr="Not all properties are set to nil in the tear down.">NilInAnotherMethod<caret></weak_warning>: XCTestCase, AProtocol {

    var nilledOutsideTearDown: String?
    var nilledInTheTearDown: String?
    var alreadyNilledInMethod: String?
    var alreadyNilledInExtension: String?
    var alreadyNilledInProtocolExtension: String?
    var alreadyNilledInSelfMethod: String?

    override func tearDown() {

        setToNil()
        setToNilInExtension()
        setToNilInProtocolExtension()
        self.setToNilUsingSelf()
        nilledInTheTearDown = nil
        super.tearDown()
    }

    private func setToNilOutsideOfTheTearDown() {
        nilledOutsideTearDown = nil
    }

    private func setToNil() {
        alreadyNilledInMethod = nil
    }

    private func setToNilUsingSelf() {
        alreadyNilledInSelfMethod = nil
    }
}

extension NilInAnotherMethod {

    fileprivate func setToNilInExtension() {
        alreadyNilledInExtension = nil
    }
}