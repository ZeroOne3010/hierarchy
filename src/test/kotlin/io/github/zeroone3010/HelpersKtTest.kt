package io.github.zeroone3010

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class HelpersKtTest : StringSpec() {
    init {
        "zeroBasedIntToLetter should convert correctly" {
            zeroBasedIntToLetter(0).toString() shouldBe("a")
            zeroBasedIntToLetter(1).toString() shouldBe("b")
            zeroBasedIntToLetter(2).toString() shouldBe("c")
            zeroBasedIntToLetter(3).toString() shouldBe("d")
        }

        "letterToZeroBasedInt should convert correctly" {
            letterToZeroBasedInt('a') shouldBe 0
            letterToZeroBasedInt('b') shouldBe 1
            letterToZeroBasedInt('c') shouldBe 2
            letterToZeroBasedInt('d') shouldBe 3
        }
    }

}
