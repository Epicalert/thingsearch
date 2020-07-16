package xyz.epicalert.thingsearch

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.security.AccessController.getContext
import java.util.*

class ItemListAdapter():
    RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {
    private var itemList = emptyList<Item>()

    //class for each viewholder in the recycleview
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameView = view.findViewById<TextView>(R.id.item_view_name)
        val tagsView = view.findViewById<TextView>(R.id.item_view_tags)
        val uuidView = view.findViewById<TextView>(R.id.item_view_uuid)
    }

    //creates the viewholder from the item_view layout
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ItemListAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)

        return ItemViewHolder(view)
    }

    internal fun setList(newList: List<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = itemList.size

    //puts item data into the viewholder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.nameView.text = item.name
        holder.tagsView.text = item.tags
        holder.uuidView.text = UUID(item.uuid_m, item.uuid_l).toString()
    }
}