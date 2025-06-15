# Avro with Nullability Annotations

This project demonstrates how to generate Java classes from Avro schema files (AVSC) and automatically add nullability annotations to the generated classes. The annotations help Kotlin understand which fields can be null and which cannot.

## Features

- Generates Java classes from Avro schema files
- Automatically adds `@Nonnull` and `@Nullable` annotations based on the Avro schema
- Uses JavaParser to modify the generated code
- Includes Kotlin tests to verify nullability behavior

## Project Structure

```
.
├── buildSrc/                    # Build-time utilities
│   └── src/main/java/org/example/
│       └── AvroClassProcessor.java  # Processes generated classes
├── src/
│   ├── main/
│   │   ├── avro/               # Avro schema files
│   │   │   └── Person.avsc     # Example schema
│   │   └── kotlin/             # Kotlin source code
│   └── test/
│       └── kotlin/             # Kotlin tests
└── build.gradle                # Build configuration
```

## How It Works

1. The Avro plugin generates Java classes from AVSC files
2. Our custom processor (`AvroClassProcessor`) adds nullability annotations:
   - `@Nonnull` for required fields
   - `@Nullable` for optional fields (fields that can be null in the Avro schema)
3. The generated classes can be used in Kotlin with proper nullability checking

## Building

```bash
./gradlew build
```

## Testing

The project includes Kotlin tests that verify the nullability behavior of the generated classes.

## Dependencies

- Apache Avro
- JavaParser
- javax.annotation-api
- Kotlin
- JUnit 5

## License

MIT 