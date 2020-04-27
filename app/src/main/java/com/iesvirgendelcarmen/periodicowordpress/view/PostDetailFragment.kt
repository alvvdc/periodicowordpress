package com.iesvirgendelcarmen.periodicowordpress.view

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import java.text.DateFormatSymbols
import java.util.*

class PostDetailFragment : Fragment() {

    lateinit var post: PostBO

    private lateinit var categoryTextView: TextView
    private lateinit var shareImageView: ImageView
    private lateinit var bookmarkImageView: ImageView
    private lateinit var featuredImageConstraintLayout: ConstraintLayout
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dateImageView: ImageView
    private lateinit var contentTextView: TextView
    private lateinit var readTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPostFromParcelable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViewsById(view)
        setReadTime()
        bindTextViews()
        setDate(view)
        loadFeatureImage(view)
    }

    private fun setDate(view: View) {
        var dateText = ""
        var postDate = post.date
        val actualDate = Date()

        if (post.modified.after(post.date)) {
            postDate = post.modified
            dateImageView.setImageResource(R.drawable.ic_sync)
        }

        val formattedHours =
            if (postDate.hours.toString().length > 1) postDate.hours.toString() else "0${postDate.hours}"
        val formattedMinutes =
            if (postDate.minutes.toString().length > 1) postDate.minutes.toString() else "0${postDate.minutes}"

        dateText += when (postDate.day) {
            actualDate.day -> "${view.context.getString(R.string.TODAY_AT)} ${formattedHours}h$formattedMinutes"
            (actualDate.day - 1) -> "${view.context.getString(R.string.YESTERDAY_AT)} ${formattedHours}h$formattedMinutes"
            else -> "${postDate.date} ${DateFormatSymbols().months[postDate.month]}"
        }

        dateTextView.text = dateText
    }

    private fun bindTextViews() {
        categoryTextView.text = post.categories[0].name.toUpperCase()
        titleTextView.text = Html.fromHtml(post.title.rendered)

        contentTextView.text = Html.fromHtml(post.content.rendered)
    }

    private fun setReadTime() {
        val numberOfWords: Int = post.content.rendered.split(" ").size
        val readTime: Int = numberOfWords / 200
        readTimeTextView.text = "$readTime min"
    }

    private fun loadFeatureImage(view: View) {
        val mainUrlToLoad = post.featuredMedia.sourceUrl

        Glide.with(view)
            .load(mainUrlToLoad)
            .placeholder(null)
            .override(1024, 768)
            .thumbnail(
                Glide.with(view)
                    .load(post.featuredMedia.mediaDetails.sizes.thumbnail?.sourceUrl)
                    .placeholder(null)
            )
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    featuredImageConstraintLayout.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun findViewsById(view: View) {
        categoryTextView = view.findViewById(R.id.category)
        shareImageView = view.findViewById(R.id.share)
        bookmarkImageView = view.findViewById(R.id.bookmark)
        featuredImageConstraintLayout = view.findViewById(R.id.featureImage)
        titleTextView = view.findViewById(R.id.title)
        dateTextView = view.findViewById(R.id.date)
        dateImageView = view.findViewById(R.id.dateIcon)
        contentTextView = view.findViewById(R.id.content)
        readTimeTextView = view.findViewById(R.id.readTime)
    }

    private fun getPostFromParcelable() {
        if (arguments != null) {
            post = arguments?.getParcelable("POST") ?: PostBO()
        }
    }
}
