# Bug Pattern Quick Reference

## Error Message → Likely Cause

| Error Type | Common Causes | First Check |
|------------|---------------|-------------|
| NullPointerException / NullReferenceException / nil dereference | Uninitialized variable, unchecked return, missing null check | Where variable is assigned |
| IndexOutOfBoundsException / IndexError | Off-by-one error, empty collection, concurrent modification | Loop conditions, bounds checks |
| DivideByZeroException | Missing validation, edge case | Input validation |
| StackOverflowError | Infinite recursion, missing base case | Recursive function exit conditions |
| TimeoutException | Network issue, blocking operation, deadlock | I/O operations, lock acquisition |
| ConcurrentModificationException | Collection modified during iteration | Iterators, concurrent access |
| ClassCastException / TypeError | Wrong type assumption, unsafe casting | Type conversions |
| OutOfMemoryError | Memory leak, holding references, unbounded growth | Object lifecycle, collection sizes |

## Symptom-Based Diagnosis

### Intermittent Issues
**Likely:** Race condition, timing dependency, resource contention
**Test:** Run repeatedly, increase concurrency, vary timing
**Fix:** Add synchronization, make operations atomic

### Works in Dev, Fails in Prod
**Likely:** Configuration difference, data volume, resource limits
**Test:** Match prod config, use prod-like data, check resources
**Fix:** Environment parity, handle scale

### Slow Performance Degradation
**Likely:** Memory leak, resource not released, unbounded cache
**Test:** Monitor memory over time, check resource usage
**Fix:** Proper cleanup, bounded collections, weak references

### Works Locally, Fails in CI
**Likely:** Test order dependency, environment assumption, timing
**Test:** Run tests in different orders, check for global state
**Fix:** Isolate tests, mock external dependencies

## Hypothesis Quick-Select

### Logic Error Hypotheses
- Off-by-one in loop/array access
- Wrong comparison operator (>, >=, ==, !=)
- Boolean logic inverted
- Integer overflow/underflow
- Floating-point precision loss

### State Management Hypotheses
- Shared state not synchronized
- State initialized in wrong order
- Stale cached data
- Event handler called multiple times
- State mutation during iteration

### Concurrency Hypotheses
- Data race on shared variable
- Deadlock from circular lock dependency
- Thread-unsafe library used from multiple threads
- Missing memory barrier
- Atomic operation needed

### Integration Hypotheses
- API version mismatch
- Breaking change in dependency
- Configuration not applied
- Network connectivity
- Authentication/authorization

## 5-Minute Triage

```
1. Read error message (2 min)
   → Extract: What failed, where, with what value

2. Check recent changes (1 min)
   → Did this work before? What changed?

3. Form initial hypothesis (1 min)
   → Most likely cause based on error type

4. Quick test (1 min)
   → Add log/breakpoint, run once

If not resolved → Full investigation
```

## Debugging Toolbox

**Always Available:**
- Logging/print statements
- Assertions
- Code review (rubber duck)

**Language-Specific:**
- Debugger (breakpoints, stepping)
- Profiler (performance issues)
- Memory analyzer (leaks)
- Thread analyzer (race conditions)

**Version Control:**
- `git bisect` (find breaking commit)
- `git blame` (who changed this)
- `git diff` (what changed)

## Prevention Checklist

**Before Committing:**
- [ ] Added test for new functionality
- [ ] Tested edge cases (null, empty, max)
- [ ] Checked error handling
- [ ] No hardcoded values
- [ ] Proper logging added

**Code Review Focus:**
- [ ] Null/nil checks present
- [ ] Array bounds checked
- [ ] Resources properly closed
- [ ] Concurrent access protected
- [ ] Error cases handled

## When to Ask for Help

**After 2 hours if:**
- Can't reproduce the bug
- No progress on narrowing down cause
- Problem outside your expertise area
- Need access/permissions you don't have

**Prepare for asking:**
- Minimal reproduction case
- What you've tried
- Your current hypotheses
- Relevant logs/errors
