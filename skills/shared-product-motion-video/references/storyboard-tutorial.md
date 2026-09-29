# Template: feature tutorial (about 50 to 120 s)

Modelled on the two reference tutorials (51 s with five steps, 57 s with six). A tutorial teaches
**one task** end to end, in numbered steps, on real footage of the product. No voice: the caption of
each step says what to do and why, and the footage shows it being done.

Length comes from the number of steps: title 4.3 s + 6.5 to 13 s per step + end card 4.5 to 6 s.
Five steps land near 50 s, six near 57 s, nine near 90 s. Past nine steps, split the task into two
tutorials instead of making a long one.

## Structure

| # | Scene | Length | What is on screen |
| - | ----- | ------ | ----------------- |
| 0 | Title card | 4.3 s | Icon + yellow mono tag "[Product] · Tutorial"; 62 sp Black title "Make a new [thing]"; 24 sp serif italic line "A [concrete example], in [N] steps." Centred, 170 dp from the top |
| 1..N | Steps | 6.5 to 13 s each | Caption left (tag "Step k", 36 to 38 sp title, one detail sentence); footage in a macOS window right (x 372, y 70, 560 dp wide); step dots top right from the first step to the end card |
| N+1 | End card | 4.5 to 6 s | Icon pop; 52 sp Black "That's a new [thing]."; 20 sp serif italic "what next" line; a mono pill with how to start, plus a short line |

## Step patterns (pick one per step)

Beats are step-local seconds.

**A. Find the entry point** (about 6.5 s)

| Beat | What happens |
| ---- | ------------ |
| 0.1 | Caption; footage enters (0.6 s), whole window |
| 1.3 | Cursor appears low in the footage |
| 1.6 to 2.8 | Camera pushes 2.6x onto the control |
| 2.6 | Loop around the control |
| 3.2 | Click |
| 3.9 | Shot dissolves (0.35 s) to the state after the click |
| 4.6 | Camera pulls back to 1.35x; cursor rests on the new state at 4.8 |

**B. Choose and fill a form** (about 9 s)

| Beat | What happens |
| ---- | ------------ |
| 0.1 | Caption; footage on the chooser at 1.35x, pushing 2.1x to the option at 0.9 |
| 0.8 | Loop around the option; click at 1.3 |
| 1.9 | Footage dims to 35% (0.4 s); the form card pops over it (easeOutBack 1.3 from 0.92) |
| 2.6 | Name field types at 0.045 s/char |
| 3.6 | Purpose field types at 0.045 s/char |
| end of typing + 0.5 | Primary button pressed; tick and a green "Created ..." line 0.3 s later |

**C. See what it made** (about 7.5 s)

| Beat | What happens |
| ---- | ------------ |
| 0.1 | Caption ("It is on its own [place]"); footage at 1x |
| 1.3 | Camera pushes 1.75 to 2.5x onto the new item |
| 2.3 | Loop around it |
| 3.4 | A code or detail card rises beside it, one line every 0.08 s (the real file, abridged) |

**D. Ask the assistant / run the command** (about 12 s)

| Beat | What happens |
| ---- | ------------ |
| 0.2 | Assistant card enters (0.5 s) |
| 0.8 | Context lines appear, as pasted |
| 1.4 | The request types at 0.032 s/char |
| typing end + 0.4 | "Editing [file]..." with animated dots |
| + 1.5 | Tick and green "Updated [file]." line; the before/after morph fires on the same frame |
| after the morph | Hold at least 2 s on the result |

**E. Try it / refine it** (about 8 to 9 s)

| Beat | What happens |
| ---- | ------------ |
| 0.1 | Caption; footage at 1x |
| 1.2 to 2.8 | Cursor enters; camera pushes 2.1x onto the controls |
| 3.5 | Click a control; loop around its effect at 3.9 |
| 5.2 to 6.4 | Camera moves to a second area (1.9 to 2.2x); a second loop at 7.0 |

**F. Ship it** (about 6 to 8 s)

| Beat | What happens |
| ---- | ------------ |
| 0.1 | Caption ("Happy? / Promote it"); footage at 1x |
| 1.2 | Camera pushes 2.2x onto the ship button; click at 1.9 |
| 2.4 | Result card (status pill, title, mono branch or URL line, three ticks 0.3 s apart) rises over the footage |

## Worked timing: the five-step reference (51 s)

| Scene | Window | Pattern |
| ----- | ------ | ------- |
| Title | 0.0 to 4.3 | title card |
| Step 1 | 4.0 to 10.7 | A |
| Step 2 | 10.4 to 19.3 | B |
| Step 3 | 19.0 to 26.5 | C |
| Step 4 | 26.2 to 38.3 | D |
| Step 5 | 38.0 to 46.8 | E |
| End | 46.5 to 51.0 | end card |

The six-step version inserted E ("Pick anything to refine it") and F ("Happy? Promote it") after D
and ran 57 s.

## Sound

Tutorial bed (96 BPM, no drums). Whoosh on each scene change; a click on every cursor press; key
ticks at the typing speeds (0.045 s/char in forms at level 0.045, 0.032 s/char in the assistant at
0.035); a pop on each card; a scribble on each loop; chimes on "Created" (84), on the assistant's done
line (88 then 91) and a rising triad on the result card; two notes (79, 84) as the end card lands.
The music fades over the last 1.6 s.

## Rules this template encodes

- **Tags are "Step 1", "Step 2"...** and the step dots always agree with them.
- The caption says the action as an imperative ("Click + New", "Ask the assistant") and the detail
  line says where and why, in one sentence.
- Everything clicked is shown clicked: cursor, press, sound, result.
- Before/after is a morph between two real captures (a draft state and the finished state), never a
  cut.
- The same real example runs through every step (one component, one screen), so the viewer follows a
  single story.
