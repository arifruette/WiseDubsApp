package ru.ari.sharingpostdetails.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kotlinx.collections.immutable.ImmutableList
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiAction
import ru.ari.sharingpostdetails.presentation.contract.SharingPostDetailsUiState
import ru.ari.sharingpostdetails.presentation.models.PostDetailsImageUiModel
import ru.ari.sharingpostdetails.presentation.models.ReservationStatusUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SharingPostDetailsScreen(
    uiState: SharingPostDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (SharingPostDetailsUiAction) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Бронирование") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (uiState) {
            SharingPostDetailsUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is SharingPostDetailsUiState.Content -> {
                val post = uiState.post

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (post.images.isNotEmpty()) {
                        PostImagesCarousel(
                            images = post.images,
                            onImageClick = { index ->
                                onAction(SharingPostDetailsUiAction.ClickImage(index))
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        StatusBlock(status = post.status)

                        InfoBlock(title = "Описание", value = post.description)
                        InfoBlock(title = "Обмен", value = post.exchange)
                        InfoBlock(
                            title = "Контакт автора",
                            value = post.authorEmail ?: "Email не указан"
                        )
                        InfoBlock(title = "Адрес выдачи", value = post.address)
                        post.addressComment?.let { comment ->
                            InfoBlock(title = "Комментарий к адресу", value = comment)
                        }
                        InfoBlock(title = "Дата публикации", value = post.createdAt)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onAction(SharingPostDetailsUiAction.ClickPrimaryAction)
                                },
                                enabled = post.primaryAction.isEnabled && !post.primaryAction.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (post.primaryAction.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(post.primaryAction.label)
                                }
                            }

                            TextButton(
                                onClick = { onAction(SharingPostDetailsUiAction.ClickContact) },
                                enabled = !post.authorTelegramId.isNullOrBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Связаться")
                            }
                        }
                    }
                }

                uiState.selectedImageIndex?.takeIf { post.images.isNotEmpty() }?.let { selectedImageIndex ->
                    FullscreenImageViewer(
                        images = post.images,
                        initialPage = selectedImageIndex,
                        onDismiss = { onAction(SharingPostDetailsUiAction.DismissImageViewer) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp)
        ) {}
        repeat(5) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (it == 0) 28.dp else 84.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                shape = RoundedCornerShape(18.dp)
            ) {}
        }
    }
}

@Composable
private fun PostImagesCarousel(
    images: ImmutableList<PostDetailsImageUiModel>,
    onImageClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { images.size }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            NetworkImage(
                url = images[page].url,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(page) }
            )
        }
    }
}

@Composable
private fun FullscreenImageViewer(
    images: ImmutableList<PostDetailsImageUiModel>,
    initialPage: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { images.size.coerceAtLeast(1) }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (images.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    NetworkImage(
                        url = images[page].url,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(12.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Не удалось загрузить изображение",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun StatusBlock(status: ReservationStatusUi) {
    val (title, containerColor, contentColor) = when (status) {
        ReservationStatusUi.AVAILABLE -> Triple(
            "Свободно",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        ReservationStatusUi.RESERVED_BY_ME -> Triple(
            "Забронировано вами",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        ReservationStatusUi.RESERVED_BY_OTHER -> Triple(
            "Занято",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun InfoBlock(
    title: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
