# Readme.gen

**Description:**

Readme.gen is a desktop application built with Jetpack Compose that automatically generates README files for software projects. It leverages the Google Gemini API to analyze project files and create comprehensive documentation, including project descriptions, installation instructions, usage examples, and API documentation for backend projects.

**Key Features:**

* **Automated README Generation:** Analyzes project code and structure to generate a README file.
* **Gemini API Integration:** Uses Google's powerful Gemini models for intelligent content generation.
* **Folder Selection:** Allows users to choose the project directory for analysis.
* **Multiple Gemini Model Support:** Offers a selection of Gemini models (e.g., Gemini Pro, Gemini Flash) to tailor the generation process.
* **API Key Input:** Securely handles user's Google Gemini API key.
* **Progress Indication:** Provides real-time feedback on the generation process.
* **Error Handling:** Displays informative messages in case of errors.

**Installation:**

1. **Prerequisites:**
    * **Java Development Kit (JDK):** Version 17 or higher.
    * **IntelliJ IDEA:** Recommended IDE for Kotlin development.

2. **Clone the Repository:**
    ```bash
    git clone [repository url]
    ```

3. **Open in IntelliJ IDEA:**
    * Import the project as a Gradle project.

4. **Build and Run:**
    * Build the project using Gradle: `./gradlew build`
    * Run the application: `./gradlew run`

**Usage:**

1. **Launch Readme.gen.**
2. **Select Project Directory:** Click "Select Directory" and choose the root folder of your project.
3. **Enter API Key:** Obtain a Google Gemini API key and enter it in the provided field. Click the "Get API Key" button to open the Google AI Studio API Key page.
4. **Select Model:** Choose the desired Gemini model from the dropdown.
5. **Generate README:** Click "Generate README". The application will analyze the project and generate a `readme.generated.md` file in the project's root directory.

**Configuration:**

* **API Key:** A valid Google Gemini API key is required. You can obtain one at [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)

**Contributing:**

Contributions are welcome! Please fork the repository and submit pull requests.

**Running Tests:**

Currently, no automated tests are implemented. This is a planned future enhancement. Add tests in the `src/test` directory and run them using `./gradlew test`.

**License:**

This project is licensed under the [MIT License](LICENSE).

**Acknowledgements:**

* **JetBrains Compose:** Used for building the user interface.
* **Google Gemini API:** Used for AI-powered README generation.
* **OkHttp:** Used for making HTTP requests to the Gemini API.
* **Gson:** Used for JSON serialization and deserialization.