package ru.ari.myposts.presentation.mappers

import kotlinx.collections.immutable.toImmutableList
import ru.ari.myposts.domain.mapper.MyPostsUiMapper
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.posts.api.domain.models.Post
import javax.inject.Inject

class MyPostsUiMapperImpl @Inject constructor() : MyPostsUiMapper {
    override fun map(posts: List<Post>) = posts.map { post ->
        MyPostUiModel(
            id = post.id,
            title = post.title,
            exchange = post.exchange,
            isActive = post.isActive,
            imageUrls = post.images.map { it.url }.toImmutableList()
        )
    }.toImmutableList()
}
