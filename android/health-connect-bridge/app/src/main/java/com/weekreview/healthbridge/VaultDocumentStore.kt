package com.weekreview.healthbridge

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME
import android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID
import android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
import android.provider.DocumentsContract.Document.MIME_TYPE_DIR

class VaultDocumentStore(private val contentResolver: ContentResolver) {
    fun findDailyNote(treeUri: Uri, fileName: String): Uri? {
        val rootId = DocumentsContract.getTreeDocumentId(treeUri)
        val logsId = findChildDocumentId(treeUri, rootId, "logs", MIME_TYPE_DIR) ?: return null
        val noteId = findChildDocumentId(treeUri, logsId, fileName, null) ?: return null
        return DocumentsContract.buildDocumentUriUsingTree(treeUri, noteId)
    }

    fun readText(documentUri: Uri): String {
        return contentResolver.openInputStream(documentUri)?.bufferedReader().use { reader ->
            requireNotNull(reader) { "Could not open daily note for reading." }.readText()
        }
    }

    fun writeText(documentUri: Uri, text: String) {
        contentResolver.openOutputStream(documentUri, "wt")?.bufferedWriter().use { writer ->
            requireNotNull(writer) { "Could not open daily note for writing." }.write(text)
        }
    }

    private fun findChildDocumentId(
        treeUri: Uri,
        parentDocumentId: String,
        displayName: String,
        mimeType: String?,
    ): String? {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocumentId)
        val columns = arrayOf(COLUMN_DOCUMENT_ID, COLUMN_DISPLAY_NAME, COLUMN_MIME_TYPE)

        contentResolver.query(childrenUri, columns, null, null, null).use { cursor ->
            if (cursor == null) return null

            val idColumn = cursor.getColumnIndexOrThrow(COLUMN_DOCUMENT_ID)
            val nameColumn = cursor.getColumnIndexOrThrow(COLUMN_DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(COLUMN_MIME_TYPE)

            while (cursor.moveToNext()) {
                val childName = cursor.getString(nameColumn)
                val childMimeType = cursor.getString(mimeColumn)
                if (childName == displayName && (mimeType == null || childMimeType == mimeType)) {
                    return cursor.getString(idColumn)
                }
            }
        }

        return null
    }
}
