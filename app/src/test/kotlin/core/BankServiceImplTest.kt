package core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import models.Account
import models.AccountHolder
import repo.Bank

class BankServiceImplTest : StringSpec() {
    private val bank = mockk<Bank>()
    private val bankService = BankServiceImpl()
    private val accountHolderName = "Roy Karp"
    private val accountNumber = 1L
    private val account = Account(
        accountNumber,
        500,
        AccountHolder(accountHolderName)
    )

    init {
        beforeEach { (_, _) ->
            bankService.bank = bank
            clearAllMocks()
        }

        "should create a bank account" {
            every { bank.getAccountNumber() } returns accountNumber
            every {
                bank.addAccount(account.copy(currentBalance = Constants.MINIMUM_ACCOUNT_BALANCE))
            } just runs

            bankService.createAccount(accountHolderName) shouldBe accountNumber

            verify {
                bank.getAccountNumber()
                bank.addAccount(account.copy(currentBalance = Constants.MINIMUM_ACCOUNT_BALANCE))
            }
        }

        "should get current balance by account number" {
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account

            bankService.getBalance(accountNumber) shouldBe 500

            verify {
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
        }

        "should throw exception when account does not exist" {
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns null

            val exception = shouldThrow<RuntimeException> {  bankService.getBalance(accountNumber) }
            exception.message shouldBe "Exception: Bank account with $accountNumber does not exist"

            verify {
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
        }

        "should deposit amount in the account for given account number" {
            every { bank.depositAmount(accountNumber, 2000) } just runs
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account.copy(currentBalance = 2500)

            bankService.depositAmount(accountNumber, 2000) shouldBe 2500

            verify {
                bank.depositAmount(accountNumber, 2000)
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
        }

        "should throw exception if account balance exceeds maximum amount limit" {
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account.copy(currentBalance = 95000)

            val exception = shouldThrow<RuntimeException> {  bankService.depositAmount(accountNumber, 50000) }
            exception.message shouldBe "Exception: Account balance cannot exceed than ${Constants.MAXIMUM_ACCOUNT_BALANCE}"

            verify {
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
            verify(exactly = 0) {
                bank.depositAmount(accountNumber, 2000)
            }
        }

        "should throw exception if deposit amount is invalid" {
            val exception = shouldThrow<RuntimeException> {  bankService.depositAmount(accountNumber, 60000) }
            exception.message shouldBe "Exception: Maximum deposit amount is ${Constants.MAXIMUM_DEPOSIT_AMOUNT} per transaction"

            val exceptionResult = shouldThrow<RuntimeException> {  bankService.depositAmount(accountNumber, 10) }
            exceptionResult.message shouldBe "Exception: Minimum deposit amount is ${Constants.MINIMUM_DEPOSIT_AMOUNT} per transaction"

            verify { bank wasNot called }
        }

        "should withdraw amount from an account" {
            every { bank.withDrawAmount(accountNumber, 1000) } just runs
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account.copy(currentBalance = 5000)

            bankService.withdrawAmount(accountNumber, 1000)

            verify {
                bank.withDrawAmount(accountNumber, 1000)
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
        }

        "should throw exception if withdraw amount is more than minimum balance" {
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account.copy(currentBalance = 500)

            val exception = shouldThrow<RuntimeException> {  bankService.withdrawAmount(accountNumber, 1000) }
            exception.message shouldBe "Exception: Account balance cannot be less than ${Constants.MINIMUM_ACCOUNT_BALANCE}"

            verify {
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
            verify(exactly = 0) {
                bank.withDrawAmount(accountNumber, 2000)
            }
        }

        "should throw exception if withdraw amount is invalid" {
            val exception = shouldThrow<RuntimeException> {  bankService.withdrawAmount(accountNumber, 50000) }
            exception.message shouldBe "Exception: Maximum withdrawal amount is ${Constants.MAXIMUM_WITHDRAWAL_AMOUNT} per transaction"

            val exceptionResult = shouldThrow<RuntimeException> {  bankService.withdrawAmount(accountNumber, 10) }
            exceptionResult.message shouldBe "Exception: Minimum withdrawal amount is ${Constants.MINIMUM_WITHDRAWAL_AMOUNT} per transaction"

            verify { bank wasNot called }
        }

        "should throw exception if maximum withdrawal count is reached" {
            every { bank.getAccountInfoByAccountNumber(accountNumber) } returns account.copy(numberOfWithdrawals = 3)

            val exception = shouldThrow<RuntimeException> { bankService.withdrawAmount(accountNumber, 1500) }

            exception.message shouldBe "Exception: No more than ${Constants.MAXIMUM_NUMBER_OF_WITHDRAWALS} withdrawals are allowed in a day"

            verify {
                bank.getAccountInfoByAccountNumber(accountNumber)
            }
            verify(exactly = 0) {
                bank.withDrawAmount(accountNumber, 1500)
            }
        }

        "should transfer amount from one account to another" {
            val sourceAccountNumber = 1L; val targetAccountNumber = 2L; val amount = 2000L
            val account1 = account.copy(currentBalance = 10000)
            val account2 = Account(2L, 1000, AccountHolder(accountHolderName))

            every { bank.getAccountInfoByAccountNumber(sourceAccountNumber) } returns account1
            every { bank.getAccountInfoByAccountNumber(targetAccountNumber) } returns account2
            every { bank.depositAmount(targetAccountNumber, amount) } just runs
            every { bank.withDrawAmount(sourceAccountNumber, amount) } just runs

            bankService.transferAmount(sourceAccountNumber, targetAccountNumber, amount) shouldBe true

            verify {
                bank.getAccountInfoByAccountNumber(sourceAccountNumber)
                bank.getAccountInfoByAccountNumber(targetAccountNumber)
                bank.depositAmount(targetAccountNumber, amount)
                bank.withDrawAmount(sourceAccountNumber, amount)
            }
        }

        "should throw exception if account does not have enough balance while transferring " {
            val sourceAccountNumber = 1L; val targetAccountNumber = 2L; val amount = 20000L
            val account1 = account.copy(currentBalance = 10000)

            every { bank.getAccountInfoByAccountNumber(sourceAccountNumber) } returns account1

            val exception = shouldThrow<RuntimeException> {
                bankService.transferAmount(sourceAccountNumber, targetAccountNumber, amount)
            }

            exception.message shouldBe "Exception: Account balance cannot be less than ${Constants.MINIMUM_ACCOUNT_BALANCE}"

            verify(exactly = 0) {
                bank.depositAmount(targetAccountNumber, amount)
                bank.withDrawAmount(sourceAccountNumber, amount)
            }
        }

        "should throw exception if deposit limit is reached while transferring" {
            val sourceAccountNumber = 1L; val targetAccountNumber = 2L; val amount = 2000L
            val account1 = account.copy(currentBalance = 10000)
            val account2 = Account(2L, 1000, AccountHolder(accountHolderName), 3)

            every { bank.getAccountInfoByAccountNumber(sourceAccountNumber) } returns account1
            every { bank.getAccountInfoByAccountNumber(targetAccountNumber) } returns account2

            val exception = shouldThrow<RuntimeException> {
                bankService.transferAmount(sourceAccountNumber, targetAccountNumber, amount)
            }

            exception.message shouldBe "Exception: No more than ${Constants.MAXIMUM_NUMBER_OF_DEPOSITS} deposits are allowed in a day"

            verify(exactly = 0) {
                bank.depositAmount(targetAccountNumber, amount)
                bank.withDrawAmount(sourceAccountNumber, amount)
            }

        }
    }
}
