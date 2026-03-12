# Flutter Agent Guidelines

**CRITICAL: All Flutter agents MUST follow these discovery rules and reference existing skills/rules**

## 1. Discovery-First Approach

Before making ANY implementation decisions:

```bash
# Check dependencies & patterns
grep -A 50 "dependencies:" pubspec.yaml
find lib -name "*_bloc.dart" -o -name "*_repository.dart"
grep -r "BlocProvider\|ChangeNotifier\|GetX" lib/
cat analysis_options.yaml
```

## 2. Skills & Rules Reference

**All agents MUST reference existing skills and rules instead of duplicating content:**

### Architecture & Design
- **Planning features**: Use `design-architecture-skill` for data/domain/view structure
- **Feature analysis**: Use `feature-analysis-skill` for complexity assessment
- **Architecture rules**: Follow `Flutter/rules/architecture.md`

### Bug Investigation & Debugging
- **Debugging**: Use `bug-solving-skill` for Flutter-specific patterns
- **Root cause analysis**: Follow layer-by-layer (data/domain/view) investigation
- **Foundation**: Reference `skills/bug-investigation` for scientific method

### Code Quality
- **Linting**: Follow `Flutter/rules/lint-rules.md` - MUST pass `flutter analyze` with 0 errors
- **Formatting**: Run `dart format .` - follow project's `analysis_options.yaml`
- **Code review**: Use `code-review-skill`

### Testing
- **Test structure**: Use `Flutter/rules/testing.md`
- **Test patterns**: Match project's existing test patterns (bloc_test, mocktail, mockito)

### State Management
- **Agnostic approach**: All agents support Bloc/Provider/GetX/Riverpod
- **Check first**: Scan project for existing patterns before recommending
- **Reference**: `design-architecture-skill` for state management templates
- **Ask when unclear**: Multiple patterns or conflicting approaches

### Performance & Optimization
- **Optimization patterns**: `optimization-skill` and `optimization-skill/examples.md`
- **Widget rebuilds**: Scope state listeners, use const constructors
- **Lists**: ListView.builder for >20 items, stable keys

### Other Rules
- **Linting**: `lint-rules.md` - MUST pass `flutter analyze` with 0 errors
- **State management**: State management agnostic - detect project patterns first

## 3. Library Detection Matrix

| Category | Check For | Ask When Multiple |
|----------|-----------|-------------------|
| **State** | flutter_bloc, provider, get, riverpod | ✅ Always ask |
| **DI** | get_it+injectable, get, riverpod | ✅ Always ask |
| **HTTP** | dio, http, retrofit | Prefer project pattern |
| **JSON** | json_serializable, freezed | Prefer existing |
| **Testing** | bloc_test, mocktail, mockito | Match existing |

## 4. When to Ask User

**ALWAYS ask when:**
- Multiple libraries for same purpose
- No clear pattern in existing code
- Conflicting approaches (e.g., Bloc + Provider)
- Feature complexity unclear

## 5. Agent-Specific Guidance

### performance-optimizer-flutter
- Use `optimization-skill` and `optimization-skill/examples.md`
- State management agnostic (Bloc/Provider/Riverpod/GetX)
- Profile before optimizing (DevTools, Performance overlay)
- Focus: widget rebuilds, memory leaks, list optimization

### accessibility-specialist-flutter
- Use `accessibility-validator` skill patterns
- WCAG AA compliance (4.5:1 text, 3:1 UI contrast)
- TalkBack/VoiceOver support
- 48x48 touch targets minimum

### code-reviewer-flutter
- MUST check `flutter analyze` (zero errors required)
- Reference `lint-rules.md` for standards
- Check data/domain/view structure compliance
- State management agnostic review

### project-architecture-analyst-flutter
- Use `design-architecture-skill` for scaffolding
- Enforces data/domain/view structure
- WAITS for approval before implementation
- Ask when multiple DI/state patterns exist

### refactoring-specialist-flutter
- Use `optimization-skill` for performance patterns
- Follow `architecture.md` for target structure
- Gradual migration (create alongside, then deprecate old)
- State management agnostic (legacy → modern)

### bug-solver-flutter
- Use `bug-solving-skill` (extends `bug-investigation`)
- Layer-based RCA: data → domain → view
- Scientific method: observe, hypothesize, experiment
- State management agnostic debugging

### feature-analyst-flutter
- Use `feature-analysis-skill` for complexity assessment
- ASK user for presentation format (diagrams/tables/flows/text)
- Recommends state management based on complexity
- WAITS for approval before architect handoff

## 6. Mandatory Checks

```markdown
✅ Before Implementation:
- [ ] Ran discovery commands
- [ ] Checked relevant skills (../skills/)
- [ ] Checked relevant rules (../rules/)
- [ ] Identified project patterns
- [ ] Reviewed `analysis_options.yaml`
```

## 7. Output Format
 with tables/quick checks
- Maximum 1 example per agent
- State management agnostic language

**For detailed patterns:**
- Create `examples.md` in relevant skill folder (e.g., `optimization-skill/examples.md`)
- Create `patterns.md` for complex scenarios
- Keep agent focused on workflow, not detailed examples

**For feature-analyst-flutter:**
- ASK user presentation preference (diagrams/tables/flows/text/mixed)
- Adapt output format to user's choice
**For detailed patterns:**
- Create `examples.md` in relevant skill folder
- Create `patterns.md` for complex scenarios
- Keep agent focused on workflow,  (state management, DI, HTTP client)
- ✅ Reference existing skills/rules (not duplicate content)
- ✅ Ask when multiple approaches exist
- ✅ Follow `analysis_options.yaml` strictly (zero errors)
- ✅ State management agnostic (Bloc/Provider/Riverpod/GetX)
- ✅ Maximum 1 example per agent (<100 lines total)
- ✅ Detailed examples in skill files (e.g., `optimization-skill/examples.md`)
- ✅ Discover project context first
- ✅ Reference existing skills/rules
- ✅ Ask when multiple approaches exist
- ✅ Follow `analysis_options.yaml` strictly
- ✅ Create skill examples for complex patterns
