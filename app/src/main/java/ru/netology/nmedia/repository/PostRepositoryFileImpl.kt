package ru.netology.nmedia.repository


import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random.Default.nextLong

@Singleton
class PostRepositoryFileImpl @Inject constructor (
    private val postDao: PostDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
    ): PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = {postDao.getPagingSource()},
        remoteMediator = PostRemoteMediator(apiService, postDao, postRemoteKeyDao, appDb)
    ).flow
        .map { pagingData ->
            pagingData.map { postEntity ->
                postEntity.toDto()
            }
                .insertSeparators { previous, _ ->
                    if (previous?.id?.rem(5) == 0L) {
                        Ad(kotlin.random.Random.nextLong(), "figma.jpg")
                    } else {
                        null
                    }
                }
        }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            for (i in body){

                i.savedOnServer=true
            }
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun save(post: Post, upload: MediaUpload?) {
        try {
            val postWithAttachment = upload?.let {
                upload(it)
            }?.let {
                post.copy(
                    attachment = Attachment(it.id, AttachmentType.IMAGE),
                    savedOnServer = true
                )
            }
            val response = apiService.save(postWithAttachment ?: post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            body.savedOnServer = false
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }
//    override suspend fun updateStatus() {
//        postDao.updateStatus()
//    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun removeById(id: Long)  {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }
    override suspend fun likeById(id: Long) {
        postDao.likeById(id)
        try {
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
//        try {
//            val media = upload(upload)
//            // TODO: add support for other types
//            val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE), see = true)
//            save(postWithAttachment)
//        } catch (e: AppError) {
//            throw e
//        } catch (e: java.io.IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

    override suspend fun dislikeById(id: Long){
        postDao.likeById(id)
        try {
            val response = apiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    override fun getNewerCount(id:Long): Flow<Int> = flow {
        while (true) {
                delay(50_000L)
                val response = apiService.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())}
                val body = response.body() ?: throw ApiError(response.code(), response.message())

                postDao.insert(body.toEntity())
                emit(body.size)

            }
        }
            .catch { e -> throw AppError.from(e) }
            .flowOn(Dispatchers.Default)
    override suspend fun updateUser(login: String, pass: String) {
        try {
            val response = apiService.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()?: throw ApiError(response.code(), response.message())
            val id = body.id
            val token = body.token
            if (token != null) {
                appAuth.setAuth(id,token)
            }

        } catch (e: IOException) {
            throw NetworkError
        }catch (e: Exception) {
            println(e)
            throw UnknownError

        }
    }

    override suspend fun registerUser(login: String, pass: String, name: String) {
        try {
            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()?: throw ApiError(response.code(), response.message())
            val id = body.id
            val token = body.token
            if (token != null) {
                appAuth.setAuth(id,token)
            }

        } catch (e: IOException) {
            throw NetworkError
        }catch (e: Exception) {
            println(e)
            throw UnknownError

        }
    }

//    override suspend fun sharedById(id: Long):Post = PostsApi.service.sharedById()


}