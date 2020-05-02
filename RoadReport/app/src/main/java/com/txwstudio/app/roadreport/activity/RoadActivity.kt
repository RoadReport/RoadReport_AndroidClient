package com.txwstudio.app.roadreport.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.txwstudio.app.roadreport.*
import com.txwstudio.app.roadreport.model.Accident
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
            this,
            mutableListOf<Accident>(
                Accident(
                    "果凍",
                    "",
                    Timestamp(Date()),
                    1,
                    "這有五個字這也五個字",
                    "這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下"
                ),
                Accident(
                    "捏捏",
                    "",
                    Timestamp(Date()),
                    2,
                    "過檢查哨",
                    "有釘子，慢行"
                ),
                Accident(
                    "肉肉",
                    "",
                    Timestamp(Date()),
                    3,
                    "紅門",
                    "正在灑釘子"
                ),
                Accident(
                    "軟軟",
                    "",
                    Timestamp(Date()),
                    4,
                    "野店",
                    "有野雞"
                )
            )
        )

        recyclerView_road.apply {
            // Use a linear layout manager
            layoutManager = viewManager

            // Specify an viewAdapter (see also next example)
            adapter = viewAdapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_road)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textView_road_toolbarTitle.text = RoadCode().getCurrRoadName(this)
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