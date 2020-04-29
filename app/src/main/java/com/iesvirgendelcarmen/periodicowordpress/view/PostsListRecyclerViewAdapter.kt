package com.iesvirgendelcarmen.periodicowordpress.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.SharePostListener
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import java.text.DateFormatSymbols
import java.util.*

class PostsListRecyclerViewAdapter(val postListListener: PostListListener, var sharePostListener: SharePostListener, var postsList: MutableList<PostBO> = mutableListOf()): RecyclerView.Adapter<PostsListRecyclerViewAdapter.PostViewHolder>() {

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list_element, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)
    }

    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val title = itemView.findViewById<TextView>(R.id.title)
        private val category = itemView.findViewById<TextView>(R.id.category)
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val image = itemView.findViewById<ConstraintLayout>(R.id.cardConstraintLayout)
        private val dateIcon = itemView.findViewById<ImageView>(R.id.dateIcon)
        private val share = itemView.findViewById<ImageView>(R.id.share)

        fun bind(post: PostBO) {
            bindComponents(post)
            setDate(post)
            loadFeatureImage(post)
            setOnClickListener(post)
            setOnShareListener(post)
        }

        private fun setOnShareListener(post: PostBO) {
            share.setOnClickListener {
                sharePostListener.onClickSharePost(post)
            }
        }

        private fun bindComponents(post: PostBO) {
            title.text = Html.fromHtml(post.title.rendered)
            image.background = null

            category.text =
                if (post.categories.size > 1 && post.categories[0].name.toLowerCase() != "uncategorized")
                    post.categories[1].name.toUpperCase()
                else if (post.categories.isNotEmpty())
                    post.categories[0].name.toUpperCase()
                else
                    "OTROS"
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