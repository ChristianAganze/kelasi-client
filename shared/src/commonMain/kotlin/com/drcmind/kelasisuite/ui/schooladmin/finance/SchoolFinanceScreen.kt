package com.drcmind.kelasisuite.ui.schooladmin.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drcmind.kelasisuite.domain.model.finance.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolFinanceScreen(
    viewModel: SchoolFinanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Finance & Trésorerie Scolaire", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Recouvrement des frais, caisse d'encaissement et solvabilité",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.openPaymentDialog() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Encaisser Paiement")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            // Navigation Tabs M3
            PrimaryTabRow(
                selectedTabIndex = uiState.activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                FinanceTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.activeTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        text = { Text(tab.label, fontWeight = if (uiState.activeTab == tab) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Corps du Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (uiState.activeTab) {
                    FinanceTab.DASHBOARD -> FinanceDashboardTab(uiState = uiState)
                    FinanceTab.CASH_REGISTER -> CashRegisterTab(uiState = uiState, onNewPayment = { viewModel.openPaymentDialog() })
                    FinanceTab.SOLVENCY -> SolvencyTab(
                        uiState = uiState,
                        onFilterChange = { viewModel.setSolvencyFilter(it) },
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onPayClick = { viewModel.openPaymentDialog(it) }
                    )
                    FinanceTab.FEE_STRUCTURE -> FeeStructureTab(
                        uiState = uiState,
                        onAddFeeClick = { viewModel.openFeeDialog() }
                    )
                }
            }
        }
    }

    // Modal Encaisser un Paiement
    if (uiState.isPaymentDialogOpen) {
        PaymentRecordDialog(
            form = uiState.paymentForm,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closePaymentDialog() },
            onUpdate = { viewModel.updatePaymentForm(it) },
            onSubmit = { viewModel.submitPayment() }
        )
    }

    // Modal Ajouter Frais à la Grille
    if (uiState.isFeeDialogOpen) {
        AddFeeDialog(
            form = uiState.feeForm,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeFeeDialog() },
            onUpdate = { viewModel.updateFeeForm(it) },
            onSubmit = { viewModel.submitFeeStructure() }
        )
    }
}

