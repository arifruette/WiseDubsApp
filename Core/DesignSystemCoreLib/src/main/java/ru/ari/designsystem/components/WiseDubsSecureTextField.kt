package ru.ari.designsystem.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.ari.designsystem.theme.WiseDubsAppTheme

@Composable
fun WiseDubsSecureTextField(
    value: String,
    isValueVisible: Boolean,
    onValueChanged: (String) -> Unit,
    onValueVisibilityChanged: () -> Unit,
    modifier: Modifier = Modifier,
    labelText: String? = null,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isValueVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        colors = TextFieldDefaults.colors().copy(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(
                onClick = onValueVisibilityChanged
            ) {
                Icon(
                    imageVector = if (isValueVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = labelText
                )
            }
        },
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .height(64.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
private fun WiseDubsSecureTextFieldPreview() {
    WiseDubsAppTheme {
        WiseDubsSecureTextField(
            value = "",
            onValueChanged = { },
            isValueVisible = false,
            onValueVisibilityChanged = { },
            labelText = "Пароль"
        )
    }
}
