package com.drcmind.kelasisuite.data.repository.communication

import com.drcmind.kelasisuite.domain.model.communication.DirectParentMessage
import com.drcmind.kelasisuite.domain.model.communication.SchoolOfficialAnnouncement
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SchoolAdminCommunicationRepository {
    fun getOfficialAnnouncements(schoolId: Long): Flow<Resource<List<SchoolOfficialAnnouncement>>>
    suspend fun publishAnnouncement(announcement: SchoolOfficialAnnouncement): Resource<SchoolOfficialAnnouncement>
    suspend fun togglePinAnnouncement(announcementId: Long): Resource<Boolean>
    fun getParentConversations(schoolId: Long): Flow<Resource<List<DirectParentMessage>>>
    suspend fun sendDirectMessage(parentId: Long, content: String): Resource<Boolean>
}
