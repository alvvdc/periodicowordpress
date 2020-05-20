package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.iesvirgendelcarmen.periodicowordpress.AppVersionRequest
import com.iesvirgendelcarmen.periodicowordpress.DrawerLayoutLock
import com.iesvirgendelcarmen.periodicowordpress.OpenWebPageRequest

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.config.SocialMedia

class InfoFragment : Fragment() {

    private lateinit var appVersionRequest: AppVersionRequest
    private lateinit var openWebPageRequest: OpenWebPageRequest
    private lateinit var drawerLayoutLock: DrawerLayoutLock

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appVersionRequest = context as AppVersionRequest
        openWebPageRequest = context as OpenWebPageRequest
        drawerLayoutLock = context as DrawerLayoutLock
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerLayoutLock.lockDrawerLayout()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val versionTextView = view.findViewById<TextView>(R.id.version)
        val twitterImageView = view.findViewById<ImageView>(R.id.twitter)
        val facebookImageView = view.findViewById<ImageView>(R.id.facebook)
        val websiteTextView = view.findViewById<TextView>(R.id.website)
        val logo1ImageView = view.findViewById<ImageView>(R.id.logo1)
        val logo2ImageView = view.findViewById<ImageView>(R.id.logo2)

        websiteTextView.text = SocialMedia.WEBSITE

        val versionName = appVersionRequest.getVersionName()
        val versionCode = appVersionRequest.getVersionCode()
        versionTextView.text = "Versión: $versionName ($versionCode)"

        twitterImageView.setOnClickListener {
            openWebPageRequest.openWebPageFromRequest(SocialMedia.TWITTER_URL)
        }

        facebookImageView.setOnClickListener {
            openWebPageRequest.openWebPageFromRequest(SocialMedia.FACEBOOK_URL)
        }

        val websiteListener = View.OnClickListener {
            openWebPageRequest.openWebPageFromRequest(SocialMedia.WEBSITE_URL)
        }

        websiteTextView.setOnClickListener(websiteListener)
        logo1ImageView.setOnClickListener(websiteListener)
        logo2ImageView.setOnClickListener(websiteListener)
    }
}
