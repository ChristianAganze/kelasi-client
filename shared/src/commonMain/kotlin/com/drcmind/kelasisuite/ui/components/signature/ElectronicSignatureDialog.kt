package com.drcmind.kelasisuite.ui.components.signature

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.model.common.ElectronicSignature
import com.drcmind.kelasisuite.domain.model.common.PathPoint
import com.drcmind.kelasisuite.domain.model.common.SignatureStroke
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ElectronicSignatureDialog(
    signerName: String,
    signerRole: String,
    documentTitle: String,
    onDismiss: () -> Unit,
    onConfirmSignature: (ElectronicSignature) -> Unit
) {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }

    val now = remember {
        val dt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.year}-${dt.monthNumber.toString().padStart(2, '0')}-${dt.dayOfMonth.toString().padStart(2, '0')} ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
    }

    val token = remember {
        val rand = (100000..999999).random()
        "SIG-KELASI-${rand.toString(16).uppercase()}"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Draw, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Signature Électronique Certifiée", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Document : $documentTitle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info signataire
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(signerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(signerRole, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(now, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    "Veuillez apposer votre signature dans le cadre ci-dessous :",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Zone de dessin Canvas pour la signature
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(1.5.dp, if (strokes.isNotEmpty() || currentStroke.isNotEmpty()) MaterialTheme.colorScheme.primary else Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentStroke = listOf(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentStroke = currentStroke + change.position
                                    },
                                    onDragEnd = {
                                        if (currentStroke.isNotEmpty()) {
                                            strokes = strokes + listOf(currentStroke)
                                            currentStroke = emptyList()
                                        }
                                    }
                                )
                            }
                    ) {
                        // Dessiner les tracés précédents
                        strokes.forEach { stroke ->
                            if (stroke.size > 1) {
                                val path = Path().apply {
                                    moveTo(stroke.first().x, stroke.first().y)
                                    for (i in 1 until stroke.size) {
                                        lineTo(stroke[i].x, stroke[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0xFF0D233A),
                                    style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }

                        // Dessiner le tracé en cours
                        if (currentStroke.size > 1) {
                            val path = Path().apply {
                                moveTo(currentStroke.first().x, currentStroke.first().y)
                                for (i in 1 until currentStroke.size) {
                                    lineTo(currentStroke[i].x, currentStroke[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF0D233A),
                                style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    if (strokes.isEmpty() && currentStroke.isEmpty()) {
                        Text(
                            text = "✍️ Signer ici (doigt ou stylet)",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Bouton effacer
                    if (strokes.isNotEmpty() || currentStroke.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                strokes = emptyList()
                                currentStroke = emptyList()
                            },
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Effacer", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Certificat de conformité
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                    Text(
                        "Jeton d'intégrité : $token",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStrokes = strokes.map { stroke ->
                        SignatureStroke(stroke.map { PathPoint(it.x, it.y) })
                    }
                    val signature = ElectronicSignature(
                        signerName = signerName,
                        signerRole = signerRole,
                        signedAt = now,
                        signatureToken = token,
                        strokes = finalStrokes,
                        isVerified = true
                    )
                    onConfirmSignature(signature)
                },
                enabled = strokes.isNotEmpty() || currentStroke.isNotEmpty()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apposer la Signature")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun RenderElectronicSignature(
    signature: ElectronicSignature,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF7F9FC),
        modifier = modifier.border(1.dp, Color(0xFF2B579A).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dessin du tracé
            if (signature.strokes.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .width(140.dp)
                        .height(45.dp)
                ) {
                    // Trouver bounding box pour mise à l'échelle
                    val allPoints = signature.strokes.flatMap { it.points }
                    val minX = allPoints.minOfOrNull { it.x } ?: 0f
                    val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
                    val minY = allPoints.minOfOrNull { it.y } ?: 0f
                    val maxY = allPoints.maxOfOrNull { it.y } ?: 1f

                    val srcW = (maxX - minX).coerceAtLeast(10f)
                    val srcH = (maxY - minY).coerceAtLeast(10f)

                    val scaleX = (size.width - 10f) / srcW
                    val scaleY = (size.height - 10f) / srcH
                    val scale = minOf(scaleX, scaleY).coerceAtMost(1f)

                    signature.strokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                val first = stroke.points.first()
                                moveTo(5f + (first.x - minX) * scale, 5f + (first.y - minY) * scale)
                                for (i in 1 until stroke.points.size) {
                                    val pt = stroke.points[i]
                                    lineTo(5f + (pt.x - minX) * scale, 5f + (pt.y - minY) * scale)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF0D233A),
                                style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
            } else {
                Text(
                    signature.signerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D233A)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Signé numériquement le ${signature.signedAt}",
                fontSize = 8.sp,
                color = Color.DarkGray
            )
            Text(
                signature.signatureToken,
                fontSize = 8.sp,
                color = Color(0xFF2B579A),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
