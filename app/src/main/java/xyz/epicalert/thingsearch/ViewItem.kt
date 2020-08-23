package xyz.epicalert.thingsearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        itemUUID = UUID.fromString(intent.getStringExtra(EXTRA_ITEM_UUID))

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        itemViewModel.findById(itemUUID).observe(this, Observer {
            if (it != null) {
                findViewById<TextView>(R.id.item_details_name).text = it.name
                findViewById<TextView>(R.id.item_details_tags).text = it.tags
                findViewById<TextView>(R.id.item_details_uuid).text = itemUUID.toString()

                itemToView = it
            }
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

    //TODO: move database adding stuff to EditItem activity, DRY
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        val uuidItem = UUID.fromString(data?.getStringExtra(EXTRA_ITEM_UUID))
        val uuidParent = UUID.fromString(data?.getStringExtra(EXTRA_ITEM_PARENT))

        val newItem = Item(
            uuidItem.mostSignificantBits, uuidItem.leastSignificantBits,
            uuidParent.mostSignificantBits, uuidParent.leastSignificantBits,
            data?.getStringExtra(EXTRA_ITEM_NAME),
            data?.getStringExtra(EXTRA_ITEM_TAGS)
        )

        if (requestCode == REQUEST_CODE_ADD_ITEM) itemViewModel.insert(newItem)

        if (requestCode == REQUEST_CODE_EDIT_ITEM) itemViewModel.update(newItem)

        //refresh parent list
        itemViewModel.getParentList(itemUUID).observe(this, Observer {
            viewAdapter.setList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.view_item, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_view_add_child -> {
                val intent = Intent(this, EditItem::class.java)
                intent.putExtra(EXTRA_ITEM_PARENT, itemUUID.toString())
                intent.putExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_ADD_ITEM)
                startActivityForResult(intent, REQUEST_CODE_ADD_ITEM)
            }

            R.id.menu_view_edit -> {
                val intent = Intent(this, EditItem::class.java)

                intent.putExtra(EXTRA_ITEM_UUID, itemUUID.toString())
                intent.putExtra(EXTRA_ITEM_PARENT, UUID(itemToView.parent_m, itemToView.parent_l).toString()) //TODO: function in item class to create uuid and parent uuid
                intent.putExtra(EXTRA_ITEM_NAME, itemToView.name)
                intent.putExtra(EXTRA_ITEM_TAGS, itemToView.tags)

                intent.putExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_EDIT_ITEM)
                startActivityForResult(intent, REQUEST_CODE_EDIT_ITEM)
            }

            R.id.menu_view_delete -> {
                //TODO: "are you sure?" prompt before deleting
                //TODO: delete or move immediate children when parent is deleted
                itemViewModel.delete(itemToView)
                finish()
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}