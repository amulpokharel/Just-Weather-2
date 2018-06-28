package amulp.com.justweather2.ui.settings

import amulp.com.justweather2.R
import android.app.ActionBar
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.main_activity.*

class SettingsFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true);
        activity!!.title = "Settings"
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                activity!!.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}