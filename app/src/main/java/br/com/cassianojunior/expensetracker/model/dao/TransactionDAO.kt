package br.com.cassianojunior.expensetracker.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.cassianojunior.expensetracker.model.Transaction

/**
 * This is the Data Acess Object Class for a Transaction Object
 */
@Dao
interface TransactionDAO {

    @Query("SELECT * FROM `transaction` ORDER BY id DESC")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transactions: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}