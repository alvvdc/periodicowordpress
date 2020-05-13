package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.iesvirgendelcarmen.periodicowordpress.CloseFragmentListener

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaBO
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*
import java.util.*

class ImageDetailFragment : Fragment() {

    lateinit var media: MediaBO

    lateinit var closeFragmentListener: CloseFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            media = arguments?.getParcelable("MEDIA") ?: MediaBO(-1, Date(), Date(), "", Rendered(""), User(-1, "", "", "", "", AvatarUrls("", "", "")), Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, "")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        closeFragmentListener = context as CloseFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authorTextView = view.findViewById<TextView>(R.id.author)
        val descriptionTextView = view.findViewById<TextView>(R.id.description)
        val mediaImageView = view.findViewById<ImageView>(R.id.image)
        val closeImageView = view.findViewById<ImageView>(R.id.close)

        authorTextView.text = media.author.name.toUpperCase()
        descriptionTextView.text = Html.fromHtml(media.caption.rendered).toString().trim()

        Glide.with(view)
            .load(media.sourceUrl)
            .placeholder(null)
            .override(1024, 768)
            .thumbnail(
                Glide.with(view)
                    .load(media.mediaDetails.sizes.thumbnail?.sourceUrl)
                    .placeholder(null)
            )
            .into(mediaImageView)

        closeImageView.setOnClickListener {
            closeFragmentListener.onClickCloseFragment()
        }
    }
}
