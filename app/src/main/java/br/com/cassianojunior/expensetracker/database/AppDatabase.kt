package br.com.cassianojunior.expensetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.cassianojunior.expensetracker.model.Transaction
import br.com.cassianojunior.expensetracker.model.dao.TransactionDAO

/**
 * This is a simple Room Database abstract class
 */
@Database(entities = [Transaction::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
}