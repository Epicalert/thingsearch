package xyz.epicalert.thingsearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.util.*
import java.util.UUID.*

class EditItem : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<EditText>(R.id.id).setText(UUID.randomUUID().toString())
        findViewById<EditText>(R.id.parent_id).setText(UUID(0,0).toString())
    }

    fun applyChanges(view: View) {
        val replyIntent = Intent()

        if (TextUtils.isEmpty(findViewById<TextView>(R.id.id).text) ||
            TextUtils.isEmpty(findViewById<TextView>(R.id.parent_id).text)) {
            Toast.makeText(applicationContext, "Cancelled because of empty ID", Toast.LENGTH_SHORT).show()

            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        }

        replyIntent.putExtra(EXTRA_ITEM_NAME, findViewById<TextView>(R.id.name).text.toString())
        replyIntent.putExtra(EXTRA_ITEM_TAGS, findViewById<TextView>(R.id.tags).text.toString())
        replyIntent.putExtra(EXTRA_ITEM_UUID, findViewById<TextView>(R.id.id).text.toString())
        replyIntent.putExtra(EXTRA_ITEM_PARENT, findViewById<TextView>(R.id.parent_id).text.toString())

        setResult(Activity.RESULT_OK, replyIntent)

        finish()
    }

    companion object {
        //TODO: put constants in their own class
        const val EXTRA_ITEM_NAME = "xyz.epicalert.thingsearch.EXTRA_ITEM_NAME"
        const val EXTRA_ITEM_TAGS = "xyz.epicalert.thingsearch.EXTRA_ITEM_TAGS"
        const val EXTRA_ITEM_UUID = "xyz.epicalert.thingsearch.EXTRA_ITEM_UUID"
        const val EXTRA_ITEM_PARENT = "xyz.epicalert.thingsearch.EXTRA_ITEM_PARENT"
    }
}