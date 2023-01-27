package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data: MutableStateFlow<Token?> = MutableStateFlow(null)
    val data = _data.asStateFlow()

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)
        if (token == null || id == 0L){
            removeAuth()
        } else{
            _data.value = Token(id, token)
        }
        sendPushToken()

    }

    @Synchronized
    fun removeAuth() {
        _data.value = null
        prefs.edit {
            remove(TOKEN_KEY)
            remove(ID_KEY)
        }
        sendPushToken()
    }

    @Synchronized
    fun setAuth(id:Long, token: String) {
        _data.value = Token(id, token)
        prefs.edit {
            putString(TOKEN_KEY, token)
            putLong(ID_KEY, id)
        }
        sendPushToken()
    }
    fun sendPushToken (token: String?=null){
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
                PostsApi.service.sendPushToken(pushToken)

            } catch (e:Exception){
                e.printStackTrace()
                //do nothing
            }

        }
    }
    fun getId(): Long = prefs.getLong(ID_KEY, 0L)

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
        @Volatile
        private var INSTANCE: AppAuth? = null

        fun getInstance(): AppAuth = requireNotNull(INSTANCE)

        fun initAuth(context: Context){
            INSTANCE = AppAuth(context)
        }
    }
}