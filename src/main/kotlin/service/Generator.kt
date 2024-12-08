package service

import modals.ProjectInfoResponse
import modals.RequiredFilesResponse
import utils.mapFolder
import utils.readDir
import java.io.File
import kotlin.concurrent.thread

fun generateReadme(basePath: String, apiKey:String, model:String, onStateUpdate: (String) -> Unit) {
    synchronized(Any()) {
        thread {
            try {
                onStateUpdate("Reading main folder files...")
                val mainFolderFile = readDir(basePath)

                onStateUpdate("Generating required files response...")
                val requiredFilesResponse = RequiredFilesResponse.parse(
                    Gemini(
                        apiKey = apiKey,
                        model = model,
                        sysInstruction = """
                        This model identifies the required files or folders to understand the project's basic details and to generate a README. The project file list will be given.
                        The response should be a JSON like this:
                        {
                            requiredFiles: [path1, path2]
                        }
                    """.trimIndent()
                    ).generateResponse(
                        mainFolderFile.joinToString("\n")
                    ).replace("```json", "").replace("```", "")
                )

                onStateUpdate("Mapping folder structure...")
                var fileMap = mapFolder(basePath)
                fileMap = fileMap.filter {
                    onStateUpdate("Filtering files: ${it.key}")
                    val path = it.key
                    requiredFilesResponse.requiredFiles.any { requiredPath ->
                        path.contains(requiredPath)
                    }
                }
                val filesArray = fileMap.keys.toTypedArray()

                onStateUpdate("Analyzing project information...")
                val projectInfoPrompt = """
                    PROJECT FILES
                    ${filesArray.joinToString("\n")}
                    
                    Understand the project and provide a response like this:
                    {
                        programmingLanguage: "Kotlin",
                        buildTool: "Gradle",
                        requiredFiles: [] // Code and build files required for generating README.md in future
                    }
                """.trimIndent()

                val projectInfo = ProjectInfoResponse.parse(
                    Gemini(
                        apiKey = apiKey,
                        model = model,
                        sysInstruction = """
                        This model analyzes the project and provides information about it, including the programming language, build tool, and files to exclude from the README file.
                        The response structure must be like:
                        {
                            programmingLanguage: "Kotlin",
                            buildTool: "Gradle",
                            requiredFiles: ["/home/user/..."] // Code and build files required for generating README.md
                        }
                    """.trimIndent()
                    ).generateResponse(projectInfoPrompt).replace("```json", "").replace("```", "")
                )

                onStateUpdate("Filtering required files for README generation...")
                fileMap = fileMap.filter {
                    projectInfo.requiredFiles.contains(it.key)
                }

                onStateUpdate("Preparing to generate README...")
                val modelInstructions = """
                    This model generates a README file for a project. The README provides information like the project's purpose, features, installation instructions, and usage guidelines.
                    
                    1. Include the project title with its version if applicable.
                    2. Provide a brief description of the project, explaining its purpose and key features.
                    3. List installation steps, including dependencies and system requirements.
                    4. Include usage instructions with examples.
                    5. Explain necessary configuration or setup details.
                    6. Provide contributing guidelines.
                    7. Describe how to run tests.
                    8. Include license information and link to the full license if applicable.
                    9. Provide contact information for maintainers or the project team.
                    10. Acknowledge external resources, libraries, or contributors important to the project.
                    11. If the project is backend project, then it should included a detailed flow and api documentation with path, request headers, request body and response body. don't forget to wrap json request,res or body in code block(```).
                    12. If it is a library or module project, explain how to import and use it in other projects.
                    13. wrap all codes in code block(```).
                """.trimIndent()

                val prompt = """
                    PROJECT FILES AND CONTENTS
                    --------------------------
                    ${
                    fileMap.map {
                        """
                            path: ${it.key}
                            content: ${it.value}
                            --------------------------
                            """.trimMargin()
                    }.joinToString("\n")
                }
                """.trimIndent()

                onStateUpdate("Generating README content...")
                val response = Gemini(sysInstruction = modelInstructions, apiKey = apiKey, model = model).generateResponse(prompt)

                onStateUpdate("Writing README file...")
                File("$basePath/readme.generated.md").writeText(response.replace("```markdown", "").replace("```", ""))
                onStateUpdate("README file generated successfully!")
            } catch (e: Exception) {
                onStateUpdate("An error occurred: ${e.message}")
            }
        }
    }
}
