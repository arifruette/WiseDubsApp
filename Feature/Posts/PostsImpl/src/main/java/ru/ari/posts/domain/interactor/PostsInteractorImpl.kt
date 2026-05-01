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

    override fun observeAllPosts(): kotlinx.coroutines.flow.Flow<List<Post>> =
        postsRepository.observeAllPosts()

    override suspend fun getFeedPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsRepository.getFeedPosts(forceRefresh = forceRefresh)
        }

    override fun observeFeedPosts(): kotlinx.coroutines.flow.Flow<List<Post>> =
        postsRepository.observeFeedPosts()

    override suspend fun getMyPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsRepository.getMyPosts(forceRefresh = forceRefresh)
        }

    override fun observeMyPosts(): kotlinx.coroutines.flow.Flow<List<Post>> =
        postsRepository.observeMyPosts()

    override suspend fun getReservedPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsRepository.getReservedPosts(forceRefresh = forceRefresh)
        }

    override fun observeReservedPosts(): kotlinx.coroutines.flow.Flow<List<Post>> =
        postsRepository.observeReservedPosts()

    override suspend fun getPostById(id: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.getPostById(id)
        }

    override suspend fun reservePost(postId: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.reservePost(postId)
        }

    override suspend fun unreservePost(postId: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            postsRepository.unreservePost(postId)
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
