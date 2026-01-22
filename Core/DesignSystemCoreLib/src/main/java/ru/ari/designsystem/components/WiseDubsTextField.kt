package ru.ari.designsystem.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import ru.ari.designsystem.theme.WiseDubsAppTheme

@Composable
fun WiseDubsTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    labelText: String? = null,
    enabled: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        label = {
            labelText?.let {
                Text(
                    labelText,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        },
        colors = TextFieldDefaults.colors().copy(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        maxLines = maxLines,
        enabled = enabled,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .height(64.dp)
    )
}

@PreviewFontScale
@Preview(showSystemUi = true)
@Composable
private fun WiseDubsTextFieldPreview() {
    WiseDubsAppTheme {
        WiseDubsTextField(
            value = "",
            onValueChanged = {},
            labelText = "Логин"
        )
    }
}