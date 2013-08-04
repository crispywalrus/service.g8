import org.scalatest._

class StubFunSpec extends FunSpec {
  describe("My class") {
    it("""should do something interesting""") (pending)
    it("""shouldn't throw exceptions""") (pending)
  }
}

class StubSpec extends FlatSpec {

  "My Class" should "do something interesting" in {
    is (pending)
  }
}
