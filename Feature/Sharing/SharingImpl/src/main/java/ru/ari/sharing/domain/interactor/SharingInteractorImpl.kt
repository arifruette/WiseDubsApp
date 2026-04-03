package ru.ari.sharing.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ari.network.domain.models.Result
import ru.ari.posts.api.domain.interactor.PostsInteractor
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharing.api.domain.interactor.SharingInteractor
import javax.inject.Inject

class SharingInteractorImpl @Inject constructor(
    private val postsInteractor: PostsInteractor
) : SharingInteractor {

    override suspend fun getPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            postsInteractor.getAllPosts(forceRefresh = forceRefresh)
        }

    override suspend fun getPostById(id: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            postsInteractor.getPostById(id)
        }
}
