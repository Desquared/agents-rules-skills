---
name: design-architecture-skill
description: data/domain/view structure templates. Project organization & scaffolding reference.
---

# Design Architecture Skill

## Feature Structure (Mandatory)
```
lib/<features|feature>/<feature>/
  data/datasources/<feature>_remote_datasource.dart  # @RestApi
      dtos/<feature>_dto.dart                        # @JsonSerializable
      repositories/<feature>_repository_impl.dart    # @Injectable
  domain/models/<feature>.dart                       # Equatable
         repositories/<feature>_repository.dart      # Abstract
  view/<state_mgmt>/                                 # bloc/, provider/, getx/, riverpod/
       widgets/<feature>_card.dart
       <feature>_page.dart
```

## Core Structure
```
lib/core/
  utils/extensions/, helpers/, validators/
  widgets/buttons/, cards/, dialogs/
  constants/app_constants.dart
  enums/app_enums.dart
test/<feature>/data/, domain/, view/
  fixtures/<feature>_fixtures.dart
```

## Quick Templates

```dart
// Datasource (@RestApi)
abstract class <Feature>RemoteDatasource {
  factory <Feature>RemoteDatasource(Dio dio) = _<Feature>RemoteDatasource;
  @GET('/api/<endpoint>') Future<<Feature>Dto> fetch();
}

// DTO (@JsonSerializable)
class <Feature>Dto {
  final String id;
  factory <Feature>Dto.fromJson(Map<String, dynamic> json) => _$<Feature>DtoFromJson(json);
}

// Model (Equatable)
class <Feature> extends Equatable {
  final String id;
  const <Feature>({required this.id});
  @override List<Object?> get props => [id];
}

// Repository
abstract class <Feature>Repository { Future<<Feature>> fetch(); }

@Injectable(as: <Feature>Repository)
class <Feature>RepositoryImpl implements <Feature>Repository {
  final <Feature>RemoteDatasource _remote;
  @override Future<<Feature>> fetch() async => (await _remote.fetch()).toModel();
}

// State Management (Bloc|ChangeNotifier|GetxController|StateNotifier)
class <Feature>StateManager {
  final <Feature>Repository _repository;
  // Implement based on chosen state management
}
```

## Error Handling
```dart
abstract class AppException implements Exception { final String message; }

// In repository
try { return await _remote.fetch(); }
on DioException catch (e) {
  if (e.response?.statusCode == 404) throw NotFoundException();
  throw NetworkException();
}
```

## After Scaffolding
```bash
flutter pub run build_runner build  # Generate *.g.dart
flutter analyze                      # Must pass (0 errors)
flutter test                         # Create & run tests
```
