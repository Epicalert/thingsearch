package xyz.epicalert.thingsearch

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.thread

@Entity(tableName = "items", primaryKeys = arrayOf("uuid_m", "uuid_l"))
data class Item(
    val uuid_m: Long,
    val uuid_l: Long,
    val parent_m: Long,
    val parent_l: Long,
    val name: String?,
    val tags: String?
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE uuid_m = :mostSignificant AND uuid_l = :leastSignificant LIMIT 1")
    fun findById(mostSignificant: Long, leastSignificant: Long): LiveData<Item>

    @Query("SELECT * FROM items WHERE uuid_m = :mostSignificant AND uuid_l = :leastSignificant LIMIT 1")
    fun findByIdSync(mostSignificant: Long, leastSignificant: Long): Item

    @Query("SELECT * FROM items")
    fun selectAll(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE name LIKE '%' + :query + '%'")
    fun search(query: String): LiveData<List<Item>>

    @Insert
    fun insertAll(vararg items: Item)

    @Delete
    fun delete(item: Item)
}

@Database(entities = arrayOf(Item::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "testdb").build()
            INSTANCE = instance
            return instance
        }
    }
}

class ItemRepository(private val itemDao: ItemDao) {
    val itemList: LiveData<List<Item>> = itemDao.selectAll()

    suspend fun insert(item: Item) {
        itemDao.insertAll(item)
    }

    fun findById(uuid: UUID): LiveData<Item> {
        return itemDao.findById(uuid.mostSignificantBits, uuid.leastSignificantBits)
    }

    fun findByIdSync(uuid: UUID): Item {
        return itemDao.findByIdSync(uuid.mostSignificantBits, uuid.leastSignificantBits)
    }

    fun search(query: String): LiveData<List<Item>> {
        return itemDao.search(query)
    }
}

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    val itemList: LiveData<List<Item>>

    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        itemList = repository.itemList
    }

    fun insert(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(item)
    }

    fun findById(uuid: UUID): LiveData<Item> {
        return repository.findById(uuid)
    }

    fun findByIdSync(uuid: UUID): Item {
        return repository.findByIdSync(uuid)
    }

    fun getParentList(uuid: UUID): LiveData<List<Item>> {
        val parentList = mutableListOf<Item>()
        val parentLiveList = MutableLiveData<List<Item>>()

        if (uuid == UUID(0,0)) {
            parentLiveList.value = parentList
            return parentLiveList
        }

        thread() {
            var item = findByIdSync(uuid)

            while (item.parent_m != 0L && item.parent_l != 0L) {
                item = findByIdSync(UUID(item.parent_m, item.parent_l))
                parentList.add(item)
            }

            parentLiveList.postValue(parentList)
        }

        return parentLiveList
    }

    fun search(query: String): LiveData<List<Item>> {
        return repository.search(query)
    }
}