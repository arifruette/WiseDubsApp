package ru.ari.registration.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ru.ari.designsystem.components.WiseDubsProgressIndicator
import ru.ari.designsystem.components.WiseDubsSecureTextField
import ru.ari.designsystem.components.WiseDubsTextField
import ru.ari.designsystem.theme.WiseDubsAppTheme
import ru.ari.registration.R
import ru.ari.registration.presentation.contract.RegistrationScreenAction
import ru.ari.registration.presentation.contract.RegistrationScreenUiState
import ru.ari.registration.presentation.models.PasswordField

@Composable
fun RegistrationScreen(
    uiState: RegistrationScreenUiState,
    onAction: (RegistrationScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyBoardController = LocalSoftwareKeyboardController.current

    when {
        uiState.isLoading -> WiseDubsProgressIndicator()

        else -> {
            ConstraintLayout(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 31.dp)
            ) {
                val (logo, mainPart, navigateButton) = createRefs()
                val navigateButtonTopBarrier = createTopBarrier(navigateButton)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .constrainAs(mainPart) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(navigateButtonTopBarrier, margin = 14.dp)
                            start.linkTo(parent.start)
                        }
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    Text(
                        text = stringResource(R.string.registration_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                    )
                    Spacer(modifier = Modifier.height(54.dp))
                    WiseDubsTextField(
                        value = uiState.emailText,
                        onValueChanged = { onAction(RegistrationScreenAction.ChangeEmailState(it)) },
                        labelText = stringResource(R.string.email_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    WiseDubsTextField(
                        value = uiState.telegramIdText,
                        onValueChanged = {
                            onAction(
                                RegistrationScreenAction.ChangeTelegramIdState(
                                    it
                                )
                            )
                        },
                        labelText = stringResource(R.string.telegram_text_field_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    WiseDubsSecureTextField(
                        value = uiState.firstPasswordText,
                        onValueChanged = {
                            onAction(
                                RegistrationScreenAction.ChangePasswordState(
                                    password = it,
                                    passwordField = PasswordField.FIRST
                                )
                            )
                        },
                        labelText = stringResource(R.string.first_password_field_placeholder),
                        isValueVisible = uiState.isFirstPasswordVisible,
                        onValueVisibilityChanged = {
                            onAction(
                                RegistrationScreenAction.ChangePasswordVisibility(PasswordField.FIRST)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    WiseDubsSecureTextField(
                        value = uiState.secondPasswordText,
                        onValueChanged = {
                            onAction(
                                RegistrationScreenAction.ChangePasswordState(
                                    password = it,
                                    passwordField = PasswordField.SECOND
                                )
                            )
                        },
                        labelText = stringResource(R.string.second_password_field_placeholder),
                        isValueVisible = uiState.isSecondPasswordVisible,
                        onValueVisibilityChanged = {
                            onAction(
                                RegistrationScreenAction.ChangePasswordVisibility(
                                    PasswordField.SECOND
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            keyBoardController?.hide()
                            onAction(
                                RegistrationScreenAction.RegisterUser
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.registration_button_text),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
                OutlinedButton(
                    onClick = {
                        keyBoardController?.hide()
                        onAction(RegistrationScreenAction.NavigateToLoginScreen)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(navigateButton) {
                            bottom.linkTo(parent.bottom, margin = 32.dp)
                        }
                ) {
                    Text(
                        text = stringResource(R.string.to_login_screen_button_text),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.hse_logo),
                    contentDescription = stringResource(R.string.hse_logo),
                    modifier = Modifier
                        .constrainAs(logo) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(mainPart.top, margin = 44.dp)
                        }
                        .imePadding()
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun RegistrationScreenPreview() {
    WiseDubsAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            RegistrationScreen(
                uiState = RegistrationScreenUiState(),
                onAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}