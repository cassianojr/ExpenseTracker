package br.com.cassianojunior.expensetracker.controller

import br.com.cassianojunior.expensetracker.database.AppDatabase
import br.com.cassianojunior.expensetracker.model.Transaction
import kotlinx.coroutines.*

/**
 * This class deals directly with the database
 * @property AppDatabase the Room Database Abstract Class
 */
class TransactionController(
    private val db: AppDatabase
) {

    /**
     * Sums all the negative transactions
     * @param amounts the values of all transactions to be added
     * @return the sum of expenses
     */
    fun getExpense(
        amounts: List<Double>
    ): Double {
        val hasNegative = amounts.any { item -> item < 0 }
        return if (hasNegative) {
            (amounts.filter { item -> item < 0 }
                .reduce { sum, amount -> sum + amount }) * -1
        } else {
            0.00
        }
    }

    /**
     * Sums all the positive transactions
     * @param amounts the values of all transactions to be added
     * @return the sum of incomes
     */
    fun getIncome(
        amounts: List<Double>
    ): Double {
        val hasPositive = amounts.any { item -> item > 0 }
        return if (hasPositive) {
            amounts.filter { item -> item > 0 }.reduce { sum, amount -> sum + amount }
        } else {
            0.00
        }
    }

    /**
     * Get only the values of all transactions
     * @param transactions The list of transactions
     * @return A list of double that corresponds of the values of the transactions
     */
    fun getAmounts(transactions: List<Transaction>) =
        transactions.map { transaction -> transaction.amount!! }

    /**
     * Sums all values of the transactions
     * @param amounts the values of transactions
     * @return total sum of amounts
     */
    fun getTotal(amounts: List<Double>) = amounts.reduce { sum, amount -> sum + amount }

    /**
     * Delete a single transaction on the database. This fun should be use in a coroutine scope
     * @param transaction transacton that will be deleted
     */
    suspend fun deleteTransaction(transaction: Transaction) =
        withContext(Dispatchers.IO) {
            db.transactionDAO().delete(transaction)
        }

    /**
     * Get all the transactions on the database. This fun should be use in a coroutine scope
     * @return list of transactions
     */
    suspend fun getTransactions() = withContext(Dispatchers.IO) {
        db.transactionDAO().getAll()
    }

    /**
     * Insert a transaction in the database. This fun should be use in a coroutine scope
     * @param transaction The transaction that will be inserted.
     */
    suspend fun insertTransaction(transaction: Transaction) =
        withContext(Dispatchers.IO) {
            db.transactionDAO().insertAll(transaction)
        }

}