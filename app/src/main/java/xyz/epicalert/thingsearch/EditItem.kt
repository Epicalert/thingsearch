package xyz.epicalert.thingsearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import org.w3c.dom.Text
import java.util.*
import java.util.UUID.*

class EditItem : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        setSupportActionBar(findViewById(R.id.toolbar))


        val requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_ADD_ITEM)

        //set EditText values
        when (requestCode) {
            //generate UUID and set parent if provided
            REQUEST_CODE_ADD_ITEM -> {
                findViewById<EditText>(R.id.id).setText(UUID.randomUUID().toString())
                if (intent.getStringExtra(EXTRA_ITEM_PARENT) == null) {
                    findViewById<EditText>(R.id.parent_id).setText(UUID(0,0).toString())
                } else {
                    findViewById<EditText>(R.id.parent_id).setText(intent.getStringExtra(EXTRA_ITEM_PARENT).toString())
                }
            }

            //copy all item info
            REQUEST_CODE_EDIT_ITEM -> {
                findViewById<EditText>(R.id.id).setText(intent.getStringExtra(EXTRA_ITEM_UUID).toString())
                findViewById<EditText>(R.id.parent_id).setText(intent.getStringExtra(EXTRA_ITEM_PARENT).toString())
                findViewById<EditText>(R.id.name).setText(intent.getStringExtra(EXTRA_ITEM_NAME).toString())
                findViewById<EditText>(R.id.tags).setText(intent.getStringExtra(EXTRA_ITEM_TAGS).toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val scanResult = IntentIntegrator.parseActivityResult(resultCode, data) ?: return

        when (requestCode) {
            REQUEST_CODE_SCAN_UUID -> {
                findViewById<EditText>(R.id.id).setText(scanResult.contents)
            }

            REQUEST_CODE_SCAN_PARENT -> {
                findViewById<EditText>(R.id.parent_id).setText(scanResult.contents)
            }
        }
    }

    fun applyChanges(view: View) {
        val replyIntent = Intent()

        if (TextUtils.isEmpty(findViewById<TextView>(R.id.id).text) ||
            TextUtils.isEmpty(findViewById<TextView>(R.id.parent_id).text)) {
            Toast.makeText(applicationContext, R.string.error_empty_id, Toast.LENGTH_SHORT).show()

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

    fun scanQR(view: View) {
        val integrator = IntentIntegrator(this)

        //set request code
        when (view.id) {
            R.id.id_qr -> {
                integrator.setRequestCode(REQUEST_CODE_SCAN_UUID)
            }

            R.id.parent_id_qr -> {
                integrator.setRequestCode(REQUEST_CODE_SCAN_PARENT)
            }
        }

        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }
}