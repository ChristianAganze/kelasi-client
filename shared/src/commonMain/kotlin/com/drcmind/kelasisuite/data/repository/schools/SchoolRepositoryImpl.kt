package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.*
import com.drcmind.kelasisuite.data.datasource.remote.schoolAdmin.SchoolAdminApiService
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DayOfWeek

class SchoolRepositoryImpl(
    private val apiService: SchoolAdminApiService,
    private val settingsStorage: SettingsStorage
) : SchoolRepository {

    override fun getSchool(): Flow<Resource<SchoolDTO>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            // Try to get from local data source first
            val localSchool = settingsStorage.getSchool()
            if (localSchool != null && localSchool.id == schoolId) {
                emit(Resource.Success(localSchool))
            } else {
                // If not found locally or if it's a different school, fetch from remote
                val remoteSchool = apiService.getSchool(schoolId!!)
                settingsStorage.saveSchool(remoteSchool) // Save to local cache
                emit(Resource.Success(remoteSchool))
            }
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun saveSchoolLocally(school: SchoolDTO) {
        settingsStorage.saveSchool(school)
    }

    override fun saveActiveAcademicYearLocally(academicYearDTO: AcademicYearDTO) {
        settingsStorage.saveActiveAcademicYear(academicYearDTO)
    }

    override fun getSchoolSections(): Flow<Resource<List<SchoolSectionDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getSchoolSections(schoolId!!)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getSectionBySchoolSectionAndSchool(
        schoolSectionId: Long
    ): Flow<Resource<List<SectionDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getSectionBySchoolSectionAndSchool(schoolSectionId, schoolId!!)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getOfferedMajorsForSchoolAndSection(
        sectionId: Long
    ): Flow<Resource<List<MajorDto>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getOfferedMajorBySchoolAndBySection(schoolId!!, sectionId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getOfferedMajorsForSchool(): Flow<Resource<List<MajorDto>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getOfferedMajorsForSchool(schoolId!!)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getGradeLevelsBySchoolAndByMajor(
        majorId: Long
    ): Flow<Resource<List<GradeLevelDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getGradeLevelsBySchoolAndByMajor(schoolId!!, majorId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getClassesForSchoolAndMajor(
        majorId: Long
    ): Flow<Resource<List<SchoolClassDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getClassesForSchoolAndMajor(schoolId!!, majorId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getClassesForSchool(): Flow<Resource<List<SchoolClassDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getClassesForSchool(schoolId!!)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getClassesBySchoolAndGradeLevel(
        gradeLevelId: Long
    ): Flow<Resource<List<SchoolClassDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getClassesBySchoolAndGradeLevel(schoolId!!,gradeLevelId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getAcademicYears(): Flow<Resource<List<AcademicYearDTO>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getAcademicYears()
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getActiveAcademicYear(): AcademicYearDTO? {
        return settingsStorage.getActiveAcademicYear()
    }

    override fun getEvaluationPeriodsBySchool(): Flow<Resource<List<EvaluationPeriodBySchoolDTO>>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.getEvaluationPeriodsBySchool(schoolId!!)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }


    override fun createClass(request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>> {
        return flow {
            emit(Resource.Loading())
            val schoolId = settingsStorage.getUserInfo().schoolId
            val response = apiService.createClass(request.copy(schoolId = schoolId!!))
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateClass(classId: Long, request: CreateClassFromTemplateRequest): Flow<Resource<SchoolClassDTO>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.updateClass(classId, request)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun deleteClass(classId: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.deleteClass(classId)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    // LearningTimeConfig Endpoints
    override fun createLearningTimeConfig(configDto: LearningTimeConfigDto): Flow<Resource<LearningTimeConfigDto>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createLearningTimeConfig(configDto)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getLearningTimeConfigById(id: Long): Flow<Resource<LearningTimeConfigDto>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getLearningTimeConfigById(id)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getAllLearningTimeConfigs(): Flow<Resource<List<LearningTimeConfigDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getAllLearningTimeConfigs()
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId: Long): Flow<Resource<List<LearningTimeConfigDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getLearningTimeConfigsBySchoolSectionConfigId(schoolSectionConfigId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(
        dayOfWeek: DayOfWeek,
        schoolSectionConfigId: Long
    ): Flow<Resource<List<LearningTimeConfigDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getLearningTimeConfigsByDayOfWeekAndSchoolSectionConfigId(dayOfWeek.name, schoolSectionConfigId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateLearningTimeConfig(id: Long, configDto: LearningTimeConfigDto): Flow<Resource<LearningTimeConfigDto>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.updateLearningTimeConfig(id, configDto)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun deleteLearningTimeConfig(id: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.deleteLearningTimeConfig(id)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    // ScheduleEntry Endpoints
    override fun createScheduleEntry(entryDto: ScheduleEntryDto): Flow<Resource<ScheduleEntryDto>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.createScheduleEntry(entryDto)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun updateScheduleEntry(id: Long, entryDto: ScheduleEntryDto): Flow<Resource<ScheduleEntryDto>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.updateScheduleEntry(id, entryDto)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun deleteScheduleEntry(id: Long): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.deleteScheduleEntry(id)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByWeekNumber(weekNumber: Int): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByWeekNumber(weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByLearningTimeConfigDayOfWeekAndWeekNumber(
        dayOfWeek: DayOfWeek,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByLearningTimeConfigDayOfWeekAndWeekNumber(dayOfWeek, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(
        teachingAssignmentId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByTeachingAssignmentIdAndWeekNumber(teachingAssignmentId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesBySchoolIdAndWeekNumber(
        schoolId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesBySchoolIdAndWeekNumber(schoolId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesBySchoolSectionIdAndWeekNumber(
        schoolSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesBySchoolSectionIdAndWeekNumber(schoolSectionId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByTemplateSchoolSectionIdAndWeekNumber(
        templateSchoolSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByTemplateSchoolSectionIdAndWeekNumber(templateSchoolSectionId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByTemplateSectionIdAndWeekNumber(
        templateSectionId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByTemplateSectionIdAndWeekNumber(templateSectionId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesByGradeLevelIdAndWeekNumber(
        gradeLevelId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesByGradeLevelIdAndWeekNumber(gradeLevelId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getScheduleEntriesBySchoolClassIdAndWeekNumber(
        schoolClassId: Long,
        weekNumber: Int
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getScheduleEntriesBySchoolClassIdAndWeekNumber(schoolClassId, weekNumber)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun duplicateScheduleEntries(
        sourceWeek: Int,
        classId: Long,
        targetWeeks: List<Int>
    ): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.duplicateScheduleEntries(sourceWeek, classId, targetWeeks)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun getWeeklySchedule(
        weekNumber: Int,
        classId: Long
    ): Flow<Resource<List<ScheduleEntryDto>>> {
        return flow {
            emit(Resource.Loading())
            val response = apiService.getWeeklySchedule(weekNumber, classId)
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }

    override fun clearWeek(
        weekNumber: Int,
        classId: Long
    ): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())
            apiService.clearWeek(weekNumber, classId)
            emit(Resource.Success(Unit))
        }.catch {
            emit(Resource.Error(message = it.message.toString()))
        }
    }
}