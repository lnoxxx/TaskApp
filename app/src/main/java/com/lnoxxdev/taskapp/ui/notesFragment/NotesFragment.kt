package com.lnoxxdev.taskapp.ui.notesFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lnoxxdev.taskapp.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("NotesFragmentBinding null")

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater)
        return binding.root
    }
}