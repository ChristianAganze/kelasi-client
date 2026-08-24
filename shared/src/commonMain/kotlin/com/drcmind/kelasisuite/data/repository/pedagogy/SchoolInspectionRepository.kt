package com.drcmind.kelasisuite.data.repository.pedagogy

import com.drcmind.kelasisuite.domain.model.pedagogy.ClassInspectionReport
import com.drcmind.kelasisuite.domain.model.pedagogy.InspectionRating
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SchoolInspectionRepository {
    fun getInspectionReports(schoolId: Long): Flow<Resource<List<ClassInspectionReport>>>
    suspend fun saveInspectionReport(report: ClassInspectionReport): Resource<ClassInspectionReport>
    suspend fun acknowledgeReport(reportId: Long, teacherFeedback: String): Resource<Boolean>
}

class SchoolInspectionRepositoryImpl : SchoolInspectionRepository {

    private val reports = mutableListOf(
        ClassInspectionReport(
            id = 1L,
            teacherId = 101L,
            teacherName = "Prof. Kasongo Ilunga",
            classroomName = "6ème Math-Physique A",
            subjectName = "Physique Quantique",
            lessonTopic = "Dualité Onde-Corpuscule & Effet Photoélectrique",
            inspectionDate = "2026-02-18",
            inspectorName = "M. Mukendi Jean-Pierre",
            inspectorRole = "Préfet des Études",
            globalScore = 88,
            rating = InspectionRating.EXCELLENT,
            strengths = "Maîtrise approfondie des concepts, utilisation remarquable du tableau et excellente interaction avec les apprenants.",
            areasForImprovement = "Rythme un peu rapide lors de la résolution de l'exercice d'application numéro 2.",
            recommendations = "Prévoir une courte phase de vérification individuelle auprès des élèves assis au fond de la classe.",
            teacherFeedback = "Bien noté, j'adapterai le temps consacré aux exercices.",
            isSignedByInspector = true,
            isAcknowledgedByTeacher = true
        ),
        ClassInspectionReport(
            id = 2L,
            teacherId = 102L,
            teacherName = "Prof. Kabeya Paul",
            classroomName = "5ème Scientifique B",
            subjectName = "Chimie Organique",
            lessonTopic = "Nomenclature des Hydrocarbures Aromatiques",
            inspectionDate = "2026-02-12",
            inspectorName = "Mme Banze Claire",
            inspectorRole = "Conseillère Pédagogique",
            globalScore = 74,
            rating = InspectionRating.BIEN,
            strengths = "Excellente tenue de la fiche de préparation et du journal de classe. Clarté de la voix.",
            areasForImprovement = "Gestion du temps : la synthèse finale a été écourtée par la sonnerie.",
            recommendations = "Mieux répartir les 50 minutes et réserver 10 minutes fermes pour la fixation et le résumé.",
            isSignedByInspector = true,
            isAcknowledgedByTeacher = false
        ),
        ClassInspectionReport(
            id = 3L,
            teacherId = 103L,
            teacherName = "Prof. Mwamba Roger",
            classroomName = "4ème Éducation de Base",
            subjectName = "Mathématiques / Algèbre",
            lessonTopic = "Résolution des Équations du 1er Degré",
            inspectionDate = "2026-02-05",
            inspectorName = "M. Mukendi Jean-Pierre",
            inspectorRole = "Préfet des Études",
            globalScore = 58,
            rating = InspectionRating.A_AMELIORER,
            strengths = "Présence et autorité en classe.",
            areasForImprovement = "Démarche trop magistrale. Manque d'implication des élèves au tableau.",
            recommendations = "Passer au tableau au moins 4 élèves différents et faire manipuler la règle des signes.",
            isSignedByInspector = true,
            isAcknowledgedByTeacher = false
        )
    )

    override fun getInspectionReports(schoolId: Long): Flow<Resource<List<ClassInspectionReport>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(reports.sortedByDescending { it.id }))
    }

    override suspend fun saveInspectionReport(report: ClassInspectionReport): Resource<ClassInspectionReport> {
        val nextId = (reports.maxOfOrNull { it.id } ?: 0L) + 1L
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
        
        val newReport = report.copy(
            id = nextId,
            inspectionDate = if (report.inspectionDate.isBlank()) dateStr else report.inspectionDate
        )
        reports.add(0, newReport)
        return Resource.Success(newReport)
    }

    override suspend fun acknowledgeReport(reportId: Long, teacherFeedback: String): Resource<Boolean> {
        val index = reports.indexOfFirst { it.id == reportId }
        if (index != -1) {
            reports[index] = reports[index].copy(
                isAcknowledgedByTeacher = true,
                teacherFeedback = teacherFeedback
            )
            return Resource.Success(true)
        }
        return Resource.Error("Rapport d'inspection introuvable")
    }
}
