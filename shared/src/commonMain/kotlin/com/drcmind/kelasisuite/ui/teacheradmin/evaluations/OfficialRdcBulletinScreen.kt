package com.drcmind.kelasisuite.ui.teacheradmin.evaluations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

data class SubjectGradeRow(
    val category: String, // "I. COURS GÉNÉRAUX", "II. COURS SCIENTIFIQUES & TECHNIQUES", "III. COURS PRATIQUES"
    val subjectName: String,
    val maxPeriod: Int,
    val maxExam: Int,
    val p1: Double?,
    val p2: Double?,
    val ex1: Double?,
    val p3: Double?,
    val p4: Double?,
    val ex2: Double?
) {
    val totalSem1: Double? get() = if (p1 != null && p2 != null && ex1 != null) p1 + p2 + ex1 else null
    val maxSem1: Int get() = (maxPeriod * 2) + maxExam
    val totalSem2: Double? get() = if (p3 != null && p4 != null && ex2 != null) p3 + p4 + ex2 else null
    val maxSem2: Int get() = (maxPeriod * 2) + maxExam
    val totalAnnual: Double? get() = if (totalSem1 != null && totalSem2 != null) totalSem1!! + totalSem2!! else null
    val maxAnnual: Int get() = maxSem1 + maxSem2
}

