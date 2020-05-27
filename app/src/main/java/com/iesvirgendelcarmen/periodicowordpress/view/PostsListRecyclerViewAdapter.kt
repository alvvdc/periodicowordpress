package com.iesvirgendelcarmen.periodicowordpress.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.iesvirgendelcarmen.periodicowordpress.BookmarkPostListener
import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.SharePostListener
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import java.text.DateFormatSymbols
import java.util.*

class PostsListRecyclerViewAdapter(
    val postListListener: PostListListener,
    var sharePostListener: SharePostListener,
    var bookmarkPostListener: BookmarkPostListener,
    var postsList: MutableList<Any> = mutableListOf(),
    var menuCategoriesList: List<MenuCategory> = emptyList()
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val POST_VIEW_TYPE = 0
        const val AD_VIEW_TYPE = 1
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (postsList[position] is PostBO) POST_VIEW_TYPE else AD_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POST_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list_element, parent, false)
            PostViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.ad_unified, parent, false)
            AdViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = postsList[position]

        if (post is PostBO)
            (holder as PostViewHolder).bind(post)
        else
            (holder as AdViewHolder).bind(post as UnifiedNativeAd)
    }

    inner class AdViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val adView: TemplateView = itemView.findViewById(R.id.adTemplate)

        fun bind(nativeAd: UnifiedNativeAd) {
            val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(Color.WHITE)).build()
            adView.setStyles(styles)
            adView.setNativeAd(nativeAd)
        }
    }

    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.title)
        private val category = itemView.findViewById<TextView>(R.id.name)
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val image = itemView.findViewById<ConstraintLayout>(R.id.cardConstraintLayout)
        private val dateIcon = itemView.findViewById<ImageView>(R.id.dateIcon)
        private val share = itemView.findViewById<ImageView>(R.id.share)
        private val bookmark = itemView.findViewById<ImageView>(R.id.bookmark)
        private val categoryCardView = itemView.findViewById<CardView>(R.id.categoryCardView)

        fun bind(post: PostBO) {
            bindComponents(post)
            setDate(post)
            setCategory(post)
            setBookmarks(post)
            loadFeatureImage(post)
            setOnClickListener(post)
            setOnShareListener(post)
        }

        private fun setBookmarks(post: PostBO) {
            if (bookmarkPostListener.isPostBookmarked(post)) setImageBookmarked()
            else setImageNonBookmarked()

            bookmark.setOnClickListener {
                val bookmarked = bookmarkPostListener.onBookmarkPost(post)
                if (bookmarked) setImageBookmarked()
                else setImageNonBookmarked()
            }
        }

        private fun setImageNonBookmarked() {
            bookmark.setImageResource(R.drawable.ic_bookmark_border_white)
        }

        private fun setImageBookmarked() {
            bookmark.setImageResource(R.drawable.ic_bookmark_white)
        }

        private fun setCategory(post: PostBO) {
            category.text = if (post.categories.isNotEmpty()) post.categories[0].name.toUpperCase()
                else "OTROS"


            categoryCardView.setCardBackgroundColor(CategoryColor.DEFAULT_COLOR)

            for (menuCategory in menuCategoriesList) {
                if (post.categories.isNotEmpty() && menuCategory.id == post.categories[0].id) {
                    categoryCardView.setCardBackgroundColor(menuCategory.color)
                    break
                }
            }
        }

        private fun setOnShareListener(post: PostBO) {
            share.setOnClickListener {
                sharePostListener.onClickSharePost(post)
            }
        }

        private fun bindComponents(post: PostBO) {
            title.text = Html.fromHtml(post.title.rendered)
            image.background = null
            category.text = ""
        }

        private fun setDate(post: PostBO) {
            var dateText = ""
            var postDate = post.date
            val actualDate = Date()

            if (post.modified.after(post.date)) {
                postDate = post.modified
                dateIcon.setImageResource(R.drawable.ic_sync)
            }

            val formattedHours = if (postDate.hours.toString().length > 1) postDate.hours.toString() else "0${postDate.hours}"
            val formattedMinutes = if (postDate.minutes.toString().length > 1) postDate.minutes.toString() else "0${postDate.minutes}"

            dateText += when (postDate.day) {
                actualDate.day -> "${itemView.context.getString(R.string.TODAY_AT)} ${formattedHours}h$formattedMinutes"
                (actualDate.day - 1) -> "${itemView.context.getString(R.string.YESTERDAY_AT)} ${formattedHours}h$formattedMinutes"
                else -> "${postDate.date} ${DateFormatSymbols().months[postDate.month]}"
            }

            date.text = dateText
        }

        private fun setOnClickListener(post: PostBO) {
            itemView.setOnClickListener {
                postListListener.onClickPost(post)
            }
        }

        private fun loadFeatureImage(post: PostBO) {
            val mainUrlToLoad =
                if (post.featuredMedia.mediaDetails.sizes.medium != null) post.featuredMedia.mediaDetails.sizes.medium?.sourceUrl
                else post.featuredMedia.sourceUrl

            Glide.with(itemView)
                .load(mainUrlToLoad)
                .placeholder(null)
                .override(1024, 768)
                .thumbnail(
                    Glide.with(itemView)
                        .load(post.featuredMedia.mediaDetails.sizes.thumbnail?.sourceUrl)
                        .placeholder(null)
                )
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        image.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }
}

interface PostListListener {
    fun onClickPost(post: PostBO)
}