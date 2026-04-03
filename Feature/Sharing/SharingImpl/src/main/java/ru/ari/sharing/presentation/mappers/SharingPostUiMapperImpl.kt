package ru.ari.sharing.presentation.mappers

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharing.domain.mapper.SharingPostUiMapper
import ru.ari.sharing.presentation.models.PostImageUiModel
import ru.ari.sharing.presentation.models.PostUiModel
import javax.inject.Inject

class SharingPostUiMapperImpl @Inject constructor() : SharingPostUiMapper {

    override fun map(post: Post): PostUiModel = PostUiModel(
        id = post.id,
        title = post.title,
        exchange = post.exchange,
        authorEmail = post.authorEmail,
        images = post.images
            .map { image -> PostImageUiModel(id = image.id, url = image.url) }
            .toImmutableList()
    )

    override fun map(posts: List<Post>): ImmutableList<PostUiModel> = posts
        .map(::map)
        .toImmutableList()
}
