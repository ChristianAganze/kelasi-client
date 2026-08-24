package com.drcmind.kelasisuite.ui.teacheradmin.preparation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drcmind.kelasisuite.data.datasource.local.settings.SettingsStorage
import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.TeachingAssignmentDTO
import com.drcmind.kelasisuite.data.repository.teacher.PreparationRepository
import com.drcmind.kelasisuite.data.repository.teacher.toLessonPreparation
import com.drcmind.kelasisuite.data.repository.teachers.TeachersRepository
import com.drcmind.kelasisuite.data.repository.teaching_assignments.AssignmentRepository
import com.drcmind.kelasisuite.domain.model.teacher.*
import com.drcmind.kelasisuite.domain.model.teacher.PreparationStatus
import com.drcmind.kelasisuite.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PreparationState(
    val preparations: List<LessonPreparation> = emptyList(),
    val availableAssignments: List<TeachingAssignmentDTO> = emptyList(),
    val selectedAssignment: TeachingAssignmentDTO? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,
    val isCreating: Boolean = false,
    
    // Header Draft
    val draftBranch: String = "",
    val draftSubBranch: String = "",
    val draftClass: String = "",
    val draftRevisionSubject: String = "",
    val draftLessonSubject: String = "",
    val draftObjective: String = "",
    val draftMaterial: String = "",
    val draftBibliography: String = "",

    // Steps Draft (Intro)
    val draftIntroDuration: String = "",
    val draftIntroMethod: String = "",
    val draftIntroContent: String = "",

    // Steps Draft (Dev)
    val draftDevDuration: String = "",
    val draftDevMethod: String = "",
    val draftDevContent: String = "",

    // Steps Draft (Synthesis)
    val draftSynthDuration: String = "",
    val draftSynthMethod: String = "",
    val draftSynthContent: String = "",

    // Steps Draft (App)
    val draftAppDuration: String = "",
    val draftAppMethod: String = "",
    val draftAppContent: String = "",

    // Word Document Preview & Templates
    val isWordPreviewOpen: Boolean = false,
    val previewPreparation: LessonPreparation? = null,
    val showTemplateSelector: Boolean = false,

    // Electronic Signature Dialog
    val showSignatureDialog: Boolean = false,
    val signingPreparation: LessonPreparation? = null
)

