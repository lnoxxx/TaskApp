package com.lnoxxdev.taskapp.ui.uiDecorManagers

import android.content.Context
import android.util.TypedValue
import com.lnoxxdev.taskapp.R

object AppColorManager {

    fun getThemeColor(context: Context, colorId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(colorId, typedValue, true)
        return typedValue.data
    }

    fun getColorById(id: Int): TagColor {
        return when (id) {
            0 -> TagColor.COLOR0
            1 -> TagColor.COLOR1
            2 -> TagColor.COLOR2
            3 -> TagColor.COLOR3
            4 -> TagColor.COLOR4
            5 -> TagColor.COLOR5
            6 -> TagColor.COLOR6
            7 -> TagColor.COLOR7
            8 -> TagColor.COLOR8
            else -> TagColor.COLOR0
        }
    }

    enum class TagColor(
        val colorContainerId: Int,
        val colorOnContainerId: Int,
        val colorVariant: Int,
        val id: Int
    ) {
        COLOR0(
            com.google.android.material.R.attr.colorSurfaceContainerHigh,
            com.google.android.material.R.attr.colorOnSurface,
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            0
        ),
        COLOR1(
            R.color.colorCustomColor1Container,
            R.color.colorOnCustomColor1Container,
            R.color.colorCustomColor1,
            1
        ),
        COLOR2(
            R.color.colorCustomColor2Container,
            R.color.colorOnCustomColor2Container,
            R.color.colorCustomColor2,
            2
        ),
        COLOR3(
            R.color.colorCustomColor3Container,
            R.color.colorOnCustomColor3Container,
            R.color.colorCustomColor3,
            3
        ),
        COLOR4(
            R.color.colorCustomColor4Container,
            R.color.colorOnCustomColor4Container,
            R.color.colorCustomColor4,
            4
        ),
        COLOR5(
            R.color.colorCustomColor5Container,
            R.color.colorOnCustomColor5Container,
            R.color.colorCustomColor5,
            5
        ),
        COLOR6(
            R.color.colorCustomColor6Container,
            R.color.colorOnCustomColor6Container,
            R.color.colorCustomColor6,
            6
        ),
        COLOR7(
            R.color.colorCustomColor7Container,
            R.color.colorOnCustomColor7Container,
            R.color.colorCustomColor7,
            7
        ),
        COLOR8(
            R.color.colorCustomColor8Container,
            R.color.colorOnCustomColor8Container,
            R.color.colorCustomColor8,
            8
        ),
    }
}