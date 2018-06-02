import XCTest

class NotAvailableNilInAnotherMethod: XCTestCase, AProtocol {

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

    private func setToNil() {
        alreadyNilledInMethod = nil
    }

    private func setToNilUsingSelf() {
        alreadyNilledInSelfMethod = nil
    }
}

extension NotAvailableNilInAnotherMethod {

    fileprivate func setToNilInExtension() {
        alreadyNilledInExtension = nil
    }
}
