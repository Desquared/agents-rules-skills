# Debugging Techniques Reference

## Systematic Approaches

### Binary Search Debugging
**When:** Failure point unclear in large codebase
**How:**
1. Remove/disable half of the code
2. Test if bug still occurs
3. If yes: bug is in remaining half
4. If no: bug is in removed half
5. Repeat on problematic half
**Time:** O(log n) to isolate

### Minimal Reproduction
**When:** Complex system with many components
**How:**
1. Create standalone script/test
2. Remove all unrelated code
3. Replace dependencies with mocks
4. Strip to <50 lines if possible
**Benefit:** Isolates exact failure point

### Differential Analysis
**When:** "It worked before"
**How:**
- Git bisect to find breaking commit
- Diff configs between environments
- Compare library versions
- Check what changed in data
**Goal:** Identify the delta that caused failure

### Rubber Duck Debugging
**When:** Logic seems correct but fails
**How:** Explain code line-by-line aloud (to anyone/anything)
**Why:** Articulation often reveals faulty assumptions

### Divide and Conquer
**When:** Multiple subsystems involved
**How:**
1. Test each component in isolation
2. Eliminate working components
3. Focus on failing component
4. Repeat recursively

## Data Collection Methods

### Strategic Logging
```
# Entry points
log("Function X called with: arg1=%s, arg2=%s", arg1, arg2)

# Key decisions
log("Condition evaluated: %s, taking branch: %s", condition, branch)

# Exit points  
log("Function X returning: %s", result)

# State changes
log("State changed: %s -> %s", old_state, new_state)
```

### Assertion Checkpoints
Add runtime checks at critical points:
```
assert(value is not None, "Value should be initialized")
assert(index < len(array), "Index out of bounds")
assert(state in valid_states, "Invalid state transition")
```

### Debugger Breakpoints
**Conditional:** Break only when condition true
**Watchpoints:** Break when value changes
**Logpoints:** Print without stopping execution

## Pattern Recognition

### Error Message Analysis
**Look for:**
- Exact error type (NullPointer, IndexError, TypeError, etc.)
- Line number and file
- Stack trace showing call chain
- Variables mentioned in error

**Extract:**
- What operation failed
- What value was unexpected
- Where in code it occurred

### Stack Trace Reading
**Top frame:** Where error occurred
**Bottom frame:** Entry point
**Middle frames:** Call chain

**Strategy:**
1. Find first frame in your code (skip library code)
2. Trace backwards to understand flow
3. Look for patterns (loops, recursion, callbacks)

### Log Pattern Analysis
```
# Look for:
- Last successful operation before failure
- Unexpected sequence of events
- Missing expected log messages
- Repeated patterns suggesting loop issues
```

## Hypothesis Generation

### By Symptom

**Null/Nil Dereference:**
- H: Variable not initialized
- H: Return value not checked
- H: Optional not unwrapped safely

**Index Out of Bounds:**
- H: Off-by-one error in loop
- H: Empty collection not checked
- H: Concurrent modification during iteration

**Infinite Loop:**
- H: Loop condition never becomes false
- H: Counter not incrementing
- H: Break statement unreachable

**Intermittent Failure:**
- H: Race condition
- H: Timing-dependent
- H: External state dependency
- H: Memory/resource exhaustion

**Wrong Result:**
- H: Logic error in calculation
- H: Type coercion issue
- H: Precision loss (floating point)
- H: Wrong operator used

### By Context

**After Recent Change:**
- H: New code introduced bug
- H: Existing code incompatible with change
- H: Assumption violated by change

**Specific Environment Only:**
- H: Configuration difference
- H: Library version mismatch
- H: Platform-specific behavior
- H: Resource availability

**Under Load:**
- H: Race condition
- H: Resource exhaustion
- H: Thread safety issue
- H: Synchronization missing

## Experimentation Strategies

### Controlled Variable Testing
**Rule:** Change exactly ONE thing per experiment

**Bad:**
```
Changed logging AND fixed null check AND updated config
→ Can't tell which fixed it
```

**Good:**
```
Experiment 1: Add null check → Still fails
Experiment 2: Update config → Fixed!
→ Know config was the problem
```

### Isolation Testing
Test each component independently:
```
# Test input validation
test_input_validation(edge_cases)

# Test business logic
test_business_logic(mock_inputs)

# Test output formatting
test_output(known_values)
```

### Edge Case Testing
```
# Boundary values
test_with(0)
test_with(-1)
test_with(MAX_VALUE)

# Empty/null
test_with(empty_list)
test_with(null)

# Special characters
test_with("'; DROP TABLE;--")
test_with("🔥emoji🔥")
```

## Validation Techniques

### Before/After Testing
```
1. Reproduce bug (confirm it fails)
2. Record exact failure mode
3. Apply fix
4. Retest (confirm it passes)
5. Test edge cases (ensure robust)
6. Run full test suite (no regressions)
```

### Regression Prevention
```
# Add test that would have caught this bug
def test_issue_123():
    # Specific case that triggered bug
    result = function(problematic_input)
    assert result == expected_output
```

### Fix Validation Checklist
- [ ] Bug reproduces reliably before fix
- [ ] Bug does not reproduce after fix
- [ ] Edge cases tested
- [ ] No new bugs introduced
- [ ] Test added to prevent regression
- [ ] Root cause addressed (not just symptom)

## Quick Reference: Time Allocation

**5-15 minutes:**
- Error message analysis
- Quick log review
- Obvious fix attempts

**30-60 minutes:**
- Hypothesis formation
- Controlled experiments
- Minimal reproduction

**2+ hours:**
- Deep investigation
- Complex interactions
- Intermittent issues
- Performance problems

**Know when to stop:** If spending >2 hours without progress, get help or take break.
