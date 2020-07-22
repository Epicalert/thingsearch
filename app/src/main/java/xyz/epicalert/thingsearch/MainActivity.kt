package xyz.epicalert.thingsearch

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ItemListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var itemViewModel: ItemViewModel

    private lateinit var itemSearcher: ItemSearcher

    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testUUID = UUID.randomUUID()

        itemSearcher = ItemSearcher()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ItemListAdapter()

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        itemViewModel.itemList.observe(this, androidx.lifecycle.Observer {
            itemSearcher.setList(it)
            updateSearchQuery(lastQuery)
        })

        recyclerView = findViewById<RecyclerView>(R.id.item_list).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView

        val textListener = object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true // you expected code in this function but it was me, dio
            }

            override fun onQueryTextChange(newText: String): Boolean {
                updateSearchQuery(newText)
                return true
            }
        }
        searchView.setOnQueryTextListener(textListener)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_ITEM && resultCode == Activity.RESULT_OK) {
            val uuidItem = UUID.fromString(data?.getStringExtra(EXTRA_ITEM_UUID))
            val uuidParent = UUID.fromString(data?.getStringExtra(EXTRA_ITEM_PARENT))

            val newItem = Item(
                uuidItem.mostSignificantBits, uuidItem.leastSignificantBits,
                uuidParent.mostSignificantBits, uuidParent.leastSignificantBits,
                data?.getStringExtra(EXTRA_ITEM_NAME),
                data?.getStringExtra(EXTRA_ITEM_TAGS)
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
        val item = viewAdapter.getList()[recyclerView.getChildAdapterPosition(view)]
        val uuid = UUID(item.uuid_m, item.uuid_l)

        val intent = Intent(this, ViewItem::class.java)
        intent.putExtra(EXTRA_ITEM_UUID, uuid.toString())
        startActivity(intent)
    }

    fun updateSearchQuery(query: String) {
        val searchResults = itemSearcher.searchNameAndTags(query)
        viewAdapter.setList(searchResults)

        lastQuery = query
    }
}