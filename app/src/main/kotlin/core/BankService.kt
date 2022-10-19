package core

import dagger.Module
import dagger.Provides
import repo.BankImpl
import javax.inject.Singleton

interface BankService {
    fun createAccount(name: String): Long
    fun getBalance(accountNumber: Long): Long
    fun depositAmount(accountNumber: Long, amount: Long): Long
    fun withdrawAmount(accountNumber: Long, amount: Long): Long
    fun transferAmount(sourceAccountNumber: Long, targetAccountNumber: Long, amount: Long): Boolean
}

@Module
class BankServiceProvider{
    @Provides @Singleton fun bankServiceProvider(): BankService = BankServiceImpl(BankImpl())
}