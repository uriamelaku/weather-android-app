# MyKotlinApp - אפליקציית מזג אויר ב-Kotlin

## 📱 תיאור הפרויקט

אפליקציית Android מתקדמת הכוללת:
- **מערכת התחברות והרשמה** - ניהול משתמשים
- **תצוגת מזג אויר** - שליפת נתוני מזג אויר בזמן אמת
- **היסטוריית חיפושים** - שמירת חיפושים קודמים
- **מועדפים** - שמירת ערים מועדפות

---

## 🛠️ טכנולוגיות

### שפות ופריימוורקים
- **Kotlin** - שפת התכנות העיקרית
- **Android SDK 36** - גרסת API מתקדמת
- **View Binding** - קישור אוטומטי של views

### ספריות חיצוניות
- **Retrofit 3.0.0** - תקשורת HTTP עם שרתים
- **Gson Converter** - המרת JSON
- **Kotlin Coroutines** - תכנות אסינכרוני
- **Material Design Components** - עיצוב UI מודרני
- **RecyclerView** - רשימות דינמיות

### דרישות מערכת
- **minSdk**: 24 (Android 7.0 Nougat)
- **targetSdk**: 35 (Android 15)
- **compileSdk**: 36
- **Java Version**: 17

---

## 📂 מבנה הפרויקט

```
MyKotlinApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mykotlinapp/
│   │   │   │   ├── LoginActivity.kt          # מסך התחברות
│   │   │   │   ├── RegisterActivity.kt       # מסך הרשמה
│   │   │   │   ├── HomeActivity.kt           # מסך הבית
│   │   │   │   ├── WeatherActivity.kt        # מסך מזג אויר
│   │   │   │   ├── ApiClient.kt              # לקוח HTTP
│   │   │   │   ├── ApiService.kt             # הגדרות API
│   │   │   │   ├── HistoryAdapter.kt         # מתאם היסטוריה
│   │   │   │   ├── FavoritesAdapter.kt       # מתאם מועדפים
│   │   │   │   └── SearchHistoryItem.kt      # מודל נתונים
│   │   │   ├── res/                          # משאבים (layouts, drawables)
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/                      # בדיקות אינטגרציה
│   │   └── test/                             # בדיקות יחידה
│   └── build.gradle.kts                      # הגדרות build של המודול
├── gradle/                                   # Gradle Wrapper
├── build.gradle.kts                          # הגדרות build ראשיות
├── settings.gradle.kts                       # הגדרות פרויקט
├── local.properties                          # מיקום Android SDK (לא מועלה לגיט)
└── .gitignore                                # קבצים להתעלמות בגיט

```

---

## 🚀 התקנה והרצה

### דרישות מוקדמות

1. **התקנת Android Studio**  
   הורד מ: https://developer.android.com/studio

2. **התקנת Android SDK**  
   נתיב ברירת מחדל: `C:\Users\User\AppData\Local\Android\Sdk`

3. **JDK 17 ומעלה**  
   מגיע עם Android Studio (JetBrains Runtime)

---

### 📥 שלב 1: שכפול הפרויקט

```powershell
# שכפול מ-Git
git clone <repository-url>
cd MyKotlinApp
```

---

### ⚙️ שלב 2: הגדרת local.properties

צור קובץ `local.properties` בשורש הפרויקט:

```properties
sdk.dir=C\:\\Users\\User\\AppData\\Local\\Android\\Sdk
```

**הערה:** הקובץ הזה **לא** מועלה לגיט (כבר ב-.gitignore)

---

### 🔨 שלב 3: בניית הפרויקט

#### אופציה A: דרך Android Studio (מומלץ)

1. פתח את Android Studio
2. **File → Open** → בחר את תיקיית `MyKotlinApp`
3. המתן לסנכרון Gradle (Sync Project with Gradle Files)
4. **Build → Rebuild Project**
5. **Run → Run 'app'** (או לחץ על ▶️)

#### אופציה B: דרך שורת פקודה

```powershell
# בדיקת תקינות
.\gradlew.bat tasks

# ניקוי build קודם
.\gradlew.bat clean

# בניית גרסת Debug
.\gradlew.bat assembleDebug

# בניית גרסת Release (חתומה)
.\gradlew.bat assembleRelease

# הרצת בדיקות
.\gradlew.bat test

# התקנה למכשיר/אמולטור
.\gradlew.bat installDebug
```

