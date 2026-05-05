package com.drcmind.kelasisuite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    icon: ImageVector,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    // Label
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = androidx.compose.material3.LocalTextStyle.current.fontWeight,
        color = AppColors.Input.label,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )

    // Input field container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = AppColors.Input.background,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isError) AppColors.Input.errorBorder else AppColors.Input.border,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AppColors.Input.icon,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Input field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = AppColors.Input.text
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = AppColors.Input.placeholder
                    )
                }
                innerTextField()
            }
        )
    }
}
