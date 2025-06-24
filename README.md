# Avro with Extended Annotations Gradle Plugin

This project provides a **Gradle plugin** for automatically adding annotations to Java classes generated from Avro schema files. The plugin is implemented in Java and can be published locally for use in other projects.

---

## Repository Structure

```
.
├── avro-annotation-plugin/      # The Gradle plugin: adds annotations to Avro-generated Java classes
│   ├── build.gradle
│   └── src/main/java/org/example/
│       ├── AvroAnnotationPlugin.java
│       ├── AnnotateAvroClassesTask.java
│       └── AvroAnnotationExtension.java
├── plugin-usage-example/        # Example project using the plugin end-to-end (Java & Kotlin tests)
│   ├── build.gradle
│   ├── src/main/avro/           # Avro schema files (e.g., Person.avsc)
│   ├── src/test/kotlin/         # Kotlin tests for annotated Avro classes
│   └── ...
├── build.gradle                 # Root build config (now minimal)
└── README.md                    # This file
```

---

## What Does the Plugin Do?

- **Automatically adds `@NotNull` and `@Nullable` annotations** to fields, getters, and setters in Java classes generated from Avro schemas, based on the schema's nullability.
- **Modifies generated code using JavaParser** to ensure annotations are applied correctly.
- **Supports both Java and Kotlin consumers**:  
  - For Kotlin, the annotations enable proper null-safety checks.
  - For Java, they provide compile-time and IDE warnings for incorrect usage.

> **Note:** This plugin automatically applies the `com.github.davidmc24.gradle.plugin.avro` plugin to provide the tasks for generating Java classes from your `.avsc` files. You do not need to apply it separately.

---

## Module Overview

### 1. `avro-annotation-plugin/`
- **Purpose:** Implements the Gradle plugin that post-processes Avro-generated Java sources, adding nullability annotations.
- **Key files:**
  - `AvroAnnotationPlugin.java`: Registers the plugin and its Gradle task.
  - `AnnotateAvroClassesTask.java`: The Gradle task that performs annotation.
  - `AvroAnnotationExtension.java`: Allows configuration via the Gradle DSL.
- **Dependencies:** JavaParser, Avro, JetBrains Annotations.

### 2. `plugin-usage-example/`
- **Purpose:** Demonstrates how to use the plugin in a real project, including Avro schema, plugin configuration, and tests (including Kotlin tests for null-safety).
- **Key files:**
  - `build.gradle`: Shows the correct way to configure the plugin for overwriting Avro-generated sources with annotated versions.
  - `src/main/avro/Person.avsc`: Example Avro schema.
  - `src/test/kotlin/PersonTest.kt`: Kotlin test verifying nullability annotations.
- **How it works:**
  - The Avro plugin generates Java sources from the schema into `build/generated-main-avro-java`.
  - The annotation plugin overwrites those sources in-place, so only the annotated versions are compiled and packaged.
  - The project depends on `org.jetbrains:annotations` so the compiler recognizes the annotations.

---

## How to Use the Plugin

### 1. Publish the Plugin Locally
From the project root, run:
```sh
./gradlew :avro-annotation-plugin:publishToMavenLocal
```

### 2. Apply the Plugin in Your Project
In your `settings.gradle`, ensure Gradle looks in your local Maven repository:
```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
```

### 3. Configure the Plugin in Your `build.gradle`
**Recommended pattern:** Overwrite the Avro-generated sources in-place.
```groovy
plugins {
    id 'org.example.avro-annotation-plugin' version '1.0.0'
}

avroAnnotation {
    inputDir = file("build/generated-main-avro-java") // Avro plugin output
    outputDir = file("build/generated-main-avro-java") // Overwrite in-place
    schemaFile = file("src/main/avro/Person.avsc")
}

dependencies {
    implementation 'org.jetbrains:annotations:24.1.0' // Required for annotation recognition
}
```

### 4. Build Your Project
```sh
./gradlew build
```
This will:
- Generate Java sources from Avro schemas
- Annotate them in-place
- Compile and package only the annotated versions

---

## Example Avro Schema

```json
{
  "type": "record",
  "name": "Person",
  "namespace": "com.example",
  "fields": [
    { "name": "id", "type": "string" },
    { "name": "age", "type": ["null", "int"], "default": null }
  ]
}
```
- `id` is required and will be annotated as `@NotNull`
- `age` is optional and will be annotated as `@Nullable`

---

## Testing

- The `plugin-usage-example` module includes Kotlin tests that verify the nullability behavior of the generated classes.
- Example: The `id` field is non-nullable and must be set; the `age` field is nullable and can be set to null.

---

## Dependencies

- Apache Avro
- JavaParser
- JetBrains Annotations
- JUnit 5
- Kotlin (for testing)

---

## License

MIT

---

**This plugin makes it easy to ensure your Avro-generated Java classes are properly annotated for null-safety, improving code quality and interoperability with Kotlin.** 