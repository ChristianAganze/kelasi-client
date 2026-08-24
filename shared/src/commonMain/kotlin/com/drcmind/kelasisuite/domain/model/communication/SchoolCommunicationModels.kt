package com.drcmind.kelasisuite.domain.model.communication

import kotlinx.serialization.Serializable

enum class AnnouncementAudience(val label: String) {
    ALL("Tous (Parents, Enseignants & Staff)"),
    PARENTS_ONLY("Parents d'élèves uniquement"),
    TEACHERS_ONLY("Corps Enseignant uniquement"),
    STAFF_ONLY("Personnel Administratif & Ouvrier"),
    SPECIFIC_CLASSES("Classes / Niveaux spécifiques")
}

enum class AnnouncementPriority(val label: String, val badgeColorHex: Long) {
    NORMAL("Information Générale", 0xFF1976D2),
    IMPORTANT("Important", 0xFFF57C00),
    URGENT("Urgent / Obligatoire", 0xFFD32F2F)
}

enum class AnnouncementType(val label: String) {
    CIRCULAR("Note Circulaire & Directives"),
    MEETING("Convocation de Réunion"),
    HOLIDAY("Congés & Vacances scolaires"),
    EXAM_ALERT("Avis d'Examens / Contrôles"),
    ACTIVITY("Événement / Activité parascolaire")
}

@Serializable
data class SchoolOfficialAnnouncement(
    val id: Long = 0L,
    val title: String,
    val summary: String,
    val content: String,
    val type: AnnouncementType,
    val priority: AnnouncementPriority,
    val audience: AnnouncementAudience,
    val targetClasses: List<String> = emptyList(),
    val publishedDate: String,
    val publishedBy: String,
    val signedByTitle: String = "La Direction Générale",
    val isPinned: Boolean = false,
    val attachmentUrl: String? = null,
    val attachmentName: String? = null
)

@Serializable
data class DirectParentMessage(
    val id: Long = 0L,
    val parentId: Long,
    val parentName: String,
    val studentName: String,
    val classroomName: String,
    val phone: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isImportant: Boolean = false
)
