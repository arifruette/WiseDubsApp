package ru.ari.sharing.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kotlinx.collections.immutable.ImmutableList
import ru.ari.designsystem.components.ShimmerPlaceholder
import ru.ari.sharing.presentation.models.PostImageUiModel
import androidx.compose.foundation.pager.PagerState
@Composable
fun ImagesCarousel(
    images: ImmutableList<PostImageUiModel>,
    modifier: Modifier = Modifier
) {
    when (images.size) {
        0 -> EmptyCard(modifier)
        else -> ImagesCarouselInternal(images, modifier)
    }
}

@Composable
private fun ImagesCarouselInternal(
    images: ImmutableList<PostImageUiModel>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSize = PageSize.Fill,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 12.dp
            ) { page ->
                CarouselCard(
                    image = images[page],
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            TextIndicator(
                pagerState = pagerState,
                totalCount = images.size,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 44.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun TextIndicator(
    pagerState: PagerState,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${pagerState.currentPage + 1} / $totalCount",
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = Color.White,
        style = MaterialTheme.typography.labelMedium
    )
}
@Composable
private fun CarouselCard(
    image: PostImageUiModel,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = image.url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        loading = {
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun EmptyCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(16f / 9f)
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Пусто",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}
