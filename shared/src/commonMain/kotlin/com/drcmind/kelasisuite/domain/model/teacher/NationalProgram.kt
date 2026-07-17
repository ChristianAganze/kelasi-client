package com.drcmind.kelasisuite.domain.model.teacher

data class NationalProgramSubject(
    val id: String,
    val name: String,
    val chapters: List<NationalProgramChapter>
)

data class NationalProgramChapter(
    val id: String,
    val title: String,
    val subChapters: List<String>
)
