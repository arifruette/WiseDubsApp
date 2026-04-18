package ru.ari.myposts.domain.mapper

import kotlinx.collections.immutable.ImmutableList
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.posts.api.domain.models.Post

interface MyPostsUiMapper {
    fun map(posts: List<Post>): ImmutableList<MyPostUiModel>
    fun map(post: Post): MyPostUiModel
}
