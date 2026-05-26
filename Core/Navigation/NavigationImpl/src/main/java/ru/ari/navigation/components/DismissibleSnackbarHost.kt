package ru.ari.navigation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier

@Composable
fun DismissibleSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { snackbarData ->
        key(snackbarData) {
            val dismissState = rememberSwipeToDismissBoxState()
            LaunchedEffect(dismissState, snackbarData) {
                snapshotFlow { dismissState.currentValue }.collect { value ->
                    if (value != SwipeToDismissBoxValue.Settled) {
                        snackbarData.dismiss()
                    }
                }
            }
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { Box(modifier = Modifier) }
            ) {
                Snackbar(snackbarData = snackbarData)
            }
        }
    }
}
