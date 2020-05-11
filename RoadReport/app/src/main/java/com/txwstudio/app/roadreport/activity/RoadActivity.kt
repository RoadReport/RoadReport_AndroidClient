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
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road)
        ROADCODE = RoadCode().getCurrentRoadCode(this)
        setupToolBar()

        FirestoreManager().getAccident(ROADCODE)

        /**
         * Testing Area
         * */
        val db = FirebaseFirestore.getInstance()
            .collection("ReportAccident").document(ROADCODE.toString())
            .collection("accidents").orderBy("time", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Accident>()
            .setQuery(db, Accident::class.java)
            .build()

        var viewAdapter2: FirestoreRecyclerAdapter<*, *> =
            object : FirestoreRecyclerAdapter<Accident?, AccidentHolder?>(options) {
                override fun onCreateViewHolder(group: ViewGroup, i: Int): AccidentHolder {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.road_accident_row, group, false)
                    return AccidentHolder(view)
                }

                override fun onBindViewHolder(holder: AccidentHolder,
                    position: Int, model: Accident) {
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

        /**
         * Testing Area End Here
         * */

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
//            adapter = viewAdapter
            adapter = viewAdapter2
            adapter?.notifyDataSetChanged()
        }
        viewAdapter2.startListening()
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

class AccidentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var situationType = itemView.textView_accidentCard_situationType as TextView
    var location = itemView.textView_accidentCard_location as TextView
    var situation = itemView.textView_accidentCard_situation as TextView
    var time = itemView.textView_accidentCard_time as TextView
    var userName = itemView.textView_accidentCard_userName as TextView
    var layout = itemView.relaLayout_accidentCard as RelativeLayout
}