package com.iesvirgendelcarmen.periodicowordpress.view

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.iesvirgendelcarmen.periodicowordpress.R
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.PostBO
import java.text.DateFormatSymbols
import java.util.*

class PostsListRecyclerViewAdapter(var postsList: List<PostBO>): RecyclerView.Adapter<PostsListRecyclerViewAdapter.PostViewHolder>() {

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

        fun bind(post: PostBO) {
            title.text = post.title.rendered
            category.text = if (post.categories.isNotEmpty()) post.categories[0].name.toUpperCase() else "OTROS"
            image.background = null

            var dateText = ""
            val postDate = post.date
            val actualDate = Date()

            val formattedHours = if (postDate.hours.toString().length > 1) postDate.hours.toString() else "0${postDate.hours}"
            val formattedMinutes = if (postDate.minutes.toString().length > 1) postDate.minutes.toString() else "0${postDate.minutes}"

            dateText += when (postDate.day) {
                actualDate.day          -> "${itemView.context.getString(R.string.TODAY_AT)} ${formattedHours}h$formattedMinutes"
                (actualDate.day - 1)    -> "${itemView.context.getString(R.string.YESTERDAY_AT)} ${formattedHours}h$formattedMinutes"
                else                    -> "${postDate.date} ${DateFormatSymbols().months[postDate.month]}"
            }
            
            date.text = dateText


            Glide.with(itemView)
                .load(post.featuredMedia.mediaDetails.sizes.medium?.sourceUrl)
                .into(object: CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        image.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }
}