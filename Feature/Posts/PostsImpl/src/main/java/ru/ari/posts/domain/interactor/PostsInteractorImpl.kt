package ru.ari.posts.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.models.CreatePostParams
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.Post
import ru.ari.posts.api.domain.models.UpdatePostParams
import ru.ari.posts.api.domain.repository.PostsRepository
import javax.inject.Inject

class PostsInteractorImpl @Inject constructor(
    private val postsRepository: PostsRepository
) : PostsInteractor {

    override suspend fun getAllPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsRepository.getAllPosts(forceRefresh = forceRefresh)
        }

    override suspend fun getMyPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsRepository.getMyPosts(forceRefresh = forceRefresh)
        }

    override suspend fun getPostById(id: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.getPostById(id)
        }

    override suspend fun createPost(params: CreatePostParams): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.createPost(params)
        }

    override suspend fun updatePost(params: UpdatePostParams): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.updatePost(params)
        }

    override suspend fun setPostActive(id: Long, isActive: Boolean): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.setPostActive(id = id, isActive = isActive)
        }
}
