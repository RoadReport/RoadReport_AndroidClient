package com.txwstudio.app.roadreport.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.StringCode
import com.txwstudio.app.roadreport.Util
import com.txwstudio.app.roadreport.activity.EventEditorActivity
import com.txwstudio.app.roadreport.activity.ImageViewerActivity
import com.txwstudio.app.roadreport.databinding.RowEventCardBinding
import com.txwstudio.app.roadreport.firebase.AuthManager
import com.txwstudio.app.roadreport.firebase.FirestoreManager
import com.txwstudio.app.roadreport.model.Accident
import com.txwstudio.app.roadreport.model.EventCardVewModel
import com.txwstudio.app.roadreport.ui.maps.MapsFragment

class EventCardAdapter(
    private val requireView: View,
    private val fm: FragmentManager,
    private val roadCode: Int
) :
    FirestoreRecyclerAdapter<Accident?, EventCardAdapter.ViewHolder>(
        FirestoreManager().getRealtimeAccidentQuery(roadCode)
    ) {

    val isEventEmpty = MutableLiveData<Boolean>(true)

    private var isUserSignedIn = AuthManager().isUserSignedIn()
    private val currentUserUid = AuthManager().getCurrUserModel()?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowEventCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Accident) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
        isEventEmpty.value = snapshots.size == 0
    }

    inner class ViewHolder(
        private val binding: RowEventCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textViewEventCardLocation.setOnClickListener {
                binding.eventCardViewModel?.locationGeoPoint?.let { LatLng ->
                    MapsFragment(false, LatLng).show(fm, MapsFragment::class.java.simpleName)
                }
            }

            binding.imageButtonEventCardMore.setOnClickListener {
                // TODO(Fix event card pattern to match MVVM)
                // What user can to the card. Different onClick behavior for card.
                // Situation 1: Signed in && posted by user, Edit or Delete.
                // Situation 2: Signed in && NOT posted by user, Report.
                if (isUserSignedIn && snapshots.getSnapshot(adapterPosition)["userUid"] == currentUserUid) {
                    // Situation 1, 0 for edit, 1 for delete.
                    val builder = AlertDialog.Builder(binding.root.context)
                    builder.setItems(R.array.roadFrag_moreOnClick_situation2) { _, which ->
                        when (which) {
                            0 -> {
                                alertDialogActionEdit()
                            }
                            1 -> {
                                alertDialogActionDelete(snapshots.getSnapshot(adapterPosition).id)
                            }
                        }
                    }.show()

                } else if (isUserSignedIn && snapshots.getSnapshot(adapterPosition)["userUid"] != currentUserUid) {
                    // Situation 2
                    val builder = AlertDialog.Builder(binding.root.context)
                    builder.setItems(R.array.roadFrag_moreOnClick_situation3) { _, which ->
                        when (which) {
                            0 -> alertDialogActionReport()
                        }
                    }.show()

                }
            }

            binding.imageViewEventCardImage.setOnClickListener {
                binding.eventCardViewModel?.imageUrl?.let { url ->
                    actionShowImage(url)
                }
            }
        }

        fun bind(events: Accident) {
            with(binding) {
                eventCardViewModel = EventCardVewModel(events)
                executePendingBindings()
            }
        }

        private fun actionShowImage(imageUrl: String) {
            requireView.context.startActivity(
                Intent(requireView.context, ImageViewerActivity::class.java)
                    .putExtra(StringCode.EXTRA_NAME_IMAGE_URL, imageUrl)
            )
        }

        private fun alertDialogActionEdit() {
            Util().snackBarShort(requireView, "在新視窗中開啟")
            val accidentModel = getItem(adapterPosition)
            val intent = Intent(requireView.context, EventEditorActivity::class.java)
            intent.putExtra(StringCode.EXTRA_NAME_EDIT_MODE, true)
            intent.putExtra(
                StringCode.EXTRA_NAME_DOCUMENT_ID,
                snapshots.getSnapshot(adapterPosition).id
            )
            val temp = Util().convertAccidentModel2Parcelable(accidentModel)
            intent.putExtra(StringCode.EXTRA_NAME_ACCIDENT_MODEL, temp)
            requireView.context.startActivity(intent)
        }

        private fun alertDialogActionDelete(docId: String) {
            FirestoreManager()
                .deleteAccident(roadCode, docId) {
                    Util().snackBarShort(
                        requireView,
                        if (it) "刪除成功" else "刪除失敗"
                    )
                }
        }

        private fun alertDialogActionReport() {
            Util().snackBarShort(requireView, "欸嘿 還沒開發")
        }

    }
}
