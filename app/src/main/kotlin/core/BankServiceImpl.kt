package core

import models.Account
import models.AccountHolder
import repo.Bank
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankServiceImpl @Inject constructor(val bank: Bank): BankService {

     override fun createAccount(name: String): Long {
        val account = Account(
            bank.getAccountNumber(),
            Constants.MINIMUM_ACCOUNT_BALANCE,
            AccountHolder(name)
        )
        bank.addAccount(account)
        return account.accountNumber
    }

    override fun getBalance(accountNumber: Long): Long {
        return getAccount(accountNumber).currentBalance
    }

    override fun depositAmount(accountNumber: Long, amount: Long): Long {
        validateDepositAmountAndBalance(amount, accountNumber)
        bank.depositAmount(accountNumber, amount)
        return getBalance(accountNumber)
    }

    override fun withdrawAmount(accountNumber: Long, amount: Long): Long {
        validateWithdrawalAmountAndBalance(amount, accountNumber)
        bank.withDrawAmount(accountNumber, amount)
        return getBalance(accountNumber)
    }

    override fun transferAmount(sourceAccountNumber: Long, targetAccountNumber: Long, amount: Long): Boolean {
        validateWithdrawalAmountAndBalance(amount, sourceAccountNumber)
        validateDepositAmountAndBalance(amount, targetAccountNumber)
        bank.withDrawAmount(sourceAccountNumber, amount)
        bank.depositAmount(targetAccountNumber, amount)
        return true
    }

    private fun validateDepositAmountAndBalance(amount: Long, accountNumber: Long) {
        validateDepositAmount(amount)
        val account = getAccount(accountNumber)
        if (account.exceedsMaximumNumberOfDeposits())
            throw RuntimeException("Exception: No more than ${Constants.MAXIMUM_NUMBER_OF_DEPOSITS} deposits are allowed in a day")
        if (account.exceedsMaximumAccountBalance(amount))
            throw RuntimeException("Exception: Account balance cannot exceed than ${Constants.MAXIMUM_ACCOUNT_BALANCE}")
    }

    private fun validateWithdrawalAmountAndBalance(amount: Long, accountNumber: Long) {
        validateWithdrawalAmount(amount)
        val account = getAccount(accountNumber)
        if (account.exceedsMaximumNumberOfWithdrawals())
            throw RuntimeException("Exception: No more than ${Constants.MAXIMUM_NUMBER_OF_WITHDRAWALS} withdrawals are allowed in a day")
        if (account.belowMinimumAccountBalance(amount))
            throw RuntimeException("Exception: Account balance cannot be less than ${Constants.MINIMUM_ACCOUNT_BALANCE}")
    }

    private fun validateWithdrawalAmount(amount: Long) {
        if(amount > Constants.MAXIMUM_WITHDRAWAL_AMOUNT)
            throw RuntimeException("Exception: Maximum withdrawal amount is ${Constants.MAXIMUM_WITHDRAWAL_AMOUNT} per transaction")
        if(amount < Constants.MINIMUM_WITHDRAWAL_AMOUNT)
            throw RuntimeException("Exception: Minimum withdrawal amount is ${Constants.MINIMUM_WITHDRAWAL_AMOUNT} per transaction")
    }

    private fun validateDepositAmount(amount: Long) {
        if(amount > Constants.MAXIMUM_DEPOSIT_AMOUNT)
            throw RuntimeException("Exception: Maximum deposit amount is ${Constants.MAXIMUM_DEPOSIT_AMOUNT} per transaction")
        if(amount < Constants.MINIMUM_DEPOSIT_AMOUNT)
            throw RuntimeException("Exception: Minimum deposit amount is ${Constants.MINIMUM_DEPOSIT_AMOUNT} per transaction")
    }

    private fun getAccount(accountNumber: Long): Account {
        return bank.getAccountInfoByAccountNumber(accountNumber)
            ?: throw RuntimeException("Exception: Bank account with $accountNumber does not exist")
    }
}