class PreparationViewModel(
    private val settingsStorage: SettingsStorage,
    private val teachersRepository: TeachersRepository,
    private val assignmentRepository: AssignmentRepository,
    private val preparationRepository: PreparationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PreparationState())
    val state: StateFlow<PreparationState> = _state.asStateFlow()

    init {
        fetchAssignments()
    }

    fun retry() {
        _state.update { it.copy(errorMessage = null) }
        fetchAssignments()
    }

    private fun fetchAssignments() {
        val schoolId = settingsStorage.getSchool()?.id
        val userId = settingsStorage.getUserInfo().userId
        if (schoolId == null || userId == null) {
            _state.update {
                it.copy(isLoading = false, errorMessage = "Connexion incomplète : impossible de charger les préparations.")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            teachersRepository.getTeachers(schoolId).collect { teachersResource ->
                if (teachersResource is Resource.Success) {
                    val myProfile = teachersResource.data?.find { it.userId == userId }
                    if (myProfile != null) {
                        fetchAssignmentsForTeacher(myProfile.id)
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Profil enseignant introuvable.") }
                    }
                } else if (teachersResource is Resource.Error) {
                    _state.update { it.copy(isLoading = false, errorMessage = teachersResource.message) }
                }
            }
        }
    }

    private suspend fun fetchAssignmentsForTeacher(teacherProfileId: Long) {
        assignmentRepository.getAssignmentsForSchool().collect { assignmentsResource ->
            if (assignmentsResource is Resource.Success) {
                val myAssignments = assignmentsResource.data?.filter { it.teacherId == teacherProfileId } ?: emptyList()
                val firstClass = myAssignments.firstOrNull()
                _state.update { 
                    it.copy(
                        availableAssignments = myAssignments,
                        selectedAssignment = firstClass,
                        draftClass = firstClass?.className ?: "",
                        draftBranch = firstClass?.subjectName ?: ""
                    ) 
                }
                if (firstClass != null) {
                    fetchPreparationsForAssignment(firstClass.id)
                } else {
                     _state.update { it.copy(isLoading = false) }
                }
            } else if (assignmentsResource is Resource.Error) {
                _state.update { it.copy(isLoading = false, errorMessage = assignmentsResource.message) }
            }
        }
    }

    fun selectAssignment(assignment: TeachingAssignmentDTO) {
        _state.update { it.copy(
            selectedAssignment = assignment,
            draftClass = assignment.className,
            draftBranch = assignment.subjectName
        ) }
        fetchPreparationsForAssignment(assignment.id)
    }

    private fun fetchPreparationsForAssignment(assignmentId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            preparationRepository.getPreparations(assignmentId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val dtos = resource.data ?: emptyList()
                        val mapped = dtos.map { dto ->
                            dto.toLessonPreparation(
                                branch = _state.value.draftBranch,
                                className = _state.value.draftClass
                            )
                        }
                        _state.update { it.copy(isLoading = false, preparations = mapped) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun startCreating() {
        _state.update { it.copy(isCreating = true) }
    }

    fun cancelCreating() {
        _state.update { it.copy(isCreating = false) }
    }

    // Header Updates
    fun updateDraftBranch(v: String) = _state.update { it.copy(draftBranch = v) }
    fun updateDraftSubBranch(v: String) = _state.update { it.copy(draftSubBranch = v) }
    fun updateDraftClass(v: String) = _state.update { it.copy(draftClass = v) }
    fun updateDraftRevisionSubject(v: String) = _state.update { it.copy(draftRevisionSubject = v) }
    fun updateDraftLessonSubject(v: String) = _state.update { it.copy(draftLessonSubject = v) }
    fun updateDraftObjective(v: String) = _state.update { it.copy(draftObjective = v) }
    fun updateDraftMaterial(v: String) = _state.update { it.copy(draftMaterial = v) }
    fun updateDraftBibliography(v: String) = _state.update { it.copy(draftBibliography = v) }

    // Intro Updates
    fun updateIntroDuration(v: String) = _state.update { it.copy(draftIntroDuration = v) }
    fun updateIntroMethod(v: String) = _state.update { it.copy(draftIntroMethod = v) }
    fun updateIntroContent(v: String) = _state.update { it.copy(draftIntroContent = v) }

    // Dev Updates
    fun updateDevDuration(v: String) = _state.update { it.copy(draftDevDuration = v) }
    fun updateDevMethod(v: String) = _state.update { it.copy(draftDevMethod = v) }
    fun updateDevContent(v: String) = _state.update { it.copy(draftDevContent = v) }

    // Synth Updates
    fun updateSynthDuration(v: String) = _state.update { it.copy(draftSynthDuration = v) }
    fun updateSynthMethod(v: String) = _state.update { it.copy(draftSynthMethod = v) }
    fun updateSynthContent(v: String) = _state.update { it.copy(draftSynthContent = v) }

    // App Updates
    fun updateAppDuration(v: String) = _state.update { it.copy(draftAppDuration = v) }
    fun updateAppMethod(v: String) = _state.update { it.copy(draftAppMethod = v) }
    fun updateAppContent(v: String) = _state.update { it.copy(draftAppContent = v) }

    fun openPreview(prep: LessonPreparation) {
        _state.update { it.copy(isWordPreviewOpen = true, previewPreparation = prep) }
    }

    fun closePreview() {
        _state.update { it.copy(isWordPreviewOpen = false, previewPreparation = null) }
    }

    fun openTemplateSelector() {
        _state.update { it.copy(showTemplateSelector = true) }
    }

    fun closeTemplateSelector() {
        _state.update { it.copy(showTemplateSelector = false) }
    }

    fun applyTemplate(template: com.drcmind.kelasisuite.domain.model.teacher.PreparationTemplate) {
        _state.update {
            it.copy(
                showTemplateSelector = false,
                isCreating = true,
                draftSubBranch = template.subBranch,
                draftRevisionSubject = template.revisionSubject,
                draftLessonSubject = template.lessonSubject,
                draftObjective = template.objective,
                draftMaterial = template.material,
                draftBibliography = template.bibliography,
                draftIntroDuration = template.introDuration,
                draftIntroMethod = template.introMethod,
                draftIntroContent = template.introContent,
                draftDevDuration = template.devDuration,
                draftDevMethod = template.devMethod,
                draftDevContent = template.devContent,
                draftSynthDuration = template.synthDuration,
                draftSynthMethod = template.synthMethod,
                draftSynthContent = template.synthContent,
                draftAppDuration = template.appDuration,
                draftAppMethod = template.appMethod,
                draftAppContent = template.appContent
            )
        }
    }

    fun dismissSnackbar() {
        _state.update { it.copy(saveSuccess = false, saveError = null) }
    }

    fun openSignatureDialog(prep: LessonPreparation) {
        _state.update { it.copy(showSignatureDialog = true, signingPreparation = prep) }
    }

    fun closeSignatureDialog() {
        _state.update { it.copy(showSignatureDialog = false, signingPreparation = null) }
    }

    fun applySignatureAndSubmit(signature: com.drcmind.kelasisuite.domain.model.common.ElectronicSignature) {
        val prep = _state.value.signingPreparation ?: return
        val id = prep.id.toLongOrNull()
        
        _state.update { state ->
            val updated = state.preparations.map {
                if (it.id == prep.id) it.copy(
                    status = PreparationStatus.SUBMITTED,
                    teacherSignature = signature
                ) else it
            }
            state.copy(
                preparations = updated,
                showSignatureDialog = false,
                signingPreparation = null,
                saveSuccess = true,
                saveError = null
            )
        }

        if (id != null) {
            viewModelScope.launch {
                preparationRepository.submitPreparation(id).collect { }
            }
        }
    }

    fun submitPreparation(prepId: String) {
        val prep = _state.value.preparations.firstOrNull { it.id == prepId }
        if (prep != null) {
            openSignatureDialog(prep)
        }
    }

    fun savePreparation() {
        val s = _state.value
        val assignmentId = s.selectedAssignment?.id ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            
            // Create the new preparation dto
            // Créer le DTO pour la nouvelle préparation
            val dto = LessonPreparationDTO(
                teachingAssignmentId = assignmentId,
                date = Clock.System.now().toString(),
                subject = s.draftLessonSubject.ifEmpty { "Nouvelle Leçon" },
                operationalObjective = s.draftObjective,
                reference = s.draftBibliography,
                introPhase = s.draftIntroContent,
                developmentPhase = s.draftDevContent,
                synthesisPhase = s.draftSynthContent,
                applicationPhase = s.draftAppContent
            )

            preparationRepository.createPreparation(dto).collect { res ->
                if (res is Resource.Success) {
                    _state.update { 
                        it.copy(
                            isSaving = false, 
                            saveSuccess = true,
                            isCreating = false
                        ) 
                    }
                    fetchPreparationsForAssignment(assignmentId)
                } else if (res is Resource.Error) {
                    _state.update { 
                        it.copy(
                            isSaving = false, 
                            saveError = res.message
                        ) 
                    }
                }
            }
        }
    }
}
