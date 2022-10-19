package driver

import core.BankService
import core.BankServiceProvider
import dagger.Component
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankDriver @Inject constructor() {
    @Inject lateinit var bankService: BankService

    fun getOperation(input: String): Pair<String, List<String>>{
        val inputCommand = input.split(" ")
        return Pair(inputCommand.first(), inputCommand.drop(1))
    }

    fun performOperation(operation: String, inputParameters: List<String>): String{
        try {
            when (operation) {
                "Create" -> {
                    val accountHolderName = inputParameters[0].plus(" ") + inputParameters[1]
                    val accountNumber = bankService.createAccount(accountHolderName)
                    return accountNumber.toString()
                }
                "Balance" -> {
                    val accountNumber = inputParameters[0].toLong()
                    val currentBalance = bankService.getBalance(accountNumber)
                    return currentBalance.toString()
                }
                "Deposit" -> {
                    val accountNumber = inputParameters[0].toLong()
                    val depositAmount = inputParameters[1].toLong()
                    val balanceAfterDeposit = bankService.depositAmount(accountNumber, depositAmount)
                    return balanceAfterDeposit.toString()
                }
                "Withdraw" -> {
                    val accountNumber = inputParameters[0].toLong()
                    val withdrawalAmount = inputParameters[1].toLong()
                    val balanceAfterWithdrawal = bankService.withdrawAmount(accountNumber, withdrawalAmount)
                    return balanceAfterWithdrawal.toString()
                }
                "Transfer" -> {
                    val sourceAccountNumber = inputParameters[0].toLong()
                    val targetAccountNumber = inputParameters[1].toLong()
                    val transferAmount = inputParameters[2].toLong()
                    val amountTransferred =
                        bankService.transferAmount(sourceAccountNumber, targetAccountNumber, transferAmount)
                    return if (amountTransferred) "Successful" else "Failure"
                }
                else -> return "Invalid Operation"
            }
        }catch (exception: RuntimeException){
            return exception.message!!
        }
    }
    fun isExitCommand(command: String): Boolean = command == "Exit"

    companion object{
        const val INPUT_FILE_PATH = "/Users/anshikagarg/work/bank-application/app/src/main/kotlin/driver/input.txt"
        const val OUTPUT_FILE_PATH = "/Users/anshikagarg/work/bank-application/app/src/main/kotlin/driver/output.txt"
    }
}

@Singleton
@Component(modules = [BankServiceProvider::class])
interface Bank{
    fun maker(): BankDriver
}

fun main() {
    val bankDriver = DaggerBank.builder().build()
    val inputFromFile = File(BankDriver.INPUT_FILE_PATH).readLines()
    inputFromFile.forEach { input ->
            val inputParameters = bankDriver.maker().getOperation(input)
            if (!bankDriver.maker().isExitCommand(inputParameters.first)) {
                val result = bankDriver.maker().performOperation(inputParameters.first, inputParameters.second)
                val outputFile = FileWriter(BankDriver.OUTPUT_FILE_PATH, true)
                outputFile.use {
                    it.write("$input\n $result\n")
                    it.close()
                }
            }
    }
}
