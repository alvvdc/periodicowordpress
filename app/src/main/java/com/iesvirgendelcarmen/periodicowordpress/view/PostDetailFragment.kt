package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.iesvirgendelcarmen.periodicowordpress.BookmarkPostListener
import com.iesvirgendelcarmen.periodicowordpress.DrawerLayoutLock

import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.SharePostListener
import com.iesvirgendelcarmen.periodicowordpress.*
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaBO
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.abs


class PostDetailFragment : Fragment() {

    private var categoryColor: Int = Color.parseColor("#5979a0")

    private lateinit var post: PostBO

    private lateinit var categoryTextView: TextView
    private lateinit var shareImageView: ImageView
    private lateinit var bookmarkImageView: ImageView
    private lateinit var featuredImageConstraintLayout: ConstraintLayout
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dateImageView: ImageView
    private lateinit var contentTextView: TextView
    private lateinit var readTimeTextView: TextView
    private lateinit var categoryCardView: CardView
    private lateinit var featuredMediaDescription: TextView
    private lateinit var shareButton: Button
    private lateinit var bookmarkButton: Button

    private lateinit var sharePostListener: SharePostListener
    private lateinit var bookmarkPostListener: BookmarkPostListener
    private lateinit var imageDetailListener: ImageDetailListener
    private lateinit var drawerLayoutLock: DrawerLayoutLock
    private lateinit var closeFragmentListFragment: CloseFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPostFromBundle()
        getCategoryColorFromBundle()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharePostListener = context as SharePostListener
        bookmarkPostListener = context as BookmarkPostListener
        imageDetailListener = context as ImageDetailListener
        drawerLayoutLock = context as DrawerLayoutLock
        closeFragmentListFragment = context as CloseFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerLayoutLock.lockDrawerLayout()

        val gestureDetector = getSwipeGestureDetector()
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)
        view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        return view
    }

    private fun getSwipeGestureDetector(): GestureDetector {
        return GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {

                val SWIPE_MIN_DISTANCE = 120
                val SWIPE_MAX_OFF_PATH = 250
                val SWIPE_THRESHOLD_VELOCITY = 200

                try {
                    if (abs(e1!!.y - e2!!.y) > SWIPE_MAX_OFF_PATH)
                        return false

                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                        closeFragmentListFragment.onClickCloseFragment()
                } catch (e: Exception) {
                }

                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViewsById(view)
        setReadTime()
        bindTextViews()
        setDate(view)
        loadFeatureImage(view)
        setOnShareListener()
        setBookmarks()

        categoryCardView = view.findViewById(R.id.actionBar)
        categoryCardView.setCardBackgroundColor(categoryColor)

        featuredImageConstraintLayout.setOnClickListener {
            imageDetailListener.onImageClickListener(post.featuredMedia)
        }

        val mAdView = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun setBookmarks() {
        if (bookmarkPostListener.isPostBookmarked(post)) setImageBookmarked()
        else setImageNonBookmarked()

        val bookmarkOnClickListener = View.OnClickListener {
            val bookmarked = bookmarkPostListener.onBookmarkPost(post)
            if (bookmarked) setImageBookmarked()
            else setImageNonBookmarked()
        }

        bookmarkImageView.setOnClickListener(bookmarkOnClickListener)
        bookmarkButton.setOnClickListener(bookmarkOnClickListener)
    }

    private fun setImageNonBookmarked() {
        bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_white)
        bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_bookmark_border_black,
            0,
            0,
            0
        )
    }

    private fun setImageBookmarked() {
        bookmarkImageView.setImageResource(R.drawable.ic_bookmark_white)
        bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_bookmark_black,
            0,
            0,
            0
        )
    }

    private fun setOnShareListener() {
        val shareClickListener = View.OnClickListener { sharePostListener.onClickSharePost(post) }

        shareImageView.setOnClickListener(shareClickListener)
        shareButton.setOnClickListener(shareClickListener)
    }

    private fun setDate(view: View) {
        var dateText = ""
        var postDate = post.date
        val actualDate = Date()

        if (post.modified.after(post.date)) {
            postDate = post.modified
            dateImageView.setImageResource(R.drawable.ic_sync)
        }

        val formattedHours = if (postDate.hours.toString().length > 1) postDate.hours.toString() else "0${postDate.hours}"
        val formattedMinutes = if (postDate.minutes.toString().length > 1) postDate.minutes.toString() else "0${postDate.minutes}"

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

//        if (post.featuredMedia.caption.rendered.isBlank()) {
//            featuredMediaDescription.text = ""
//            featuredMediaDescription.setBackgroundColor(Color.parseColor("#00000000"))
//        } else {
            featuredMediaDescription.text = "Foto de ${post.featuredMedia.author.name}".toUpperCase()
            featuredMediaDescription.setBackgroundColor(Color.parseColor("#B3000000"))
        //}
    }

    private fun setReadTime() {
        val numberOfWords: Int = post.content.rendered.split(" ").size
        val readTime: Int = numberOfWords / 200
        readTimeTextView.text = "$readTime min"
    }

    private fun loadFeatureImage(view: View) {
        val mainUrlToLoad = post.featuredMedia.sourceUrl

        if (mainUrlToLoad == "") {
            featuredImageConstraintLayout.visibility = View.GONE
        } else {

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
    }

    private fun findViewsById(view: View) {
        categoryTextView = view.findViewById(R.id.name)
        shareImageView = view.findViewById(R.id.share)
        bookmarkImageView = view.findViewById(R.id.bookmark)
        featuredImageConstraintLayout = view.findViewById(R.id.featureImage)
        titleTextView = view.findViewById(R.id.title)
        dateTextView = view.findViewById(R.id.date)
        dateImageView = view.findViewById(R.id.dateIcon)
        contentTextView = view.findViewById(R.id.content)
        readTimeTextView = view.findViewById(R.id.readTime)
        featuredMediaDescription = view.findViewById(R.id.featuredMediaDescription)
        shareButton = view.findViewById(R.id.shareButton)
        bookmarkButton = view.findViewById(R.id.bookmarkButton)
    }

    private fun getPostFromBundle() {
        if (arguments != null) {
            post = arguments?.getParcelable("POST") ?: PostBO()
        }
    }

    private fun getCategoryColorFromBundle() {
        if (arguments != null) {
            categoryColor = arguments?.getInt("COLOR") ?: CategoryColor.DEFAULT_COLOR
        }
    }
}

interface ImageDetailListener {
    fun onImageClickListener(media :MediaBO)
}
