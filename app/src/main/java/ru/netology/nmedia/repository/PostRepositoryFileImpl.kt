package ru.netology.nmedia.repository


import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class PostRepositoryFileImpl (private val dao: PostDao): PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)
    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            for (i in body){
                i.see = true
                i.savedOnServer=true
            }
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            body.see=true
            body.savedOnServer = true
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }
    override suspend fun updateStatus() {
        dao.updateStatus()
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = PostsApi.service.upload(media)
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
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }
    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        try {
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE), see = true)
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long){
        dao.likeById(id)
        try {
            val response = PostsApi.service.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    override fun getNewerCount(id:Long): Flow<Int> = flow {
        while (true) {
                delay(10_000L)
                val response = PostsApi.service.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())}
                val body = response.body() ?: throw ApiError(response.code(), response.message())
            for (i in body){
                i.see = false
            }
                dao.insert(body.toEntity())
                emit(body.size)

            }
        }
            .catch { e -> throw AppError.from(e) }
            .flowOn(Dispatchers.Default)
    override suspend fun updateUser(login: String, pass: String) {
        try {
            val response = PostsApi.service.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()?: throw ApiError(response.code(), response.message())
            val id = body.id
            val token = body.token
            if (token != null) {
                AppAuth.getInstance().setAuth(id,token)
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
            val response = PostsApi.service.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()?: throw ApiError(response.code(), response.message())
            val id = body.id
            val token = body.token
            if (token != null) {
                AppAuth.getInstance().setAuth(id,token)
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