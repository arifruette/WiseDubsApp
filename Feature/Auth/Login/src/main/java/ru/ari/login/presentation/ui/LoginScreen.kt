package ru.ari.login.presentation.ui

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
import ru.ari.login.R
import ru.ari.login.presentation.contract.LoginScreenAction
import ru.ari.login.presentation.contract.LoginScreenUiState

@Composable
fun LoginScreen(
    uiState: LoginScreenUiState,
    onAction: (LoginScreenAction) -> Unit,
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
                        text = stringResource(id = R.string.auth_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                    )
                    Spacer(modifier = Modifier.height(54.dp))
                    WiseDubsTextField(
                        value = uiState.emailText,
                        onValueChanged = { onAction(LoginScreenAction.ChangeEmailState(it)) },
                        labelText = stringResource(R.string.email_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    WiseDubsSecureTextField(
                        value = uiState.passwordText,
                        onValueChanged = { onAction(LoginScreenAction.ChangePasswordState(it)) },
                        labelText = stringResource(R.string.password_placeholder),
                        isValueVisible = uiState.isPasswordTextVisible,
                        onValueVisibilityChanged = { onAction(LoginScreenAction.ChangePasswordVisibility) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            keyBoardController?.hide()
                            onAction(LoginScreenAction.LoginUser)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.enter_button_text),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
                OutlinedButton(
                    onClick = {
                        keyBoardController?.hide()
                        onAction(LoginScreenAction.NavigateToRegistrationScreen)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                        .constrainAs(navigateButton) {
                            bottom.linkTo(parent.bottom, margin = 32.dp)
                        }
                ) {
                    Text(
                        text = stringResource(R.string.to_registration_screen_button_text),
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    WiseDubsAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            LoginScreen(
                uiState = LoginScreenUiState(),
                onAction = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}