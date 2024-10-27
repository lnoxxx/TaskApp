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
import com.lnoxxdev.data.tagRepository.TagRepository
import com.lnoxxdev.taskapp.R
import com.lnoxxdev.taskapp.databinding.DialogAddTagBinding
import com.lnoxxdev.taskapp.ui.uiDecorManagers.AppColorManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO to mvvm?
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
            setIcon(R.drawable.ic_add_tag_dialog)
            setView(binding.root)
            setBackground(background)
        }
        binding.tvError.visibility = View.GONE
        initRv()
        selectColor(AppColorManager.TagColor.COLOR1)
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
        val nameError = tagRepository.tagNameValidate(name)
        if (nameError != null) {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = getString(nameError)
            return
        }
        val color = adapter.getSelectedColor()
        CoroutineScope(Dispatchers.IO).launch {
            tagRepository.insert(name, color.id)
        }
        dialog.dismiss()
    }

    override fun selectColor(color: AppColorManager.TagColor) {
        val colorContainer = resources.getColor(color.colorContainerId, context?.theme)
        val colorText = resources.getColor(color.colorOnContainerId, context?.theme)
        val colorHint = resources.getColor(color.colorVariant, context?.theme)

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
        binding.rvTagColor.addItemDecoration(ItemDecorationAddTagColors())
        binding.rvTagColor.adapter = adapter
    }
}