package com.weekreview.healthbridge

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DailyNoteUpdater {
    private val fileDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val stepsLineRegex = Regex("""^(\s*[-*]\s*)?Steps 🟢:.*$""")

    fun dailyNoteFileName(date: LocalDate): String = "${date.format(fileDateFormatter)}.md"

    fun buildStepsLine(stepCount: Long): String {
        val formatted = NumberFormat.getIntegerInstance(Locale.US).format(stepCount)
        return "- Steps 🟢: $formatted"
    }

    fun replaceStepsLine(noteText: String, newLine: String): ReplaceResult {
        val lineEnding = if (noteText.contains("\r\n")) "\r\n" else "\n"
        val trailingLineEnding = noteText.endsWith("\n")
        val lines = noteText.split(Regex("""\r?\n""")).let {
            if (trailingLineEnding) it.dropLast(1) else it
        }.toMutableList()

        val index = lines.indexOfFirst { it.contains("Steps 🟢:") }
        if (index == -1) {
            return ReplaceResult.MissingStepsLine
        }

        val oldLine = lines[index]
        val prefix = stepsLineRegex.matchEntire(oldLine)?.groupValues?.get(1).orEmpty()
        val replacement = if (prefix.isNotEmpty()) {
            "$prefix${newLine.removePrefix("- ")}"
        } else {
            newLine
        }
        lines[index] = replacement

        val updatedText = lines.joinToString(lineEnding) + if (trailingLineEnding) lineEnding else ""
        return ReplaceResult.Replaced(oldLine, replacement, updatedText)
    }
}

sealed interface ReplaceResult {
    data object MissingStepsLine : ReplaceResult
    data class Replaced(
        val oldLine: String,
        val newLine: String,
        val updatedText: String,
    ) : ReplaceResult
}
