package ru.ari.sharing.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ari.network.domain.models.Result
import ru.ari.sharing.api.domain.interactor.SharingInteractor
import ru.ari.sharing.api.domain.models.Post
import ru.ari.sharing.api.domain.repository.SharingRepository
import javax.inject.Inject

class SharingInteractorImpl @Inject constructor(
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override suspend fun getPosts(forceRefresh: Boolean): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            sharingRepository.getPosts(forceRefresh = forceRefresh)
        }

    override suspend fun getPostById(id: Long): Result<Post> =
        withContext(Dispatchers.IO) {
            sharingRepository.getPostById(id)
        }
}
