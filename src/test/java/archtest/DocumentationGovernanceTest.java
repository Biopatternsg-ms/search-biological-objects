/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package archtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentationGovernanceTest {

    private static final Path USE_CASES_SOURCE_DIR = Path.of("src/main/java/com/biopatternsg/application/usecase");
    private static final Path USE_CASES_DOCS_DIR = Path.of(".agents/use_cases");
    private static final Path USE_CASES_README = USE_CASES_DOCS_DIR.resolve("README.md");
    private static final Path ARCHITECTURE_DOC = Path.of(".agents/architecture.md");

    @Test
    @DisplayName("Every UseCase should have matching documentation with sequenceDiagram and index references")
    void everyUseCaseShouldBeDocumentedAndIndexed() throws IOException {
        List<String> useCaseNames = listUseCaseClassNames();
        assertFalse(useCaseNames.isEmpty(), "Expected at least one *UseCase.java file");

        String readmeContent = Files.readString(USE_CASES_README);
        String architectureSection5 = extractSection5(Files.readString(ARCHITECTURE_DOC));

        for (String useCaseName : useCaseNames) {
            Path docFile = USE_CASES_DOCS_DIR.resolve(useCaseName + ".md");
            assertTrue(Files.exists(docFile), "Missing documentation file: " + docFile);

            String docContent = Files.readString(docFile);
            assertTrue(docContent.contains("sequenceDiagram"),
                    "Missing Mermaid sequenceDiagram in " + docFile);
            assertTrue(readmeContent.contains(useCaseName + ".md"),
                    "UseCase not indexed in .agents/use_cases/README.md: " + useCaseName);
            assertTrue(architectureSection5.contains(useCaseName),
                    "UseCase not referenced in Section 5 of .agents/architecture.md: " + useCaseName);
        }
    }

    @Test
    @DisplayName("No orphan UseCase documentation files should exist without a matching UseCase class")
    void noOrphanUseCaseDocsShouldExist() throws IOException {
        Set<String> useCaseNames = Set.copyOf(listUseCaseClassNames());

        try (Stream<Path> files = Files.list(USE_CASES_DOCS_DIR)) {
            List<String> orphanDocs = files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".md") && !name.equals("README.md"))
                    .map(name -> name.substring(0, name.length() - 3))
                    .filter(name -> !useCaseNames.contains(name))
                    .toList();

            assertTrue(orphanDocs.isEmpty(), "Orphan UseCase docs found: " + orphanDocs);
        }
    }

    private List<String> listUseCaseClassNames() throws IOException {
        try (Stream<Path> files = Files.list(USE_CASES_SOURCE_DIR)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith("UseCase.java"))
                    .map(name -> name.substring(0, name.length() - 5))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private String extractSection5(String architectureContent) {
        int start = architectureContent.indexOf("## 5.");
        assertTrue(start >= 0, "Section 5 heading not found in .agents/architecture.md");
        int nextSection = architectureContent.indexOf("\n## ", start + 5);
        return nextSection >= 0 ? architectureContent.substring(start, nextSection) : architectureContent.substring(start);
    }
}
