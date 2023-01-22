package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.util.SingleLiveEvent

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryFileImpl(AppDb.getInstance(context = application).postDao())

    private val _tokenReceived = SingleLiveEvent<Int>()
    val tokenReceived: LiveData<Int>
        get() = _tokenReceived

    fun registerUser(login: String, pass: String, name: String){
        viewModelScope.launch {
            try {
                repository.registerUser(login,pass,name)
                _tokenReceived.value = 0
            } catch (e: Exception){
                _tokenReceived.value = -1
            }

        }
    }
}
