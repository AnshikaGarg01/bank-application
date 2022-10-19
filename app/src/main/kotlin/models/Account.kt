package models

import core.Constants

data class Account(
    val accountNumber: Long,
    var currentBalance: Long,
    val accountHolder: AccountHolder,
    var numberOfDeposits: Long = 0,
    var numberOfWithdrawals: Long = 0
){
    fun exceedsMaximumAccountBalance(depositAmount: Long) =
        (currentBalance + depositAmount) > Constants.MAXIMUM_ACCOUNT_BALANCE

    fun exceedsMaximumNumberOfDeposits() =
        numberOfDeposits >= Constants.MAXIMUM_NUMBER_OF_DEPOSITS

    fun belowMinimumAccountBalance(withdrawalAmount: Long) =
        (currentBalance - withdrawalAmount) < Constants.MINIMUM_ACCOUNT_BALANCE

    fun exceedsMaximumNumberOfWithdrawals() =
        numberOfWithdrawals >= Constants.MAXIMUM_NUMBER_OF_WITHDRAWALS
}