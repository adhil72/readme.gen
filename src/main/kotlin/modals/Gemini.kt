package modals

import com.google.gson.Gson

data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
){
    companion object{
        fun parse(stringData: String):GeminiResponse{
            val gson = Gson()
            return gson.fromJson(stringData, GeminiResponse::class.java)
        }
    }

}

data class Candidate(
    val content: Content,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>
)

data class Content(
    val parts: List<Part>,
    val role: String
)

data class Part(
    val text: String
)

data class SafetyRating(
    val category: String,
    val probability: String
)

data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)

data class ProjectInfoResponse(
    val programmingLanguage: String,
    val buildTool: String,
    val requiredFiles: List<String>,
){

    companion object{
        fun parse(stringData: String):ProjectInfoResponse{
            val gson = Gson()
            return gson.fromJson(stringData, ProjectInfoResponse::class.java)
        }
    }
}

data class RequiredFilesResponse(
    val requiredFiles: List<String>
){
    companion object{
        fun parse(stringData: String):RequiredFilesResponse{
            val gson = Gson()
            return gson.fromJson(stringData, RequiredFilesResponse::class.java)
        }
    }
}