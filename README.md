# Avro with Extended Annotations Gradle Plugin

This project provides a **Gradle plugin** for automatically adding annotations to Java classes generated from Avro schema files. The plugin is implemented in Java and can be published locally for use in other projects.

This was created after using the davidmc Gradle plugin didn't allow for comfortable use of the generated objects in Kotlin


---

## What Does the Plugin Do?

- **Automatically adds `@NotNull` and `@Nullable` annotations** to fields, getters, and setters in Java classes generated from Avro schemas, based on the schema's nullability.
- **Modifies generated code using JavaParser** to ensure annotations are applied correctly.
- **Supports both Java and Kotlin consumers**:  
  - For Kotlin, the annotations enable proper null-safety checks.
  - For Java, they provide compile-time and IDE warnings for incorrect usage.

> **Note:** This plugin automatically applies the `com.github.davidmc24.gradle.plugin.avro` plugin to provide the tasks for generating Java classes from your `.avsc` files. You do not need to apply it separately.

---

## Benefits

### For Kotlin Users
- The annotations help Kotlin understand which fields can be null and which cannot
- Enables proper nullability checking when using the generated classes
- Provides better IDE support and compile-time safety

### For Java Users
- Provides warnings at compile time and IDE level
- Helps catch potential null pointer issues early
- Improves code quality and maintainability

---

## How to Use the Plugin

### 1. For local plugin usage: Publish the Plugin Locally

From the project root, run:
```sh
./gradlew :avro-annotation-plugin:publishToMavenLocal
```

### 2. Apply the Plugin in Your Project.
If importing the plugin from the Gradle Plugin Portal, skip to step 3. Otherwise, in your `settings.gradle`, ensure Gradle looks in your local Maven repository:
```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
```


### 3. Update your `build.gradle`:
```groovy
plugins {
    id 'org.example.avro-annotation-plugin' version '1.0.0'
}

avroAnnotation {
    inputDir = file("src/main/java/generated") // Directory with generated Java classes
    outputDir = file("build/generated-annotated") // Output directory for annotated classes
    schemaFile = file("src/main/avro/Person.avsc") // Avro schema file
}
```

### 3. Run the Task

```sh
./gradlew annotateAvroClasses
```

This will process all Java files in `inputDir`, annotate them according to the Avro schema, and write the results to `outputDir`.

---

## Example Avro Schema

```json
{
  "type": "record",
  "name": "Person",
  "namespace": "com.example",
  "fields": [
    { 
      "name": "id",
     "type": "string" 
    },
    { 
      "name": "age",
       "type": ["null", "int"],
        "default": null 
        }
  ]
}
```
- `id` is required and will be annotated as `@NotNull`
- `age` is optional and will be annotated as `@Nullable`

---

## How It Works (Implementation Details)

- The plugin module (`avro-annotation-plugin/`) contains:
  - `AvroAnnotationPlugin.java`: The plugin entry point, registers the task and extension.
  - `AnnotateAvroClassesTask.java`: The Gradle task that performs annotation.
  - `AvroAnnotationExtension.java`: Allows configuration via the Gradle DSL.
- The task uses **JavaParser** to parse and modify Java source files, adding the appropriate annotations based on the Avro schema (using the Avro library).
- The plugin is published to your local Maven repository and can be reused in any Gradle project.

---

## Project Structure

```
.
├── avro-annotation-plugin/
│   ├── build.gradle
│   └── src/main/java/org/example/
│       ├── AvroAnnotationPlugin.java
│       ├── AnnotateAvroClassesTask.java
│       └── AvroAnnotationExtension.java
├── src/
│   ├── main/
│   └── test/
│       ├── avro/               # Avro schema files
│       │   └── Person.avsc     # Example schema
│       └── kotlin/             # Kotlin tests
└── build.gradle                # Build configuration
```

---

## Testing

- The project includes Kotlin tests that verify the nullability behavior of the generated classes.
- Example: The `id` field is non-nullable and must be set; the `age` field is nullable and can be set to null.

---

## Dependencies

- Apache Avro
- JavaParser
- JetBrains Annotations
- Kotlin (for testing it actually works)
- JUnit 5

---

## License

MIT

---

**This plugin makes it easy to ensure your Avro-generated Java classes are properly annotated for null-safety, improving code quality and interoperability with Kotlin.** 