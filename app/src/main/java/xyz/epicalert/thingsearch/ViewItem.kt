package xyz.epicalert.thingsearch

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ViewItem : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var itemViewModel: ItemViewModel

    private lateinit var itemToView: Item
    private lateinit var itemUUID: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemUUID = UUID.fromString(intent.getStringExtra(EditItem.EXTRA_ITEM_UUID))

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        itemViewModel.findById(itemUUID).observe(this, Observer {
            findViewById<TextView>(R.id.item_details_name).text = it.name
            findViewById<TextView>(R.id.item_details_tags).text = it.tags
            findViewById<TextView>(R.id.item_details_uuid).text = itemUUID.toString()
        })

        viewManager = LinearLayoutManager(this)
        viewAdapter = ItemListAdapter()

        itemViewModel.getParentList(itemUUID).observe(this, Observer {
            viewAdapter.setList(it)
        })

        recyclerView = findViewById<RecyclerView>(R.id.item_details_parents).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter
        }
    }
}