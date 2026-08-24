package com.drcmind.kelasisuite.ui.parentadmin.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.data.datasource.remote.dto.FeeDTO
import com.drcmind.kelasisuite.domain.model.parent.PaymentReceipt
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    androidx.compose.runtime.LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Finances & Frais Scolaires", fontWeight = FontWeight.Bold)
                        Text(
                            "Paiements Mobile Money & Quittances certifiées",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "Erreur",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val totalDue = state.fees.sumOf { it.amountDue }
                val totalPaid = state.fees.sumOf { it.amountPaid }
                val totalPending = totalDue - totalPaid

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Financial Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Situation Financière Globale", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Facturé", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Text("${totalDue.toInt()} $", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Déjà Réglé", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                                        Text("${totalPaid.toInt()} $", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    Column {
                                        Text("Solde Restant", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                        Text("${totalPending.toInt()} $", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    // Section: Frais à Payer
                    item {
                        Text(
                            text = "Frais & Échéances Scolaires",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (state.fees.isEmpty()) {
                        item {
                            Text("Aucun frais en attente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(state.fees) { fee ->
                            FeeCard(
                                fee = fee,
                                onPayClick = { viewModel.openPaymentDialog(fee) }
                            )
                        }
                    }

                    // Section: Historique des Quittances
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Historique des Quittances & Reçus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (state.paymentHistory.isEmpty()) {
                        item {
                            Text("Aucune quittance disponible.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(state.paymentHistory) { receipt ->
                            ReceiptHistoryCard(
                                receipt = receipt,
                                onClick = { viewModel.viewReceipt(receipt) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Payment Dialog
    if (state.isPaymentDialogOpen && state.selectedFeeForPayment != null) {
        PaymentFlowDialog(
            fee = state.selectedFeeForPayment!!,
            onDismiss = { viewModel.closePaymentDialog() },
            onPaymentSuccess = { receipt ->
                viewModel.onPaymentSuccess(receipt)
            }
        )
    }

    // Modal Receipt Dialog
    if (state.isReceiptDialogOpen && state.activeReceipt != null) {
        PaymentReceiptDialog(
            receipt = state.activeReceipt!!,
            onDismiss = { viewModel.closeReceiptDialog() }
        )
    }
}

@Composable
fun FeeCard(fee: FeeDTO, onPayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (fee.isFullyPaid) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (fee.isFullyPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (fee.isFullyPaid) Icons.Default.CheckCircle else Icons.Default.Payment,
                    contentDescription = null,
                    tint = if (fee.isFullyPaid) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fee.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Montant total : ${fee.amountDue.toInt()} $  •  Payé : ${fee.amountPaid.toInt()} $",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (fee.isFullyPaid) "En règle • Échéance : ${fee.dueDate}" else "Reste : ${(fee.amountDue - fee.amountPaid).toInt()} $ • Échéance : ${fee.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (fee.isFullyPaid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
            }

            if (!fee.isFullyPaid) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onPayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Payer")
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "Soldé",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptHistoryCard(receipt: PaymentReceipt, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE7F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF512DA8), modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(receipt.receiptNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(receipt.feeDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${receipt.paymentDate} • ${receipt.paymentProvider}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${receipt.amountPaid} ${receipt.currency}",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32)
                )
                Text("Voir reçu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