---

## 🔧 פקודות Gradle שימושיות

### בנייה (Build)

```powershell
# בנייה רגילה
.\gradlew.bat build

# בנייה מהירה (ללא tests)
.\gradlew.bat assembleDebug --parallel

# בנייה עם מידע מפורט
.\gradlew.bat build --info

# בנייה עם ניפוי באגים
.\gradlew.bat build --debug
```

### ניקוי (Clean)

```powershell
# מחיקת תיקיית build
.\gradlew.bat clean

# ניקוי + בנייה מחדש
.\gradlew.bat clean build
```

### בדיקות (Testing)

```powershell
# הרצת כל הבדיקות
.\gradlew.bat test

# בדיקות Unit בלבד
.\gradlew.bat testDebugUnitTest

# בדיקות אינטגרציה (דורש מכשיר/אמולטור)
.\gradlew.bat connectedAndroidTest
```

### התקנה (Install)

```powershell
# התקנת גרסת Debug
.\gradlew.bat installDebug

# התקנת גרסת Release
.\gradlew.bat installRelease

# הסרת האפליקציה
.\gradlew.bat uninstallDebug
```

### ניתוח תלויות (Dependencies)

```powershell
# הצגת עץ תלויות
.\gradlew.bat :app:dependencies

# בדיקת גרסאות מעודכנות
.\gradlew.bat dependencyUpdates
```

### ביצועים (Performance)

```powershell
# build עם Build Cache
.\gradlew.bat build --build-cache

# build מקבילי
.\gradlew.bat build --parallel

# פרופיל build
.\gradlew.bat build --profile
```

---

## 🐛 פתרון בעיות (Troubleshooting)

### בעיה: "SDK location not found"

**פתרון:**
```powershell
# צור קובץ local.properties עם:
sdk.dir=C\:\\Users\\User\\AppData\\Local\\Android\\Sdk
```

---

### בעיה: "Gradle sync failed"

**פתרון:**
```powershell
# 1. נקה cache של Gradle
.\gradlew.bat clean --refresh-dependencies

# 2. מחק תיקיות cache ידנית
Remove-Item -Recurse -Force .gradle, .kotlin, build, app\build

# 3. סנכרן מחדש ב-Android Studio
```

---

### בעיה: "JAVA_HOME not set"

**פתרון:**
```powershell
# הגדר משתנה סביבה זמני
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# או קבוע (דורש הרשאות אדמין):
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "Machine")
```

---

### בעיה: build איטי

**פתרון:**
```powershell
# הוסף ל-gradle.properties:
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.jvmargs=-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError
```

---

## 📝 הערות חשובות

### קבצים שלא מועלים לגיט (.gitignore)

```gitignore
# Gradle
.gradle/
build/
**/build/

# Kotlin
.kotlin/

# Android Studio
.idea/
*.iml

# Local config
local.properties

# OS
.DS_Store
Thumbs.db
```

---

### AGP 9.0+ - Kotlin מובנה

**חשוב:** בגרסת Android Gradle Plugin 9.0 ומעלה, Kotlin מובנה בתוך AGP.

❌ **לא צריך להוסיף:**
```kotlin
id("org.jetbrains.kotlin.android") // מיותר!
```

✅ **מספיק:**
```kotlin
plugins {
    id("com.android.application")
}
```

---

## 📞 תמיכה ופידבק

- **Issues:** דווח על באגים ב-GitHub Issues
- **Pull Requests:** תרומות מתקבלות בברכה!
- **Documentation:** תיעוד נוסף ב-Wiki

---

## 📄 רישיון

הפרויקט הזה הוא קוד פתוח ללא רישיון ספציפי.

---

## 🎯 תכונות עתידיות

- [ ] הוספת מפה אינטראקטיבית
- [ ] תמיכה בריבוי שפות (i18n)
- [ ] נושאים כהים/בהירים (Dark/Light themes)
- [ ] widgets למסך הבית
- [ ] התראות על שינויי מזג אויר
- [ ] אינטגרציה עם Google Maps

---

**בנוי באהבה עם Kotlin ❤️ ו-Android Studio**

