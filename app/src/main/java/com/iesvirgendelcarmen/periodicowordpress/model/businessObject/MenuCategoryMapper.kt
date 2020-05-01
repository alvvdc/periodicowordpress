package com.iesvirgendelcarmen.periodicowordpress.model.businessObject

import android.graphics.Color
import com.iesvirgendelcarmen.periodicowordpress.config.CategoryColor
import com.iesvirgendelcarmen.periodicowordpress.model.wordpress.Category

class MenuCategoryMapper {
    companion object {
        private val colors = CategoryColor.colors
        private var colorIndex = 0

        fun convertCategoryToMenuCategory(category: Category): MenuCategory {
            return MenuCategory(category.id, category.name.toUpperCase(), getNextColor())
        }

        private fun getNextColor(): Int {
            if (colorIndex >= colors.size)
                colorIndex = 0

            return colors[colorIndex++]
        }
    }
}