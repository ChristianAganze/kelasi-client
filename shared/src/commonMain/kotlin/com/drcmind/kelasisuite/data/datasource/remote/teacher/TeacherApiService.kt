package com.drcmind.kelasisuite.data.datasource.remote.teacher

import com.drcmind.kelasisuite.data.datasource.remote.dto.ClassLogDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.LessonPreparationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.StudentEvaluationDTO
import com.drcmind.kelasisuite.data.datasource.remote.dto.ReportCardDTO

interface TeacherApiService {
    // Lesson Preparations
    suspend fun createPreparation(request: LessonPreparationDTO): LessonPreparationDTO
    suspend fun getPreparations(teachingAssignmentId: Long): List<LessonPreparationDTO>
    suspend fun submitPreparation(preparationId: Long): LessonPreparationDTO
    
    // Class Logs
    suspend fun createClassLog(request: ClassLogDTO): ClassLogDTO
    suspend fun getClassLogs(teachingAssignmentId: Long): List<ClassLogDTO>
    
    // Student Evaluations (Attendance & Grades)
    suspend fun submitStudentEvaluation(request: StudentEvaluationDTO): StudentEvaluationDTO
    suspend fun getStudentEvaluations(teachingAssignmentId: Long): List<StudentEvaluationDTO>
    
    // Reports
    suspend fun getReportCards(classId: Long, termId: Long): List<ReportCardDTO>
    suspend fun saveReportCard(request: ReportCardDTO): ReportCardDTO
}
