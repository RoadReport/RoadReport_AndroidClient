package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import kotlinx.android.synthetic.main.activity_road.*
import java.util.*

class RoadActivity : AppCompatActivity() {

    var ROADCODE = -1

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()

        FirestoreManager().getAccident(ROADCODE)

        viewManager = LinearLayoutManager(this)
        viewAdapter = AccidentCardAdapter(
            mutableListOf<AccidentData>(
                AccidentData("", "", Timestamp(Date()), 5, "", "Test")

            )
        )

        recyclerView_road.apply {
            // Use a linear layout manager
            layoutManager = viewManager

            // Specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_road)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lateinit var toolbarTitle: String
        when (ROADCODE) {
            RoadCode.ROADCODE_24 -> toolbarTitle = getString(
                R.string.roadName_24
            )
            RoadCode.ROADCODE_182 -> toolbarTitle = getString(
                R.string.roadName_182
            )
            else -> toolbarTitle = getString(R.string.all_unknownError)
        }

        textView_road_toolbarTitle.text = toolbarTitle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_road, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_addAccident -> {
                if (FirebaseAuthHelper().checkSignInStatus()) {
                    RoadCode().startActivityWithCode(
                        this,
                        AccidentEventActivity(), ROADCODE
                    )
//                startActivity(Intent(this, AccidentEventActivity::class.java))
                } else {
                    Util().toast(this, getString(R.string.roadActivity_SignInFirst))
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}