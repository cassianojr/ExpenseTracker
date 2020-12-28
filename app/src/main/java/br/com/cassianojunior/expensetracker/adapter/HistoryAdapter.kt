package br.com.cassianojunior.expensetracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.cassianojunior.expensetracker.R
import br.com.cassianojunior.expensetracker.model.Transaction
import java.text.NumberFormat
import java.util.*

/**
 * The adapter class for the History Reclycler View
 * @property context The app context
 * @property dataSource the list of transactions that will be viewed
 */
class HistoryAdapter(private val context: Context, private val dataSource: List<Transaction>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName = view.findViewById(R.id.list_item_name) as TextView
        val itemAmount = view.findViewById(R.id.list_item_amount) as TextView
        val itemBorder = view.findViewById(R.id.list_item_border) as FrameLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView = inflater.inflate(R.layout.history_list_item, parent, false)

        return ViewHolder(rowView)
    }

    override fun getItemCount() = dataSource.size

    fun getItemAt(position: Int) = dataSource[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = dataSource[position]

        holder.itemName.text = transaction.name

        holder.itemAmount.text = context.getString(
            R.string.list_item_amount_placeholder,
            transaction.amount!!.formatCurrency()
        )

        val background = if(transaction.amount > 0){ R.drawable.shape_income} else{R.drawable.shape_expense}
        holder.itemBorder.background = ContextCompat.getDrawable(context, background)

    }

    /**
     * Format a double for the BR currency
     */
    private fun Double.formatCurrency(): String {
        val locale = Locale("pt", "BR")
        return NumberFormat.getCurrencyInstance(locale).format(this)
    }
}