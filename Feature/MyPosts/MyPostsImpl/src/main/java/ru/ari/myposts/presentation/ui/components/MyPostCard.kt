package ru.ari.myposts.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.ari.myposts.presentation.contract.MyPostsActionHandler
import ru.ari.myposts.presentation.contract.MyPostsScreenAction
import ru.ari.myposts.presentation.models.MyPostUiModel

@Composable
fun MyPostCard(
    post: MyPostUiModel,
    actionHandler: MyPostsActionHandler,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    actionHandler.onAction(MyPostsScreenAction.ClickPost(post.id))
                }
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .height(128.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstImageUrl = post.imageUrls.firstOrNull()
            if (firstImageUrl != null) {
                AsyncImage(
                    model = firstImageUrl,
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Обмен: ${post.exchange}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (post.isActive) {
                        "Опубликован"
                    } else {
                        "В архиве"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier
                    .size(1.dp)
                    .aspectRatio(1f)
            )
        }
    }
}
