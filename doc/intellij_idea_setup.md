# IntelliJ IDEA Setup

- [Download](https://www.jetbrains.com/idea/download/#section=windows) and install any edition
- [Install plugins](https://www.jetbrains.com/help/idea/managing-plugins.html)
    - Gradle
    - Lombok
    - JUnit
- Open project `File` -> `Open...` -> `ase-be-services`
- Ensure that all configs bellow are set to `Java 11`
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
- Enable formatter markers in comments
    - `File` -> `Settings` -> `Editor` -> `Code Style` -> `Formatter Control`
- Enable same-line annotations
    - `File` -> `Settings` -> `Editor` -> `Code Style` -> `Java`
      -> `Wrapping and Braces` -> `Do not wrap after single annotation`
- Remove trailing spaces
    - `File` -> `Settings` -> `Editor` -> `General` -> `On Save` -> `Remove trailing spaces`
- Use spaces instead of tabs
    - `File` -> `Settings` -> `Editor` -> `Code Style` -> `Java`
      -> `Tabs and Indents` -> **un**check `Use tab character`
    - Do this for other languages as well

---

[<: Application Setup](application_setup.md) | [Codebase Overview :>](codebase_overview.md)