import XCTest

class <weak_warning descr="Not all properties are set to nil in the tear down.">NilInAnotherMethod<caret></weak_warning>: XCTestCase {

    var nilledOutsideTearDown: String?
    var nilledInTheTearDown: String?
    var alreadyNilledInMethod: String?
    var alreadyNilledInExtension: String?
    var alreadyNilledInSelfMethod: String?

    override func tearDown() {

        setToNil()
        setToNilInExtension()
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
