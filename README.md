# Photo Album Android App

**CS 213 - Assignment 4**  
**Authors:** Haaris Toor & Aditi Negi

---

## Technical Specifications

- **Language:** Java
- **Build System:** Kotlin DSL (build.gradle.kts)
- **Minimum SDK:** API 36 (Android 14.0)
- **Tested on:** Pixel 6 emulator (1080x2400, 420 dpi, API 36/37)

**Key Dependencies:**
- AndroidX AppCompat, Material Components, ConstraintLayout
- RecyclerView for lists/grids
- Gson for JSON data persistence

---

## GenAI Usage Documentation

### Tools Used
- **Anthropic Claude** (Sonnet 4.5)
- **Gemini AI** (via Android Studio integration)

### Development Approach

The use of GenAI was mostly limited to code refinement and debugging.

#### 1. Logic Porting & Model Setup
- **Manually adapted** Album, Photo, and Tag models from JavaFX to Android
- **AI-assisted**: DataManager refinement for Android-specific file paths and GSON integration for persistence

#### 2. UI Layout & Refinement
- **Self-implemented**: Initial XML structure based on FX implementation
- **AI-assisted**: Layout constraint optimization and responsive design for 1080x2400 resolution

#### 3. Debugging Complex Components
- **Self-implemented**: RecyclerView adapter architecture
- **AI-assisted**: Adapter logic debugging and Bitmap loading optimization (inSampleSize) to prevent memory crashes

#### 4. Feature Integration
- **Self-implemented**: Feature requirements and architecture
- **AI-assisted**: Connecting Java logic to Android UI elements (AutoCompleteTextView, RadioGroup for AND/OR search logic)

#### 5. Technical Troubleshooting
- **AI-assisted**: GSON dependency configuration in build.gradle.kts, Git repository management

### Components Breakdown

**Self-Written:**
- Core business logic (Album/Photo/Tag operations)
- Application architecture and navigation flow
- Feature requirements and implementation strategy

**AI-Assisted:** 
- XML layout refinement and constraint optimization
- Performance optimization (bitmap scaling, memory management)
- README.md file generation

By working this way, I maintained control over the app's architecture while using AI to solve specific Android implementation challenges and ensure platform best practices.
