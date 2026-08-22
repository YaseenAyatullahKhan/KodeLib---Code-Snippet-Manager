

## 🗃️ 1. Full Project Directory Structure
```
KodeLib-FoP-WIX1002/
 ├── src/
 │    └── main/
 │         └── java/
 │              ├── app/
 │              │   └── MainApp.java
 │              ├── controller/
 │              │   ├── WelcomeController.java
 │              │   ├── LoginController.java
 │              │   ├── SignupController.java
 │              │   ├── HomeController.java
 │              │   ├── SnippetEditorController.java
 │              │   ├── SearchController.java
 │              │   ├── FilterAndSortController.java
 │              │   └── CollectionsController.java
 │              ├── model/
 │              │   ├── Version.java
 │              │   ├── User.java
 │              │   ├── Tag.java
 │              │   └── CollectionGroup.java
 │              ├── service/
 │              │   ├── SnippetService.java
 │              │   ├── SearchService.java
 │              │   ├── FilterandSortService.java
 │              │   ├── VersionService.java
 │              │   ├── UserService.java
 │              │   ├── CollectionService.java
 │              │   └── AutoSaveService.java
 │              ├── util/
 │              │   ├── JSONUtil.java
 │              │   ├── PasswordUtil.java
 │              │   ├── DateTimeUtil.java
 │              │   └── ValidationUtil.java
 │              └── view/
 │                  ├── Welcome.fxml
 │                  ├── Login.fxml
 │                  ├── Signup.fxml
 │                  ├── Home.fxml
 │                  ├── Editor.fxml
 │                  ├── Search.fxml
 │                  └── Collections.fxml
 │
 ├── data/
 │    ├── snippets/
 │    │   ├── snippet1.json
 │    │   ├── snippet2.json
 │    │   └── ... (auto-incremented)
 │    └── users.json
 │
 ├── bin/ (compiled output)
 │    ├── app/
 │    ├── controller/
 │    ├── model/
 │    ├── service/
 │    ├── util/
 │    └── view/
 │
 ├── lib/ (dependencies)
 │    └── javafx/ (JavaFX SDK)
 │
 ├── docs/
 │    └── flowcharts/
 │        ├── Overall_Programme.pdf
 │        ├── Searching_Retrieving.pdf
 │        ├── Filter_Sort.pdf
 │        ├── Organisation_Features.pdf
 │        ├── JavaFX_Dashboards.pdf
 │        ├── Version_History.pdf
 │        └── Multi-user_Login.pdf
 │
 └── README.md
```
---
**All application logic is stored inside** `src/main/java/` **using a clean and modular package structure.**

---
## **2.1** `app/` – Application Entry Point

- Entry point for the JavaFX application launcher.

Example:

    MainApp.java

### **2.2** `controller/` – JavaFX Controllers

- Manages UI interactions and links FXML views to backend logic.
- Handles user events and navigation between pages.

Examples:
```
WelcomeController.java           (welcome screen)
LoginController.java             (authentication)
SignupController.java            (user registration)
HomeController.java              (main dashboard)
SnippetEditorController.java     (create/edit snippets with version display)
SearchController.java            (search results with filtering/sorting)
FilterAndSortController.java     (filter and sort logic for search)
CollectionsController.java       (manage collections and assignments)
```

### **2.3** `model/` – Data Models

- Plain Java classes representing core data structures.
- Contains attributes, constructors, getters/setters, and minimal utilities.
- Does NOT contain application logic.

Examples:
```
Version.java       (snippet content with version metadata)
User.java          (user account with credentials)
Tag.java           (tags with nested hierarchy support e.g. Algorithm/Sorting/QuickSort)
CollectionGroup.java (snippet collections for organization)
```

### **2.4** `service/` – Application Logic Layer

- Implements all core application functionalities.
- Separates application logic from UI for modularity and testability.

Examples:
```
SnippetService.java          (CRUD operations for snippets)
SearchService.java           (keyword search with nested tag matching)
FilterandSortService.java    (filtering and sorting operations)
VersionService.java          (version history, comparison, revert)
UserService.java             (user authentication and registration)
CollectionService.java       (collections, backlinks, grouping)
AutoSaveService.java         (5-minute auto-save timer)
```

