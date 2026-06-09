package com.drcmind.kelasisuite.domain.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

object UtilsFunctions {
    @Composable
    fun ConfirmationDialog(
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
        title: String,
        text: String,
        modifier: Modifier = Modifier,
        confirmButtonText: String = "Confirmer",
        dismissButtonText: String = "Annuler",
        icon: ImageVector? = null // Optionnel : pour ajouter un visuel (ex: icône de suppression)
    ) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            icon = icon?.let {
                { Icon(imageVector = it, contentDescription = null) }
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismissRequest() // Ferme le dialogue après confirmation
                }) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = dismissButtonText)
                }
            }
        )
    }
}