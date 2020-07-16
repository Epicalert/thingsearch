package xyz.epicalert.thingsearch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.util.*

const val REQUEST_CODE_EDIT_ITEM = 101
const val REQUEST_CODE_ADD_ITEM = 102

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testUUID = UUID.randomUUID()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ItemListAdapter()

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        itemViewModel.itemList.observe(this, androidx.lifecycle.Observer {list ->
            list?.let {viewAdapter.setList(it)}
        })

        recyclerView = findViewById<RecyclerView>(R.id.item_list).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_ITEM && resultCode == Activity.RESULT_OK) {
            val uuidItem = UUID.fromString(data?.getStringExtra(EditItem.EXTRA_ITEM_UUID))
            val uuidParent = UUID.fromString(data?.getStringExtra(EditItem.EXTRA_ITEM_PARENT))

            val newItem = Item(
                uuidItem.mostSignificantBits, uuidItem.leastSignificantBits,
                uuidParent.mostSignificantBits, uuidParent.leastSignificantBits,
                data?.getStringExtra(EditItem.EXTRA_ITEM_NAME),
                data?.getStringExtra(EditItem.EXTRA_ITEM_TAGS)
            )

            itemViewModel.insert(newItem)
        }
    }

    fun gotothething(view: View) {
        Log.i("MainActivity", "goin to the thing")
        val intent = Intent(this, EditItem::class.java)

        startActivityForResult(intent, REQUEST_CODE_ADD_ITEM)
    }

    fun editItem(view: View) {
        val item = itemViewModel.itemList.value?.get(recyclerView.getChildLayoutPosition(view))?: return
        val uuid = UUID(item.uuid_m, item.uuid_l)

        val intent = Intent(this, ViewItem::class.java)
        intent.putExtra(EditItem.EXTRA_ITEM_UUID, uuid.toString())

        startActivity(intent)
    }
}