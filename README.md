# Avro with Nullability Annotations

This project demonstrates how to generate Java classes from Avro schema files (AVSC) and automatically add nullability annotations to the generated classes. The annotations help Kotlin understand which fields can be null and which cannot.

## Features

- Generates Java classes from Avro schema files
- Automatically adds `@NotNull` and `@Nullable` annotations based on the Avro schema
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

## Example Schema

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
      "type": ["null", "int"]
    }
  ]
}
```

## How It Works

1. The Avro plugin generates Java classes from AVSC files
2. Our custom processor (`AvroClassProcessor`) adds nullability annotations:
   - `@NotNull` for required fields (like `id`)
   - `@Nullable` for optional fields (like `age`)
3. The generated classes can be used in Kotlin with proper nullability checking

## Building

```bash
./gradlew build
```

## Testing

The project includes Kotlin tests that verify the nullability behavior of the generated classes. For example:
- `id` field is non-nullable and must be set
- `age` field is nullable and can be set to null

## Dependencies

- Apache Avro
- JavaParser
- JetBrains Annotations
- Kotlin
- JUnit 5

## License

MIT 