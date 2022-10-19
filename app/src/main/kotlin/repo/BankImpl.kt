package repo

import models.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankImpl @Inject constructor(): Bank {
    private var accounts: MutableList<Account> = mutableListOf()

    override fun addAccount(account: Account) {
        accounts.add(account)
    }

    override fun getAccountInfoByAccountNumber(accountNumber: Long): Account? {
        return accounts.firstOrNull { it.accountNumber == accountNumber }
    }

    override fun depositAmount(accountNumber: Long, amount: Long) {
        val account = accounts.first { it.accountNumber == accountNumber }
        account.currentBalance += amount
        account.numberOfDeposits++
    }

    override fun withDrawAmount(accountNumber: Long, amount: Long) {
        val account = accounts.first { it.accountNumber == accountNumber }
        account.currentBalance -= amount
        account.numberOfWithdrawals++
    }

    override fun getAccountNumber(): Long {
        numberOfAccounts += 1
        return numberOfAccounts
    }

    companion object {
        var numberOfAccounts: Long = 0
    }

}

