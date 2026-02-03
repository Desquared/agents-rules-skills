---
name: feature-analysis-skill
description: Feature feasibility analysis framework. Complexity assessment.
---

# Feature Analysis Skill

## Analysis Framework

### 1. Discovery
```bash
grep -r "<similar_feature>" lib/features/    # Find patterns
grep "<package>" pubspec.yaml                # Check dependencies
find lib -path "*/domain/models/*.dart"      # Review models
```

### 2. Complexity Assessment

**Simple:**
- Single API call, straightforward UI, minimal state
- Example: Display static model details
- State Management: Simple solution (Cubit, ChangeNotifier, GetxController)

**Complex:**
- Multiple API calls, state orchestration, complex business rules
- Example: Multi-step flow
- State Management: Robust solution (Bloc with events, StateNotifier, complex GetX)

### 3. Technical Checklist
- [ ] API endpoint exists?
- [ ] DTO/Model mapping needed?
- [ ] Repository pattern required?
- [ ] State management approach? (Check existing patterns in project)
- [ ] UI components available in design system?
- [ ] Native platform features needed?
- [ ] Accessibility considerations?
- [ ] Performance implications?

### 4. Dependencies
- Check `pubspec.yaml` for existing vs new packages
- Check native side: `plugin/ios/`, `plugin/android/`

### 5. Output Format
```markdown
# Feature Analysis: [Name]

## Feasibility: ✅ / ⚠️ / ❌

##State Management**: [Based on project patterns: Bloc/Provider/GetX/Riverpod]
**Rationale**: [Why this approach fits]

## Technical Requirements
- API: [endpoint]
- New Models: [list]
- UI Components: [available/needed]
- Native Features: [yes/no]
- State Management: [Match existing patternse/needed]
- Native Features: [yes/no]

## Dependencies
- Existing: [packages]
- New: [packages]

## Risks
1. [Risk]
2. [Risk]

## Estimated Effort
- Development: X hours
- Testing: Y hours
```
