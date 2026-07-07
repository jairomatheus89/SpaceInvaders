
# SpaceInvaders

## Como Rodar?:
Se tiver no windows na pasta raiz do projeto execute o `gradlew.bat` com o comando: ``.\gradlew run``

Se tiver no Linux: ``./gradlew run`` 

## Projeto:
### source: 
	core/src/main/java/com/jairo/spaceinvaders/

O codigo principal e "start" do projeto é o ``FirstScreen.java`` é nele que vamos fazer alterações e implementar o loops principais como update() e render().

### !warning!
como o projeto ainda é pequeno esta sendo utilizado apenas uma tela/cena ainda para toda a aplicação utilizando apenas de booleans para renderizar ou nao, e metodos para resetar entidades, como status de player, vetores de inimigos, e vetores de projeteis da cena por exemplo 

---
A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
