# ⚡ TRUTH WATCH INDIA
### A Native Android News Platform Built for the Modern News Experience.

<p align="center">

  **Breaking News. Real-time Discovery. Native Android.**

  A premium editorial news experience powered by WordPress REST API, Kotlin, Jetpack Compose, and a locally persistent data architecture.

</p>

---

## `01 / THE PROJECT`

Truth Watch India is a native Android news application designed to bring the experience of a modern digital newspaper into a fast, intuitive, and feature-rich mobile environment.

Instead of wrapping a website inside a WebView, this project uses native Android technologies to build the entire experience — from news discovery and article reading to bookmarks, offline caching, personalization, and background synchronization.

**The engineering philosophy:**

- Native-first architecture.
- Real-world API integration.
- Offline-capable data layer.
- Editorial-focused UI.
- Reactive application state.
- Modular and maintainable code.

---

## `02 / TECH STACK`

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design System | Material 3 |
| Architecture | MVVM + Clean Architecture |
| Networking | Retrofit + OkHttp |
| Backend | WordPress REST API |
| Local Database | Room |
| Preferences | Jetpack DataStore |
| Image Loading | Coil |
| Background Tasks | WorkManager |
| Notifications | Android Notification APIs / FCM integration |
| Asynchronous Programming | Kotlin Coroutines + Flow |
| Testing | JUnit / Android testing tools |

---

## `03 / SYSTEM ARCHITECTURE`

The application follows a layered architecture designed to separate presentation, business logic, and data operations.

```text
                    TRUTH WATCH INDIA
                           |
              +------------+------------+
              |                         |
        PRESENTATION                DOMAIN
              |                         |
       Jetpack Compose            Use Cases
       ViewModels                  Repository
       StateFlow                   Contracts
              |                         |
              +------------+------------+
                           |
                         DATA
                           |
             +-------------+-------------+
             |             |             |
          Remote         Local       Preferences
             |             |             |
          Retrofit        Room        DataStore
             |             |             |
          WordPress      SQLite       User Settings
             |
          REST API

              BACKGROUND SERVICES
                       |
                 WorkManager
                       |
              Notification Layer
```

### Core engineering principles

- Separation of concerns.
- Repository-based data access.
- Reactive UI state management.
- Persistent local storage.
- Network-aware background operations.
- Reusable Compose components.

---

## `04 / FEATURE ENGINEERING`

### 📰 Editorial News Experience

- Featured news and editorial story layouts.
- Dedicated Uttar Pradesh news section.
- Lucknow news discovery.
- National news.
- International news.
- Breaking-news ticker.
- Category-based news discovery.
- Native article cards and image loading.

### 🔎 News Discovery Engine

- WordPress-powered article search.
- Debounced search queries.
- Search history.
- Category filtering.
- Loading, empty and error states.

### 📖 Advanced Article Reader

- Native article reading interface.
- Reading progress indicator.
- Estimated reading time.
- Dynamic font scaling.
- Hindi and English text-to-speech.
- Full-screen image viewing.
- Article sharing.
- Offline reading support.

### 🔖 Persistence & Offline Architecture

- Room database integration.
- Persistent bookmarks.
- Article caching.
- Reactive bookmark observation.
- Local data retrieval.
- Data persistence across application restarts.

### ⚙️ Personalization

- Persistent user preferences.
- Reader font-size settings.
- Notification preferences.
- Cache management.
- Light and dark theme support where configured.

### 🔔 Background Infrastructure

- WorkManager-based background synchronization.
- Network connectivity constraints.
- Notification channels.
- Notification deep-link routing.

**Infrastructure note:** Remote Firebase Cloud Messaging delivery requires valid Firebase configuration and server-side message integration. A notification channel or WorkManager implementation alone does not establish live remote push delivery.

---

## `05 / BACKEND INTEGRATION`

The application connects directly to the official Truth Watch India WordPress backend.

**REST API**

```text
https://truthwatchindia.org/wp-json/wp/v2/
```

Integrated endpoints include:

| Resource | Endpoint |
|---|---|
| Posts | `/posts?_embed=1` |
| Categories | `/categories` |
| Search | `/posts?search={query}&_embed=1` |
| About | `/pages/128` |
| Contact | `/pages/130` |
| Editorial Team | `/pages/7031` |

