package com.txwstudio.app.roadreport

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val target = when (preference?.key) {
            "ads" -> "https://bit.ly/RoadRAds"
            "pp" -> "https://bit.ly/RoadRTos"
            "tos" -> "https://bit.ly/RoadRPp"
            "license" -> "https://bit.ly/RoadRLicense"
            "about" -> "https://bit.ly/RoadRAbout"
            else -> ""
        }
        if (!target.isBlank()) {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, Uri.parse(target))
        }

        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals("theme")) {
            val stringWow = findPreference<ListPreference>("theme")?.value
            val wow = context?.getSharedPreferences("main", MODE_PRIVATE)
            wow?.edit()?.putString("theme", stringWow)?.apply()
        }
    }

}