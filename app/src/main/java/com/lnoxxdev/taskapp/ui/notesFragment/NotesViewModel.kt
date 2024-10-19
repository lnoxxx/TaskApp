package com.lnoxxdev.taskapp.ui.notesFragment

import androidx.lifecycle.ViewModel
import com.lnoxxdev.data.notesRepository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel() {

}