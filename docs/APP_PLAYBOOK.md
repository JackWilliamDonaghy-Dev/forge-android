# Native Android app playbook — Kotlin + AI collaboration

Purpose: make personal tools and one-off birthday gift apps **easy, creative, reviewable, installable, and maintainable**.
This file is the durable reference workflow, independent of any one chat.

## Roles
- Creator/user: choose the idea, priorities and feeling; test on a real Android phone,
  review features and pull requests; approve merging.
- AI collaborator: implement Kotlin/Jetpack Compose code; prepare GitHub commits and PRs;
  inspect GitHub Actions jobs and raw logs on errors; fix and rerun; fetch the successful
  artifact and hand back an installable APK where possible.
- Don't ask the creator to copy long build logs if connected GitHub tooling can read them.

## Per-app recipe
1. One-sentence goal and 2–4 must-have interactions. Keep the first iteration small.
2. New repository (for gifts use **private by default**), e.g. `gift-<nickname>-android`.
   Confirm public/private status; never accidentally disclose gift text, images or names.
3. Kotlin + Jetpack Compose; minSdk selected for the recipient's real phone; local-first
   and no sign-in by default; simple app state and accessible large controls.
4. Unique `applicationId`; never recycle another installed app's package name.
5. Add `.gitignore`, README, tests, and GitHub Actions to build & attach a **debug APK**.
6. Change code on a feature/experiment branch, submit a draft PR to the stable main.
7. Check Actions status and read logs if failed; fix in branch until green.
   Confirm unit tests, debug APK compile, artifact upload.
8. Retrieve the GitHub Actions artifact; unwrap ZIP and provide direct APK where tools permit.
   GitHub mobile artifact interface is inconvenient; avoid making user hunt for it.
9. Creator tests on phone (including offline behavior, text/audio/haptics, back, rotation,
   different screen sizes), requests changes, or approves merge.
10. Tag/document a stable milestone, keep experiments isolated, do not merge by accident.
    Bump `versionCode` for each installable update.

## Signing and distribution
- GitHub-hosted debug builds **do not guarantee the same debug signing key**.
  An update can be rejected by Android; uninstalling loses app data.
- Before repeatedly delivering gifts or personal tool updates, use a stable signed
  release keystore generated securely by the creator. **Never commit a keystore or
  password**. Secrets stay in a trusted local environment or protected CI secrets.
- Ideally set up a tested recovery / backup process; never claim updates are seamless
  before signing is stable.
- Side-loading Android APKs can require installing permission; iOS needs another route.
- Do not use public GitHub Actions artifacts to distribute surprises/photos/messages
  when confidentiality is important; use private repos and deliberate sharing.

## Experiment policy
- `main` always remains a reliable baseline.
- Each bold idea gets its own `experiment/<concept>` branch.
- One notable new interaction per experiment, not five unrelated features.
- Document hypothesis, demo steps, limitations/privacy, and success criteria.
- Prefer physical phone testing over a simulated polished UI.
- If a concept fails, simply leave branch unmerged and keep v1 working.
- Do not upgrade Kotlin/Gradle/Compose for all branches just to try a preview feature:
  update and test on the individual branch first.

## Forge-specific lessons
- Voice prompts should be useful but brief; mention **next exercise during rest** and
  **one common form correction** during the exercise, no repeated round numbers.
- Settings are a separate screen (voice, beeps, theme) with shared persisted state.
- Preparation: three tap-anywhere screens: water, dressed for workout, floor mat.
- Include cool-space/hydration reminder and avoid reliance on unreliable phone
  background execution for the v1 timer.
- Current core workout preset durations and exercise definitions are in `WorkoutPlan.kt`.
