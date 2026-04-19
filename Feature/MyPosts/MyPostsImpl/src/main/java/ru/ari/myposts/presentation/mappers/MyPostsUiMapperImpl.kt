package ru.ari.myposts.presentation.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import kotlinx.collections.immutable.toImmutableList
import ru.ari.myposts.domain.mapper.MyPostsUiMapper
import ru.ari.myposts.presentation.models.MyPostUiModel
import ru.ari.posts.api.domain.models.Post
import javax.inject.Inject

class MyPostsUiMapperImpl @Inject constructor() : MyPostsUiMapper {

    override fun map(posts: List<Post>) = posts.map(::map).toImmutableList()

    override fun map(post: Post): MyPostUiModel = MyPostUiModel(
        id = post.id,
        title = post.title,
        exchangeText = "Обмен: ${post.exchange}",
        isActive = post.isActive,
        previewImageUrl = post.images.firstOrNull()?.url,
        archiveIcon = if (post.isActive) {
            Icons.Default.Archive
        } else {
            Icons.Default.Unarchive
        },
        archiveActionTargetActive = !post.isActive
    )
}
