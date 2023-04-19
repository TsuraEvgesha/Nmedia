package ru.netology.nmedia.viewmodelimport android.net.Uriimport androidx.lifecycle.LiveDataimport androidx.lifecycle.MutableLiveDataimport androidx.lifecycle.ViewModelimport androidx.lifecycle.viewModelScopeimport androidx.paging.PagingDataimport androidx.paging.mapimport dagger.hilt.android.lifecycle.HiltViewModelimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.ExperimentalCoroutinesApiimport kotlinx.coroutines.flow.Flowimport kotlinx.coroutines.flow.flatMapLatestimport kotlinx.coroutines.flow.flowOnimport kotlinx.coroutines.flow.mapimport kotlinx.coroutines.launchimport ru.netology.nmedia.auth.AppAuthimport ru.netology.nmedia.dto.MediaUploadimport ru.netology.nmedia.dto.Postimport ru.netology.nmedia.model.FeedModelStateimport ru.netology.nmedia.model.PhotoModelimport ru.netology.nmedia.repository.PostRepositoryimport ru.netology.nmedia.util.SingleLiveEventimport java.io.Fileimport javax.inject.Inject//private const val JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY"private val empty = Post(0,0,"Me","","",0,false,0, false,null, true)private val noPhoto = PhotoModel()@OptIn(ExperimentalCoroutinesApi::class)@HiltViewModelclass PostViewModel @Inject constructor(    private val repository: PostRepository,    appAuth: AppAuth) : ViewModel() {    val data: Flow<PagingData<Post>> = appAuth        .authStateFlow        .flatMapLatest { (myId, _) ->        repository.data            .map { posts ->                    posts.map{it.copy(ownedByMe = it.authorId == myId)}            }    }.flowOn(Dispatchers.Default)    private val _tokenReceived = SingleLiveEvent<Int>()    val tokenReceived: LiveData<Int>        get() = _tokenReceived//    val newerCount: LiveData<Int> = data.switchMap{//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)//            .catch { e -> e.printStackTrace() }//            .asLiveData(Dispatchers.Default, 1_000L)//    }    private val _dataState = MutableLiveData(FeedModelState(idle = true))    private val edited = MutableLiveData(empty)    private val _postCreated = SingleLiveEvent<Unit>()    val postCreated: LiveData<Unit>    get() = _postCreated    val dataState: LiveData<FeedModelState>        get() = _dataState    private val _photo = MutableLiveData(noPhoto)    val photo: LiveData<PhotoModel>        get() = _photo    init {        loadPosts()    }    fun loadPosts() = viewModelScope.launch {        _dataState.value = FeedModelState(loading = true)        try {            repository.getAll()            _dataState.value= FeedModelState(idle = true)        } catch (e:Exception){            _dataState.value = FeedModelState(error = true)        }    }//    fun updateStatus(){//        viewModelScope.launch {//            repository.updateStatus()//        }//    }    fun save() {    edited.value?.let {        _postCreated.value = Unit        viewModelScope.launch {            try {                when(_photo.value) {                    noPhoto -> repository.save(it)                    else -> _photo.value?.file?.let { file ->                        repository.saveWithAttachment(it, MediaUpload(file))                    }                }                _dataState.value = FeedModelState()            } catch (e: Exception) {                _dataState.value = FeedModelState(error = true)            }        }    }    edited.value = empty    _photo.value = noPhoto    }    fun changePhoto(uri: Uri?, file: File?) {        _photo.value = if(uri != null && file !=null) {            PhotoModel(uri, file)        } else {            null        }    }    fun likeById(id:Long) {        viewModelScope.launch {            try {                repository.likeById(id)                _dataState.value = FeedModelState()            } catch (e: Exception) {                _dataState.value = FeedModelState(error = true)            }        }    }    fun dislikeById(id:Long) {        viewModelScope.launch {            try {                repository.dislikeById(id)                _dataState.value = FeedModelState()            } catch (e: Exception) {                _dataState.value = FeedModelState(error = true)            }        }    }//        fun sharedById(id: Long) {//            thread {//                val old = _data.value?.posts.orEmpty()//                val posts = _data.value?.posts.orEmpty()//                posts.map {//                    if (it.id != id) it else it.copy()//                }//                try {//                    repository.sharedById(id)//                } catch (e: IOException) {//                    _data.postValue(_data.value?.copy(posts = old))//                }//            }////        }    fun refreshPosts() = viewModelScope.launch {        try {            _dataState.value = FeedModelState(refreshing = true)            repository.getAll()            _dataState.value = FeedModelState(idle = true)        } catch (e: Exception) {            _dataState.value = FeedModelState(error = true)        }    }    fun removeById(id: Long) {        viewModelScope.launch {            try {                repository.removeById(id)                _dataState.value=FeedModelState()            }catch (e:Exception){                _dataState.value = FeedModelState(error = true)            }        }    }    fun edit(post: Post) {        edited.value = post    }    fun editContent(content: String) {        edited.value.let {            val trimmed = content.trim()            if (edited.value?.content == trimmed) {                return            }            edited.value = edited.value?.copy(content = trimmed)        }    }}