package com.iesvirgendelcarmen.periodicowordpress.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.iesvirgendelcarmen.periodicowordpress.CloseFragmentListener
import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MediaBO
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.*
import java.util.*
import kotlin.math.abs


class ImageDetailFragment : Fragment() {

    lateinit var media: MediaBO

    lateinit var closeFragmentListener: CloseFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            media = arguments?.getParcelable("MEDIA") ?: MediaBO(-1, Date(), Date(), "", Rendered(""), User(-1, "", "", "", "", AvatarUrls("", "", "")), Rendered(""), Rendered(""), "", "", "", MediaDetails(-1, -1, "", Sizes()), -1, "")
        }

        sharedElementEnterTransition = TransitionInflater.from(activity).inflateTransition(R.transition.image_transition)
        sharedElementReturnTransition = TransitionInflater.from(activity).inflateTransition(R.transition.image_transition)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        closeFragmentListener = context as CloseFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val gestureDetector = getSwipeGestureDetector()
        val view = inflater.inflate(R.layout.fragment_image_detail, container, false)
        view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authorTextView = view.findViewById<TextView>(R.id.author)
        val descriptionTextView = view.findViewById<TextView>(R.id.description)
        val mediaImageView = view.findViewById<ImageView>(R.id.image)
        val closeImageView = view.findViewById<ImageView>(R.id.close)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)

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

        val animationDuration = 300L
        var faded = false

        val animatorListenerAdapterForFadeOut = object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                authorTextView.visibility = View.INVISIBLE
                descriptionTextView.visibility = View.INVISIBLE
                closeImageView.visibility = View.INVISIBLE
            }
        }

        val animatorListenerAdapterForFadeIn = object: AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                authorTextView.visibility = View.VISIBLE
                descriptionTextView.visibility = View.VISIBLE
                closeImageView.visibility = View.VISIBLE
            }
        }

        val fadeClickListener = View.OnClickListener {
            if (faded) {
                authorTextView.animate().setDuration(animationDuration).alpha(1f).setListener(animatorListenerAdapterForFadeIn)
                descriptionTextView.animate().setDuration(animationDuration).alpha(1f).setListener(animatorListenerAdapterForFadeIn)
                closeImageView.animate().setDuration(animationDuration).alpha(1f).setListener(animatorListenerAdapterForFadeIn)
            }
            else {
                authorTextView.animate().setDuration(animationDuration).alpha(0f).setListener(animatorListenerAdapterForFadeOut)
                descriptionTextView.animate().setDuration(animationDuration).alpha(0f).setListener(animatorListenerAdapterForFadeOut)
                closeImageView.animate().setDuration(animationDuration).alpha(0f).setListener(animatorListenerAdapterForFadeOut)
            }
            faded = !faded
        }

        constraintLayout.setOnClickListener(fadeClickListener)
        //mediaImageView.setOnClickListener(fadeClickListener)
    }

    private fun getSwipeGestureDetector(): GestureDetector {
        return GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                try {
                    val diffY = e2!!.y - e1!!.y
                    val diffX = e2.x - e1.x

                    if (!(abs(diffX) > abs(diffY))) {
                        if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (!(diffY > 0)) {
                                closeFragmentListener.onClickCloseFragment()
                            }
                        }
                    }
                } catch (exception: Exception) { }

                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }
}
