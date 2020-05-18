package com.txwstudio.app.roadreport.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.txwstudio.app.roadreport.*
import com.txwstudio.app.roadreport.model.Accident
import kotlinx.android.synthetic.main.activity_road.*
import kotlinx.android.synthetic.main.road_accident_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class RoadActivity : AppCompatActivity() {

    var ROADCODE = -1

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapterForTest: RecyclerView.Adapter<*>
    private lateinit var viewAdapterRealtime: FirestoreRecyclerAdapter<*, *>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        accidentStartListening()
    }

    override fun onPause() {
        super.onPause()
        accidentStopListening()
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
                if (FirebaseAuthHelper().userIsSignedIn()) {
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

    private fun setupToolBar() {
        setSupportActionBar(toolbar_road)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        textView_road_toolbarTitle.text = RoadCode().getCurrRoadName(this)
    }

    private fun setupRecyclerView() {
        // Version 1 - Get data once from firestore
        FirestoreManager().getAccident(ROADCODE)


        // Version 2 - Use firestore ui handle realtime update
        val options = FirestoreManager().getRealtimeAccidentQuery(ROADCODE)

        viewAdapterRealtime =
            object : FirestoreRecyclerAdapter<Accident?, AccidentCardHolder?>(options) {
                override fun onCreateViewHolder(group: ViewGroup, i: Int): AccidentCardHolder {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.road_accident_row, group, false)
                    return AccidentCardHolder(view)
                }

                override fun onBindViewHolder(
                    holder: AccidentCardHolder,
                    position: Int, model: Accident
                ) {
                    val formattedTime = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                        .format(model.time.toDate())

                    //TODO: Situation Type class
                    when (model.situationType.toInt()) {
                        1 -> {
                            holder.layout.background = getDrawable(R.drawable.bg_accident_type_1)
                            holder.situationType.text =
                                getString(R.string.accidentEvent_situationType_1)
                        }
                        2 -> {
                            holder.layout.background = getDrawable(R.drawable.bg_accident_type_2)
                            holder.situationType.text =
                                getString(R.string.accidentEvent_situationType_2)
                        }
                        3 -> {
                            holder.layout.background = getDrawable(R.drawable.bg_accident_type_3)
                            holder.situationType.text =
                                getString(R.string.accidentEvent_situationType_3)
                        }
                        4 -> {
                            holder.layout.background = getDrawable(R.drawable.bg_accident_type_4)
                            holder.situationType.text =
                                getString(R.string.accidentEvent_situationType_4)
                        }
                    }
//        holder.situationType.text = accidentMutableList[position].situationType.toString()
                    holder.location.text = model.location
                    holder.situation.text = model.situation
                    holder.time.text = formattedTime
                    holder.userName.text = model.userName
                }
            }


        // Test data 1 - Testing recycler view using static mutableList data.
        viewAdapterForTest = AccidentCardAdapter(
            this,
            mutableListOf<Accident>(
                Accident(
                    "你媽ㄋㄟㄋㄟ",
                    "",
                    Timestamp(Date()),
                    1,
                    "這有五個字這也五個字",
                    "這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下這是一條特別特別特別長的訊息，不知道能不能塞的下"
                ),
                Accident(
                    "土屋和幾",
                    "",
                    Timestamp(Date()),
                    2,
                    "過檢查哨",
                    "有釘子，慢行"
                ),
                Accident(
                    "Trevor WWWW",
                    "",
                    Timestamp(Date()),
                    3,
                    "紅門",
                    "正在灑釘子"
                ),
                Accident(
                    "Trevor Wu",
                    "",
                    Timestamp(Date()),
                    4,
                    "養雞場",
                    "有野雞"
                )
            )
        )


        viewManager = LinearLayoutManager(this)

        recyclerView_road.apply {
            // Use a linear layout manager
            layoutManager = viewManager

            // Specify an viewAdapter (see also next example)
            adapter = viewAdapterRealtime
        }
    }

    private fun accidentStartListening() {
        viewAdapterRealtime.startListening()
        // TODO: Accident update interval
    }

    private fun accidentStopListening() = viewAdapterRealtime.stopListening()

}