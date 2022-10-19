package repo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import models.Account
import models.AccountHolder

class BankTest: StringSpec() {
    private val bank = BankImpl()
    private val account = Account(1, 500, AccountHolder("Roy Karp"))
    init {
        "should get a new account number" {
            bank.getAccountNumber() shouldBe 1
            bank.getAccountNumber() shouldBe 2
        }

        "should get account information by account number"{
            bank.addAccount(account)

            bank.getAccountInfoByAccountNumber(1) shouldBe account
        }

        "should deposit amount to the account" {
            bank.addAccount(account)

            bank.depositAmount(1, 1000)

            val account = bank.getAccountInfoByAccountNumber(1)

            account!!.currentBalance shouldBe 1500
            account.numberOfDeposits shouldBe 1
        }

        "should withdraw amount from the account" {
            val account1 = Account(3, 500, AccountHolder("Roy Karp"))
            val account2 = Account(4, 1500, AccountHolder("Roy Karp"))
            bank.addAccount(account1)
            bank.addAccount(account2)

            bank.withDrawAmount(3, 100)
            bank.withDrawAmount(3, 100)
            bank.withDrawAmount(4, 400)

            var account = bank.getAccountInfoByAccountNumber(3)
            account!!.currentBalance shouldBe 300
            account.numberOfWithdrawals shouldBe 2

            account = bank.getAccountInfoByAccountNumber(4)
            account!!.currentBalance shouldBe 1100
            account.numberOfWithdrawals shouldBe 1

        }
    }
}