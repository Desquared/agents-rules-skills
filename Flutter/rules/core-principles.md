# Core Flutter Principles

## Interaction Guidelines

* **User Persona:** Assume the user is familiar with programming concepts but may be new to Dart.
* **Explanations:** When generating code, provide explanations for Dart-specific features like null safety, futures, and streams.
* **Clarification:** If a request is ambiguous, ask for clarification on the intended functionality and target platform.
* **Dependencies:** When suggesting new dependencies from `pub.dev`, explain their benefits.
* **Formatting:** Use the `dart_format` tool to ensure consistent code formatting.
* **Fixes:** Use the `dart_fix` tool to automatically fix many common errors.
* **Linting:** Use the Dart linter with a recommended set of rules to catch common issues.

## Flutter Style Guide

* **SOLID Principles:** Apply SOLID principles throughout the codebase.
* **Concise and Declarative:** Write concise, modern, technical Dart code. Prefer functional and declarative patterns.
* **Composition over Inheritance:** Favor composition for building complex widgets and logic.
* **Immutability:** Prefer immutable data structures. Widgets (especially `StatelessWidget`) should be immutable.
* **State Management:** Separate ephemeral state and app state. Use a state management solution for app state.
* **Widgets are for UI:** Everything in Flutter's UI is a widget. Compose complex UIs from smaller, reusable widgets.

## Code Quality

* **Code structure:** Adhere to maintainable code structure and separation of concerns (e.g., UI logic separate from business logic).
* **Naming conventions:** Avoid abbreviations and use meaningful, consistent, descriptive names for variables, functions, and classes.
* **Conciseness:** Write code that is as short as it can be while remaining clear.
* **Simplicity:** Write straightforward code. Code that is clever or obscure is difficult to maintain.
* **Error Handling:** Anticipate and handle potential errors. Don't let your code fail silently.
* **Styling:**
    * Line length: Lines should be 80 characters or fewer.
    * Use `PascalCase` for classes, `camelCase` for members/variables/functions/enums, and `snake_case` for files.
* **Functions:**
    * Functions short and with a single purpose (strive for less than 20 lines).
* **Testing:** Write code with testing in mind. Use the `file`, `process`, and `platform` packages for testability.
* **Logging:** Use the `logging` package instead of `print`.
