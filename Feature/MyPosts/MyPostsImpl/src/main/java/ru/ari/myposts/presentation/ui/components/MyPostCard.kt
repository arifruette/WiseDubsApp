package ru.ari.myposts.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Image
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
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(14.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(12.dp)
            .defaultMinSize(minHeight = 68.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        post.previewImageUrl?.let { previewImageUrl ->
            PreviewImage(
                previewImageUrl = previewImageUrl,
                title = post.title,
                modifier = Modifier.imageBox()
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = post.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onArchiveClick,
                    enabled = post.archiveActionEnabled,
                    modifier = Modifier
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = post.archiveIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = post.exchangeText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                MetaText(
                    text = post.locationText,
                    icon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f, fill = false)
                )
                MetaText(
                    text = post.createdAgoText,
                    icon = Icons.Default.AccessTime
                )
            }
        }
    }
}

@Composable
private fun PreviewImage(
    previewImageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = previewImageUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        loading = {
            ImagePlaceholder(modifier = Modifier.fillMaxSize())
        },
        error = {
            ImagePlaceholder(modifier = Modifier.fillMaxSize())
        },
        modifier = modifier
    )
}

@Composable
private fun ImagePlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.55f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun MetaText(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Modifier.imageBox(): Modifier = this.then(
    Modifier
        .size(80.dp)
        .clip(RoundedCornerShape(10.dp))
)