// -------------------------------------------------------------
// 1. DASHBOARD FINANCIER
// -------------------------------------------------------------
@Composable
fun FinanceDashboardTab(uiState: SchoolFinanceUiState) {
    val summary = uiState.dashboardSummary ?: return

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Cartes KPIs Générales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FinanceKpiCard(
                    title = "Montant Total Recouvré",
                    value = "${summary.totalCollected.toInt()} USD",
                    subtitle = "Taux de recouvrement : ${(summary.collectionRatePercentage * 10).toInt() / 10.0}%",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                FinanceKpiCard(
                    title = "Reste à Recouvrer (Impayés)",
                    value = "${summary.totalOutstanding.toInt()} USD",
                    subtitle = "Prévision totale : ${summary.totalExpected.toInt()} USD",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.weight(1f)
                )
                FinanceKpiCard(
                    title = "Encaissé Aujourd'hui",
                    value = "${summary.todayCollected.toInt()} USD",
                    subtitle = "Espèces & Mobile Money",
                    containerColor = Color(0xFF2E7D32).copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Décomposition par canal de paiement
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Canaux d'Encaissement & Moyens de Paiement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PaymentChannelPill(label = "Espèces (Caisse)", amount = "${summary.cashCollected.toInt()} USD", icon = Icons.Default.Payments)
                        PaymentChannelPill(label = "Mobile Money (M-Pesa / OM)", amount = "${summary.mobileMoneyCollected.toInt()} USD", icon = Icons.Default.PhoneAndroid)
                        PaymentChannelPill(label = "Banque / Virement", amount = "${summary.bankCollected.toInt()} USD", icon = Icons.Default.AccountBalance)
                    }
                }
            }
        }

        item {
            // Répartition Solvabilité Élèves
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("État de Solvabilité des Élèves (${summary.totalStudentsCount} élèves)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SolvencyStatBadge(label = "Totalement en règle", count = summary.fullyPaidStudentsCount, color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                        SolvencyStatBadge(label = "Acompte versé", count = summary.partialPaidStudentsCount, color = Color(0xFFF57C00), modifier = Modifier.weight(1f))
                        SolvencyStatBadge(label = "Non en règle (0%)", count = summary.nonPaidStudentsCount, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CAISSE & ENCAISSEMENT RAPIDE
// -------------------------------------------------------------
@Composable
fun CashRegisterTab(uiState: SchoolFinanceUiState, onNewPayment: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Journal des Transactions Récentes (${uiState.recentTransactions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.recentTransactions, key = { it.id }) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.studentName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${tx.feeDescription} • ${tx.classroomName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Réf: ${tx.transactionRef} • ${tx.paymentDate} • Par ${tx.paymentMethod.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${tx.amountPaid.toInt()} ${tx.currency}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Payé & Validé",
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SOLVABILITÉ & IMPAYÉS
// -------------------------------------------------------------
@Composable
fun SolvencyTab(
    uiState: SchoolFinanceUiState,
    onFilterChange: (SolvencyFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onPayClick: (StudentSolvencyItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Barre de Recherche & Filtres
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Rechercher un élève...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            )

            // Chips Filtres
            SolvencyFilter.entries.forEach { filter ->
                FilterChip(
                    selected = uiState.solvencyFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table des élèves
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.filteredSolvencyList, key = { it.studentId }) { student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(student.studentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("${student.rollNumber} • ${student.classroomName} • Contact: ${student.parentPhone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${student.totalPaid.toInt()} / ${student.totalDue.toInt()} USD",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            LinearProgressIndicator(
                                progress = { (student.paidPercentage / 100.0).toFloat() },
                                modifier = Modifier.width(120.dp).height(6.dp),
                                color = if (student.isFullyPaid) Color(0xFF2E7D32) else if (student.totalPaid > 0) Color(0xFFF57C00) else MaterialTheme.colorScheme.error,
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        }

                        Box(modifier = Modifier.width(130.dp), contentAlignment = Alignment.Center) {
                            if (student.isFullyPaid) {
                                Surface(
                                    color = Color(0xFF2E7D32).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("En règle", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            } else {
                                Text(
                                    text = "Reste: ${student.balanceRemaining.toInt()} USD",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        if (!student.isFullyPaid) {
                            Button(
                                onClick = { onPayClick(student) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Payer", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. GRILLE TARIFAIRE
// -------------------------------------------------------------
@Composable
fun FeeStructureTab(uiState: SchoolFinanceUiState, onAddFeeClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Grille Tarifaire des Frais Scolaires", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(
                onClick = onAddFeeClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ajouter une Rubrique")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.feeStructures, key = { it.id }) { fee ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(fee.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text("Catégorie : ${fee.category.label} • Classes : ${fee.targetClassName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Échéance limite : ${fee.dueDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }

                        Text(
                            text = "${fee.amount.toInt()} ${fee.currency}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS
// -------------------------------------------------------------
@Composable
fun PaymentRecordDialog(
    form: PaymentDialogFormState,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (PaymentDialogFormState.() -> PaymentDialogFormState) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Encaisser un Paiement Scolaire", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = form.studentName,
                    onValueChange = { onUpdate { copy(studentName = it) } },
                    label = { Text("Élève bénéficiaire") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.amountInput,
                    onValueChange = { onUpdate { copy(amountInput = it) } },
                    label = { Text("Montant Encaissé (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Mode de Paiement :", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(PaymentMethod.CASH, PaymentMethod.MPESA, PaymentMethod.ORANGE_MONEY, PaymentMethod.BANK_TRANSFER).forEach { method ->
                        FilterChip(
                            selected = form.paymentMethod == method,
                            onClick = { onUpdate { copy(paymentMethod = method) } },
                            label = { Text(method.shortCode) }
                        )
                    }
                }
                OutlinedTextField(
                    value = form.notes,
                    onValueChange = { onUpdate { copy(notes = it) } },
                    label = { Text("Observation / Référence Quittance") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Valider & Émettre Reçu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun AddFeeDialog(
    form: FeeStructureFormState,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (FeeStructureFormState.() -> FeeStructureFormState) -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Rubrique Tarifaire", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { onUpdate { copy(title = it) } },
                    label = { Text("Libellé du Frais (ex: Minerval 2ème Trim)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.amountInput,
                    onValueChange = { onUpdate { copy(amountInput = it) } },
                    label = { Text("Montant (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.dueDate,
                    onValueChange = { onUpdate { copy(dueDate = it) } },
                    label = { Text("Date d'échéance (AAAA-MM-JJ)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

// -------------------------------------------------------------
// COMPOSANTS VISUELS REUTILISABLES
// -------------------------------------------------------------
@Composable
fun FinanceKpiCard(
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PaymentChannelPill(label: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(amount, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SolvencyStatBadge(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, textAlign = TextAlign.Center)
        }
    }
}
