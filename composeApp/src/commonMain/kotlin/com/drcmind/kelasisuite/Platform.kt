package com.drcmind.kelasisuite

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform