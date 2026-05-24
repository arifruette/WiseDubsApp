package ru.ari.sharing.presentation.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharing.domain.mapper.SharingPostUiMapper
import ru.ari.sharing.presentation.models.PostImageUiModel
import ru.ari.sharing.presentation.models.PostReservationStatusUi
import ru.ari.sharing.presentation.models.PostUiModel
import javax.inject.Inject

class SharingPostUiMapperImpl @Inject constructor() : SharingPostUiMapper {

    override fun map(post: Post, currentUserId: Long?): PostUiModel = PostUiModel(
        id = post.id,
        title = post.title,
        exchange = post.exchange,
        authorEmail = post.authorEmail,
        reservationStatus = post.toReservationStatus(currentUserId),
        images = post.images
            .map { image -> PostImageUiModel(id = image.id, url = image.url) }
            .toImmutableList()
    )

    override fun map(posts: List<Post>, currentUserId: Long?): ImmutableList<PostUiModel> = posts
        .map { post -> map(post = post, currentUserId = currentUserId) }
        .toImmutableList()

    private fun Post.toReservationStatus(currentUserId: Long?): PostReservationStatusUi = when {
        !isReserved -> PostReservationStatusUi.AVAILABLE
        reservedById == currentUserId && currentUserId != null -> {
            PostReservationStatusUi.RESERVED_BY_ME
        }
        else -> PostReservationStatusUi.RESERVED_BY_OTHER
    }
}
