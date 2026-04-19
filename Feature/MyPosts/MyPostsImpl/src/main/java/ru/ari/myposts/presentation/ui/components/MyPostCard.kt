package ru.ari.myposts.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import ru.ari.myposts.presentation.models.MyPostUiModel

@Composable
fun MyPostCard(
    post: MyPostUiModel,
    onClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreviewImage(
            previewImageUrl = post.previewImageUrl,
            title = post.title,
            modifier = Modifier.imageBox()
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = post.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = post.exchangeText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            IconButton(
                onClick = onArchiveClick,
                enabled = post.archiveActionEnabled,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                    shape = CircleShape
                )
                    .size(24.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = post.archiveIcon,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun PreviewImage(
    previewImageUrl: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    if (previewImageUrl == null) {
        ImagePlaceholder(modifier = modifier)
        return
    }

    SubcomposeAsyncImage(
        model = previewImageUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        loading = {
            ImagePlaceholder(modifier = Modifier.fillMaxWidth())
        },
        error = {
            ImagePlaceholder(modifier = Modifier.fillMaxWidth())
        },
        modifier = modifier
    )
}

@Composable
private fun ImagePlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    )
}

private fun Modifier.imageBox(): Modifier = this.then(
    Modifier
        .size(100.dp)
        .clip(RoundedCornerShape(20.dp))
)
