# Avro with Extended Annotations Plugin

This Gradle plugin provides enhanced Avro schema processing capabilities with support for annotations and custom processing. It automatically adds nullability annotations to generated Avro classes, making them more Kotlin-friendly and providing better compile-time safety for Java users.

## Benefits

### For Kotlin Users
- The annotations help Kotlin understand which fields can be null and which cannot
- Enables proper nullability checking when using the generated classes
- Provides better IDE support and compile-time safety

### For Java Users
- Provides warnings at compile time and IDE level
- Helps catch potential null pointer issues early
- Improves code quality and maintainability

## Features

- Generates Java classes from Avro schema files
- Automatically adds `@NotNull` and `@Nullable` annotations based on the Avro schema
- Uses JavaParser to modify the generated code
- Integration with Kotlin projects
- Custom class processing capabilities
- Includes Kotlin tests to verify nullability behavior

## Using the Plugin

### From Gradle Plugin Portal

In your project's `build.gradle`:

```groovy
plugins {
    id 'io.github.rondb.avro-annotations' version '0.1.0'
}
```

### Local Development

To use the plugin in another project during development:

1. In your project's `settings.gradle`, add the local repository:
```groovy
pluginManagement {
    repositories {
        maven {
            url = uri("${rootProject.projectDir}/../avro-with-annotations/build/repo")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
```

2. In your project's `build.gradle`, add the required Avro plugin and apply our plugin:
```groovy
plugins {
    id 'com.github.davidmc24.gradle.plugin.avro' version '1.9.1'
    id 'io.github.rondb.avro-annotations'
}
```

### Configuration

The plugin automatically configures:
- Avro schema generation with private fields and setters
- Annotation processing
- Test source set configuration
- Required dependencies

### Tasks

The plugin adds the following tasks:
- `processAvroClasses`: Processes generated Avro classes with annotations

### Dependencies

The plugin automatically adds the following dependencies:
- Apache Avro
- JetBrains Annotations
- JavaParser Core

## How It Works

### Internal Processing

1. The Avro plugin generates Java classes from AVSC files in the test directory, using the davidmc plugin
2. The `AvroClassProcessor` adds nullability annotations:
   - `@NotNull` for required fields (like `id`)
   - `@Nullable` for optional fields (like `age`)
   - Either of these annotaitons for methods and parameters in order to ensure compile time safety when calling getters, setters or using the Builder
3. The generated classes can be used in Kotlin with proper nullability checking

### Example Schema

The project includes a simple `Person` schema with:
- `id` (string, required, non-nullable)
- `age` (int, optional, nullable)

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

### Task Execution Order

The build process follows this order to ensure proper compilation and testing:

1. `compileJava` - Compiles the `AvroClassProcessor` class
2. `generateTestAvroJava` - Generates Java classes from the Avro schema in test directory
3. `processAvroClasses` - Processes the generated classes to add nullability annotations
4. `compileTestKotlin` - Compiles the Kotlin tests
5. `test` - Runs the tests

The task dependencies are configured in `build.gradle` to ensure this order is maintained:
```gradle
task processAvroClasses(type: JavaExec) {
    dependsOn compileJava
    dependsOn generateTestAvroJava
    // ... task configuration
}

compileTestKotlin.dependsOn generateTestAvroJava
compileTestKotlin.dependsOn processAvroClasses
```

## Development

### Building

```bash
./gradlew build
```

### Testing

The project includes several otlin tests that verify the nullability behavior of the generated classes

```bash
./gradlew test
```

### Publishing

To publish the plugin to the Gradle Plugin Portal:

1. Set up your Gradle Plugin Portal credentials in `~/.gradle/gradle.properties`:
```properties
gradle.publish.key=<your-api-key>
gradle.publish.secret=<your-api-secret>
```

2. Publish the plugin:
```bash
./gradlew publishPlugins
```

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── AvroAnnotationsPlugin.java  # Plugin implementation
│   │   │   └── AvroClassProcessor.java     # Core processing logic
│   │   └── resources/
│   │       └── META-INF/gradle-plugins/    # Plugin metadata
│   └── test/
│       ├── avro/                           # Avro schema files
│       │   └── Person.avsc                 # Example schema
│       └── kotlin/                         # Kotlin tests
└── build.gradle                            # Build configuration
```

## License

MIT 