data class StudentBulletinData(
    val id: Long,
    val studentName: String,
    val gender: String,
    val birthDate: String,
    val birthPlace: String,
    val permanentNumber: String,
    val className: String,
    val option: String,
    val schoolYear: String,
    val schoolName: String,
    val educationalProvince: String,
    val cityCommune: String,
    val schoolNationalCode: String,
    val grades: List<SubjectGradeRow>,
    val p1Conduct: String = "TB",
    val p2Conduct: String = "TB",
    val sem1Conduct: String = "TB",
    val p3Conduct: String = "TB",
    val p4Conduct: String = "TB",
    val sem2Conduct: String = "TB",
    val annualConduct: String = "TB",
    val p1Rank: String = "1er / 40",
    val p2Rank: String = "2e / 40",
    val sem1Rank: String = "1er / 40",
    val p3Rank: String = "1er / 40",
    val p4Rank: String = "1er / 40",
    val sem2Rank: String = "1er / 40",
    val annualRank: String = "1er / 40",
    val teacherComment: String = "Élève très doué, consciencieux et discipliné. Excellents résultats en sciences exactes et raisonnement logique.",
    val juryDecision: String = "Passe dans la classe supérieure avec DISTINCTION.",
    val headmasterName: String = "Prof. Jean-Claude TSHIMANGA",
    val teacherName: String = "Prof. Roger MUKENDI"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialRdcBulletinScreen(
    modifier: Modifier = Modifier
) {
    val sampleGradesDavid = listOf(
        // I. Cours Généraux
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Français / Expression & Littérature", 20, 40, 16.5, 17.0, 34.0, 16.0, 17.5, 35.0),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Anglais", 20, 40, 15.0, 16.0, 31.0, 15.5, 16.5, 33.0),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Histoire & Éduc. Citoyenneté", 20, 40, 18.0, 17.5, 36.0, 18.0, 18.5, 37.0),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Géographie de la RDC & Monde", 20, 40, 17.0, 16.0, 33.0, 16.5, 17.0, 34.0),
        // II. Cours Scientifiques
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Mathématiques (Algèbre & Analyse)", 30, 60, 27.5, 28.5, 56.0, 28.0, 29.0, 58.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Physique Générale & Mécanique", 25, 50, 22.0, 23.0, 45.0, 23.5, 24.0, 47.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Chimie Minérale & Organique", 20, 40, 18.0, 17.5, 35.0, 18.5, 19.0, 38.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Biologie Humaine & Écologie", 20, 40, 16.0, 17.0, 33.0, 17.0, 16.5, 34.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Informatique & Algorithmique", 20, 40, 19.0, 19.5, 39.0, 19.5, 20.0, 40.0),
        // III. Cours Pratiques
        SubjectGradeRow("III. COURS PRATIQUES & COMPORTEMENT", "Dessin Scientifique & Technique", 10, 20, 8.5, 9.0, 17.0, 9.0, 9.5, 18.0),
        SubjectGradeRow("III. COURS PRATIQUES & COMPORTEMENT", "Éducation Physique & Santé", 10, 20, 9.0, 9.5, 19.0, 9.5, 10.0, 19.5)
    )

    val sampleGradesSarah = listOf(
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Français / Expression & Littérature", 20, 40, 18.0, 18.5, 37.0, 19.0, 18.5, 38.0),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Anglais", 20, 40, 17.5, 18.0, 36.0, 18.0, 18.5, 37.0),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Histoire & Éduc. Citoyenneté", 20, 40, 19.0, 18.5, 38.0, 19.0, 19.0, 38.5),
        SubjectGradeRow("I. COURS GÉNÉRAUX", "Géographie de la RDC & Monde", 20, 40, 18.0, 17.5, 35.0, 18.0, 18.0, 36.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Mathématiques (Algèbre & Analyse)", 30, 60, 29.0, 29.5, 59.0, 29.5, 30.0, 60.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Physique Générale & Mécanique", 25, 50, 24.0, 24.5, 48.0, 24.5, 25.0, 49.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Chimie Minérale & Organique", 20, 40, 19.0, 19.5, 39.0, 19.5, 19.5, 39.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Biologie Humaine & Écologie", 20, 40, 18.5, 19.0, 38.0, 18.5, 19.0, 38.0),
        SubjectGradeRow("II. COURS SCIENTIFIQUES & TECHNIQUES", "Informatique & Algorithmique", 20, 40, 20.0, 20.0, 40.0, 20.0, 20.0, 40.0),
        SubjectGradeRow("III. COURS PRATIQUES & COMPORTEMENT", "Dessin Scientifique & Technique", 10, 20, 9.5, 9.5, 19.0, 9.5, 10.0, 19.5),
        SubjectGradeRow("III. COURS PRATIQUES & COMPORTEMENT", "Éducation Physique & Santé", 10, 20, 9.5, 10.0, 19.5, 10.0, 10.0, 20.0)
    )

    val students = listOf(
        StudentBulletinData(
            id = 1,
            studentName = "KABEYA MUKENDI David",
            gender = "M",
            birthDate = "14/03/2009",
            birthPlace = "Kinshasa",
            permanentNumber = "140309-KIN-0042",
            className = "4ème Secondaire (Humanités Scientifiques)",
            option = "SCIENTIFIQUE",
            schoolYear = "2025 - 2026",
            schoolName = "COMPLEXE SCOLAIRE EXCELLENCE",
            educationalProvince = "KINSHASA - MONT-AMBA",
            cityCommune = "KINSHASA / LEMBA",
            schoolNationalCode = "110423",
            grades = sampleGradesDavid,
            annualRank = "2e / 40",
            teacherComment = "Élève très doué, consciencieux et discipliné. Excellents résultats en sciences exactes et raisonnement logique.",
            juryDecision = "Passe dans la classe supérieure avec DISTINCTION."
        ),
        StudentBulletinData(
            id = 2,
            studentName = "TSHILANDA KASONGO Sarah",
            gender = "F",
            birthDate = "22/07/2009",
            birthPlace = "Lubumbashi",
            permanentNumber = "220709-LSH-0018",
            className = "4ème Secondaire (Humanités Scientifiques)",
            option = "SCIENTIFIQUE",
            schoolYear = "2025 - 2026",
            schoolName = "COMPLEXE SCOLAIRE EXCELLENCE",
            educationalProvince = "KINSHASA - MONT-AMBA",
            cityCommune = "KINSHASA / LEMBA",
            schoolNationalCode = "110423",
            grades = sampleGradesSarah,
            annualRank = "1ère / 40",
            teacherComment = "Remarquable trimestre et année académique exemplaire ! Félicitations du jury pour sa grande régularité.",
            juryDecision = "Passe dans la classe supérieure avec LA PLUS GRANDE DISTINCTION."
        )
    )

    var selectedStudentIndex by remember { mutableStateOf(0) }
    val currentStudent = students[selectedStudentIndex]
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Action bar & student switcher
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bulletin Officiel RDC - MINEPST",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var studentDropdownExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedButton(
                                onClick = { studentDropdownExpanded = true }
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(currentStudent.studentName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = studentDropdownExpanded,
                                onDismissRequest = { studentDropdownExpanded = false }
                            ) {
                                students.forEachIndexed { index, student ->
                                    DropdownMenuItem(
                                        text = { Text(student.studentName) },
                                        onClick = {
                                            selectedStudentIndex = index
                                            studentDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                // PDF Export simulation
                            }
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Imprimer / Export PDF")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Bulletin Render View (Container formatted like real A4 DRC Official Sheet)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E3A8A)),
                    width = 2.dp
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 1. En-tête national & armoiries RDC
                    item {
                        RdcOfficialHeader(student = currentStudent)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 2. Grille matricielle officielle des cotes
                    item {
                        RdcGradesMatrixTable(student = currentStudent)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. Synthèse, Délibération du Jury et Signatures
                    item {
                        RdcBulletinFooter(student = currentStudent)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RdcOfficialHeader(student: StudentBulletinData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top national symbols row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DRC Official Flag
            RdcFlagCanvas(modifier = Modifier.size(width = 64.dp, height = 44.dp))

            // Center Institutional Texts
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RÉPUBLIQUE DÉMOCRATIQUE DU CONGO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "MINISTÈRE DE L'ÉDUCATION NATIONALE ET NOUVELLE CITOYENNETÉ",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "PAIX - JUSTICE - TRAVAIL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            // DRC Coat of Arms Emblem
            RdcCoatOfArmsEmblem(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFF1E3A8A), thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Identification de l'Établissement & Province
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("PROVINCE ÉDUCATIONNELLE : ${student.educationalProvince}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("VILLE / COMMUNE : ${student.cityCommune}", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                Text("ÉTABLISSEMENT : ${student.schoolName}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text("CODE ÉCOLE : ${student.schoolNationalCode}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("SECTION : SECONDAIRE GÉNÉRAL & HUMANITÉS", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                Text("OPTION : ${student.option}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Titre du Document
        Surface(
            color = Color(0xFF1E3A8A),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "BULLETIN SCOLAIRE OFFICIEL • ANNÉE ${student.schoolYear}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Signalétique de l'élève
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    Text("NOM & POST-NOM : ${student.studentName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                    Text("NÉ(E) À : ${student.birthPlace} LE : ${student.birthDate}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                Column(modifier = Modifier.weight(0.8f)) {
                    Text("SEXE : ${student.gender} • CLASSE : ${student.className}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = Color.Black)
                    Text("N° PERMANENT : ${student.permanentNumber}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun RdcGradesMatrixTable(student: StudentBulletinData) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .background(Color(0xFF1E3A8A))
                .border(1.dp, Color(0xFF0F172A))
        ) {
            HeaderCell("DISCIPLINES / COURS", width = 230.dp, isLight = false)
            HeaderCell("MAX", width = 50.dp, isLight = false)
            HeaderCell("1ère P", width = 55.dp, isLight = false)
            HeaderCell("2ème P", width = 55.dp, isLight = false)
            HeaderCell("EXAM 1", width = 60.dp, isLight = false)
            HeaderCell("TOT 1", width = 65.dp, isLight = false, isBold = true)
            HeaderCell("3ème P", width = 55.dp, isLight = false)
            HeaderCell("4ème P", width = 55.dp, isLight = false)
            HeaderCell("EXAM 2", width = 60.dp, isLight = false)
            HeaderCell("TOT 2", width = 65.dp, isLight = false, isBold = true)
            HeaderCell("TOT ANNUEL", width = 85.dp, isLight = false, isBold = true)
        }

        // Group rows by category
        val categories = student.grades.groupBy { it.category }

        var totalMaxPeriod = 0
        var totalMaxExam = 0
        var sumP1 = 0.0
        var sumP2 = 0.0
        var sumEx1 = 0.0
        var sumTot1 = 0.0
        var sumP3 = 0.0
        var sumP4 = 0.0
        var sumEx2 = 0.0
        var sumTot2 = 0.0
        var sumAnnual = 0.0

        categories.forEach { (catName, items) ->
            // Category Header Row
            Row(
                modifier = Modifier
                    .background(Color(0xFFE2E8F0))
                    .border(0.5.dp, Color(0xFFCBD5E1))
            ) {
                Text(
                    text = catName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).width(230.dp)
                )
                Spacer(modifier = Modifier.width(550.dp))
            }

            items.forEach { row ->
                totalMaxPeriod += row.maxPeriod
                totalMaxExam += row.maxExam
                sumP1 += row.p1 ?: 0.0
                sumP2 += row.p2 ?: 0.0
                sumEx1 += row.ex1 ?: 0.0
                sumTot1 += row.totalSem1 ?: 0.0
                sumP3 += row.p3 ?: 0.0
                sumP4 += row.p4 ?: 0.0
                sumEx2 += row.ex2 ?: 0.0
                sumTot2 += row.totalSem2 ?: 0.0
                sumAnnual += row.totalAnnual ?: 0.0

                GradeDataRow(row)
            }
        }

        val totalMaxSem1 = (totalMaxPeriod * 2) + totalMaxExam
        val totalMaxSem2 = (totalMaxPeriod * 2) + totalMaxExam
        val totalMaxAnnual = totalMaxSem1 + totalMaxSem2

        // Synthese Rows: MAXIMAS TOTAUX
        SummaryRow(
            label = "MAXIMA TOTAL",
            valMax = "-",
            p1 = "$totalMaxPeriod",
            p2 = "$totalMaxPeriod",
            ex1 = "$totalMaxExam",
            tot1 = "$totalMaxSem1",
            p3 = "$totalMaxPeriod",
            p4 = "$totalMaxPeriod",
            ex2 = "$totalMaxExam",
            tot2 = "$totalMaxSem2",
            totAnnual = "$totalMaxAnnual",
            bgColor = Color(0xFFF1F5F9),
            isBold = true
        )

        // TOTAUX DES POINTS OBTENUS
        SummaryRow(
            label = "TOTAL DES POINTS OBTENUS",
            valMax = "",
            p1 = "%.1f".format(sumP1),
            p2 = "%.1f".format(sumP2),
            ex1 = "%.1f".format(sumEx1),
            tot1 = "%.1f".format(sumTot1),
            p3 = "%.1f".format(sumP3),
            p4 = "%.1f".format(sumP4),
            ex2 = "%.1f".format(sumEx2),
            tot2 = "%.1f".format(sumTot2),
            totAnnual = "%.1f".format(sumAnnual),
            bgColor = Color(0xFFE2E8F0),
            isBold = true
        )

        // POURCENTAGE %
        val pctP1 = (sumP1 / totalMaxPeriod) * 100
        val pctP2 = (sumP2 / totalMaxPeriod) * 100
        val pctEx1 = (sumEx1 / totalMaxExam) * 100
        val pctTot1 = (sumTot1 / totalMaxSem1) * 100
        val pctP3 = (sumP3 / totalMaxPeriod) * 100
        val pctP4 = (sumP4 / totalMaxPeriod) * 100
        val pctEx2 = (sumEx2 / totalMaxExam) * 100
        val pctTot2 = (sumTot2 / totalMaxSem2) * 100
        val pctAnnual = (sumAnnual / totalMaxAnnual) * 100

        SummaryRow(
            label = "POURCENTAGE %",
            valMax = "100%",
            p1 = "%.1f%%".format(pctP1),
            p2 = "%.1f%%".format(pctP2),
            ex1 = "%.1f%%".format(pctEx1),
            tot1 = "%.1f%%".format(pctTot1),
            p3 = "%.1f%%".format(pctP3),
            p4 = "%.1f%%".format(pctP4),
            ex2 = "%.1f%%".format(pctEx2),
            tot2 = "%.1f%%".format(pctTot2),
            totAnnual = "%.1f%%".format(pctAnnual),
            bgColor = Color(0xFFFEF3C7),
            isBold = true,
            textColor = Color(0xFF92400E)
        )

        // PLACE / RANG
        SummaryRow(
            label = "PLACE / RANG OCCUPÉ",
            valMax = "",
            p1 = student.p1Rank,
            p2 = student.p2Rank,
            ex1 = "-",
            tot1 = student.sem1Rank,
            p3 = student.p3Rank,
            p4 = student.p4Rank,
            ex2 = "-",
            tot2 = student.sem2Rank,
            totAnnual = student.annualRank,
            bgColor = Color.White,
            isBold = true,
            textColor = Color(0xFF1E3A8A)
        )

        // APPLICATION & CONDUITE
        SummaryRow(
            label = "APPLICATION & CONDUITE",
            valMax = "",
            p1 = student.p1Conduct,
            p2 = student.p2Conduct,
            ex1 = "-",
            tot1 = student.sem1Conduct,
            p3 = student.p3Conduct,
            p4 = student.p4Conduct,
            ex2 = "-",
            tot2 = student.sem2Conduct,
            totAnnual = student.annualConduct,
            bgColor = Color(0xFFF8FAFC),
            isBold = true
        )
    }
}

@Composable
fun GradeDataRow(row: SubjectGradeRow) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = row.subjectName,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp).width(230.dp)
        )
        TableCell("${row.maxPeriod}", width = 50.dp, color = Color.DarkGray)
        TableCell(row.p1?.let { "%.1f".format(it) } ?: "-", width = 55.dp)
        TableCell(row.p2?.let { "%.1f".format(it) } ?: "-", width = 55.dp)
        TableCell(row.ex1?.let { "%.1f".format(it) } ?: "-", width = 60.dp)
        TableCell(row.totalSem1?.let { "%.1f".format(it) } ?: "-", width = 65.dp, isBold = true, bgColor = Color(0xFFF1F5F9))
        TableCell(row.p3?.let { "%.1f".format(it) } ?: "-", width = 55.dp)
        TableCell(row.p4?.let { "%.1f".format(it) } ?: "-", width = 55.dp)
        TableCell(row.ex2?.let { "%.1f".format(it) } ?: "-", width = 60.dp)
        TableCell(row.totalSem2?.let { "%.1f".format(it) } ?: "-", width = 65.dp, isBold = true, bgColor = Color(0xFFF1F5F9))
        TableCell(row.totalAnnual?.let { "%.1f".format(it) } ?: "-", width = 85.dp, isBold = true, bgColor = Color(0xFFE0E7FF), textColor = Color(0xFF1E3A8A))
    }
}

@Composable
fun SummaryRow(
    label: String,
    valMax: String,
    p1: String,
    p2: String,
    ex1: String,
    tot1: String,
    p3: String,
    p4: String,
    ex2: String,
    tot2: String,
    totAnnual: String,
    bgColor: Color = Color.White,
    isBold: Boolean = false,
    textColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .background(bgColor)
            .border(0.5.dp, Color(0xFFCBD5E1))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp).width(230.dp)
        )
        TableCell(valMax, width = 50.dp, isBold = isBold, textColor = textColor)
        TableCell(p1, width = 55.dp, isBold = isBold, textColor = textColor)
        TableCell(p2, width = 55.dp, isBold = isBold, textColor = textColor)
        TableCell(ex1, width = 60.dp, isBold = isBold, textColor = textColor)
        TableCell(tot1, width = 65.dp, isBold = true, textColor = textColor)
        TableCell(p3, width = 55.dp, isBold = isBold, textColor = textColor)
        TableCell(p4, width = 55.dp, isBold = isBold, textColor = textColor)
        TableCell(ex2, width = 60.dp, isBold = isBold, textColor = textColor)
        TableCell(tot2, width = 65.dp, isBold = true, textColor = textColor)
        TableCell(totAnnual, width = 85.dp, isBold = true, textColor = textColor)
    }
}

@Composable
fun HeaderCell(text: String, width: androidx.compose.ui.unit.Dp, isLight: Boolean = true, isBold: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
        color = if (isLight) Color.Black else Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(width)
            .padding(vertical = 4.dp)
    )
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    color: Color = Color.Black,
    textColor: Color = color,
    bgColor: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .width(width)
            .background(bgColor)
            .padding(vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RdcBulletinFooter(student: StudentBulletinData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Appréciation Titulaire & Décision du Jury
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "AVIS DU PROFESSEUR TITULAIRE :",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "« ${student.teacherComment} »",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "DÉCISION DU JURY DE FIN D'ANNÉE :",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = student.juryDecision,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Signatures Officielles (3 Colonnes réglementaires)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Signature Enseignant / Titulaire
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Le Professeur Titulaire", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(28.dp))
                Text(student.teacherName, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = Color(0xFF1E3A8A))
                Text("Signé numériquement", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Signature Parents / Tuteur
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Visa des Parents / Tuteur", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(28.dp))
                Text("Vu et pris connaissance", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("Date : ___/___/2026", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sceau & Signature Chef d'Établissement
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Le Chef d'Établissement", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(28.dp))
                Text(student.headmasterName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                Text("Sceau Officiel de l'École", style = MaterialTheme.typography.labelSmall, color = Color(0xFF15803D))
            }
        }
    }
}

/**
 * Exact Canvas representation of the Flag of the Democratic Republic of the Congo (RDC):
 * - Sky Blue background
 * - Red diagonal stripe from bottom-left to top-right
 * - Yellow thin borders around the red stripe
 * - Yellow five-pointed star in the top-left canton
 */
@Composable
fun RdcFlagCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Sky Blue background
        drawRect(color = Color(0xFF007FFF), size = size)

        // 2. Yellow diagonal border
        val yellowBandWidth = h * 0.35f
        val redBandWidth = h * 0.22f

        val yellowPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, (h - yellowBandWidth).coerceAtLeast(0f))
            lineTo(w, 0f)
            lineTo(w, yellowBandWidth.coerceAtMost(h))
            close()
        }
        drawPath(path = yellowPath, color = Color(0xFFFFD700))

        // 3. Red diagonal stripe
        val redPath = Path().apply {
            moveTo(0f, h - (yellowBandWidth - redBandWidth) / 2)
            lineTo(0f, (h - (yellowBandWidth + redBandWidth) / 2).coerceAtLeast(0f))
            lineTo(w, (yellowBandWidth - redBandWidth) / 2)
            lineTo(w, (yellowBandWidth + redBandWidth) / 2)
            close()
        }
        drawPath(path = redPath, color = Color(0xFFCE1126))

        // 4. Yellow 5-pointed Star in top-left
        val starCenterX = w * 0.20f
        val starCenterY = h * 0.28f
        val starRadius = h * 0.22f
        val starInnerRadius = starRadius * 0.4f

        val starPath = Path().apply {
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) starRadius else starInnerRadius
                val angle = Math.toRadians((i * 36 - 90).toDouble())
                val x = starCenterX + (r * cos(angle)).toFloat()
                val y = starCenterY + (r * sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path = starPath, color = Color(0xFFFFD700))
    }
}

/**
 * DRC Coat of Arms Emblem (Armoiries de la RDC)
 * Includes canvas rendering of Leopard head motif, spear and tusk heraldry
 */
@Composable
fun RdcCoatOfArmsEmblem(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(3.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f

                // Elephant tusk curve (left)
                val tuskPath = Path().apply {
                    moveTo(cx - w * 0.35f, cy + h * 0.35f)
                    quadraticTo(cx - w * 0.45f, cy - h * 0.15f, cx - w * 0.15f, cy - h * 0.4f)
                }
                drawPath(tuskPath, color = Color(0xFFD97706), style = Stroke(width = 2.dp.toPx()))

                // Spear (right diagonal)
                drawLine(
                    color = Color(0xFFDC2626),
                    start = Offset(cx + w * 0.35f, cy + h * 0.35f),
                    end = Offset(cx + w * 0.15f, cy - h * 0.4f),
                    strokeWidth = 2.dp.toPx()
                )

                // Leopard Head center circle badge
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = w * 0.22f,
                    center = Offset(cx, cy - h * 0.05f)
                )
                drawCircle(
                    color = Color(0xFF1E3A8A),
                    radius = w * 0.22f,
                    center = Offset(cx, cy - h * 0.05f),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Leopard spots / eyes
                drawCircle(color = Color(0xFF1E293B), radius = w * 0.035f, center = Offset(cx - w * 0.08f, cy - h * 0.08f))
                drawCircle(color = Color(0xFF1E293B), radius = w * 0.035f, center = Offset(cx + w * 0.08f, cy - h * 0.08f))
                drawCircle(color = Color(0xFF1E293B), radius = w * 0.04f, center = Offset(cx, cy - h * 0.01f))

                // Bottom Ribbon base
                val ribbonPath = Path().apply {
                    moveTo(cx - w * 0.42f, cy + h * 0.38f)
                    lineTo(cx + w * 0.42f, cy + h * 0.38f)
                    lineTo(cx + w * 0.35f, cy + h * 0.46f)
                    lineTo(cx - w * 0.35f, cy + h * 0.46f)
                    close()
                }
                drawPath(ribbonPath, color = Color(0xFF1E3A8A))
            }
            Text(
                text = "RDC",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                fontSize = 7.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 1.dp)
            )
        }
    }
}
