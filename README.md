# Elyndra

Frontend de biblioteca de ROMs y lanzador de emuladores para Android, con una interfaz *liquid glass* pensada para mando y pantalla horizontal (consolas portátiles, tablets, Android TV-like).

Elyndra **no incluye ni descarga ROMs ni emuladores**: escanea las carpetas que tú le indiques y lanza los juegos con los emuladores que ya tengas instalados en el dispositivo.

---

## Características

**Biblioteca**
- Escaneo recursivo de carpetas de ROMs mediante SAF (`OpenDocumentTree`), sin permisos de almacenamiento global.
- Detección automática de plataforma por extensión y por nombre de carpeta (las extensiones ambiguas como `iso`, `bin`, `cue` o `chd` se resuelven con pistas de carpeta).
- 21 plataformas en el catálogo integrado: NES, SNES, N64, Game Boy / Color / Advance, NDS, 3DS, GameCube, Wii, Switch, PS1, PS2, PSP, Vita, Genesis, Master System, Game Gear, Saturn, Dreamcast y Arcade.
- Apps de Android instaladas añadidas como "juegos" desde un selector, para tenerlo todo en la misma biblioteca.
- Vista de rejilla o lista, búsqueda, filtros por plataforma y favoritos, y barra alfabética de salto rápido.
- Registro de tiempo de juego y última vez jugado por título.

**Metadatos (scraping)**
- ScreenScraper (emparejado por hash CRC32), IGDB, SteamGridDB y RetroAchievements.
- Carátulas, fondos, descripciones y género; scraping automático tras un escaneo o manual por juego.
- Las credenciales las pone cada usuario en Ajustes → Integraciones; no se distribuye ninguna clave con la app.

**Personalización**
- Tema claro, oscuro o el del sistema.
- 9 colores de acento que retiñen toda la interfaz.
- Idioma: español, inglés o el del sistema — se aplica al instante, sin reiniciar.
- Columnas de la rejilla y visibilidad de los juegos ocultos.

## Requisitos

| | |
|---|---|
| Android mínimo | 12 (API 31) |
| `compileSdk` / `targetSdk` | 35 |
| JDK | 17 |

## Compilar

```bash
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/Elyndra-debug.apk`. La variante de debug usa el sufijo de id `.debug`, así que puede convivir con una instalación de release.

Para una build firmada de release, configura tu `signingConfig` y ejecuta `./gradlew assembleRelease` (R8 y shrink de recursos ya están activados).

## Arquitectura

Clean architecture en tres capas, con Hilt como inyector.

```
app/src/main/java/com/elyndra/app/
├── data/          Room, DataStore, Retrofit, escáner de ROMs, launcher de emuladores
│   ├── local/     entidades, DAOs y base de datos
│   ├── remote/    APIs y fuentes de scraping (ScreenScraper, IGDB, SteamGridDB, RA)
│   ├── repository/ implementaciones de los repositorios de dominio
│   └── scanner/   recorrido de carpetas y detección de ROMs
├── domain/        modelos, interfaces de repositorio y casos de uso (sin dependencias de Android)
└── ui/            Compose: pantallas, componentes de vidrio, navegación y tema
```

**Stack**: Kotlin · Jetpack Compose (Material 3) · Hilt · Room · DataStore Preferences · Navigation Compose (rutas serializables) · Retrofit + kotlinx.serialization · Coil · Coroutines/Flow.

Algunos detalles que conviene conocer antes de tocar código:

- **`ui/components/glass/`** — el sistema de diseño propio (`LiquidGlassSurface`, `GlassButton`, `GlassChip`, `GlassTopBar`, el rail lateral). Los estilos y radios viven en `ui/theme/GlassStyle.kt`.
- **`ui/theme/AccentPalettes.kt`** — las 9 paletas de acento. `ElyndraTheme` las aplica con `withAccent()`, que solo cambia los roles de acento: superficies, fondo y error se mantienen, así que el contraste validado para claro/oscuro se conserva con cualquier acento.
- **`ui/AppLocale.kt`** — el idioma por app se aplica sobrescribiendo `LocalContext` y `LocalConfiguration`, sin depender de appcompat.
- **`ui/UiMessage.kt`** — los ViewModels emiten `UiMessage` (id de recurso + argumentos) en vez de `String`, porque el único contexto que alcanzan es el de la aplicación y ese sigue el idioma *del dispositivo*, no el elegido en Ajustes.
- **`util/Constants.kt`** — `KNOWN_EMULATOR_PACKAGES` debe mantenerse sincronizado con el bloque `<queries>` del `AndroidManifest.xml`; es lo que hace visibles esos paquetes a `PackageManager` en Android 11+. Cualquier emulador que falte en ambas listas sigue siendo utilizable escribiendo su nombre de paquete a mano.
- **`app/schemas/`** — historial de esquemas de Room exportado por KSP. Se versiona a propósito: es lo que permite escribir migraciones.

## Configurar las fuentes de metadatos

Todas son opcionales y gratuitas; Elyndra funciona sin ninguna, solo que sin carátulas automáticas.

| Fuente | Qué necesitas | Dónde se obtiene |
|---|---|---|
| ScreenScraper | `devid` + `devpassword` de un "software" registrado (tu cuenta personal es opcional y amplía la cuota) | screenscraper.fr |
| IGDB | Client ID + Client Secret (autentica vía Twitch) | dev.twitch.tv/console/apps |
| SteamGridDB | Clave de API | steamgriddb.com/profile/preferences/api |
| RetroAchievements | Usuario + clave de API | retroachievements.org/settings |

RetroAchievements se guarda para el futuro seguimiento de logros: su API pública no tiene un endpoint de búsqueda por juego fiable, así que no se usa para carátulas. LaunchBox no puede integrarse: solo publica una descarga masiva `Metadata.zip` para su app de escritorio, sin API de búsqueda.

## Licencia

Sin licencia definida todavía.
