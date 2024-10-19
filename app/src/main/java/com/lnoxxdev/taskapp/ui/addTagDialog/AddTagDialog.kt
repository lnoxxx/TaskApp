package com.lnoxxdev.taskapp.ui.addTagDialog

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lnoxxdev.data.appDatabase.Tag
import com.lnoxxdev.data.tagRepository.TagRepository
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.DialogAddTagBinding
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddTagDialog : DialogFragment(), ColorSelectedListener {

    @Inject
    lateinit var tagRepository: TagRepository

    private var _binding: DialogAddTagBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Dialog add tag binding null!")

    private val adapter = RvColorsAdapter(this)

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //dialog create
        val dialog = MaterialAlertDialogBuilder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_tag, null)
        _binding = DialogAddTagBinding.bind(view)
        val background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.shape_dialog_background)
        dialog.apply {
            setTitle(R.string.add_tag_dialog_title)
            setIcon(R.drawable.ic_add_tag)
            setView(binding.root)
            setBackground(background)
        }
        initRv()
        selectColor(AppColorManager.TagColor.COLOR0)
        val createdDialog = dialog.create()
        //click listeners
        binding.btnNegative.setOnClickListener {
            createdDialog.dismiss()
        }
        binding.btnPositive.setOnClickListener {
            createTag(createdDialog)
        }
        return createdDialog
    }

    private fun createTag(dialog: Dialog) {
        val name = binding.etTagName.text.toString()
        val nameError = tagNameValidate(name)
        if (nameError != null) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = getString(nameError)
            return
        }
        val color = adapter.getSelectedColor()
        CoroutineScope(Dispatchers.IO).launch {
            tagRepository.insert(Tag(name.trim(' '), color.id))
        }

        dialog.dismiss()
    }

    private fun tagNameValidate(name: String): Int? {
        if (name.isEmpty()) return R.string.error_empty_name
        if (name.length < 2) return R.string.error_short_name
        if (name.length > 30) return R.string.error_long_name
        return null
    }

    override fun selectColor(color: AppColorManager.TagColor) {
        val colorContainer = if (color != AppColorManager.TagColor.COLOR0) resources.getColor(
            color.colorContainerId,
            context?.theme
        ) else AppColorManager.getThemeColor(requireContext(), color.colorContainerId)
        val colorText = if (color != AppColorManager.TagColor.COLOR0) resources.getColor(
            color.colorOnContainerId,
            context?.theme
        ) else AppColorManager.getThemeColor(requireContext(), color.colorOnContainerId)
        val colorHint = if (color != AppColorManager.TagColor.COLOR0) resources.getColor(
            color.colorVariant,
            context?.theme
        ) else AppColorManager.getThemeColor(requireContext(), color.colorVariant)

        ObjectAnimator.ofArgb(
            binding.cvBackgroundEt,
            "cardBackgroundColor",
            binding.cvBackgroundEt.cardBackgroundColor.defaultColor, colorContainer
        ).start()

        binding.etTagName.setTextColor(colorText)
        binding.etTagName.setHintTextColor(colorHint)
    }

    private fun initRv() {
        binding.rvTagColor.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTagColor.adapter = adapter
    }

}