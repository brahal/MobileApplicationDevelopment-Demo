package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RemoteMediaUploader(
    private val baseUrl: String = "http://10.0.2.2:7077"
) {
    private val client = OkHttpClient()

    /**
     * Uploadt content:// Uri als multipart/form-data (field name: filedata)
     * und gibt die vollständige URL zurück: http://.../content/img/xxx.jpg
     */
    @Throws(IOException::class)
    fun uploadImage(contentResolver: ContentResolver, uri: Uri): String {
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IOException("openInputStream() returned null for uri=$uri")

        val contentType = contentResolver.getType(uri) ?: "application/octet-stream"
        val fileBody = bytes.toRequestBody(contentType.toMediaTypeOrNull())

        val multipart = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                /* name = */ "filedata",
                /* filename = */ "upload.jpg",
                /* body = */ fileBody
            )
            .build()

        val req = Request.Builder()
            .url("$baseUrl/api/upload")
            .post(multipart)
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("Upload failed: HTTP ${resp.code}")

            val body = resp.body?.string() ?: throw IOException("Empty response body")
            val json = JSONObject(body)
            val data = json.getJSONObject("data")

            // ✅ Dein Server liefert "filedata" (siehe Server-Log). Fallback auf "upload".
            val path = when {
                data.has("filedata") -> data.getString("filedata")
                data.has("upload") -> data.getString("upload")
                else -> throw IOException("No value for 'filedata' or 'upload' in response: $body")
            }

            // Zugriff auf Datei: http://10.0.2.2:7077/content/img/....
            return "$baseUrl/$path"
        }
    }
}