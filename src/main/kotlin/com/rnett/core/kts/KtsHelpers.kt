package com.rnett.core.kts

import java.net.URL

fun getNewestCommit(gitURL: String, default: String = ""): String {
    try {
        return URL("https://api.github.com/repos/$gitURL/commits").readText()
                .substringAfter("\"sha\":\"").substringBefore("\",").substring(0, 10)
    } catch (e: Exception) {
        return default
    }
}

fun jitpack(gitUrl: String, version: String = "", getCommit: Boolean = false): String {
    val user = gitUrl.substringBefore('/')
    val project = gitUrl.substringAfter('/')

    val versionString: String

    if (version == "")
        versionString = getNewestCommit(gitUrl)
    else if (getCommit)
        versionString = getNewestCommit(gitUrl, version)
    else
        versionString = version

    return "com.github.$user:$project:$versionString"
}