### **2.5** `util/` – Utility Classes

- Reusable helper functions and utilities.

Examples:
```
JSONUtil.java       (JSON read/write with Jackson)
PasswordUtil.java   (SHA-256 hashing and verification)
DateTimeUtil.java   (timestamp formatting and parsing)
ValidationUtil.java (password strength validation)
```

### **2.6** `view/` – JavaFX GUI Files

- FXML files defining the user interface.
- 7-page application with complete user flow.

Examples:
```
Welcome.fxml    (welcome with Login/Signup options)
Login.fxml      (user authentication)
Signup.fxml     (user registration with password validation)
Home.fxml       (main dashboard with navigation)
Editor.fxml     (snippet editor with version history and backlinks)
Search.fxml     (search results with filters and sorting)
Collections.fxml (create and manage snippet collections)
```

## 🗂 **3. Data Storage Structure `/data/`**

File-based JSON storage for all persistent data.
```
/data
 ├── snippets/
 │     ├── snippet1.json
 │     ├── snippet2.json
 │     ├── snippet3.json
 │     └── ... (auto-incremented)
 │
 ├── users.json (user accounts)
 └── config.json (app settings)
```
*Contains 1 test snippet named Python Basics*

### **3.1** `snippets/`

- Each snippet stored as individual JSON file.
- File naming: snippet1.json, snippet2.json, etc. (auto-incremented).
- Each file contains array of Version objects (complete history).
- Version 0 = creation with timestamp, subsequent versions only on content changes.

### **3.2** `users.json`

- User accounts with secured credentials:
  - username
  - password hash (SHA-256)
  - salt for hashing

### **3.3** `config.json`

- Global application configuration (placeholder for future settings).

## 📘 **4. Documentation Folder `/docs/`**

Contains project documentation and flow.
```
/docs
 └── flowcharts/ (program flows)
```

### **4.1** `project-directory-structure.md`

This file describing the entire project structure and responsibilities.

### **4.2** `spec.txt`

Complete project specification including:
- Basic features (manage, search, organize)
- Extra features (GUI, version history, multi-user)
- Mark allocations and requirements

### **4.3** `technology_choices.md`

Architecture and technology decisions:
- JSON for flexible file-based storage
- SHA-256 for secure password hashing
- JavaFX for cross-platform GUI
- MVC pattern for separation of concerns

### **4.4** `flowcharts/`

Visual system design and overall program flow documentation.

## 🔁 5. Regulatory Guidelines We Followed

- ✔ Consistent package naming (lowercase, no spaces/dashes)
- ✔ Application logic isolated in `service/` layer
- ✔ Data files stored in `/data/` only
- ✔ Each FXML view has corresponding controller
- ✔ No plain text passwords (SHA-256 required)
- ✔ Version control with proper .gitignore

## ✨ 6. Features Implemented

### **Manage Snippets (Basic Feature)**
- Create, read, update, delete snippets
- Auto-save every 5 minutes with logging
- Full version history per snippet with diff comparison

### **Search & Retrieve (Basic Feature)**
- Keyword search across title, language, tags, description, code, packages
- Case-sensitive/insensitive search toggle
- Filter by language, tags, created/modified date ranges
- 10 sort options (title, language, tags, dates - ascending/descending)
- Maximum 25 results per search for readability

### **Organization (Basic Feature)**
- Nested tag categories with parent/child matching (Algorithm/Sorting matches Algorithm/Sorting/QuickSort)
- Collections for grouping and categorizing snippets
- Backlinks showing which snippets reference the current one

### **Extra Features**
- **JavaFX GUI**: 7-page JavaFX application with login, home, editor, search, and collections
- **Version History**: Complete change tracking with version comparison and revert capability using 5 minutes continuous auto-saving
- **Multi-User**: User registration with strong password requirements and double-layer, secure authentication

*test login details: yaseen123, Password#123*
