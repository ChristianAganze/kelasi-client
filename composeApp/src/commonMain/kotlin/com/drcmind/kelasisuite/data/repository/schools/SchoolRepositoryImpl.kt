package com.drcmind.kelasisuite.data.repository.schools

import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.schools.SchoolsAPIService
import com.drcmind.kelasisuite.domain.dto.AcademicYearDTO
import com.drcmind.kelasisuite.domain.dto.CreateClassFromTemplateRequest
import com.drcmind.kelasisuite.domain.dto.EvaluationPeriodBySchoolDTO
import com.drcmind.kelasisuite.domain.dto.EvaluationPeriodDTO
import com.drcmind.kelasisuite.domain.dto.GradeLevelDTO
import com.drcmind.kelasisuite.domain.dto.MajorDto
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.domain.dto.SchoolDTO
import com.drcmind.kelasisuite.domain.dto.SchoolSectionDTO
import com.drcmind.kelasisuite.domain.dto.SectionDTO
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SchoolRepositoryImpl(
    private val apiService: SchoolsAPIService,
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
            val response = apiService.createClass(request)
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
}
