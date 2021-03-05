# IntelliJ IDEA Setup

### Install and configure IDE

- [Download](https://www.jetbrains.com/idea/download/#section=windows) and install any edition
- [Install plugins](https://www.jetbrains.com/help/idea/managing-plugins.html)
    - Gradle
    - Lombok
    - JUnit
- Open project `File` -> `Open...` -> `ase-be-services`
- Ensure that all configs below are set to `Java 11`
    - `File` -> `Project Structure`
        - `Project`
            - `Project SDK`
            - `Project language level`
        - `Modules` -> `Dependencies` -> `Module SDK`
        - `SDKs`
    - `File` -> `Settings` -> `Build, Execution, Deployment`
        - `Compiler` -> `Java Compiler` -> `Project bytecode version`
        - `Build Tools` -> `Gradle` -> `Gradle JVM`
- Enable annotation processing
    - `File` -> `Settings` -> `Build, Execution, Deployment`
      -> `Compiler` -> `Annotation Processors`

### Run application

- `java/com/bloxico/AppEntry.java` -> right click -> `Run 'AppEntry.main()'`
- `java/com/bloxico/ase/TestSuite.java` -> right click -> `Run 'TestSuite'`

### Configure editor

- `File` -> `Settings` -> `Editor`
    - `Code Style`
        - `Formatter Control` -> `Enable formatter markers in comments`
        - `Java`
            - `Wrapping and Braces`
                - `Braces placement` -> `In method declaration` -> `Next line if wrapped`
                - `Filed annotations` -> `Do not wrap after single annotation`
            - `Tabs and Indents`
                - `Use tab character` **un**check
                - `Tab size` : `4`
    - `General` -> `On Save` -> `Remove trailing spaces on` : `Modified lines`

---

[<: Application Setup](application_setup.md) | [Codebase Overview :>](codebase_overview.md)