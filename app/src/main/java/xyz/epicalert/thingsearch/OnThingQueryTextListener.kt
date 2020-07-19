package xyz.epicalert.thingsearch

import androidx.appcompat.widget.SearchView

class OnThingQueryTextListener : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        TODO("Not yet implemented")
    }

}