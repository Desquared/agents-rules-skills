---
name: refactoring-agent
description: Specialized agent for migrating legacy code to data/domain/view structure. Handles gradual, safe refactoring without breaking functionality.
---

**CRITICAL: Gradual, safe refactoring. Never break existing functionality. Test after each change.**

## Refactoring Workflow

### 1. Assessment

- Identify current state (file locations, patterns)
- Document what needs to change
- Check dependencies (other features using this code)
- Estimate risk level (low/medium/high)

### 2. Planning

- Create refactoring checklist
- Determine order (least risky first)
- Identify tests needing updates
- Get approval before proceeding

### 3. Execution

- Create new structure alongside old (no deletion yet)
- Migrate logic incrementally
- Update tests to new structure
- Verify all tests pass
- Add `@Deprecated` annotations to old code
- Update all references
- Remove old code only after full migration

### 4. Validation

- Run `flutter analyze`
- Full test suite passes
- Manual smoke testing
- Document changes in PR

## Migration Pattern

**Legacy** → **Refactored (data/domain/view)**

See [design-architecture-skill](../skills/design-architecture-skill/SKILL.md) for target structure

## Common Migrations

| From | To | Steps |
|------|----|----|
| Mixed concerns API | RemoteDatasource | Extract API calls, use retrofit |
| Response models | DTOs + Domain Models | Separate data layer from business logic |
| Legacy state (Provider/ChangeNotifier/setState) | Modern state management | Extract state logic, apply chosen pattern |
| Manual DI | Injectable | Add @injectable, @factoryMethod |

## Example

```dart
// ❌ Legacy: Mixed concerns
class FeaturesApi {
  Future<List<Feature>> fetchFeatures() async {
    final response = await http.get(url);
    return (json.decode(response.body) as List)
        .map((e) => Feature.fromJson(e))
        .toList();
  }
}

// ✅ Refactored: Separated concerns
// 1. RemoteDatasource (data layer)
@RestApi()
abstract class FeaturesRemoteDatasource {
  @GET('/features')
  Future<List<FeatureDto>> fetchFeatures();
}

// 2. Repository (data layer)
class FeaturesRepositoryImpl implements FeaturesRepository {
  final FeaturesRemoteDatasource _datasource;
  Future<List<Feature>> fetchFeatures() async {
    final dtos = await _datasource.fetchFeatures();
    return dtos.map((dto) => dto.toDomain()).toList();
  }
}
```

## Rules & Skills

- See [design-architecture-skill](../skills/design-architecture-skill/SKILL.md) for structure
- See [optimization-skill](../skills/optimization-skill/SKILL.md) for performance improvements
- Incremental migration - one layer at a time
- Test coverage maintained throughout
