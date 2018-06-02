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

class NilInAnotherMethod: XCTestCase, AProtocol {

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
        nilledOutsideTearDown = nil
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
