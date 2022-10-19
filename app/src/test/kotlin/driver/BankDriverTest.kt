package driver

import core.BankService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BankDriverTest : StringSpec() {
    private val bankService = mockk<BankService>()
    private val bankDriver = BankDriver()
    init {
        beforeEach { (_, _) ->
            bankDriver.bankService = bankService
        }

        "should create a bank account" {
            every {
                bankService.createAccount("Roy Karp")
            } returns 1234

            bankDriver.performOperation("Create", listOf("Roy", "Karp")) shouldBe "1234"

            verify { bankService.createAccount("Roy Karp") }
        }

        "should display account balance" {
            every {
                bankService.getBalance(1234)
            } returns 2000

            bankDriver.performOperation("Balance", listOf("1234")) shouldBe "2000"

            verify { bankService.getBalance(1234) }
        }

        "should deposit amount in a account" {
            every {
                bankService.depositAmount(1234, 500)
            } returns 1000

            bankDriver.performOperation("Deposit", listOf("1234", "500")) shouldBe "1000"

            verify { bankService.depositAmount(1234, 500) }
        }

        "should withdraw amount from a account" {
            every {
                bankService.withdrawAmount(1234, 500)
            } returns 10000

            bankDriver.performOperation("Withdraw", listOf("1234", "500")) shouldBe "10000"

            verify { bankService.withdrawAmount(1234, 500) }
        }

        "should transfer amount from one account to another" {
            every {
                bankService.transferAmount(1, 2, 5000)
            } returns true

            bankDriver.performOperation("Transfer", listOf("1", "2", "5000"))

            verify {
                bankService.transferAmount(1, 2, 5000)
            }
        }

    }
}