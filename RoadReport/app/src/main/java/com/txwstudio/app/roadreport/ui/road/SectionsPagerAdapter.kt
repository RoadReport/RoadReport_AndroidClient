package com.txwstudio.app.roadreport.ui.road

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.ui.livecam.LiveCamFragment
import com.txwstudio.app.roadreport.ui.roadevent.RoadFragment
import com.txwstudio.app.roadreport.ui.weather.WeatherFragment

private val TAB_TITLES = arrayOf(
    R.string.roadActivity_tab_accidentEvent,
    R.string.roadActivity_tab_weather,
    R.string.roadActivity_tab_liveCam
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceHolderFragment (defined as a static inner class below).
//        return PlaceHolderFragment.newInstance(position + 1)

        return when (position) {
            0 -> RoadFragment()
            1 -> WeatherFragment()
            2 -> LiveCamFragment()
            else -> PlaceHolderFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 3
    }
}