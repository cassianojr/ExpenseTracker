package br.com.cassianojunior.expensetracker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import br.com.cassianojunior.expensetracker.adapter.HistoryAdapter
import br.com.cassianojunior.expensetracker.controller.TransactionController
import br.com.cassianojunior.expensetracker.database.AppDatabase
import br.com.cassianojunior.expensetracker.model.Transaction
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var txtBalance: TextView
    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView
    private lateinit var txtPlaceholderRecycler: TextView

    private lateinit var txtName: EditText
    private lateinit var txtAmount: EditText
    private lateinit var btnAddTransaction: Button

    private lateinit var listHistory: RecyclerView

    private lateinit var transactions: List<Transaction>

    private lateinit var controller:TransactionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init the views
        txtBalance = findViewById(R.id.txt_balance)
        txtIncome = findViewById(R.id.txt_income)
        txtExpense = findViewById(R.id.txt_expense)
        txtName = findViewById(R.id.txt_name)
        txtAmount = findViewById(R.id.txt_amount)
        btnAddTransaction = findViewById(R.id.btn_add_transaction)
        txtPlaceholderRecycler = findViewById(R.id.recycler_view_placeholder)

        listHistory = findViewById(R.id.list_history)

        //instantiate the database object
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "expense-tracker"
        ).build()

        //init the controller class
        controller = TransactionController(db)

        updateView()

        btnAddTransaction.setOnClickListener {
            if (txtName.text.isEmpty() || txtAmount.text.isEmpty()) return@setOnClickListener

            val name: String = txtName.text.toString()
            val amount: Double = java.lang.Double.parseDouble(txtAmount.text.toString())

            val transaction = Transaction(null, name, amount)

            (this@MainActivity as CoroutineScope).launch {
                controller.insertTransaction(transaction)
            }.invokeOnCompletion {
                updateView()
            }

            txtName.setText("")
            txtAmount.setText("")
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = listHistory.adapter as HistoryAdapter
                val transaction = adapter.getItemAt(viewHolder.adapterPosition)

                (this@MainActivity as CoroutineScope).launch {
                    controller.deleteTransaction(transaction)
                }.invokeOnCompletion {
                    updateView()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(listHistory)

    }

    /**
     *  Update the recycler view with all transactions and calculate balance, income and expenses
     */
    private fun updateView() {

        (this@MainActivity as CoroutineScope).launch {
            transactions = controller.getTransactions()

            if (transactions.isNotEmpty()) {

                val amounts = controller.getAmounts(transactions)
                val balance = controller.getTotal(amounts)

                val color = if (balance >= 0) {
                    R.color.green
                } else {
                    R.color.red
                }
                txtBalance.setTextColor(ContextCompat.getColor(this@MainActivity, color))

                val income = controller.getIncome(amounts)
                val expense = controller.getExpense(amounts)

                setValues(balance.formatCurrency(), income.formatCurrency(), (expense*-1).formatCurrency())

                val adapter = HistoryAdapter(applicationContext, transactions)
                listHistory.adapter = adapter

            } else {
                setValues()
            }

            listHistory.visibility = shouldBeVisible(transactions.isNotEmpty())
            txtPlaceholderRecycler.visibility = shouldBeVisible(transactions.isEmpty())
        }
    }

    /**
     * Set the text for the passed values, by default will set all values for R$ 0,00
     */
    private fun setValues(balance:String = "R$ 0,00", income:String = "R$ 0,00", expense:String = "R$ 0,00"){
        txtBalance.text = getString(R.string.txt_balance, balance)
        txtIncome.text = getString(R.string.txt_income, income)
        txtExpense.text = getString(R.string.txt_expense, expense)
    }

    /**
     * Verifies if the view should be visible at the activity
     */
    private fun shouldBeVisible(condition:Boolean): Int {
        return if (condition) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * Format a double for the BR currency
     */
    private fun Double.formatCurrency(): String {
        val locale = Locale("pt", "BR")
        return NumberFormat.getCurrencyInstance(locale).format(this)
    }

}
