package ru.ari.sharing.domain.mapper

import kotlinx.collections.immutable.ImmutableList
import ru.ari.posts.api.domain.models.Post
import ru.ari.sharing.presentation.models.PostUiModel

interface SharingPostUiMapper {
    fun map(post: Post): PostUiModel
    fun map(posts: List<Post>): ImmutableList<PostUiModel>
}
