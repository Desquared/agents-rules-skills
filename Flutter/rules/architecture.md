# Application Architecture

## Separation of Concerns

* **Data/Domain/View Pattern:** Follow clean architecture with clear separation between data, domain, and view layers
* **Mandatory Structure:** Each feature must follow this structure:
    * **Data Layer:** Datasources (API clients), DTOs (JSON models), Repository implementations
    * **Domain Layer:** Business models, Repository interfaces, Business logic
    * **View Layer:** State management (Bloc/Provider/GetX/Riverpod), Widgets, Pages
    * **Core Layer:** Shared utilities, extensions, helpers, validators, constants, enums
* **Feature-based Organization:** Organize code by feature with each feature containing its own data/domain/view subfolders:
    ```
    lib/<features|feature>/<feature>/
      data/datasources/, dtos/, repositories/
      domain/models/, repositories/
      view/<state_management>/, widgets/, <feature>_page.dart
    ```
* **Repository Pattern:** Use repository interfaces in domain layer, implementations in data layer
* **State Management Agnostic:** View layer can use Bloc, Provider, GetX, or Riverpod based on project patterns

## API Design Principles

When building reusable APIs, such as a library, follow these principles:

* **Consider the User:** Design APIs from the perspective of the person who will be using them. The API should be intuitive and easy to use correctly.
* **Documentation is Essential:** Good documentation is a part of good API design. It should be clear, concise, and provide examples.
