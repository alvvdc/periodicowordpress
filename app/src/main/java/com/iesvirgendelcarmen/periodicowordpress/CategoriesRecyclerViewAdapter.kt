package com.iesvirgendelcarmen.periodicowordpress

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategory
import com.iesvirgendelcarmen.periodicowordpress.model.businessObject.MenuCategoryMapper
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

class CategoriesRecyclerViewAdapter(var categories :List<MenuCategory>, val menuCategoryListener: MenuCategoryListener): RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoryViewHolder>() {

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.navigation_category_element, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val icon = itemView.findViewById<ImageView>(R.id.icon)
        val name = itemView.findViewById<TextView>(R.id.name)
        val layout = itemView.findViewById<ConstraintLayout>(R.id.layout)

        fun bind(category: MenuCategory) {
            val drawable = icon.drawable.mutate()
            drawable.setColorFilter(category.color, PorterDuff.Mode.SRC_IN)
            icon.setImageDrawable(drawable)

            name.text = category.name

            //layout.setBackgroundColor(Color.parseColor("#d8d8d8"))
            layout.setOnClickListener {
                menuCategoryListener.onClickMenuCategory(category)
            }
        }
    }
}

interface MenuCategoryListener {
    fun onClickMenuCategory(category: MenuCategory)
}