# WiKamay – Android UI Prototype

Native Android app (Kotlin + Jetpack Compose) for the capstone project
**WiKamay: An Android-Based Mobile Application for Filipino Sign Language Translation and Learning**.

This is a **UI-only prototype**. There is no AI, AR, camera recognition, speech, dataset, or database.
Buttons that will need those features show a clearly labeled placeholder pop-up.

> Build status: the source files were written without access to Android Studio or an emulator,
> so the project has **not** been compiled or run yet. Open it, let Gradle sync, and run it.
> If you get build errors, send me the error text and I will fix them.

## How to open and run

1. Install **Android Studio Koala (2024.1.1) or newer** (needed by Android Gradle Plugin 8.5.2).
   It includes JDK 17, which the project uses.
2. Unzip `WiKamay.zip`. Choose **File > Open** and select the `WiKamay` folder (the one containing `settings.gradle.kts`).
3. Wait for **Gradle sync** to finish (first sync downloads libraries, so you need internet).
   If Android Studio asks to install **Android SDK 34**, accept.
4. Create an emulator in **Tools > Device Manager** (any phone, API 24 or newer), or enable USB debugging on a phone.
5. Press the green **Run** button.
6. For an APK: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Screen flow (no Home screen)

Splash -> **Text/Speech to Sign** (default) <-> **Sign to Text/Speech** (use the pill toggle at the top)
The square button at the upper-right opens the **Learning Menu**:
- TRY -> FSL Alphabet (pick a letter -> practice with Previous/Next)
- Numbers (pick a number -> practice with Previous/Next)
- Tutorials (lesson list -> lesson details with Previous/Next lesson)

On learning screens: top-left button = Back; top-right button = jump straight back to Translation.

## Where things are  (`app/src/main/java/com/wikamay/app/`)

| Folder / file | What it contains |
|---|---|
| `MainActivity.kt` | Starts the app |
| `navigation/` | `Routes.kt` (screen names) and `WiKamayNavGraph.kt` (who opens what) |
| `ui/screens/` | One file per screen |
| `ui/components/` | Reusable pieces: buttons, toggle, placeholders, scan frame, dialogs, cards |
| `ui/theme/` | `Color.kt` (all colors) and `Theme.kt` (text styles) |
| `data/SampleData.kt` | All dummy content: letters, numbers, lessons, sample translations |
| `res/values/strings.xml` | App name |

## Comment tags used in the code

- `EDITABLE:` safe to change (text, colors, sizes, sample data)
- `PURPOSE:` what a class, variable, or component is for
- `FUNCTION:` what a function or event handler does
- `NAVIGATION:` how screens open each other
- `PLACEHOLDER:` fake feature that will get real functionality later

## Easy things to try

- Change the main color: `PrimarySlate` in `ui/theme/Color.kt`.
- Change the welcome text: top of `ui/screens/SplashScreen.kt`.
- Add or edit lessons, letters, numbers: `data/SampleData.kt`.
- Change grid size per page: `COLUMNS` and `ROWS` in `ui/screens/ItemSelectionScreen.kt`.

## Differences from the wireframe

- The toggle shows text labels ("Sign → Text" on the left, "Text → Sign" on the right).
- The scan button stays visible after a result appears, so you can scan again.
- Previous/Next arrows were added to the letter/number practice screen (required by the brief).
- Numbers reuse the alphabet grid and practice screens (the wireframe has no separate Numbers design).
