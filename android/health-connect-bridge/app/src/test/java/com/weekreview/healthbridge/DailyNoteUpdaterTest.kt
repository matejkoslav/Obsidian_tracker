package com.weekreview.healthbridge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyNoteUpdaterTest {
    @Test
    fun dailyNoteFileNameUsesDayMonthYear() {
        assertEquals("30-06-2026.md", DailyNoteUpdater.dailyNoteFileName(LocalDate.of(2026, 6, 30)))
    }

    @Test
    fun buildStepsLineUsesCommaSeparatorsAndNoSuffix() {
        assertEquals("- Steps 🟢: 8,432", DailyNoteUpdater.buildStepsLine(8432))
        assertEquals("- Steps 🟢: 0", DailyNoteUpdater.buildStepsLine(0))
    }

    @Test
    fun replaceStepsLineReplacesWholeLine() {
        val note = "# Daily\n- Steps 🟢:\n- Other: yes\n"

        val result = DailyNoteUpdater.replaceStepsLine(note, "- Steps 🟢: 8,432")

        assertTrue(result is ReplaceResult.Replaced)
        result as ReplaceResult.Replaced
        assertEquals("- Steps 🟢:", result.oldLine)
        assertEquals("- Steps 🟢: 8,432", result.newLine)
        assertEquals("# Daily\n- Steps 🟢: 8,432\n- Other: yes\n", result.updatedText)
    }

    @Test
    fun replaceStepsLinePreservesIndentAndBullet() {
        val note = "  * Steps 🟢: 100\n"

        val result = DailyNoteUpdater.replaceStepsLine(note, "- Steps 🟢: 1,200")

        assertTrue(result is ReplaceResult.Replaced)
        result as ReplaceResult.Replaced
        assertEquals("  * Steps 🟢: 1,200\n", result.updatedText)
    }

    @Test
    fun replaceStepsLineFailsWhenMissing() {
        val result = DailyNoteUpdater.replaceStepsLine("- Water: yes\n", "- Steps 🟢: 1")

        assertEquals(ReplaceResult.MissingStepsLine, result)
    }
}
