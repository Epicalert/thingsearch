package xyz.epicalert.thingsearch

class ItemSearcher() {
    lateinit var itemList: List<Item>

    fun setList(newList: List<Item>) {
        itemList = newList
    }

    fun searchNameAndTags(query: String): List<Item> {
        val resultingList = mutableListOf<Item>()

        val lowercaseQuery = query.toLowerCase()

        for (item in itemList) {
            val searchables = item.name?.toLowerCase() + " " + item.tags?.toLowerCase()

            if (query in searchables) {
                resultingList.add(item)
            }
        }

        return resultingList
    }
}