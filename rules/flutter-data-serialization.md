# Data Handling & Serialization

## Data Flow

* **Data Structures:** Define data structures (classes) to represent the data used in the application.
* **Data Abstraction:** Abstract data sources (e.g., API calls, database operations) using Repositories/Services to promote testability.

## JSON Serialization

* **JSON Serialization:** Use `json_serializable` and `json_annotation` for parsing and encoding JSON data.
* **Field Renaming:** When encoding data, use `fieldRename: FieldRename.snake` to convert Dart's camelCase fields to snake_case JSON keys.

```dart
// In your model file
import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';

@JsonSerializable(fieldRename: FieldRename.snake)
class User {
  final String firstName;
  final String lastName;

  User({required this.firstName, required this.lastName});

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);
}
```

## Logging

* **Structured Logging:** Use the `log` function from `dart:developer` for structured logging that integrates with Dart DevTools.

```dart
import 'dart:developer' as developer;

// For simple messages
developer.log('User logged in successfully.');

// For structured error logging
try {
  // ... code that might fail
} catch (e, s) {
  developer.log(
    'Failed to fetch data',
    name: 'myapp.network',
    level: 1000, // SEVERE
    error: e,
    stackTrace: s,
  );
}
```
