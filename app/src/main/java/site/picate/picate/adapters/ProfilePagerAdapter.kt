package site.picate.picate.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import site.picate.picate.fragments.AccountFragment
import site.picate.picate.fragments.UpdateFragment

class ProfilePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                AccountFragment()
            }

            else -> {
                UpdateFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

}