**Contact Form 7**

```text
/wp-json/contact-form-7/v1/contact-forms/6/feedback
```

The backend provides actual news content, metadata, categories, author information, and featured media.

---

## `06 / PROJECT STRUCTURE`

```text
TruthWatchAndroid/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/org/truthwatchindia/app/
│       │   │
│       │   ├── data/
│       │   │   ├── api/
│       │   │   ├── local/
│       │   │   ├── preferences/
│       │   │   └── repository/
│       │   │
│       │   ├── domain/
│       │   │
│       │   ├── notifications/
│       │   │
│       │   ├── worker/
│       │   │
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   ├── navigation/
│       │   │   └── screens/
│       │   │       ├── bookmarks/
│       │   │       ├── category/
│       │   │       ├── contact/
│       │   │       ├── detail/
│       │   │       ├── home/
│       │   │       ├── search/
│       │   │       └── settings/
│       │   │
│       │   └── AndroidManifest.xml
│       │
│       └── test/
│
├── gradle/
│   └── libs.versions.toml
│
├── build.gradle.kts
└── settings.gradle.kts
```

*Illustrative structure based on the implementation report. Verify against the latest repository before treating it as an exhaustive file listing.*

---

## `07 / GETTING STARTED`

### Requirements

- Android Studio.
- JDK compatible with the project's Gradle configuration.
- Android SDK.
- Git.
- Internet connection for live news data.

### Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>

cd TruthWatchAndroid
```

### Build the project

```bash
./gradlew assembleDebug
```

### Run unit tests

```bash
./gradlew testDebugUnitTest
```

### Install on a connected Android device

```bash
./gradlew installDebug
```

**APK output:**

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## `08 / TESTING & VERIFICATION`

The implementation report records:

| Verification | Reported result |
|---|---|
| Unit tests | 13 passed |
| Debug APK compilation | Successful |
| Database and repository tests | Included |
| Search ViewModel tests | Included |
| Article ViewModel tests | Included |
| WordPress model tests | Included |

These are reported implementation results, not an independent production-readiness certification. Instrumented device testing, comprehensive lint verification, and release validation should be completed separately.

---

## `09 / DESIGN IDENTITY`

The application retains the Truth Watch India visual identity.

| Design token | Value |
|---|---|
| Primary | `#970347` |
| Charcoal | `#2D2D2D` |
| Body text | `#313131` |

**Design direction:**

- Premium digital newspaper.
- Editorial typography.
- Content-first layouts.
- Minimal visual clutter.
- Native Android interactions.
- Hindi and English content support.

---

## `10 / ENGINEERING ROADMAP`

The project is being developed with a focus on long-term reliability and production quality.

- [x] Native Android foundation.
- [x] WordPress REST API integration.
- [x] Room persistence and repository layer.
- [x] Search and discovery implementation.
- [x] Enhanced article-reading experience.
- [x] Local preferences and settings.
- [x] WorkManager synchronization implementation.
- [ ] Comprehensive real-device verification.
- [ ] Complete FCM server-side integration and delivery verification.
- [ ] Full accessibility and performance audit.
- [ ] Release signing and production validation.

---

## `11 / WHY THIS PROJECT?`

This project explores more than rendering news articles.

It brings together several important Android engineering concepts:

- Native UI engineering with Jetpack Compose.
- Clean Architecture and repository patterns.
- Reactive state management.
- REST API consumption.
- Local-first persistence.
- Offline-capable application design.
- Background task scheduling.
- Multilingual text processing.
- Native speech synthesis.
- Deep linking and notification infrastructure.

The goal is to engineer an application that behaves like a real digital news product, rather than a website packaged as an Android application.

---

## `12 / PROJECT INFORMATION`

**Project:** Truth Watch India Android

**Platform:** Android

**Application ID:** `org.truthwatchindia.app`

**Language:** Kotlin

**Backend:** WordPress REST API

**Status:** Active development and verification

**Website:** https://truthwatchindia.org/

---

<p align="center">

### BUILT WITH KOTLIN. ENGINEERED FOR ANDROID.

**Truth Watch India**

*From raw news data to a native editorial experience.*

</p>
