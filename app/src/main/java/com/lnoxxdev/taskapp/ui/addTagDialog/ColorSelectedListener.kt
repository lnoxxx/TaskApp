package com.lnoxxdev.taskapp.ui.addTagDialog

import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager

interface ColorSelectedListener {
    fun selectColor(color: AppColorManager.TagColor)
}