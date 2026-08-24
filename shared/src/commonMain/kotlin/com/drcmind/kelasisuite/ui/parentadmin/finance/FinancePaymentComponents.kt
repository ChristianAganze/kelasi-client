package com.drcmind.kelasisuite.ui.parentadmin.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.domain.model.parent.MobileMoneyProvider
import com.drcmind.kelasisuite.domain.model.parent.PaymentReceipt
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFlowDialog(
    fee: FeeDTO,
    onDismiss: () -> Unit,
    onPaymentSuccess: (PaymentReceipt) -> Unit
) {
    val providers = listOf(
        MobileMoneyProvider("Vodacom M-Pesa", "MPESA", 0xFFE60000, "*1122#"),
        MobileMoneyProvider("Orange Money", "ORANGE", 0xFFFF6600, "*144#"),
        MobileMoneyProvider("Airtel Money", "AIRTEL", 0xFFE4002B, "*501#"),
        MobileMoneyProvider("Banque (Rawbank / Illicocash)", "BANK", 0xFF003366, "Virement")
    )

    var selectedProvider by remember { mutableStateOf(providers.first()) }
    var phoneNumber by remember { mutableStateOf("+243 812 345 678") }
    var amountToPay by remember { mutableStateOf("${fee.amountDue - fee.amountPaid}") }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf("Connexion à la passerelle...") }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Règlement des Frais Scolaires",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = fee.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!isProcessing) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer")
                        }
                    }
                }

                HorizontalDivider()

                if (isProcessing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(54.dp), strokeWidth = 4.dp)
                        Text(
                            text = processingStep,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Veuillez valider l'invite de confirmation avec votre code PIN secret sur votre téléphone...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Fee summary card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Solde restant à régler", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = "${fee.amountDue - fee.amountPaid} $",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "Échéance : ${fee.dueDate}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Select Payment Provider
                    Text(
                        text = "1. Choisissez le mode de paiement",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        providers.forEach { provider ->
                            val isSelected = selectedProvider == provider
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProvider = provider }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(provider.colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = provider.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedProvider = provider }
                                    )
                                }
                            }
                        }
                    }

                    // Input Phone Number
                    Text(
                        text = "2. Coordonnées de transaction",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Numéro Mobile Money / Compte") },
                        leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = amountToPay,
                        onValueChange = { amountToPay = it },
                        label = { Text("Montant à régler (USD)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Confirm Payment Button
                    LaunchedEffect(isProcessing) {
                        if (isProcessing) {
                            delay(1200)
                            processingStep = "Envoi de la requête USSD vers ${selectedProvider.name}..."
                            delay(1600)
                            processingStep = "Vérification de la confirmation PIN..."
                            delay(1200)
                            processingStep = "Génération de la quittance numérique officielle..."
                            delay(1000)

                            val receipt = PaymentReceipt(
                                receiptNumber = "REC-2026-${(10000..99999).random()}",
                                transactionRef = "${selectedProvider.code}-TX-${(1000000..9999999).random()}",
                                feeDescription = fee.description,
                                studentName = "Kavira Mukwege",
                                studentClass = "4ème Scientifique A",
                                parentName = "Responsable Financier",
                                amountPaid = amountToPay.toDoubleOrNull() ?: (fee.amountDue - fee.amountPaid),
                                currency = "USD",
                                paymentProvider = selectedProvider.name,
                                payerPhoneOrAccount = phoneNumber,
                                paymentDate = "24 Août 2026 à 09:15",
                                verificationToken = "TOKEN-${(1000..9999).random()}-KS-VALID",
                                status = "Paiement Validé & Encaissé"
                            )
                            isProcessing = false
                            onPaymentSuccess(receipt)
                        }
                    }

                    Button(
                        onClick = { isProcessing = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        enabled = phoneNumber.isNotBlank() && (amountToPay.toDoubleOrNull() ?: 0.0) > 0.0
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmer le paiement de $amountToPay $")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentReceiptDialog(
    receipt: PaymentReceipt,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Official Receipt Document Layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header School Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "RÉPUBLIQUE DÉMOCRATIQUE DU CONGO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "MINISTÈRE DE L'ÉDUCATION NATIONALE",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "COMPLEXE SCOLAIRE KELASI SUITE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B365D)
                            )
                            Text(
                                text = "Service de la Trésorerie & Comptabilité Scolaire",
                                fontSize = 10.sp,
                                color = Color.DarkGray
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32))
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("QUITTANCE OFFICIELLE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text(receipt.receiptNumber, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 2.dp, color = Color(0xFF1B365D))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "REÇU DE PAIEMENT NUMÉRIQUE CERTIFIÉ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1B365D),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail Table Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF9FAFB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ReceiptRow("Élève bénéficiaire :", "${receipt.studentName} (${receipt.studentClass})")
                            ReceiptRow("Responsable payeur :", receipt.parentName)
                            ReceiptRow("Motif du paiement :", receipt.feeDescription)
                            ReceiptRow("Date & Heure :", receipt.paymentDate)
                            ReceiptRow("Moyen de paiement :", receipt.paymentProvider)
                            ReceiptRow("Compte / Téléphone :", receipt.payerPhoneOrAccount)
                            ReceiptRow("Référence transaction :", receipt.transactionRef)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount Paid Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("MONTANT TOTAL ENCAISSÉ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("Reçu en bonne et due forme", fontSize = 10.sp, color = Color.DarkGray)
                            }
                            Text(
                                text = "${receipt.amountPaid} ${receipt.currency}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Certified Stamp and Security
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(0.55f)) {
                            Text("Certificat d'intégrité numérique :", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(receipt.verificationToken, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            Text("Authentifiable auprès de l'établissement", fontSize = 8.sp, color = Color.Gray)
                        }

                        // Digital Stamp Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEDE7F6),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF512DA8)),
                            modifier = Modifier.weight(0.45f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF512DA8), modifier = Modifier.size(20.dp))
                                Text("SCEAU COMPTABILITÉ", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF512DA8))
                                Text("COMPLEXE SCOLAIRE", fontSize = 7.sp, color = Color.DarkGray)
                                Text("Payé & Enregistré", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Actions Footer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fermer")
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enregistrer Quittance")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
