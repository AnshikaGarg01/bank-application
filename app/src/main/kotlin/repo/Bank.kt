package repo

import models.Account

interface Bank {
    fun addAccount(account: Account)
    fun getAccountInfoByAccountNumber(accountNumber: Long): Account?
    fun depositAmount(accountNumber: Long, amount: Long)
    fun withDrawAmount(accountNumber: Long, amount: Long)
    fun getAccountNumber(): Long
}
