package br.com.cassianojunior.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name="name") val name:String?,
    @ColumnInfo(name="amount") val amount: Double?
)