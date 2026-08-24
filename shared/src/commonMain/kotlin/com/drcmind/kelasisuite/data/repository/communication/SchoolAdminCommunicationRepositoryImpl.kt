package com.drcmind.kelasisuite.data.repository.communication

import com.drcmind.kelasisuite.domain.model.communication.*
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SchoolAdminCommunicationRepositoryImpl : SchoolAdminCommunicationRepository {

    private val announcements = mutableListOf(
        SchoolOfficialAnnouncement(
            id = 1L,
            title = "Assemblée Générale Ordinaire des Parents d'Élèves",
            summary = "Présentation du bilan du 1er semestre et perspectives pour les examens d'État.",
            content = "Chers Parents et Tuteurs,\n\nLa Direction du Complexe Scolaire a l'honneur de vous convier à la Grande Assemblée Générale des Parents d'élèves qui se tiendra ce Samedi 28 Février 2026 à partir de 09h30 précises dans la Grande Salle polyvalente.\n\nOrdre du jour :\n1. Bilan pédagogique et discipline du premier trimestre.\n2. Dispositions pratiques relatives aux frais de participation aux épreuves hors-session.\n3. Divers et échanges constructifs.\n\nVotre présence active et ponctuelle est vivement souhaitée pour le suivi de nos apprenants.",
            type = AnnouncementType.MEETING,
            priority = AnnouncementPriority.IMPORTANT,
            audience = AnnouncementAudience.PARENTS_ONLY,
            publishedDate = "2026-02-18 14:00",
            publishedBy = "Secrétariat Général",
            signedByTitle = "Le Préfet des Études",
            isPinned = true,
            attachmentName = "Convocation_Assemblee_2026.pdf"
        ),
        SchoolOfficialAnnouncement(
            id = 2L,
            title = "Circulaire N° 04/CS/2026 : Calendrier des Évaluations de la 2ème Période",
            summary = "Rappel des dates de clôture de dépôt des questionnaires et calendrier des épreuves.",
            content = "À l'attention de l'ensemble du Corps Enseignant et des Élèves,\n\nIl est rappelé aux professeurs que les épreuves d'interrogations et devoirs de synthèse pour la 2ème période débuteront officiellement le Lundi 09 Mars 2026. Les fiches de préparation et questionnaires types doivent être déposés auprès des chefs de départements au plus tard ce Vendredi.",
            type = AnnouncementType.CIRCULAR,
            priority = AnnouncementPriority.URGENT,
            audience = AnnouncementAudience.ALL,
            publishedDate = "2026-02-15 08:30",
            publishedBy = "Direction des Études",
            signedByTitle = "La Direction Pédagogique",
            isPinned = true
        ),
        SchoolOfficialAnnouncement(
            id = 3L,
            title = "Congés Pédagogiques & Pont du Week-End",
            summary = "Suspension des cours vendredi après-midi pour concertation pédagogique des enseignants.",
            content = "Chers Parents, les cours seront suspendus ce vendredi à partir de 12h00 pour permettre la tenue de la journée pédagogique trimestrielle des enseignants.",
            type = AnnouncementType.HOLIDAY,
            priority = AnnouncementPriority.NORMAL,
            audience = AnnouncementAudience.ALL,
            publishedDate = "2026-02-10 11:00",
            publishedBy = "Direction",
            signedByTitle = "La Direction",
            isPinned = false
        )
    )

    private val parentMessages = mutableListOf(
        DirectParentMessage(
            id = 1L,
            parentId = 201L,
            parentName = "Mme Kabila Madeleine",
            studentName = "Kabila Marc",
            classroomName = "6ème Math-Physique A",
            phone = "+243 812 345 678",
            lastMessage = "Bonjour Monsieur le Préfet, j'aimerais solliciter un rendez-vous pour échanger sur l'orientation de Marc.",
            timestamp = "Aujourd'hui à 10:45",
            unreadCount = 1,
            isImportant = true
        ),
        DirectParentMessage(
            id = 2L,
            parentId = 202L,
            parentName = "M. Mbemba Faustin",
            studentName = "Mbemba Sarah",
            classroomName = "6ème Math-Physique A",
            phone = "+243 998 765 432",
            lastMessage = "Bien reçu le reçu de paiement des frais scolaires, merci pour la réactivité.",
            timestamp = "Hier à 16:20",
            unreadCount = 0,
            isImportant = false
        ),
        DirectParentMessage(
            id = 3L,
            parentId = 203L,
            parentName = "Dr. Lumumba Jean",
            studentName = "Lumumba David",
            classroomName = "6ème Math-Physique A",
            phone = "+243 823 456 789",
            lastMessage = "David a été souffrant ce matin, voici le certificat médical justificatif de son absence.",
            timestamp = "19 Fév à 08:15",
            unreadCount = 2,
            isImportant = true
        )
    )

    override fun getOfficialAnnouncements(schoolId: Long): Flow<Resource<List<SchoolOfficialAnnouncement>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(announcements.sortedWith(compareByDescending<SchoolOfficialAnnouncement> { it.isPinned }.thenByDescending { it.id })))
    }

    override suspend fun publishAnnouncement(announcement: SchoolOfficialAnnouncement): Resource<SchoolOfficialAnnouncement> {
        val nextId = (announcements.maxOfOrNull { it.id } ?: 0L) + 1L
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateStr = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"

        val newAnnouncement = announcement.copy(
            id = nextId,
            publishedDate = dateStr
        )
        announcements.add(0, newAnnouncement)
        return Resource.Success(newAnnouncement)
    }

    override suspend fun togglePinAnnouncement(announcementId: Long): Resource<Boolean> {
        val index = announcements.indexOfFirst { it.id == announcementId }
        if (index != -1) {
            val current = announcements[index]
            announcements[index] = current.copy(isPinned = !current.isPinned)
            return Resource.Success(true)
        }
        return Resource.Error("Annonce non trouvée")
    }

    override fun getParentConversations(schoolId: Long): Flow<Resource<List<DirectParentMessage>>> = flow {
        emit(Resource.Loading())
        emit(Resource.Success(parentMessages))
    }

    override suspend fun sendDirectMessage(parentId: Long, content: String): Resource<Boolean> {
        val index = parentMessages.indexOfFirst { it.parentId == parentId }
        if (index != -1) {
            val current = parentMessages[index]
            parentMessages[index] = current.copy(
                lastMessage = "Direction : $content",
                timestamp = "À l'instant",
                unreadCount = 0
            )
        }
        return Resource.Success(true)
    }
}
