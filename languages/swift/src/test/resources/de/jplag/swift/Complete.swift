import Foundation

private let fileConstant = 0

public class MyClass {
    public static let classConstant = 0.0
    
    private let x: Int

    public var name: String = "" {
        willSet {
            precondition(!name.isEmpty, "name must not be empty")
        }
        didSet {
            nameLength = name.count
        }
    } //NO TOKEN

    public var optionalName: String? {
        didSet {
            nameLength = name.count
        }
    } //NO TOKEN

    private var nameLength = 0
    private let a = "A"

    public init(name: String) {
        self.name = name
        self.x = 0
    }

    func computeIteratively(max: Int) -> Int {
        var sum = 0
        for i in x..<max {
            if i == 0 {
                continue
            }
            sum += i
        }

        var i = x
        while i < max {
            sum -= i
        }

        i = x
        repeat {
            sum *= i
        } while i < max

        switch max {
        case 0:
            sum = 0
        case 1:
            break
        case 2:
            fallthrough
        default:
            sum /= 2
        }

        sum += (x..<max).reduce(0, +)
        return sum
    }

    func textLength() -> (String) -> Int {
        let closure: (String) -> Int = { text in
            return text.count
        }
        return closure
    }
}

private struct MyStruct {
    var fullName: String
    var nameComponents: [String] { fullName.components(separatedBy: " ") }
    var settableNameComponents: [String] {
        get { nameComponents }
        set { fullName = newValue.joined(separator: " ") }
    } //NO TOKEN

    func hasMultipleComponents() -> Bool {
        defer {
            print("later")
        }
        if nameComponents.count > 1 {
            return true
        } else if settableNameComponents.count > 1 {
            return true
        } else {
            return false
        }
    }
}

enum MyEnum: String {
    case one = "ONE"

    var value: Int {
        switch self {
        case .one: return 1
        }
    }

    func throwAny() throws {
        throw NSError(domain: "error", code: -1)
    }

    func catchSome() throws {
        let _ = try? throwAny()
        do {
            try throwAny()
        }
        catch let error {
            print(error)
        }
        let _ = try! throwAny()
        do {
            try throwAny()
        }
    }
}

enum ListEnum {
    case a, b, c
}

indirect enum Tree<T> {
    case leaf(value: T)
    case node(value: T, children: [Tree])
}

protocol MyProtocol {
    var property: String { get set }

    init(property: String)
    func doSomething(_ x: Int, y: Int)
}
