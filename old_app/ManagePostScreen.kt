package ru.wisedubsapp.feature.edit.ui.presentation.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import ru.wisedubsapp.core.ui.components.WiseDubsProgressIndicator
import ru.wisedubsapp.core.ui.components.WiseDubsTextField
import ru.wisedubsapp.feature.core.domain.models.SharingTypedImage
import ru.wisedubsapp.feature.edit.ui.R
import ru.wisedubsapp.feature.edit.ui.presentation.utils.FieldLimitsType
import ru.wisedubsapp.feature.edit.ui.viewmodel.contract.ManagePostScreenAction
import ru.wisedubsapp.feature.edit.ui.viewmodel.contract.ManagePostScreenUiEffect
import ru.wisedubsapp.feature.edit.ui.viewmodel.contract.ManagePostScreenUiState

@Composable
fun ManagePostScreen(
    uiState: ManagePostScreenUiState,
    uiEffect: Flow<ManagePostScreenUiEffect>,
    navigateBack: () -> Unit,
    onAction: (ManagePostScreenAction) -> Unit,
    requestInstantPostsRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyBoardController = LocalSoftwareKeyboardController.current
    val pickMediaLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            onAction(
                ManagePostScreenAction.AddImage(SharingTypedImage.Local(uri.toString()))
            )
        }
    }
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is ManagePostScreenUiEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    when {
        uiState.isLoading -> WiseDubsProgressIndicator(modifier = Modifier.fillMaxSize())
        else -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(bottom = 80.dp)
                ) {
                    ImageRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp),
                        onImageRemove = { onAction(ManagePostScreenAction.RemoveImage(it)) },
                        images = uiState.post.images,
                        onAddImageClicked = {
                            pickMediaLauncher.launch(
                                PickVisualMediaRequest(
                                    PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(17.dp)
                    ) {
                        WiseDubsTextField(
                            value = uiState.post.title,
                            onValueChanged = {
                                if (it.length <= 40) {
                                    onAction(ManagePostScreenAction.ChangeTitleState(it))
                                } else {
                                    onAction(ManagePostScreenAction.TextFieldLimitsExceeded(FieldLimitsType.TitleLimits))
                                }
                            },
                            labelText = stringResource(R.string.title_field_text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        WiseDubsTextField(
                            value = uiState.post.description.orEmpty(),
                            onValueChanged = {
                                if (it.length <= 400) {
                                    onAction(ManagePostScreenAction.ChangeDescriptionState(it))
                                } else {
                                    onAction(ManagePostScreenAction.TextFieldLimitsExceeded(FieldLimitsType.DescriptionLimits))
                                }
                            },
                            labelText = stringResource(R.string.description_field_text),
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                        WiseDubsTextField(
                            value = uiState.post.exchange.orEmpty(),
                            onValueChanged = {
                                onAction(
                                    ManagePostScreenAction.ChangeExchangeState(it)
                                )
                            },
                            labelText = stringResource(R.string.exchange_field_text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        WiseDubsTextField(
                            value = uiState.post.room,
                            onValueChanged = {
                                if (it.length <= 15) {
                                    onAction(ManagePostScreenAction.ChangeRoomState(it))
                                } else {
                                    onAction(ManagePostScreenAction.TextFieldLimitsExceeded(FieldLimitsType.RoomLimits))
                                }
                            },
                            labelText = stringResource(R.string.room_text_field),
                            modifier = Modifier.fillMaxWidth()
                        )
                        WiseDubsTextField(
                            value = uiState.post.corpus,
                            onValueChanged = {
                                if (it.length <= 15) {
                                    onAction(ManagePostScreenAction.ChangeCorpusState(it))
                                } else {
                                    onAction(ManagePostScreenAction.TextFieldLimitsExceeded(FieldLimitsType.CorpusLimits))
                                }
                            },
                            labelText = stringResource(R.string.corpus_text_field_text),
                            modifier = Modifier.fillMaxWidth()
                        )
                        WiseDubsTextField(
                            value = uiState.post.reservedBy
                                ?: stringResource(R.string.empty_picked_info_text),
                            onValueChanged = {},
                            labelText = stringResource(R.string.picked_field_text),
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Button(
                    onClick = {
                        keyBoardController?.hide()
                        if (uiState.isEditMode) {
                            onAction(
                                ManagePostScreenAction.UpdateSharingPost(onSuccess = {
                                    requestInstantPostsRefresh()
                                    navigateBack()
                                })
                            )
                        } else {
                            onAction(
                                ManagePostScreenAction.CreateSharingPost(onSuccess = {
                                    requestInstantPostsRefresh()
                                    navigateBack()
                                })
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = if (uiState.isEditMode) stringResource(R.string.save_changes_bottom_button_text) else stringResource(
                            R.string.create_post_bottom_button_text
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
