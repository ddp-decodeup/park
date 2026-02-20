# Park Enforcement - Flutter Mobile Application Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [What is This Application?](#what-is-this-application)
3. [Key Features](#key-features)
4. [Technology Stack](#technology-stack)
5. [How the Application Works](#how-the-application-works)
6. [Project Folder Structure](#project-folder-structure)
7. [API Configuration](#api-configuration)
8. [Feature Implementation Status](#feature-implementation-status)
9. [Core Services](#core-services)
10. [Application Features with API Details](#application-features-with-api-details)
11. [Authentication & API Integration](#authentication--api-integration)
12. [Brand & Configuration System](#brand--configuration-system)
13. [Communication with Server](#communication-with-server)
14. [Data Storage](#data-storage)
15. [Offline Functionality](#offline-functionality)
16. [User Interface & Design](#user-interface--design)
17. [Multi-Language Support](#multi-language-support)
18. [Navigation & Screens](#navigation--screens)
19. [Data Management](#data-management)
20. [Reusable Components](#reusable-components)

---

## API Configuration

### Base URL and Server Configuration

The application communicates with a backend server hosted at:
- **Base URL**: `https://devapi.parkloyalty.com/`

The base URL can be changed during brand selection, allowing the app to work with multiple parking enforcement agencies by pointing to their respective API servers.

**Server Configuration File**: [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)

This file contains all API endpoints used throughout the application. The API client automatically handles:
- Token injection for authenticated requests
- Request/response logging for debugging
- Connection error handling with retry logic
- Request timeout (30 seconds) to prevent app hanging

---

## Feature Implementation Status

### Understanding the Development Progress

This section explains what's been completed, what's being worked on, and what's planned for the future. Think of it like a construction project - some areas are finished, some are under construction, and some are planned for later.

**Status Indicators**:
- ✅ **COMPLETED (100%)**: Fully built, tested, and ready to use. Officers can use these features right now.
- 🟡 **IN PROGRESS (70-85%)**: Mostly built and working, but still being refined. Officers can use these, but we're still improving them.
- 🔵 **PLANNED (0%)**: Designed but not built yet. These will be added in future updates.

### The Current Project Progress

**1. Brand Selection (100% - COMPLETED)**

**Status**: Fully implemented with static data and manual configuration

**What This Feature Does**:
When an officer opens the app for the very first time, they need to tell the app which parking enforcement agency they work for. Think of it like telling your email app whether you use Gmail, Outlook, or Yahoo - each one connects to a different server.

**How It Works**:
1. Officer opens the app
2. App shows a screen asking "Which agency do you work for?"
3. Officer enters three pieces of information:
   - **Site ID**: A unique number that identifies their parking agency
   - **Customer Name**: The name of their parking enforcement company
   - **Base URL**: The server address where the company's data is stored (like https://mycompanyservers.com)
4. Officer clicks "Next" and the app saves this information
5. From now on, every time the officer opens the app, it automatically knows which agency they work for

**Why This Matters**:
The same app is used by many different parking companies. This feature ensures that:
- Each officer's data goes to their company's server, not someone else's
- Each officer sees forms and information specific to their agency
- The app is flexible enough to work for any parking enforcement organization

**Location in Code**: [lib/features/brand/](lib/features/brand/)

**User Flow**:
```
First Time Using App → Select Your Agency → Enter Server Info → Done! 
(Next time: App remembers your choice)
```

---

**2. Authentication/Login (100% - COMPLETED)**

**Status**: Fully implemented with API integration and design

**What This Feature Does**:
This is the security system that ensures only authorized officers can use the app. Think of it like the  front door of a building - you need to show your ID to get in.

**How It Works**:
1. Officer enters their **username** (or employee ID) on the login screen
2. Officer enters their **password** (kept secret, hidden while typing)
3. Officer clicks "Login"
4. App sends this information securely to the server to verify
5. Server checks: "Is this username and password correct?"
6. If correct: Server gives the app a special "security key" (called a token)
7. App stores this security key and remembers the officer is logged in
8. Officer is taken to the Home Dashboard and can start working

**Security Features**:
- Password is masked (hidden) while typing
- Special verification (reCAPTCHA) prevents bots from trying to break in
- Password reset option if officer forgets their password

**What Happens If You Enter Wrong Credentials**:
- If the username is wrong, the app shows "User not found"
- If the password is wrong, the app shows "Invalid password"
- This protects officer privacy (doesn't say which one was wrong)

**The Security Key (Token)**:
After successful login, the officer gets an invisible "security key" that:
- Proves to the server that they're authorized
- Is automatically added to every request from the app
- Stays valid until they log out or it expires
- Is stored safely on their phone

**Location in Code**: [lib/features/login/](lib/features/login/)

**User Flow**:
```
Open App → Login Screen → Enter Username → Enter Password 
→ App Verifies with Server → Gets Security Key → Home Dashboard
```

---

**3. Splash Screen (100% - COMPLETED)**

**Status**: Fully implemented

**What This Feature Does**:
A splash screen is the first image officers see when they open the app - it shows the agency's logo and a loading animation.

**What Happens Behind the Scenes**:
While the splash screen displays, the app is working  hard:
- Checking if the officer is still logged in
- Loading form templates for offline use
- Requesting GPS permission
- Loading other necessary data
- Making sure everything is ready

**Why Splash Screens Are Useful**:
- Shows something interesting while the app is starting up
- Gives the app time to prepare without looking like it's frozen
- Shows the company's branding to officers
- Professional appearance

**User Experience**:
Officer opens app → Sees splash screen with agency logo and loading symbol → After 2-3 seconds → App shows login or home screen

---

**4. Home Dashboard (100% - COMPLETED)**

**Status**: Fully implemented with API integration

**What This Feature Does**:
The Home Dashboard is the "command center" or main screen that officers see after logging in. It's where they can access all the tools they need for their job, similar to a desktop on a computer with shortcuts to different applications.

**What Information Is Shown**:
- Officer's name and badge number (confirming who's logged in)
- Current shift information
- Daily citation targets and progress (e.g., "You need to issue 15 citations today, you've issued 3 so far")
- Real-time parking information in their jurisdiction
- Quick access buttons to main features:
  - Create Citation (New Ticket)
  - View My Activity (Charts and Statistics)
  - Search Citations (Lookup)
  - And more

**How It Gets Information**:
When the home screen loads:
1. App connects to server and asks for officer's information
2. App downloads form templates (the structures for creating citations)
3. App downloads dropdown data (all available violation types, vehicle makes, etc.)
4. All this information is displayed on the screen
5. Information is saved locally so it works offline next time

**Real-Time Updates**:
The dashboard automatically shows the latest information for:
- How many citations issued today
- Current time and shift
- Any system updates or messages

**Location in Code**: [lib/features/home/](lib/features/home/)

**Why This Is Important**:
The Home Dashboard is the hub of the entire app. Everything officers do starts from this screen. It's designed to be the quickest way to access the tools they need most.

---

**5. Citation Issuance/Ticketing (80% - IN PROGRESS)**

**Status**: Core functionality implemented, refinements still being made

**What This Feature Does**:
This is the most important feature - where officers create digital parking citations (tickets) instead of using paper. It's the core of what parking enforcement officers do every day.

**How Citation Creation Works** (Step by Step):

**Step 1 - Start Creating Citation**:
Officer taps the "Create Citation" button on home screen

**Step 2 - Enter Vehicle Information**:
- License plate number (the most important piece of information)
- Vehicle make (e.g., Honda, Toyota, Ford)
- Vehicle model (e.g., Civic, Camry, Mustang)
- Vehicle color (e.g., blue, red, white)

**Step 3 - Select Violation Type**:
Officer selects which parking violation occurred:
- Expired meter (parking time ran out)
- No parking zone (parked in prohibited area)
- Handicap space without permit
- Fire lane violation
- Double parked
- And many others specific to their agency

**Step 4 - Add Location Details**:
The app automatically captures:
- GPS coordinates (exact latitude/longitude)
- Time of citation (exact date and time)
Officer can also add:
- Parking meter number
- Parking space number
- Street name and location description

**Step 5 - Take Photos**:
Officer can take up to 10 photos showing:
- The vehicle from several angles
- The violation (e.g., newspaper next to expired meter showing date)
- The parking sign or violation details
- Any other relevant evidence

Photos are automatically compressed to save data.

**Step 6 - Add Officer Information**:
App automatically includes:
- Officer's badge number
- Officer's shift
- Agency information

**Step 7 - Add Notes** (Optional):
Officer can add any additional comments about the violation.

**Step 8 - Preview and Review**:
Officer sees everything they've entered:
- All information is displayed for review
- Photos are shown
- Officer can edit before submitting if they found an error

**Step 9 - Similarity Check**:
Before submitting, the app checks:
- "Has a similar citation been issued recently for this vehicle and location?"
- If yes, it warns the officer (helps prevent duplicate citations)

**Step 10 - Submit Citation**:
Officer clicks "Submit" and:
- If online: Citation is immediately sent to the server
- If offline: Citation is saved locally and sent automatically when internet returns

**The Complete Citation Record**:
After submission, the citation includes:
- All vehicle information
- Violation details and type
- Exact GPS location and timestamp
- All photos (uploaded to server)
- Officer who issued it
- Agency it's for

**Different Payment Methods Supported**:
- **Pay-by-Plate**: Vehicle owner can pay just by knowing the license plate
- **Pay-by-Space**: Payment tied to parking meter/space for bulk parking violations

**Location in Code**: [lib/features/ticketing/](lib/features/ticketing/)

**Why This Matters**:
Before mobile apps, officers wrote everything on paper:
- Risk of smudged ink, illegible handwriting
- Photos had to be collected separately
- Manual entry into computer took hours
- Information was lost if paper got damaged

With this app:
- All information is captured accurately
- Photos are attached immediately
- Information is sent to server in real-time
- No risk of data loss
- Saves officers 20+ minutes per citation
- Complete digital record for legal proceedings

---

**6. Activity Analytics (75% - IN PROGRESS)**

**Status**: Multiple chart types and analytics views working, minor refinements ongoing

**What This Feature Does**:
This feature provides officers with detailed reports and charts showing their daily work activity. It's like a performance dashboard that shows "How'm doing today?"

**What Information Is Shown**:

**Bar Charts** showing metrics like:
- Number of citations issued
- Number of scans performed
- Number of permits checked
- Vehicle drives-offs (vehicles that left after officer found a violation)

**Line Charts** showing trends over time:
- Daily: How activity varies throughout a single day
- Weekly: How activity compares day by day in a week
- Monthly: How activity trends over weeks

**Violation Breakdown**: Pie chart or bar chart showing:
- Which types of violations are most common?
- Most cited violation (e.g., expired meter is 40% of citations)
- Least cited violation

**Activity Timeline**: Detailed log showing:
- 9:05 AM - Citation issued for license plate ABC123
- 9:15 AM - Citation issued for license plate XYZ789
- 10:00 AM - Parking meter scan performed
- And so on...

**Location Data**: Map or location report showing:
- Which areas the officer worked in
- Which blocks or zones had the most violations
- Heat map of enforcement activity

**Daily Summary**: Overview showing for today:
- Total citations issued
- Total fines issued
- Hours worked
- Number of locations visited
- Most common violation in their area

**License Plate Recognition (LPR) Data** (if available):
- Scanned license plates that matched known violations
- Vehicle information from scanning equipment
- Which plates need to be followed up on

**How Officers Use This**:

Officer taps "My Activity" on home screen:
- Can view charts and graphs for their shift
- Can compare today's performance to previous days
- Can identify which areas have more violations
- Can see which types of violations are most common
- Can track their overall productivity

**Real-World Examples**:
- "I usually issue more citations in the morning - weekdays 9-11 AM is my peak"
- "Downtown area has more violations than residential area"
- "Expired meters account for 45% of violations, no parking zones are 35%"
- "I average 12 citations per shift"

**Location in Code**: [lib/features/my_activity/](lib/features/my_activity/)

**Why This Matters**:
Performance data helps officers:
- Understand their work patterns
- Identify peak enforcement times
- Allocate time more efficiently
- Set personal goals
- Track progress throughout the day
- Improve their enforcement strategy

Management can also:
- See which officers are most productive
- Identify areas with most violations
- Plan resource allocation
- Generate reports for city/agency leadership

---

**7. Citation Lookup (70% - IN PROGRESS)**

**Status**: Core lookup functionality working, additional features being added

**What This Feature Does**:
This feature allows officers to search for and view previous citations. It's like a "citation history" database where officers can look up any past violation.

**How Lookup Works**:

**Search Options**:
Officers can search by:
- License plate number (most common)
- Date range (e.g., show me all citations from last Monday)
- Parking meter number
- Citation status (unpaid, paid, disputed, etc.)
- Violation type

**What Information Is Retrieved**:
When an officer searches, they can see:
- Who issued the original citation
- When it was issued
- Which vehicle (license plate)
- Type of violation
- Location
- Associated photos
- Payment status
- Amount owed

**Real-World Uses**:

*Example 1*: "That car has been parked here three times this month"
- Officer searches by license plate
- Sees history of all citations for that vehicle
- Knows they're a repeat offender

*Example 2*: "Is this spot a parking violation?"
- Officer searches by parking meter number
- Sees if similar citations were issued before
- Confirms the violation type

*Example 3*: Officer needs to verify payment
- Searches by citation number
- Sees if payment was received
- Confirms in system

**Parking Timing Records**:
If applicable to the agency:
- Shows timing enforcement records
- When specific parking areas can/cannot have vehicles
- Helps verify if vehicle was in a time-restricted zone

**License Plate Recognition (LPR) Data**:
Shows scanned plates that matched:
- Known violators
- Scofflaws (people with many unpaid violations)
- Vehicle details from registration databases

**Location in Code**: [lib/features/lookup/](lib/features/lookup/)

**Why This Matters**:
Lookup helps officers:
- Understand violation patterns
- Make informed decisions about problem areas
- Document repeat violators
- Verify past actions
- Provide evidence if disputes arise

---

**8. Manual Scanning & Data Entry (0% - PLANNED)**

**Status**: Placeholder screens exist, API integration and full functionality not started yet

**What This Feature Will Do**:
This feature will allow officers to scan license plates using their phone's camera. Instead of manually typing the license plate, they can point their phone at the plate and it automatically reads it.

**How It Will Work**:
- Officer points phone camera at license plate
- App uses optical character recognition (OCR) to read the numbers and letters
- Plate is automatically entered into the citation form
- Also supports barcode and QR code scanning if needed

**Why This Will Be Useful**:
- Faster data entry (scan instead of type)
- Fewer typos (plates are correctly recognized)
- Works with parking validation stickers and permits

**Status**: Will be added in a future version

---

**9. Municipal Citation Handling (0% - PLANNED)**

**Status**: Basic structure exists, functionality not started yet

**What This Feature Will Do**:
Some cities have special "Municipal Citations" with additional requirements beyond standard parking violations. These might include:
- Court hearing information
- Special municipal violation codes
- Additional fees or penalties
- Different submission requirements

**Why This Will Be Useful**:
Different cities have different legal requirements. This feature will support those variations.

**Status**: Will be added in a future version

---

**10. Reports & Export (0% - PLANNED)**

**Status**: Placeholder only, not implemented yet

**What This Feature Will Do**:
Officers will be able to generate comprehensive reports of their work and export them in different formats:
- Daily enforcement report
- Weekly activity summary
- Monthly statistics
- Violation breakdown reports
- Revenue reports (citations issued vs. fines collected)
- Export as PDF, Excel, or CSV files

**Real-World Uses**:
- Officer submits daily report to supervisor
- Agency generates weekly productivity report
- City generates monthly revenue report
- Export data for auditing purposes

**Status**: Will be added in a future version

---

**11. Settings & Preferences (0% - PLANNED)**

**Status**: Placeholder only, not implemented yet

**What This Feature Will Do**:
Officers will be able to customize their app experience:
- Update profile information
- Change password
- Set notification preferences
- Choose shift schedule
- Select app theme (dark mode/light mode if available)
- Clear cached data to free up storage
- Log out from the app

**Real-World Uses**:
- Officer changes their password for security
- Officer updates their badge number if it changes
- Officer clears cache to free up phone storage if needed
- Officer logs out at end of employment

**Status**: Will be added in a future version

---

**Park Enforcement** is a mobile application built for parking enforcement officers. It helps them efficiently manage parking citations, track their daily activities, and generate reports about their enforcement work.

**Platform Support**: iOS and Android
**Language**: English and Hindi
**Architecture Approach**: Clean, modular design with separation of concerns
**Development Framework**: Flutter with GetX state management
**Target Audience**: Parking enforcement officers in 40+ agencies/cities

---

## What is This Application?

### Purpose and Use Case

This application is designed to help parking enforcement officers do their job more efficiently and accurately. Instead of writing citations on paper or using outdated systems, officers use this app on their mobile phone to record parking violations in real-time, capture photographic evidence, and track their daily activities.

Think of it like a digital clipboard that not only records violations but also keeps track of everything the officer does during their shift - all automatically and without needing the internet if they're in areas with poor connectivity.

### Main Functions

**Creating Citations (Digital Tickets)**
When an officer finds a parked vehicle that's violating parking regulations, they use this app to create a citation. Instead of handwriting a ticket, they simply:
- Enter the license plate number of the violating vehicle
- Select the type of violation (expired meter, no parking zone, etc.)
- Take photos of the vehicle and violation for evidence
- The app automatically records the exact GPS location and time
- They review the information to make sure it's correct before submitting

This takes only 2-3 minutes instead of 10-15 minutes with paper tickets.

**Tracking Daily Activity**
The app automatically records everything the officer does during their shift - every citation issued, every scan performed, every location visited. This creates a complete record that can be reviewed later. Officers can see how many citations they issued, which violations are most common, and which areas they covered.

**Viewing Performance Reports**
Officers can see charts and graphs showing their performance - things like "How many citations did I issue this week?" or "What time of day do I issue the most tickets?" These reports help officers understand their work patterns and improve their efficiency.

**Processing Payments**
The app supports two different payment methods:
- **Pay-by-Plate**: Someone can pay a parking fine just by entering the license plate number without needing the citation number
- **Pay-by-Space**: For parking meter violations, the payment can be tied to the specific parking space or meter number

**Working Without Internet**
One of the most important features is that the app works even when there's no internet connection. Officers working in areas with poor signal (like parking garages or remote locations) can:
- Still create citations and take photos
- Store everything locally on their phone
- When they return to an area with internet, the app automatically sends all the citations to the server
- Officers don't need to do anything - it's automatic

This means officers never lose critical work due to connectivity issues.

---

## Technology Stack

### What Technology Does This App Use?

The app is built using modern, reliable technology that works on both iPhones and Android phones. Here's what that means in simple terms:

**The App Framework**
The app is built with a framework called Flutter, which is like a set of building blocks that allows the same app to run on both iPhones and Android devices without having to build two completely different apps. This saves time and money and ensures that both iPhone and Android users get the same experience.

**How It Looks (User Interface)**
The app is designed to be responsive, which means it automatically adjusts how it looks based on the phone size. Whether an officer is using a small phone, a large phone, or even a tablet, the app automatically arranges the buttons and screens to fit nicely. Icons and images stay sharp and clear on any screen size.

**How It Stores Information**
The app uses two different systems to store information on the officer's phone:
- **Quick Storage**: For important items that need fast access (like login passwords, preferences) - this works like a simple notebook where you can quickly find what you wrote
- **Database Storage**: For larger amounts of information (like all citations created, templates, dropdown options) - this works more like a filing cabinet where you can search through thousands of documents quickly

**Location Information**
The app can automatically know where the officer is standing using GPS (the same technology that powers Google Maps). This location is automatically added to every citation for accuracy and evidence.

**Communication with Servers**
The app automatically handles sending information to the company's servers, even if the internet connection is slow or keeps dropping. If the connection fails, the app keeps trying until it succeeds. This is all automatic - officers don't need to do anything.

**Photos and Images**
Officers can take photos directly with the camera or choose existing photos from their phone's gallery. The app automatically compresses these photos to save data and make uploads faster.

**Charts and Statistics**
The app uses professional charting libraries to display statistics as colorful graphs and charts that are easy to understand at a glance.

**Security and Protection**
The app includes bot protection (reCAPTCHA) on the login screen to prevent hackers from trying to break in. This is the same protection used by banks and large tech companies.

**Why This Technology Matters**
This stack was chosen specifically because:
- It's reliable and used by major companies worldwide
- It works the same way on both iPhones and Android
- It performs well even on older phones
- It uses less data and battery power
- It's secure and protects officer information

---

## How the Application Works

### Overall Architecture - In Plain English

The app is organized like a well-run organization with different departments, each responsible for specific tasks:

**The User Interface (What Officers See)**
This is the visible part of the app - the screens, buttons, forms, and messages. It's designed to be intuitive and easy to use, so officers can focus on their job, not figuring out how to use the app. Think of this like the front desk of an office - it's what customers (officers) interact with.

**The Business Logic (The Decision Maker)**
This layer makes decisions and processes information. For example:
- If an officer tries to log in with a wrong password, this layer says "no, that's not correct"
- If an officer enters invalid information, this layer catches it before sending to the server
- If an officer is offline, this layer decides to save the citation locally instead of sending it immediately
Think of this like the manager's office - it makes the decisions.

**The Services Layer (The Worker Bees)**
This layer handles specific technical tasks:
- Talking to the company's servers to send and receive information
- Managing the officer's GPS location and maps
- Storing information on the phone's local storage
- Checking if the phone has internet connection
Think of this like different departments in a company - each handles one specific job.

**The Data Layer (The Filing System)**
This layer contains all the structured information the app uses - officer profiles, citation records, form templates, analytics data, stored locally on the phone.
Think of this like the company's filing cabinets and databases.

### How Information Flows

When an officer uses the app, information flows like this:

```
Officer enters information on screen
    ↓
App processes and validates the information
    ↓
App decides if it needs to send to server or save locally
    ↓
If online: sends to server
If offline: saves locally and sends later
    ↓
Server processes the information and sends a response back
    ↓
App stores the response and updates what the officer sees on screen
```

Each step is designed to be as fast as possible so officers aren't waiting around.

### Why This Design Matters

This layered approach means:
- **Easy to Fix**: If there's a problem, developers know exactly which layer to look in
- **Easy to Update**: New features can be added without breaking existing ones
- **Easy to Test**: Each layer can be tested independently to make sure it works
- **Better Performance**: Each layer is optimized for its specific job


---

## Folder Structure

```
lib/
├── main.dart                          # Application entry point
│
├── app/                               # Core application layer
│   ├── core/                          # Core functionality
│   │   ├── api/
│   │   │   └── api_client.dart       # HTTP client with interceptors
│   │   │
│   │   ├── bindings/
│   │   │   └── app_binding.dart      # Global bindings setup
│   │   │
│   │   ├── constants/
│   │   │   ├── api_endpoints.dart    # API endpoint URLs
│   │   │   ├── app_icons.dart        # Icon asset paths
│   │   │   ├── app_images.dart       # Image asset paths
│   │   │   ├── app_sizes.dart        # Spacing, sizing constants
│   │   │   ├── field_types.dart      # Form field type constants
│   │   │   ├── template_types.dart   # Template type definitions
│   │   │   ├── storage_keys.dart     # Local storage key constants
│   │   │   ├── enums.dart            # Data set types enumeration
│   │   │   └── consts.dart           # Application-wide constants
│   │   │
│   │   ├── controllers/
│   │   │   ├── app_controller.dart           # Global app state
│   │   │   ├── auth_controller.dart          # Authentication logic
│   │   │   ├── base_controller.dart          # Base controller class
│   │   │   └── [feature]_controller.dart     # Feature-specific controllers
│   │   │
│   │   ├── di/
│   │   │   └── di.dart                # Dependency injection setup
│   │   │
│   │   ├── exceptions/
│   │   │   ├── api_exception.dart            # API error handling
│   │   │   ├── exception_handler.dart        # Global exception handling
│   │   │   └── network_exception.dart        # Network error handling
│   │   │
│   │   ├── localization/
│   │   │   ├── app_locales.dart              # Supported locales definition
│   │   │   ├── app_translations.dart         # Translation provider
│   │   │   ├── local_keys.dart               # Translation key constants
│   │   │   └── translations/                 # Translation JSON files
│   │   │
│   │   ├── models/
│   │   │   ├── template_model.dart           # Dynamic form templates
│   │   │   ├── citation_similarity_request_model.dart
│   │   │   ├── drop_down_model.dart          # Dropdown data models
│   │   │   ├── offline_request_model.dart    # Offline queue models
│   │   │   └── template_model.g.dart         # Generated code
│   │   │
│   │   ├── repositories/
│   │   │   └── event_logging_repository.dart # Event logging service
│   │   │
│   │   ├── routes/
│   │   │   ├── app_routes.dart               # Route constants
│   │   │   └── app_pages.dart                # Route definitions with bindings
│   │   │
│   │   ├── services/
│   │   │   ├── auth_service.dart             # Authentication state service
│   │   │   ├── brand_config_service.dart     # Brand configuration management
│   │   │   ├── local_storage_service.dart    # Hive database service
│   │   │   ├── location_service.dart         # GPS location service
│   │   │   └── offline_sync_service.dart     # Offline queue sync service
│   │   │
│   │   └── theme/
│   │       ├── app_colors.dart       # Color palette definitions
│   │       ├── app_text_styles.dart  # Text style definitions
│   │       └── app_theme.dart        # Global theme configuration
│   │
│   └── shared/                         # Shared utilities and widgets
│       ├── controller/
│       │   ├── bottom_nav_bar_controller.dart
│       │   ├── drawer_controller.dart
│       │   └── loader_controller.dart
│       │
│       ├── utils/
│       │   ├── date_utils.dart         # Date and time utilities
│       │   ├── snackbar_utils.dart     # Toast notification utilities
│       │   └── extensions/             # Dart extension methods
│       │
│       └── widgets/                    # Reusable UI components
│           ├── app_scaffold.dart
│           ├── custom_appbar.dart
│           ├── custom_text_field.dart
│           ├── custom_drop_down.dart
│           ├── custom_dialog.dart
│           ├── custom_bottom_nav_bar.dart
│           ├── custom_tab_bar.dart
│           ├── app_divider.dart
│           ├── color_button.dart
│           ├── outline_button.dart
│           ├── loader.dart
│           ├── no_data_layout.dart
│           └── [other shared widgets]
│
└── features/                           # Feature modules (MVCS)
    │
    ├── brand/                          # Brand selection feature
    │   ├── bindings/
    │   │   └── brand_binding.dart
    │   ├── controller/
    │   │   └── brand_controller.dart
    │   ├── data/
    │   │   ├── brand_option.dart       # Brand model
    │   │   └── [repositories]
    │   └── ui/
    │       └── brand_page.dart
    │
    ├── splash/                         # Splash screen feature
    │   ├── splash_binding.dart
    │   ├── splash_controller.dart
    │   └── splash_page.dart
    │
    ├── login/                          # Authentication feature
    │   ├── login_binding.dart
    │   ├── controller/
    │   │   └── login_controller.dart
    │   ├── data/
    │   │   ├── login_repository.dart
    │   │   └── models/
    │   └── ui/
    │       ├── screens/
    │       │   ├── login_page.dart
    │       │   └── forgot_password_page.dart
    │       └── widgets/
    │
    ├── home/                           # Home dashboard feature
    │   ├── home_binding.dart
    │   ├── controller/
    │   │   └── home_controller.dart
    │   ├── data/
    │   │   ├── repository/
    │   │   │   ├── home_repository.dart           # API repository
    │   │   │   └── home_storage_repository.dart   # Local storage
    │   │   └── models/
    │   │       ├── welcome_model.dart
    │   │       └── update_officer_quest_model.dart
    │   └── ui/
    │       ├── screens/
    │       │   └── home_page.dart
    │       └── widgets/
    │
    ├── ticketing/                      # Citation issuance feature
    │   ├── bindings/
    │   │   ├── ticket_issue_bindings.dart
    │   │   ├── pay_by_plate_bindings.dart
    │   │   └── pay_by_space_bindings.dart
    │   ├── controller/
    │   │   ├── ticket_issue_controller.dart              # Main ticketing logic
    │   │   ├── ticket_issue_preview_controller.dart      # Preview & confirmation
    │   │   ├── pay_by_plate_controller.dart
    │   │   └── pay_by_space_controller.dart
    │   ├── data/
    │   │   ├── repository/
    │   │   │   └── ticket_issue_repository.dart
    │   │   └── models/
    │   │       ├── ticket_creation_request.dart
    │   │       ├── citation_similarity_request.dart
    │   │       ├── bulk_upload_response.dart
    │   │       └── [other ticket models]
    │   └── ui/
    │       ├── screens/
    │       │   ├── ticketing_page.dart                   # Landing page
    │       │   ├── ticket_issue_screen.dart              # Form entry
    │       │   ├── ticket_issue_preview_page.dart        # Preview & confirm
    │       │   ├── pay_by_plate_screen.dart
    │       │   └── pay_by_space_screen.dart
    │       └── widgets/
    │
    ├── municipal_citation/             # Municipal citation handling
    │   ├── bindings/
    │   │   └── municipal_citation_bindings.dart
    │   ├── controller/
    │   │   └── municipal_citation_controller.dart
    │   ├── data/
    │   │   ├── repository/
    │   │   └── models/
    │   └── ui/
    │       ├── screens/
    │       │   └── municipal_citation_screen.dart
    │       └── widgets/
    │
    ├── lookup/                         # Citation lookup feature
    │   ├── bindings/
    │   │   └── citation_result_binding.dart
    │   ├── controller/
    │   └── ui/
    │       └── screens/
    │           └── citation_result_page.dart
    │
    ├── scan/                           # License plate scanning (LPR)
    │   ├── bindings/
    │   │   └── scan_bindings.dart
    │   ├── controller/
    │   ├── data/
    │   └── ui/
    │       ├── screens/
    │       │   └── manual_data_entry_page.dart
    │       └── widgets/
    │
    ├── my_activity/                    # Activity analytics & reporting
    │   ├── bindings/
    │   │   ├── graph_view_bindings.dart
    │   │   ├── daily_summary_bindings.dart
    │   │   └── lpr_hits_bindings.dart
    │   ├── controller/
    │   │   ├── graph_view_controller.dart                # Analytics & charts
    │   │   ├── daily_summary_controller.dart             # Daily statistics
    │   │   └── lpr_hits_controller.dart                  # LPR details
    │   ├── data/
    │   │   ├── repository/
    │   │   │   └── graph_view_repositories.dart
    │   │   └── models/
    │   │       ├── activity_log_model.dart
    │   │       ├── chart_data_model.dart
    │   │       ├── location_model.dart
    │   │       ├── violation_data_model.dart
    │   │       └── [other analytics models]
    │   └── ui/
    │       ├── screens/
    │       │   ├── my_activity_page.dart
    │       │   ├── graph_view/
    │       │   │   └── graph_view.dart                   # Charts UI
    │       │   ├── daily_summary/
    │       │   │   └── daily_summary_screen.dart
    │       │   └── lpr_hits/
    │       │       └── lpr_hits_screen.dart
    │       └── widgets/
    │
    ├── reports/                        # Reporting feature
    │   └── ui/
    │       └── screens/
    │           └── reports.dart
    │
    └── settings/                       # Application settings
        └── ui/
            └── screens/
                └── settings.dart

assets/                                 # Application assets
├── config/
│   └── brand_options.json              # Brand configuration file
├── fonts/
│   ├── sf_pro_bold.otf
│   ├── sf_pro_medium.otf
│   └── sf_pro_regular.otf
├── icons/                              # SVG icons
├── images/                             # PNG/JPG images
└── [localization files]
```

---

---

## Core Services

### What Are Services?

Think of services as the "worker bees" of the app. While the screens and buttons are what officers see, services are constantly working behind the scenes to handle important tasks. Each service has one specific job, and it does it well.

### 1. API Client Service (The Messenger)

**File Location**: [lib/app/core/api/api_client.dart](lib/app/core/api/api_client.dart)

**What It Does**:
This service is like a messenger who delivers mail between the officer's phone and the company's servers. It handles all communication back and forth.

**Specific Responsibilities**:

- **Automatic Security Pass**: Every time the messenger delivers a message to the server, it attaches an electronic "pass" (called an authentication token) that proves the officer is allowed to send information. This is like showing an ID badge.

- **Record Keeping**: The messenger keeps a log of every message sent and received. If something goes wrong, developers can look at this log to see what happened. It's like recording all incoming and outgoing mail.

- **Handling Network Problems**: If the internet connection is bad or drops, the messenger doesn't just give up. Instead, it waits and tries again. If a message takes too long to send (more than 30 seconds), the messenger stops waiting and tells the officer something went wrong.

- **Session Management**: If the officer's security pass expires (meaning they've been logged in for too long), the messenger automatically logs them out to protect their account.

- **Flexible Communication**: The messenger can:
  - Fetch information from the server (GET)
  - Send new information to the server (POST)
  - Update existing information (PATCH)
  - Upload photos and files

**Why This Matters**: Without this service, every feature would need to figure out how to send information to the server. By centralizing it here, the app is more secure and reliable.

---

### 2. Authentication Service (The Security Guard)

**File Location**: [lib/app/core/services/auth_service.dart](lib/app/core/services/auth_service.dart)

**What It Does**:
This service is like a security guard at the front of a building. It keeps track of whether an officer is logged in and has permission to use the app.

**Specific Responsibilities**:

- **Token Storage**: When an officer logs in, they receive a special "key" (called a token) that proves they've authenticated. This service securely stores that key so the officer doesn't need to log in every time they open the app.

- **Session Information**: The guard keeps track of:
  - When the officer logged in (current login time)
  - When they last logged in (previous login time)
  - Their ID, name, badge number, and other profile info

- **Login Status**: The service always knows if the officer is currently logged in or logged out.

- **Cleanup**: When an officer logs out or their session expires, this service immediately clears all their information to protect privacy.

**Real-World Example**: 
Imagine the officer logs in at 8 AM. The service gives them a special "visitor pass" that says they're allowed to use the app. Throughout the day, this pass is used to prove they're authorized. At 5 PM when they log out, the pass is destroyed and they can't use the app anymore until they log in again.

**Why This Matters**: This centralized security approach means every feature doesn't have to worry about authentication - it's all handled in one place.

---

### 3. Location Service (GPS Tracker)

**File Location**: [lib/app/core/services/location_service.dart](lib/app/core/services/location_service.dart)

**What It Does**:
This service handles everything related to GPS and location. It's like having a personal GPS device that always knows where the officer is standing.

**Specific Responsibilities**:

- **Asking Permission**: When the app first runs, it asks the officer for permission to use their phone's GPS. This is required by all smartphones for privacy protection. The officer can always change this in their phone settings.

- **Getting Coordinates**: Once permission is granted, this service gets the officer's exact latitude and longitude coordinates (like 40.7128° N, 74.0060° W). This is accurate to within a few feet.

- **Converting to Addresses**: The service can convert coordinates into readable addresses (like "123 Main Street, Downtown Area"). 

- **Caching**: To save battery and data, the service stores the most recent location information so it doesn't have to keep pinging the GPS if the officer hasn't moved much.

- **Error Handling**: If GPS isn't available in an area (like indoors or underground), the service handles it gracefully and tells the officer.

**Real-World Usage**:
When an officer creates a citation, this service automatically includes the exact GPS location where the citation was issued. This proves exactly where the violation was issued and provides evidence that can be used in court if needed.

**Why This Matters**: Automatic GPS coordinates add credibility to citations and prevent disputes about location.

---

### 4. Local Storage Service (The Filing Cabinet)

**File Location**: [lib/app/core/services/local_storage_service.dart](lib/app/core/services/local_storage_service.dart)

**What It Does**:
This service manages everything stored locally on the officer's phone. Think of it as a private filing cabinet that keeps information safe and organized.

**Two Different Storage Systems**:

**1. Quick Storage (GetStorage) - The Notepad**

Used for: Small, simple information that needs fast access
- Login tokens (security keys)
- Agency selection
- Officer preferences and settings
- Last time information was accessed

Think of this like a notepad on your desk for quick reference.

**Characteristics**:
- Very fast to access (like looking at a post-it note)
- Information survives even if the phone is turned off
- Not secure for sensitive data (like someone seeing your notebook)
- Good for temporary settings

**2. Database Storage (Hive) - The Filing Cabinet**

Used for: Large amounts of complex information that might need to be searched
- Offline citations created while without internet
- Cached form templates for offline use
- Citation history from previous weeks/months
- Dropdown options (all available violation types, vehicle makes, etc.)
- Activity logs and records

Think of this like an actual filing cabinet with organized folders and drawers.

**Characteristics**:
- Fast even with thousands of records
- Secure storage
- Can search through the data
- Information is organized by type
- Survives phone restarts

**Why Two Systems?**

Think of it like a restaurant:
- **Quick Storage** = The waiter's notepad for current orders (fast access)
- **Database Storage** = The restaurant's inventory system for managing thousands of items

Using the right storage tool for each job makes the app faster and more efficient.

**Examples**:
- **Quick Storage Use**: Officer logs in, their token is stored here for fast retrieval every time they do something
- **Database Storage Use**: Officer creates 50 citations throughout the day while offline - all stored here safely, then sent to server when internet returns

---

### 5. Brand Configuration Service (The Agency Manager)

**File Location**: [lib/app/core/services/brand_config_service.dart](lib/app/core/services/brand_config_service.dart)

**What It Does**:
This service is like a configuration manager that handles the fact that this app works for many different parking agencies. Each agency has different rules, servers, and requirements.

**Specific Responsibilities**:

- **Brand Loading**: When the app starts, this service reads a configuration file that lists all available parking agencies (like "Downtown Parking Authority," "Airport Parking," "Shopping Mall Enforcement," etc.)

- **Brand Selection**: Officers select which agency they work for the first time they use the app

- **API Configuration**: Once an agency is selected, this service sets the correct server address for that agency. Think of it like switching between different company mail rooms - each has a different address.

- **Persistent Memory**: The service remembers the selected agency so the officer doesn't need to select it every time they open the app

- **Brand Information**: The service stores and provides:
  - Agency name and logo
  - Agency identifier
  - Specific form templates for that agency
  - Server location for that agency

**Real-World Example**:
The app can be used by Officer A in Downtown Area (using Server A) and Officer B at the Airport (using Server B) simultaneously. Both are using the same app, but the brand service makes sure each is connected to their correct server and sees their agency's information.

**Currently Supports**: 40+ different parking enforcement agencies and cities worldwide

**Why This Matters**: This design allows one app to serve many different agencies, reducing costs and ensuring consistent experience across all organizations.

---

### 6. Offline Sync Service (The Smart Backup)

**File Location**: [lib/app/core/services/offline_sync_service.dart](lib/app/core/services/offline_sync_service.dart)

**What It Does**:
This service is the "intelligent backup system" that makes the app truly remarkable - it allows officers to work normally even without internet, and automatically syncs when connection is restored.

**Specific Responsibilities**:

- **Connection Monitoring**: This service continuously checks if the phone has internet. It's like having someone constantly checking if the power is back on.

- **Offline Queue**: When an officer creates a citation while offline, this service saves it locally in a safe place. It's like putting completed work in a tray labeled "To Be Submitted."

- **Automatic Sync**: The moment internet is restored, this service wakes up and starts sending all the waiting citations to the server. Officers don't need to do anything - it's automatic.

- **Smart Retry Logic**: If sending fails (maybe the connection dropped again), the service keeps trying until it succeeds. It doesn't give up.

- **Duplicate Prevention**: The service makes sure the same citation isn't submitted twice. It's like checking your sent mail to make sure you didn't send the same letter twice.

- **Order Preservation**: Citations are sent in the exact order they were created (First In, First Out), just like a queue at a store.

**How It Works in Practice**:

```
Officer's Workday:
9:00 AM - Officer goes into a parking garage (no internet)
9:05 AM - Creates citation #1 (saved locally)
9:10 AM - Creates citation #2 (saved locally)
9:15 AM - Creates citation #3 (saved locally)
10:00 AM - Officer leaves garage and gets back online
10:01 AM - Service automatically detects internet and starts sending
10:02 AM - Citation #1 sent successfully ✓
10:03 AM - Citation #2 sent successfully ✓
10:04 AM - Citation #3 sent successfully ✓
Officer sees confirmation that all work was synced
```

**Real-World Impact**:
Without this service, officers would:
- Lose all their work if they went offline
- Have to remember which citations they created and submit them manually
- Need an internet connection to do their job

With this service, officers can work anywhere, anytime, and the app handles everything.

**Why This Matters**: This transforms the app from "must have internet to work" to "works great without internet and syncs when possible" - a huge improvement in functionality and reliability for field workers.

## Application Features with API Details

### Feature 1: Brand Selection

**Implementation**: Fully completed with static configuration

**How It Works**:
1. Officer opens app for the first time
2. Brand selection screen displays with input fields
3. Officer enters Site ID, Customer Name, and Base URL
4. Selection is validated and saved locally
5. App uses the configured base URL for all subsequent API calls

**File Locations**:
- Controller: [lib/features/brand/controller/brand_controller.dart](lib/features/brand/controller/brand_controller.dart)
- UI: [lib/features/brand/ui/brand_page.dart](lib/features/brand/ui/brand_page.dart)
- Data Model: [lib/features/brand/data/brand_option.dart](lib/features/brand/data/brand_option.dart)

**User Flow**:
```
App Start → Brand Selection Screen → Enter Site Details → Save → Splash Screen
```

---

### Feature 2: Officer Authentication

**Implementation**: Completed with API integration and design

**API Endpoint**: `auth/site_officer_login`
- **Method**: POST
- **Authentication**: Not required (first-time login)
- **Base URL**: https://devapi.parkloyalty.com/

**Request Body Structure**:
```
{
  "site_officer_user_name": "officer_username",
  "site_officer_password": "officer_password",
  "site_id": "agency_site_id"
}
```

**Response Structure**:
```
{
  "status": true,
  "response": "auth_token_string",
  "metadata": {
    "last_login": "2026-02-16T10:30:00Z",
    "current_login": "2026-02-16T11:45:00Z"
  }
}
```

**Error Response**:
```
{
  "status": false,
  "response": "Invalid credentials or user not found"
}
```

**Initial Dataset Loading** (Parallel): `informatics/get_dataset_no_token`
- **Method**: POST
- **Purpose**: Fetch shift lists, hearing times, and other dropdown data
- **Request**: 
```
{
  "type": "shift_list|hearing_time|other",
  "shard": "shard_identifier",
  "site_id": "agency_site_id"
}
```

**File Locations**:
- Controller: [lib/features/login/controller/login_controller.dart](lib/features/login/controller/login_controller.dart)
- Repository: [lib/features/login/data/login_repository.dart](lib/features/login/data/login_repository.dart)
- Response Model: [lib/features/login/data/login_response.dart](lib/features/login/data/login_response.dart)
- UI:
  - Login page: [lib/features/login/ui/screens/login_page.dart](lib/features/login/ui/screens/login_page.dart)
  - Password recovery: [lib/features/login/ui/screens/forgot_password_page.dart](lib/features/login/ui/screens/forgot_password_page.dart)
  - Recaptcha: [lib/features/login/ui/screens/recaptcha_page.dart](lib/features/login/ui/screens/recaptcha_page.dart)

**User Flow**:
```
Login Screen → Validate Form → POST to Server → Token Received → Store Token → Redirect to Home
```

**Authentication Storage**:
- Token stored in: GetStorage with key defined in [lib/app/core/constants/storage_keys.dart](lib/app/core/constants/storage_keys.dart)
- Service: [lib/app/core/services/auth_service.dart](lib/app/core/services/auth_service.dart)

---

### Feature 3: Home Dashboard

**Implementation**: Completed with API integration

**Primary APIs Used**:

**1. Welcome/Dashboard Data**: `screens/welcome_page`
- **Method**: GET
- **Purpose**: Fetch officer's dashboard information
- **Response Includes**: Officer name, badge, shift info, citation targets, parking occupancy

**2. Form Templates**: `templates/mobile/primary_template`
- **Method**: GET with query parameter: `?template_type=citation`
- **Purpose**: Fetch dynamic form templates for citation creation
- **Cached**: Stored locally for offline availability

**3. Citation Book Issuance**: `citations/issue_citation_book`
- **Method**: POST
- **Request**:
```
{
  "device_id": "unique_device_identifier"
}
```
- **Response**: Citation book number and starting ticket range

**4. Officer Profile Update**: `l2-onboarder/update_site_officer`
- **Method**: POST
- **Purpose**: Update officer profile information
- **Request Includes**: Badge number, shift assignment, location

**5. Dropdown Data**: `informatics/get_dataset`
- **Method**: POST
- **Purpose**: Fetch violation types, vehicle makes, street names, zones
- **Request**:
```
{
  "type": "violation_types|vehicle_makes|streets|zones",
  "shard": "shard_identifier"
}
```
- **Cached**: Stored locally to reduce API calls

**File Locations**:
- Controller: [lib/features/home/controller/home_controller.dart](lib/features/home/controller/home_controller.dart)
- Repository: [lib/features/home/data/repository/home_repository.dart](lib/features/home/data/repository/home_repository.dart)
- Storage: [lib/features/home/data/repository/home_storage_repository.dart](lib/features/home/data/repository/home_storage_repository.dart)
- UI: [lib/features/home/ui/screens/home_page.dart](lib/features/home/ui/screens/home_page.dart)
- Models: [lib/features/home/data/models/](lib/features/home/data/models/)
  - welcome_model.dart
  - update_officer_quest_model.dart
  - citation_book.dart

**Data Flow**:
```
App Launch → Fetch Welcome Data → Load Templates → Download Dropdown Data → Cache Locally → Display Dashboard
```

---

### Feature 4: Citation Issuance (Ticketing)

**Implementation**: 80% complete with core functionality and refinements in progress

**Primary APIs Used**:

**1. Fetch Citation Template**: `templates/mobile/primary_template`
- **Method**: GET with query: `?template_type=citation`
- **Purpose**: Get the dynamic form structure for citation fields
- **Response Includes**: Field names, types, validation rules, dropdown options
- **Caching**: Stored locally for offline citation creation

**2. Citation Similarity Check**: `citations-issuer/citation_similarity_check`
- **Method**: POST
- **Purpose**: Check if a similar citation was recently issued (duplicate prevention)
- **Request**:
```
{
  "lp_number": "license_plate",
  "zone": "parking_zone",
  "code": "violation_code",
  "description": "violation_description",
  "block": "city_block",
  "street": "street_name",
  "side": "side_of_street",
  "state": "state_code",
  "ticket_no": "unique_ticket_number"
}
```
- **Response**:
```
{
  "is_similar": true/false,
  "previous_citation": {...} or null
}
```

**3. Upload Citation Photos**: `static_file/bulk_upload`
- **Method**: POST (Multipart/form-data)
- **Purpose**: Upload evidence photos for the citation
- **Request**:
```
{
  "upload_type": "CitationImages",
  "type": "citation",
  "files": [binary_image_data...],
  "data": ["filename1.jpg", "filename2.jpg"]
}
```
- **Response Includes**: Image URLs for the uploaded photos

**4. Citation Submission**: Not directly in API endpoints list, but handled through structured data

**File Locations**:
- Controller:
  - Main: [lib/features/ticketing/controller/ticket_issue_controller.dart](lib/features/ticketing/controller/ticket_issue_controller.dart)
  - Preview: [lib/features/ticketing/controller/ticket_issue_preview_controller.dart](lib/features/ticketing/controller/ticket_issue_preview_controller.dart)
  - Pay-by-plate: [lib/features/ticketing/controller/pay_by_plate_controller.dart](lib/features/ticketing/controller/pay_by_plate_controller.dart)
  - Pay-by-space: [lib/features/ticketing/controller/pay_by_space_controller.dart](lib/features/ticketing/controller/pay_by_space_controller.dart)

- Repository: [lib/features/ticketing/data/repository/ticket_issue_repository.dart](lib/features/ticketing/data/repository/ticket_issue_repository.dart)

- Models: [lib/features/ticketing/data/models/](lib/features/ticketing/data/models/)
  - ticket_creation_request.dart (Main citation data structure)
  - citation_similarity_request.dart
  - bulk_upload_response.dart

- UI Screens:
  - Landing page: [lib/features/ticketing/ui/screens/ticketing_page.dart](lib/features/ticketing/ui/screens/ticketing_page.dart)
  - Form entry: [lib/features/ticketing/ui/screens/ticket_issue_screen.dart](lib/features/ticketing/ui/screens/ticket_issue_screen.dart)
  - Preview: [lib/features/ticketing/ui/screens/ticket_issue_preview_page.dart](lib/features/ticketing/ui/screens/ticket_issue_preview_page.dart)
  - Pay-by-plate: [lib/features/ticketing/ui/screens/pay_by_plate_screen.dart](lib/features/ticketing/ui/screens/pay_by_plate_screen.dart)
  - Pay-by-space: [lib/features/ticketing/ui/screens/pay_by_space_screen.dart](lib/features/ticketing/ui/screens/pay_by_space_screen.dart)

**Citation Creation Flow**:
```
Select Ticketing → Load Template → Fill Form → Capture Photos → Preview Data 
→ Check Similarity → Upload Photos → Submit Citation → Confirmation
```

**Offline Handling**:
- If offline, citations are saved to local database (Hive)
- Queued in: [lib/app/core/models/offline_request_model.dart](lib/app/core/models/offline_request_model.dart)
- Automatically synced when connection is restored

---

### Feature 5: Activity Analytics & Reporting

**Implementation**: 75% complete with multiple visualization types

**Primary APIs Used**:

**1. Bar Chart Data**: `analytics/mobile/get_counts`
- **Method**: GET with query: `?shift=shift_id`
- **Purpose**: Fetch count metrics (scans, tickets, permits, drive-offs)
- **Response**: Numeric values for each metric type

**2. Line Chart Data**: `analytics/mobile/get_array_counts`
- **Method**: GET with query: `?shift=shift_id&timeline=daily|weekly|monthly`
- **Purpose**: Fetch historical trends for line chart visualization
- **Response**: Array of data points with timestamps

**3. Violation Breakdown**: `analytics/mobile/get_violation_counts_by_officer`
- **Method**: GET with query: `?shift=shift_id`
- **Purpose**: Get violation count statistics broken down by violation type
- **Response**: Violation categories with their counts

**4. Activity Log**: `analytics/mobile/get_activity_updates_by_officer`
- **Method**: GET with query: `?shift=shift_id`
- **Purpose**: Fetch timestamped activity records
- **Response**: List of activities with timestamps and details

**5. Location Data**: `location-update/updates`
- **Method**: GET with query: `?shift=shift_id`
- **Purpose**: Get enforcement location history
- **Response**: Map data with officer's movement/coverage areas

**6. Daily Summary**: `analytics/officer_daily_summary`
- **Method**: GET
- **Purpose**: Get consolidated daily statistics
- **Response**: Total citations, fines, hours worked, locations visited

**7. Pay-by-Plate Analytics**: `analytics/mobile/pay_by_plate`
- **Method**: GET
- **Purpose**: Analytics specific to pay-by-plate payment system
- **Response**: Pay-by-plate transaction statistics

**File Locations**:
- Controller:
  - Graph view: [lib/features/my_activity/controller/graph_view_controller.dart](lib/features/my_activity/controller/graph_view_controller.dart)
  - Daily summary: [lib/features/my_activity/controller/daily_summary_controller.dart](lib/features/my_activity/controller/daily_summary_controller.dart)
  - LPR hits: [lib/features/my_activity/controller/lpr_hits_controller.dart](lib/features/my_activity/controller/lpr_hits_controller.dart)

- Repository: [lib/features/my_activity/data/repositories/](lib/features/my_activity/data/repositories/)
  - graph_view_repositories.dart
  - daily_summary_repository.dart

- Models: [lib/features/my_activity/data/models/](lib/features/my_activity/data/models/)
  - activity_log_model.dart
  - chart_data_model.dart
  - location_model.dart
  - violation_data_model.dart

- UI Screens:
  - Main activity page: [lib/features/my_activity/ui/screens/my_activity_page.dart](lib/features/my_activity/ui/screens/my_activity_page.dart)
  - Charts: [lib/features/my_activity/ui/screens/graph_view/graph_view.dart](lib/features/my_activity/ui/screens/graph_view/graph_view.dart)
  - Daily summary: [lib/features/my_activity/ui/screens/daily_summary/daily_summary_screen.dart](lib/features/my_activity/ui/screens/daily_summary/daily_summary_screen.dart)
  - LPR details: [lib/features/my_activity/ui/screens/lpr_hits/lpr_hits_screen.dart](lib/features/my_activity/ui/screens/lpr_hits/lpr_hits_screen.dart)

**Analytics Flow**:
```
Open My Activity → Fetch Analytics Data → Load Charts → Display Trends 
→ Show Activity Log → Navigate to Details
```

---

### Feature 6: Citation Lookup

**Implementation**: 70% complete with search and results

**Primary APIs Used**:

**1. Get Citations**: `citations-issuer/ticket`
- **Method**: GET
- **Purpose**: Retrieve list of citations based on search criteria
- **Query Params**: license plate, date range, status, etc.

**2. Parking Timing Records**: `parking-timing/mark`
- **Method**: GET/POST
- **Purpose**: Get or update parking timing enforcement records

**3. LPR Data**: `informatics/get_data_from_lpr`
- **Method**: GET
- **Purpose**: Retrieve license plate recognition scan data
- **Response**: Scanned plates, match status, vehicle information

**File Locations**:
- Controller: [lib/features/lookup/controller/](lib/features/lookup/controller/)
- Repository: [lib/features/lookup/data/repository/](lib/features/lookup/data/repository/)
- Models: [lib/features/lookup/data/models/](lib/features/lookup/data/models/)
  - citation.dart
  - request_model.dart

- UI Screens:
  - Results page: [lib/features/lookup/ui/screens/citation_result_page.dart](lib/features/lookup/ui/screens/citation_result_page.dart)
  - Citation search: [lib/features/lookup/ui/screens/citations_page.dart](lib/features/lookup/ui/screens/citations_page.dart)
  - Timing records: [lib/features/lookup/ui/screens/timing_records_page.dart](lib/features/lookup/ui/screens/timing_records_page.dart)

**Lookup Flow**:
```
Enter Search Criteria → API Query → Display Results → View Citation Details
```

---

## Brand & Configuration System

### Multi-Agency Support

The app is designed to support 40+ different parking enforcement agencies. Each agency can have:
- Different server addresses (APIs hosted in different locations)
- Unique agency identifiers (site_id)
- Agency-specific branding and logos
- Custom form templates and citation types
- Different violation codes and fee structures

### Brand Selection Flow at Startup

**When**: First app launch or when brand data is lost/cleared

**Steps**:
1. Officer selects their parking enforcement agency
2. Officer enters Site ID, Customer Name, and Base URL
3. App validates the configuration
4. App stores the selection locally
5. Splash screen initializes with selected brand
6. All subsequent API calls use the configured base URL

**Configuration Storage**: [lib/app/core/services/brand_config_service.dart](lib/app/core/services/brand_config_service.dart)

**File Paths for Brand Assets**:
- Logos: [assets/icons/](assets/icons/)
- Images: [assets/images/](assets/images/)
- Configuration: [assets/config/brand_options.json](assets/config/brand_options.json)

### Runtime Brand Usage

Once a brand is selected:
- All API calls use `BaseUrl` + endpoint path
- Form templates are specific to that agency
- Dropdown data (violation types, etc.) are agency-specific
- Analytics and reporting reflect only that agency's data
- Officer profile is linked to the selected agency

---

## Authentication & API Integration

### Authentication Architecture

**Flow Diagram**:
```
1. Officer opens app
2. Brand is selected/retrieved
3. Officer navigates to login
4. Credentials sent to: POST /auth/site_officer_login
5. Server returns: status, token, and metadata
6. Token stored in AuthService and GetStorage
7. Token automatically injected in all future API requests
8. On 401 (unauthorized) response: auto-logout triggered
```

### Token Storage & Security

**Storage Details**:
- **Storage Type**: GetStorage (on-device encrypted key-value store)
- **Storage Key**: Defined in [lib/app/core/constants/storage_keys.dart](lib/app/core/constants/storage_keys.dart)
- **Service**: [lib/app/core/services/auth_service.dart](lib/app/core/services/auth_service.dart)

**Token Usage**:
- Every API request automatically includes: `Authorization: Bearer {token}`
- Token added via API client interceptor in [lib/app/core/api/api_client.dart](lib/app/core/api/api_client.dart)
- No manual token handling needed by features

### API Request/Response Flow

**Request Lifecycle**:
```
Feature → Controller → Repository → API Client → HTTP Request
                                         ↓
                              Add token to header
                              Log request details
                              Set timeout (30s)
                                         ↓
                                    POST/GET to server
                                         ↓
                              Receive & log response
                              Parse JSON response
                              Map to model/DTO
                                         ↓
                              Return to Repository
```

### Error Handling

**Common API Errors**:

1. **401 Unauthorized**:
   - Cause: Token expired or invalid
   - Action: Auto-logout, redirect to login
   - Handling: [lib/app/core/exceptions/api_exception.dart](lib/app/core/exceptions/api_exception.dart)

2. **400 Bad Request**:
   - Cause: Invalid request data
   - Action: Show error message from server response
   - Handling: User sees error snackbar

3. **500 Internal Server Error**:
   - Cause: Server-side issue
   - Action: Show error message and optionally retry
   - Handling: Logged for debugging

4. **Network Error / No Connection**:
   - Cause: Device offline or network unreachable
   - Action: Queue request for offline sync
   - Handling: [lib/app/core/services/offline_sync_service.dart](lib/app/core/services/offline_sync_service.dart)

**Exception Classes**:
- [lib/app/core/exceptions/api_exception.dart](lib/app/core/exceptions/api_exception.dart)
- [lib/app/core/exceptions/network_exception.dart](lib/app/core/exceptions/network_exception.dart)
- [lib/app/core/exceptions/exception_handler.dart](lib/app/core/exceptions/exception_handler.dart)

---

## Communication with Server

### API Client Configuration

**File**: [lib/app/core/api/api_client.dart](lib/app/core/api/api_client.dart)

**Features**:
- Base URL management (configurable per brand)
- Automatic timeout (30 seconds)
- Request/response logging
- Token injection via interceptors
- Error parsing and mapping
- Retry logic for network failures
- SSL certificate verification
- Support for multipart file uploads

### Request Interceptors

All outgoing requests automatically get:
- `Authorization` header with bearer token
- `Content-Type` header based on request type
- Request logging with timestamp
- Connection timeout configuration

### Response Interceptors

All incoming responses:
- Are parsed from JSON to Dart models
- Have their status codes checked
- Are logged for debugging
- Trigger automatic logout on 401

### Endpoints Organization

All endpoints are centralized in: [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)

This ensures:
- Single source of truth for all API paths
- Easy to update endpoint URLs
- No hardcoded URLs scattered in code
- Clear visibility of all server dependencies

### Supported HTTP Methods

- **GET**: For retrieving data (templates, analytics, lookups)
- **POST**: For sending new data (citations, login, updates)
- **PATCH**: For updating existing data
- **Multipart POST**: For file uploads (images)

---

## Data Storage

### Two-Tier Storage Architecture

The app uses two complementary storage systems optimized for different use cases:

**1. Quick Storage (GetStorage)**

**Purpose**: Fast access to frequently-used simple data

**Used For**:
- Login tokens
- Agency/brand selection
- Officer preferences
- Last access times
- Simple user settings

**Characteristics**:
- Key-value store (like a simple dictionary)
- Very fast access time
- Survives app restarts
- Persists until explicitly deleted
- Limited to simple data types

**Service Location**: [lib/app/core/services/local_storage_service.dart](lib/app/core/services/local_storage_service.dart)

**Storage Keys Definition**: [lib/app/core/constants/storage_keys.dart](lib/app/core/constants/storage_keys.dart)

**2. Database Storage (Hive)**

**Purpose**: Structured storage for complex data collections

**Used For**:
- Offline citation queue (citations created while offline)
- Cached form templates (available for offline use)
- Citation history
- Dropdown options (violation types, vehicle makes, etc.)
- Activity logs

**Characteristics**:
- NoSQL database optimized for mobile
- Fast even with thousands of records
- Supports complex object types
- Can query and filter data
- Type-safe with generated code
- Survives app restarts

**Hive Models**: [lib/app/core/models/offline_request_model.dart](lib/app/core/models/offline_request_model.dart)

**Generated Code**: offline_request_model.g.dart (auto-generated, do not edit)

### Why Two Storage Systems?

- **Quick Storage** is perfect for simple data that's accessed frequently
- **Database Storage** is perfect for large collections that might need searching/filtering
- Using the right tool for each job keeps the app performant
- Reduces memory usage by not keeping everything in RAM

### Cache Management Strategy

**Form Templates**:
- Downloaded once from server when needed
- Stored in Hive database
- Reused when offline
- Updated on each fresh app session
- Location: [lib/features/home/data/repository/home_storage_repository.dart](lib/features/home/data/repository/home_storage_repository.dart)

**Dropdown Data**:
- Fetched once per session (if offline, use cached)
- Examples: violation types, vehicle makes, streets, zones
- Cached indefinitely until app clear or fresh download
- Reduces API calls significantly

**Images & Citations**:
- Local temporary storage during creation
- Uploaded to server when submitted
- Deleted locally after successful upload
- If offline, kept until sync succeeds

---

## Offline Functionality

### Why Offline Is Important

Parking enforcement officers work outdoors, often in areas with poor or no cell phone signal:
- Inside parking garages (underground, no signal)
- Rural areas with limited coverage
- City areas with dead spots
- Tunnels and covered parking structures

Without offline support, officers would be unable to work in these areas. With offline support, they work normally and the app syncs later.

### How Offline Works - In Plain English

**Scenario**: Officer enters a parking garage with no internet signal

**What Happens**:

1. **Officer Creates Citation** (No Internet):
   - Officer enters all information as normal
   - Takes photos as normal
   - Clicks "Submit"
   - App detects "no internet connection"
   - Instead of failing, app says: "Saved locally - will send when online"
   - Citation is saved safely on the phone in a local "outbox"

2. **Officer Works Offline** (Multiple Citations):
   - Officer creates Citation #1 → Saved locally
   - Officer creates Citation #2 → Saved locally
   - Officer creates Citation #3 → Saved locally
   - Officer can see how many are waiting to be sent (e.g., "3 pending")

3. **Officer Leaves Garage** (Gets Internet):
   - Officer drives out of garage and gets cell signal
   - App automatically detects internet is back
   - App automatically starts sending all pending citations
   - Officer is notified: "Syncing... 3 citations to send"

4. **Smart Sending**:
   - App sends Citation #1 → Success ✓ (deleted locally)
   - App sends Citation #2 → Success ✓ (deleted locally)
   - App sends Citation #3 → Success ✓ (deleted locally)
   - Officer is notified: "All citations synced successfully"

**What If Internet Drops During Sync?**:
- If sending fails partway through, the app remembers where it stopped
- When internet returns again, it continues from where it left off
- No citations are ever lost or sent twice

### Technical Details (Simplified)

**What Gets Saved Locally**:
- Citation information (all fields)
- Photos associated with the citation
- Officer ID and agency info
- Timestamp of when it was created

**How App Checks for Internet**:
- The app continuously monitors the phone's connection
- Checks approximately every 30 seconds to 1 minute
- The moment internet is detected, sync starts automatically

**Storage Location**:
- Saved in the phone's secure local database (not lost even if app closes)
- Survives phone restart
- Takes minimal storage space

**Order of Sending**:
- Citations are sent in the order they were created (like a queue at a store - first in, first out)
- App ensures each is sent before moving to the next one

### What Works Offline

✅ **Works Without Internet**:
- Create new citations
- Fill out forms completely
- Take multiple photos
- View form templates (already downloaded)
- View dropdown options (already downloaded)
- Add all details to citations
- Search saved citations locally

❌ **Requires Internet**:
- First login (initial authentication)
- Download new form templates
- Get latest dropdown options
- Submit/sync citations (queued for later)
- View real-time analytics
- View live photos from server

### Real-World Example

**Officer's Day**:
```
8:00 AM - Logs in at office with internet ✓
8:15 AM - Downloads templates and dropdown options ✓
8:30 AM - Drives to parking garage (no internet)

During Garage Coverage:
8:35 AM - Creates Citation #1 (saved locally) - no internet ✗
8:40 AM - Creates Citation #2 (saved locally) - no internet ✗
8:50 AM - Creates Citation #3 (saved locally) - no internet ✗
9:00 AM - Creates Citation #4 (saved locally) - no internet ✗
9:15 AM - Leaves garage, reaches street level (gets internet) ✓

Automatic Sync:
9:16 AM - App detects internet
9:17 AM - Sends Citation #1 ✓
9:18 AM - Sends Citation #2 ✓
9:19 AM - Sends Citation #3 ✓
9:20 AM - Sends Citation #4 ✓
9:21 AM - All citations synced! Officer sees confirmation.

Result: Officer completed 4 citations despite being offline
```

### Offline Indicators (What Officers See)

The app shows officers:
- **Pending Count**: "3 citations waiting to be sent"
- **Sync Status**: "Last synced 2 hours ago" or "Syncing now..."
- **Icon Indicator**: Green dot = online, Red dot = offline
- **Notification**: "All citations synced successfully"

### Why This Is Revolutionary

**Before Mobile Apps**:
- Officers couldn't work without internet
- Had to wait until returning to office to enter citations
- Data could be lost if written on paper
- Took hours to transcribe paper notes into computer

**With This App**:
- Officers work anywhere, anytime
- Automatic syncing means no data loss
- Complete digital record created in real-time
- No transcription needed
- Faster processing from issuance to server

---

## User Interface & Design

### Overall Look and Feel

The app is designed to be **professional, clean, and easy to use** even in bright outdoor sunlight (since officers work outside).

**Visual Design Philosophy**:
- **Consistent Appearance**: Every screen looks like it belongs in the same app
- **Clear Color System**: Specific colors mean specific things (blue = action, red = warning, green = success)
- **Easy to Read**: Large text, good contrast, works in bright sunlight
- **Professional Look**: Looks professional and trustworthy for a government enforcement app

### Color System

Like a traffic light, the app uses colors to communicate meaning:

**Blue (Primary Color)**:
- Used for: Main buttons and important actions
- Meaning: "This is what you should do next" or "This is primary action"
- Files: [lib/app/core/theme/app_colors.dart](lib/app/core/theme/app_colors.dart)

**Red (Warning Color)**:
- Used for: Error messages, delete buttons, warnings
- Meaning: "Something needs attention" or "This is dangerous"

**Green (Success Color)**:
- Used for: Confirming success, checkmarks, completed items
- Meaning: "This worked!"

**Examples in Real Use**:
- Login button = Blue (calls attention)
- Delete citation button = Red (warns officer)
- "Citation submitted successfully" = Green checkmark
- Required form field = Red asterisk

### Text and Fonts

The app uses professional fonts that match what officers are familiar with:

**Font Choice**: San Francisco (matches iPhone/Apple devices)
- Why this font: Clean, professional, easy to read on small screens

**Text Sizes**:
- **Page Titles**: Largest text (officer knows what screen they're on)
- **Section Headers**: Medium text (breaks content into logical sections)
- **Body Text**: Regular reading size (form fields, descriptions)
- **Supporting Text**: Smaller text (hints, help text, timestamps)
- **Buttons**: Clear, large enough to tap

**Location**: [lib/app/core/theme/app_text_styles.dart](lib/app/core/theme/app_text_styles.dart)

### Screen Sizes and Responsive Design

Officers use phones of all different sizes:

**Small Phones** (like iPhone SE):
- 4-4.7 inch screens
- App adjusts layout to show everything without scrolling too much
- Buttons positioned for easy thumb access

**Regular Phones** (like iPhone 12, 13):
- 5-6 inch screens
- Standard layout that most phones get
- Comfortable to hold and use

**Large Phones and Tablets** (6+ inches):
- Big screens can show more information at once
- App intelligently spreads content to use available real estate
- Two-column layouts on tablets

**How It Works**:
- Developers specify sizes in an intelligent unit system
- App automatically calculates real pixel sizes based on screen size
- Result: App looks good on any device
- Technology: Uses Flutter's `flutter_screenutil` package

### Building Blocks - Reusable UI Components

Think of UI components like LEGO blocks - the same pieces are used throughout the app to create a consistent experience:

**Text Input Boxes** (where officer types):
- Smart validation (catches errors before submit)
- Shows helpful error messages
- Location: [lib/app/shared/widgets/](lib/app/shared/widgets/)

**Dropdown Menus** (where officer selects from a list):
- Example: Selecting violation type from a list
- Has search feature if list is long
- Location: [lib/app/shared/widgets/](lib/app/shared/widgets/)

**Buttons**:
- **Filled Buttons**: Blue main buttons for important actions
- **Outlined Buttons**: Less important alternate actions
- Both respond with color change when tapped

**Checkboxes and Radio Buttons**:
- Multiple choice vs single choice
- Used for form selections

**Cards and Containers**:
- Information grouped together
- Consistent spacing and styling

**Navigation Bar**:
- At bottom of screen
- Shows which section of app officer is in
- Quick access to main features

**Toolbars**:
- At top of screen
- Shows page title
- Has back button or settings

**Loading Spinners**:
- Shows data is being downloaded
- Officer knows to wait

**Error Messages**:
- Clear explanation of what went wrong
- Tells officer how to fix it

**Empty State Displays**:
- When there's no data to show
- Helpful message instead of blank screen

**Custom Dialogs** (pop-up windows):
- Confirmation dialogs: "Are you sure?"
- Input dialogs: asking officer for information
- Error dialogs: explaining problems

All of these components are stored and reused: [lib/app/shared/widgets/](lib/app/shared/widgets/)

### Consistent Spacing and Sizing

To keep the app looking uniform throughout:

**Sizes are Defined In One Place**: [lib/app/core/constants/app_sizes.dart](lib/app/core/constants/app_sizes.dart)
- Padding (space inside containers)
- Margins (space between elements)
- Border radius (how rounded corners are)
- Icon sizes
- Button heights

**Benefit**: If designers want to change spacing globally, they change it once and it updates everywhere in the app

### Icons and Images

The app uses visual symbols to communicate quickly:

**Icons**:
- **Format**: SVG (scalable vector graphics = they stay sharp on any device)
- **Usage**: Violation symbols, checkmarks, warning icons, etc.
- **Location**: [assets/icons/](assets/icons/)
- **Reference List**: [lib/app/core/constants/app_icons.dart](lib/app/core/constants/app_icons.dart)
- **Why SVG**: Can be scaled up or down without getting blurry

**Images**:
- **Format**: PNG/JPG photos
- **Usage**: Company logos, background images, photo attachments
- **Location**: [assets/images/](assets/images/)
- **Reference List**: [lib/app/core/constants/app_images.dart](lib/app/core/constants/app_images.dart)

### Real-World Visual Example

```
┌─────────────────────────────────────┐
│  PARK ENFORCEMENT APP               │  ← Page Title (Largest text)
├─────────────────────────────────────┤
│                                     │
│  Create Citation                    │  ← Section Header
│  ─────────────────                  │
│                                     │
│  [Violation Type: Click to select]  │  ← Dropdown (Blue, tapable)
│                                     │
│  [License Plate: __________]        │  ← Text Input Field
│                                     │
│  Photo attached ✓                   │  ← Success indicator (Green)
│                                     │
│        [Preview]  [Submit]          │  ← Buttons (Blue = Primary)
│                                     │
└─────────────────────────────────────┘

Bottom Navigation:
[Home] [Activity] [Lookup] [Settings]  ← Quick access to features
```

The entire interface follows these same principles - consistent colors, sizes, and components throughout.

---

---

## Multi-Language Support

### Why Language Support Matters

Parking enforcement agencies work in diverse communities where officers may speak different languages:
- Some officers prefer English
- Some officers prefer Hindi
- Officers can switch languages anytime - even mid-shift

This makes the app accessible to all officers regardless of their working language preference.

### Supported Languages

**Available Languages**:
- **English**: Default language (what you see when you first open the app)
- **Hindi**: Full support for Hindi-speaking officers

**Adding More Languages** (for future expansion):
- Spanish, Portuguese, or any other language can be added using the same system
- Takes translation file + configuration change

### How Officers Change Language

**Officer's Perspective**:
1. Open the app → Go to Settings
2. Select "Language" or "भाषा" (hindi for language)
3. Choose "English" or "हिंदी" (Hindi)
4. Click "Apply" or "Done"
5. Entire app immediately switches language - no restart needed!
6. App remembers choice for next time

**What Changes**:
- All button labels change language
- All form fields change language
- All instructions and help text change language
- Everything except data (like license plates) changes language

### How It Works Behind the Scenes (Technical Details)

The app stores translations in two places:

**Translation Keys** (definitions): [lib/app/core/localization/local_keys.dart](lib/app/core/localization/local_keys.dart)
- List of all text that needs translation
- Like a dictionary of "things we display to users"
- Example: `confirm_citation`, `violation_type`, `photo_attachment`

**Translation Files** (actual words): [lib/app/core/localization/translations/](lib/app/core/localization/translations/)
- English translation file
- Hindi translation file
- Each file is a database: `confirm_citation → "Confirm Citation"` (English) or `confirm_citation → "पुष्टि करें उद्धरण"` (Hindi)

**Language Configuration**: [lib/app/core/localization/app_locales.dart](lib/app/core/localization/app_locales.dart)
- Tells app which languages are available
- Manages language switching

### Real-World Example

**Officer Using English**:
```
┌────────────────────────────────────┐
│  CREATE CITATION                   │
├────────────────────────────────────┤
│  Violation Type: [Select]          │
│  License Plate: ________________   │
│  [Submit Citation]                 │
└────────────────────────────────────┘
```

**Same Officer Switches to Hindi**:
```
┌────────────────────────────────────┐
│  उद्धरण बनाएं                        │
├────────────────────────────────────┤
│  उल्लंघन का प्रकार: [चुनें]          │
│  लाइसेंस प्लेट: _____________       │
│  [उद्धरण प्रस्तुत करें]             │
└────────────────────────────────────┘
```

**Name stays same**: "LICENSE PLATE" doesn't translate to another language - it's just displayed with the appropriate UI language

### Adding New Text to the App

When developers add new text to the app:

1. **Define the text key** in [lib/app/core/localization/local_keys.dart](lib/app/core/localization/local_keys.dart)
   - Example: `missing_license_plate` (the label)

2. **Add translations** to both translation files:
   - English translation file: `"Missing license plate"` (English version)
   - Hindi translation file: `"लाइसेंस प्लेट गायब"` (Hindi version)

3. **Use in the app code**: 
   - Developer writes: `Text(LocalKeys.missing_license_plate.tr)`
   - `.tr` means "translate this" - app automatically picks the right translation

### Easy Language Switching Feature

**Why Immediate Switch Works**:
- New officers joining mid-shift can switch to their language anytime
- Officers can demonstrate app to colleagues in different language
- If language setting breaks something, officer can switch back immediately
- No app restart needed (annoying for users)

### Future Language Expansion

To add a new language in the future:
1. Create new translation file [lib/app/core/localization/translations/new_language.json](lib/app/core/localization/translations/)
2. Copy all keys and provide translations
3. Update [lib/app/core/localization/app_locales.dart](lib/app/core/localization/app_locales.dart) to include new language
4. Update Settings screen to show new language as an option
5. Done! App now supports new language system-wide

---

---

## Navigation & Screens

### How Officers Move Through the App

The app is organized into screens, and officers navigate between screens using buttons, menus, and gestures. Think of it like flipping through pages in a book or opening different rooms in a building.

### The Officer's Journey - From Opening App to Using Features

**When Officer First Opens App**:

```
┌─────────────────────────────────────────────────────────────┐
│ 1. BRAND SELECTION SCREEN                                  │
│    "Select Your Agency"                                    │
│    [City of Miami] [Miami Beach] [Other Agency]            │
│    Officer picks their agency                              │
│                    ↓                                        │
│ 2. SPLASH SCREEN (Loading Screen)                          │
│    "Initializing..." ✓✓✓                                    │
│    App downloads forms and permissions check               │
│                    ↓                                        │
│ 3. LOGIN SCREEN                                            │
│    "Officer Login"                                         │
│    Username: [____________]                                │
│    Password: [____________]                                │
│    Officer enters credentials                              │
│                    ↓                                        │
│ 4. HOME DASHBOARD (Main Screen)                            │
│    Officer sees main menu with all features                │
│                    ↓                                        │
│    [Create Citation] [Activity] [Search] [Settings]        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Next Time Officer Opens App**:
- Brand and login are remembered
- Opens directly to Home Dashboard
- Much faster!

### The Feature Navigation Map

Once logged in, officers access features from the home screen:

**Main Menu (Home Screen)**:

```
OFFICER DASHBOARD
─────────────────────────────────
│
├─ CREATE CITATION             ← Frequent action (big button)
│  └─ Form Entry
│  └─ Photo Capture  
│  └─ Preview & Submit
│
├─ MY ACTIVITY                 ← View personal stats
│  └─ Performance Charts
│  └─ Daily Summary
│  └─ Violation breakdown
│
├─ SEARCH CITATIONS            ← Lookup past work
│  └─ Find by License
│  └─ Find by Date
│  └─ View Results
│
├─ SCANNING                    ← Future feature (coming soon)
│
├─ MUNICIPAL CITATION          ← Future feature (coming soon)
│
├─ REPORTS                     ← Future feature (coming soon)
│
└─ SETTINGS                    ← Officer preferences
   └─ Language
   └─ Profile
   └─ Notifications
```

### Each Feature's Screen Flow

**CREATE CITATION Feature**:

```
Step 1: Landing Page
└─ Shows "Create New Citation" button
└─ Shows past citations count
   │
   ↓
Step 2: Violation Form
└─ "Select Violation Type" dropdown
└─ "Enter License Plate" field
└─ "Select Zone" dropdown
└─ "Add Details" notes field
└─ "Capture Photo" button
   │
   ↓
Step 3: Photo Review
└─ Shows all attached photos
└─ Can add more or remove
└─ Shows preview of data
   │
   ↓
Step 4: Final Submission
└─ "Submit Citation" button
└─ Shows confirmation: "Citations submitted!" ✓
└─ Asks "Create another?" or returns to dashboard
```

**MY ACTIVITY Feature**:

```
Main Activity Dashboard
│
├─ Performance Chart (bar graph showing citations per week)
│  │
│  ├─ Tap for bigger view
│  └─ Shows trend comparison
│
├─ Daily Summary (today's numbers)
│  │
│  ├─ Total citations today: 15
│  ├─ Total hours worked: 8
│  └─ Avg per hour: 1.9
│
├─ Violation Breakdown (pie chart)
│  │
│  ├─ Metered violations: 8
│  ├─ Street sweeping: 4
│  └─ Reserved space: 3
│
└─ LPR Hits (license plate reader matches)
   │
   ├─ List of flagged vehicles
   └─ Time and location info
```

**SEARCH CITATIONS Feature**:

```
Search Criteria
│
├─ License Plate: [Enter plate]
├─ Date Range: [From] [To]
├─ Meter Number: [Optional]
│
└─ [SEARCH] Button
   │
   ↓
Results Page
│
├─ Citation #1234
│  ├─ Vehicle: ABC-123
│  ├─ Violation: Metered
│  └─ Date: Jan 15, 2024
│
├─ Citation #1235
│  ├─ Vehicle: XYZ-789
│  ├─ Violation: No parking
│  └─ Date: Jan 16, 2024
│
└─ [Tap to view details]
```

### Screen File Locations (For Developers)

The screens are organized by feature:

**Authentication Screens**:
- Brand selection: [lib/features/brand/ui/brand_page.dart](lib/features/brand/ui/brand_page.dart)
- Login page: [lib/features/login/ui/screens/login_page.dart](lib/features/login/ui/screens/login_page.dart)
- Password recovery: [lib/features/login/ui/screens/forgot_password_page.dart](lib/features/login/ui/screens/forgot_password_page.dart)
- Security verification: [lib/features/login/ui/screens/recaptcha_page.dart](lib/features/login/ui/screens/recaptcha_page.dart)

**Home/Dashboard**:
- Main dashboard: [lib/features/home/ui/screens/home_page.dart](lib/features/home/ui/screens/home_page.dart)

**Create Citation Feature**:
- Landing: [lib/features/ticketing/ui/screens/ticketing_page.dart](lib/features/ticketing/ui/screens/ticketing_page.dart)
- Form entry: [lib/features/ticketing/ui/screens/ticket_issue_screen.dart](lib/features/ticketing/ui/screens/ticket_issue_screen.dart)
- Preview: [lib/features/ticketing/ui/screens/ticket_issue_preview_page.dart](lib/features/ticketing/ui/screens/ticket_issue_preview_page.dart)
- Pay-by-plate variant: [lib/features/ticketing/ui/screens/pay_by_plate_screen.dart](lib/features/ticketing/ui/screens/pay_by_plate_screen.dart)
- Pay-by-space variant: [lib/features/ticketing/ui/screens/pay_by_space_screen.dart](lib/features/ticketing/ui/screens/pay_by_space_screen.dart)

**My Activity Feature**:
- Dashboard: [lib/features/my_activity/ui/screens/my_activity_page.dart](lib/features/my_activity/ui/screens/my_activity_page.dart)
- Charts/Graphs: [lib/features/my_activity/ui/screens/graph_view/graph_view.dart](lib/features/my_activity/ui/screens/graph_view/graph_view.dart)
- Daily summary: [lib/features/my_activity/ui/screens/daily_summary/daily_summary_screen.dart](lib/features/my_activity/ui/screens/daily_summary/daily_summary_screen.dart)
- LPR hits: [lib/features/my_activity/ui/screens/lpr_hits/lpr_hits_screen.dart](lib/features/my_activity/ui/screens/lpr_hits/lpr_hits_screen.dart)

**Search/Lookup Feature**:
- Search results: [lib/features/lookup/ui/screens/citation_result_page.dart](lib/features/lookup/ui/screens/citation_result_page.dart)
- Citations list: [lib/features/lookup/ui/screens/citations_page.dart](lib/features/lookup/ui/screens/citations_page.dart)
- Timing records: [lib/features/lookup/ui/screens/timing_records_page.dart](lib/features/lookup/ui/screens/timing_records_page.dart)

### App Navigation Rules (How Routing Works)

**Navigation System**: [lib/app/core/routes/app_pages.dart](lib/app/core/routes/app_pages.dart)

**Route Definitions**: [lib/app/core/routes/app_routes.dart](lib/app/core/routes/app_routes.dart)

**How It Works**:
- Each screen has a unique route name (like a URL)
- When officer taps button, app navigates to that route
- When officer presses back, app returns to previous screen
- App remembers which feature controller needs to be active

**Example Navigation Sequence**:
1. Officer on Home → Taps "Create Citation"
2. App loads Ticketing controller and shows ticketing_page
3. After submitting, app returns to Home
4. Ticketing controller is cleaned up (frees memory)

---

---

## Data Management

### How the App Thinks About Data

The app manages data like an organization with different departments:
- **Data** = Information that needs to be displayed or processed
- **State** = The current condition/status of the app (is citation data loaded? is form valid?)
- **Controllers** = Managers that coordinate how data flows between screens and services

### The Data Flow Pattern

Think of it like a restaurant order system:

```
CUSTOMER (Officer opens form)
    ↓
WAITER (Screen/View)
    ↓ "I need violation types"
    ↓
MANAGER (Controller - GetX)
    ↓ "Get violation types for me"
    ↓
KITCHEN (Repository)
    ↓ "Check API client for data"
    ↓
DATA SOURCE (API Client → Server OR → Local Storage)
    ↓
RESPONSE (Returns violation types list)
    ↓
MANAGER (Controller updates state)
    ↓
WAITER (Screen refreshes automatically)
    ↓
CUSTOMER (Officer sees updated dropdown)
```

### Understanding Controllers (The Coordinators)

Controllers manage:
- **What data the screen displays**
- **When to show loading spinners**
- **What happens when buttons are tapped**
- **Errors and what to show the officer**

**Base Controller**: [lib/app/core/controllers/base_controller.dart](lib/app/core/controllers/base_controller.dart)

All controllers inherit from `BaseController`, which is like a template that provides:
- Error handling (when something goes wrong)
- Loading state management (show spinner vs show data)
- Consistent lifecycles (when controller starts, when it stops)
- Exception handling utilities

**Global App Controller**: [lib/app/core/controllers/app_controller.dart](lib/app/core/controllers/app_controller.dart)
- Manages app-wide state that every feature needs
- Example: Is officer logged in? Who is the current officer?
- Available throughout app lifetime

**Feature Controllers**: Located in each feature folder
- Example: `ticketing_controller.dart`, `my_activity_controller.dart`
- Manages state for that specific feature only
- Created when officer opens that feature
- Cleaned up when officer closes that feature
- Frees memory when feature not being used

### Reactive Variables (The Smart Update System)

Controllers use a special type of variable that automatically updates the screen when the value changes:

```
Example patterns:
• isLoading = false.obs  ← Observer variable
• citations = <Citation>[].obs  ← List of citations
• currentOfficer = Rx<Officer?>(null)  ← Complex object
```

**How It Works**:

```
Controller Code:
    isLoading.value = true  ← Change value
        ↓
    Automatically notifies all screens observing this variable
        ↓
Screen Code:
    Obx(() => isLoading.value ? Spinner() : Content())
    ↓
    Automatically rebuilds and shows spinner
```

**Benefit**: No manually refreshing screens. Data changes automatically trigger UI updates.

### Feature Data Flow - Citation Creation Example

**Step 1: Officer Opens Citation Form**
- Controller initializes
- Gets violation types from repository
- Sets `isLoading = true`
- Screen shows spinner

**Step 2: Data Arrives**
- Repository gets data from API client or local storage
- Controller: `violations.value = [Metered, Street Sweep, Reserved]`
- Sets `isLoading = false`
- Screen automatically updates to show dropdown menu

**Step 3: Officer Fills Form & Submits**
- Officer taps violation: Controller stores selection
- Officer enters license plate: Controller stores text
- Officer taps Submit: Controller calls `createCitation()`
- Controller sets `isLoading = true`
- Screen shows "Submitting..."

**Step 4: Citation Sent to Server**
- Repository sends data via API client
- Server responds: "Citation #12345 created"
- Controller: `lastCitationId.value = 12345`
- Controller: `isLoading = false`
- Screen shows: "Success! Citation created"
- Screen shows button: "Create Another" or "Return to Home"

### Repository Pattern (The Connection Layers)

Repositories are like connectors that sit between controllers and data sources:

```
Feature Flow:
    Controller
         ↓
    Repository  ← "I need citations"
    ├─ API Repository
    │   └─ Connected to API Client (server data)
    └─ Storage Repository
        └─ Connected to Local Storage (cached data)
```

**Benefit**:
- Controller doesn't care WHERE data comes from
- Can easily switch between API and local storage
- Easy to test (replace real data with fake data for testing)
- Clean separation of concerns

**Example**:
- Get citation online: `apiClient.get('/citations-issuer/ticket')`
- Get citation offline: `hiveDatabase.getLocalCitations()`
- Controller just calls: `repository.getCitations()` - repository decides which one to use

### Model Classes (The Data Containers)

Models are like containers that hold specific types of data:

**Examples**:
```
LoginResponse
├─ status: true/false
├─ response: "token_string"
└─ metadata: 
   ├─ last_login: "2024-01-15"
   └─ current_login: "2024-01-20"

Citation
├─ id: "12345"
├─ vehicle_plate: "ABC-123"
├─ violation_type: "Metered"
├─ location: "5th and Main"
├─ timestamp: "2024-01-20 14:30:00"
└─ photos: ["photo1.jpg", "photo2.jpg"]

OfficerStats
├─ total_citations: 45
├─ this_week: 12
├─ this_month: 38
└─ accuracy_rate: 98%
```

**Why Models Matter**:
- Type safety (app knows what fields exist)
- Automatic validation (catches missing data)
- IDE autocomplete (developers see available fields)
- Easy conversion from JSON (server data → dart objects)

---

---

## Reusable Components

### Why Reusable Components Matter

Imagine a building where every room is designed differently - different door sizes, different lighting, different color schemes. It would be confusing and expensive to build! 

The app uses pre-built, reusable components like building blocks - the same components used on multiple screens in consistent ways.

**Benefits**:
- **Consistency**: Same buttons look and behave the same everywhere
- **Speed**: Build new features faster using existing components
- **Quality**: Test components once, work everywhere
- **Maintenance**: Update component once, improves entire app
- **Smaller App**: Reuse code instead of duplicating

### The Component Library

**Location**: [lib/app/shared/widgets/](lib/app/shared/widgets/)

All reusable UI components live in this "library" so developers can grab them when building screens.

### Main Components

**1. Text Input Fields** (Where officers type)

```
┌──────────────────────────────────┐
│ License Plate                    │  ← Label
│ ┌──────────────────────────────┐ │
│ │ ABC-1234                     │ │  ← Input area where officer types
│ └──────────────────────────────┘ │
│ !  Invalid plate format          │  ← Error message (if error)
└──────────────────────────────────┘
```

**Features**:
- Validates data as officer types
- Shows error messages instantly
- Custom keyboard for phone numbers, numbers only, etc.
- Works on all screen sizes

---

**2. Dropdown Selectors** (Where officer picks from list)

```
┌──────────────────────────────────┐
│ Violation Type                   │  ← Label
│ ┌──────────────────────────────┐ │
│ │ Metered Violation       ▼    │ │  ← Shows selected item + expand arrow
│ └──────────────────────────────┘ │
│ When tapped shows menu:          │
│ • Metered Violation              │
│ • Street Sweeping                │
│ • No Parking                     │
│ • Reserved Space                 │
│ • Handicap Violation             │
└──────────────────────────────────┘
```

**Features**:
- Searchable if list is long
- Scrollable menu
- Clear selection indicator
- Keyboard friendly

---

**3. Checkboxes** (Multiple selection)

```
Options:
☐ Issue warning              ← Can check/uncheck
☑ Update database           ← Checked
☐ Notify owner
```

**4. Radio Buttons** (Single selection)

```
Payment Method:
⦿ Pay-by-License Plate      ← Only one can be selected
○ Pay-by-Space
○ Citation Number
```

---

**5. Buttons**

```
Primary (Main action - blue):
┌──────────────────────────┐
│   SUBMIT CITATION        │
└──────────────────────────┘

Secondary (Less important - outlined):
┌──────────────────────────┐
│    SAVE AS DRAFT         │
└──────────────────────────┘

Danger (Destructive - red):
┌──────────────────────────┐
│      DELETE              │
└──────────────────────────┘
```

**Features**:
- Change color when tapped (feedback)
- Disable when clicked (prevent double-submission)
- Show loader while processing
- Adaptive to different screen sizes

---

**6. Cards** (Content containers)

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃ Citation #12345           ┃
┃ ─────────────────────────  ┃
┃ Vehicle: ABC-1234         ┃
┃ Violation: Metered        ┃
┃ Date: Jan 20, 2024        ┃
┃ Status: Submitted ✓       ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

**Features**:
- Grouped related information
- Shadow effect for depth
- Consistent spacing
- Can be tappable

---

**7. Bottom Navigation Bar** (Quick access to features)

```
┌─────────────────────────────────────┐
│ Home  │ Activity │ Search │ Settings│
│ 🏠    │   📊     │  🔍    │    ⚙️    │
└─────────────────────────────────────┘
```

**Features**:
- Always visible at bottom
- Shows current position (highlighted)
- Quick switch between main features
- Badge with count (e.g., "Activity (3)" for 3 new items)

---

**8. Top Toolbar**

```
┌─────────────────────────────────────┐
│ ◀ Create Citation        ⋮         │
│    (back)  (page title)  (menu)    │
└─────────────────────────────────────┘
```

**Features**:
- Shows page title
- Back button to return to previous screen
- Menu button for options
- Status icons

---

**9. Loading Spinners** (Shows data is loading)

```
Your data is loading. Please wait...

    ⟳  ← Spinning circle
```

**10. Error Messages** (When something goes wrong)

```
⚠ Error: Unable to submit citation
Please check your internet connection
and try again.

[Retry]  [Cancel]
```

---

**11. Empty State Displays** (When there's no data)

```
📭 No Citations Found

You haven't created any citations yet.
Tap "Create Citation" to get started.
```

---

**12. Custom Dialogs** (Pop-up windows)

```
Confirmation Dialog:
┌──────────────────────────────┐
│ Are you sure?                │
│ This will delete the         │
│ citation. This can't be      │
│ undone.                      │
│                              │
│  [Cancel]  [Delete]         │
└──────────────────────────────┘
```

---

### Size and Spacing System

To keep everything aligned and looking professional, all sizes are defined in one place:

**File**: [lib/app/core/constants/app_sizes.dart](lib/app/core/constants/app_sizes.dart)

Includes:
- **Padding** (space inside containers): 8px, 12px, 16px, 20px
- **Margins** (space between elements): 8px, 12px, 16px, 20px, 24px
- **Border radius** (rounded corner amount): 4px, 8px, 12px, 16px
- **Icon sizes**: 16px, 24px, 32px, 48px
- **Button heights**: 40px, 48px, 56px

**Why This Matters**:
- Everything lines up neatly
- Proportions look balanced
- Easy to update spacing globally

### Helper Utilities

Developers also have access to helper functions for common tasks:

**Date & Time Utilities**: [lib/app/shared/utils/date_utils.dart](lib/app/shared/utils/date_utils.dart)
- Format dates nicely (e.g., "Jan 20, 2024")
- Convert time zones
- Calculate time differences

**Notification Utilities**: [lib/app/shared/utils/snackbar_utils.dart](lib/app/shared/utils/snackbar_utils.dart)
- Show toast notifications (messages at bottom of screen)
- Show error messages
- Show success confirmations

**Dart Extensions**: [lib/app/shared/utils/extensions/](lib/app/shared/utils/extensions/)
- Shortcut methods
- Example: `"hello".capitalize()` → "Hello"
- Example: `5.toBithDescription()` → "5 bytes"

### Shared Controllers (For Complex Features)

**Bottom Navigation Controller**: [lib/app/shared/controller/bottom_nav_bar_controller.dart](lib/app/shared/controller/bottom_nav_bar_controller.dart)
- Manages which tab is active
- Handles switching between features
- Available throughout app lifetime

**Drawer Navigation Controller**: [lib/app/shared/controller/drawer_controller.dart](lib/app/shared/controller/drawer_controller.dart)
- Manages slide-out menu (if used)
- Handles menu item selection

**Loading/Progress Controller**: [lib/app/shared/controller/loader_controller.dart](lib/app/shared/controller/loader_controller.dart)
- Shows/hides loading spinners
- Accessible from anywhere in app

### Real-World Component Usage

When developers build a new screen:

```
They need:
1. ✓ Text input field → Grab InputField component
2. ✓ Dropdown selector → Grab DropdownButton component
3. ✓ Submit button → Grab PrimaryButton component
4. ✓ Error display → Grab ErrorText component

Assemble them on screen with consistent spacing:

┌────────────────────────────────┐
│  New Feature Page              │
├────────────────────────────────┤
│                                │
│  Violation:                    │  ← Text input component
│  [________________]            │
│                                │
│  Zone:                         │  ← Dropdown component
│  [Select Zone ▼]              │
│                                │
│  [SUBMIT]                      │  ← Button component
│                                │
└────────────────────────────────┘

Result: Consistent look with existing screens, built quickly!
```

### Adding New Reusable Components

If developers need a new component that appears in multiple places:

1. Create component in [lib/app/shared/widgets/](lib/app/shared/widgets/)
2. Test it works correctly
3. Document how to use it
4. Other developers can reuse it
5. Update all places using it by updating once

---

---

## Getting Started for New Developers

### Understanding The Big Picture First

Before diving into code, new developers should grasp the overall structure:

**1. Understand What The App Does** (Suggested viewing order):
   - Read [What is This Application?](#what-is-this-application) section
   - Look at [Key Features](#key-features) section
   - Skim [Feature Implementation Status](#feature-implementation-status) to see what's done

**2. Understand How It's Organized**:
   - Read [How the Application Works](#how-the-application-works) section
   - Read [Project Folder Structure](#project-folder-structure) section
   - Read [Core Services](#core-services) section

**3. Understand The Technical Foundation**:
   - Read [Technology Stack](#technology-stack) section to know what tools are used
   - Read [Data Management](#data-management) to understand how data flows
   - Read [Navigation & Screens](#navigation--screens) to see the screens

**4. Review Architecture Patterns**:
   - Each feature follows this pattern:
     ```
     Feature Folder
     ├─ data/
     │  ├─ models/          (Data containers)
     │  └─ repository/      (Gets data from API or storage)
     ├─ ui/
     │  └─ screens/         (What officer sees)
     └─ controller/         (Manages the logic)
     ```

### Exploring The Code Structure

**Start with simplest feature**:

1. **Brand Selection** (Simplest - 100% complete): [lib/features/brand/](lib/features/brand/)
   - Minimal logic
   - Simple UI
   - No API calls (just configuration)
   - Perfect for understanding folder structure

2. **Then Login** (Medium complexity - 100% complete): [lib/features/login/](lib/features/login/)
   - API integration
   - State management
   - Error handling
   - Still relatively straightforward

3. **Then Ticketing** (Most complex - 80% complete): [lib/features/ticketing/](lib/features/ticketing/)
   - Dynamic forms
   - File uploads
   - Complex workflows
   - Offline support

### Adding A New Feature - The Steps

**When adding a new feature:**

**Step 1: Create Folder Structure**
```
lib/features/new_feature/
├── data/
│   ├── models/
│   │   ├── new_feature_request.dart
│   │   └── new_feature_response.dart
│   └── repository/
│       └── new_feature_repository.dart
├── ui/
│   └── screens/
│       └── new_feature_page.dart
└── controller/
    └── new_feature_controller.dart
```

Copy the folder structure from an existing feature for consistency.

**Step 2: Create Models** (Data containers)
- Define what data the API sends back
- Define what data you need to send to the API
- Example:
  ```
  NewFeatureResponse {
    status: boolean
    data: List<Item>
  }
  ```

**Step 3: Create Repository** (Data connector)
- Connect to API or local storage
- Handle errors gracefully
- Example:
  ```
  class NewFeatureRepository {
    getItems() async {
      return await apiClient.get('/api/endpoint');
    }
  }
  ```

**Step 4: Create Controller** (Business logic)
- Extend BaseController
- Use reactive variables for state
- Call repository to get data
- Example:
  ```
  class NewFeatureController extends BaseController {
    var items = <Item>[].obs;
    
    getItems() async {
      isLoading.value = true;
      items.value = await repository.getItems();
      isLoading.value = false;
    }
  }
  ```

**Step 5: Create UI Screen** (What user sees)
- Use components from [lib/app/shared/widgets/](lib/app/shared/widgets/)
- Use Obx() to make it reactive
- Example:
  ```
  Obx(() => Column(
    children: [
      if (controller.isLoading.value) Spinner(),
      if (!controller.isLoading.value) ... display items
    ]
  ))
  ```

**Step 6: Add Routing**
- Add to [lib/app/core/routes/app_pages.dart](lib/app/core/routes/app_pages.dart)
- Define route name: `/new-feature`
- Link controller and screen
- Link as menu item or button

**Step 7: Add API Endpoint** (If needed)
- Add to [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)
- Example:
  ```
  static const newFeature = '/api/new-feature';
  ```

### Testing Your Feature

**On Your Device**:
```
1. Build app: flutter run
2. Open app
3. Navigate to your feature
4. Test offline (disable internet, submit data)
5. Test online (enable internet, see it sync)
6. Check for errors in console
```

**Things to Test**:
- ✓ No network → Shows offline message
- ✓ Server error → Shows error message
- ✓ Success → Shows confirmation
- ✓ Loading states → Spinner shows/hides
- ✓ Form validation → Catches invalid data
- ✓ On both iOS and Android

### Common Patterns You'll Encounter

**Getting Data From Server**:
```
controller.isLoading.value = true;
var data = await repository.getData();
controller.data.value = data;
controller.isLoading.value = false;
```

**Handling Errors**:
```
try {
  var data = await repository.getData();
  controller.data.value = data;
} catch (e) {
  showErrorMessage(e.toString());
}
```

**Offline Sync**:
```
if (isOnline) {
  await syncPendingCitations();
} else {
  saveLocally();
  showMessage("Saved - will sync when online");
}
```

**Reactive UI Update**:
```
// Controller:
var citationCount = 0.obs;
citationCount.value = 5;  // Automatically updates UI

// Screen:
Obx(() => Text('Citations: ${controller.citationCount}'))
```

### Important Files To Know

**Core Configuration**:
- API Endpoints: [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)
- Routes: [lib/app/core/routes/app_pages.dart](lib/app/core/routes/app_pages.dart)
- Colors/Fonts/Sizes: [lib/app/core/theme/](lib/app/core/theme/)

**Services (Shared Across Features)**:
- API Client: [lib/app/core/services/api_client_service.dart](lib/app/core/services/api_client_service.dart)
- Auth Service: [lib/app/core/services/auth_service.dart](lib/app/core/services/auth_service.dart)
- Storage: [lib/app/core/services/local_storage_service.dart](lib/app/core/services/local_storage_service.dart)

**Utilities**:
- Date formatting: [lib/app/shared/utils/date_utils.dart](lib/app/shared/utils/date_utils.dart)
- Notifications: [lib/app/shared/utils/snackbar_utils.dart](lib/app/shared/utils/snackbar_utils.dart)

---

## Common Development Tasks

### Adding a New API Endpoint

**Step 1**: Define the endpoint in [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)
```
static const String getOfficerStats = '/officers/statistics';
```

**Step 2**: Create request/response models in feature's `data/models/` folder
```
OfficerStatsRequest {
  officer_id: string
  date_range: string
}

OfficerStatsResponse {
  total_citations: number
  this_week: number
  accuracy: double
}
```

**Step 3**: Use in repository:
```
var response = await apiClient.getRequest(
  ApiEndpoints.getOfficerStats,
  queryParams: {'officer_id': '123', 'range': 'week'}
);
```

**Step 4**: Handle the response in controller:
```
var stats = await repository.getOfficerStats();
controller.stats.value = stats;
```

### Adding Data To Storage

**For Quick Data** (tokens, preferences):
```
// Define key:
// lib/app/core/constants/storage_keys.dart
static const String officerPreference = 'officer_preference';

// Store:
GetStorage().write(StorageKeys.officerPreference, 'value');

// Retrieve:
var value = GetStorage().read(StorageKeys.officerPreference);
```

**For Complex Data** (collections, citations):
```
// Create Hive model with annotation:
@HiveType(typeId: 1)
class Citation {
  @HiveField(0)
  String id;
  
  @HiveField(1)
  String licensePlate;
}

// Store:
var box = await Hive.openBox<Citation>('citations');
box.add(Citation());

// Retrieve:
var citations = box.values.toList();
```

### Adding Translations

**Step 1**: Add key to [lib/app/core/localization/local_keys.dart](lib/app/core/localization/local_keys.dart)
```
static const String submitCitation = 'submit_citation';
static const String validationError = 'validation_error';
```

**Step 2**: Add translations to JSON files:
- [lib/app/core/localization/translations/en.json](lib/app/core/localization/translations/en.json)
- [lib/app/core/localization/translations/hi.json](lib/app/core/localization/translations/hi.json)

```json
// English
{
  "submit_citation": "Submit Citation",
  "validation_error": "Please fix the errors above"
}

// Hindi
{
  "submit_citation": "उद्धरण प्रस्तुत करें",
  "validation_error": "कृपया ऊपर की त्रुटियों को ठीक करें"
}
```

**Step 3**: Use in screens:
```
Text(LocalKeys.submitCitation.tr)
```

The `.tr` extension automatically gets the right translation for current language.

### Handling Loading and Error States

**Show Loading**: [lib/app/shared/controller/loader_controller.dart](lib/app/shared/controller/loader_controller.dart)
```
LoaderController loaderController = Get.find();

// Show loader:
loaderController.showLoader();

// Hide loader:
loaderController.hideLoader();

// Or in UI:
Obx(() => 
  controller.isLoading.value ? Spinner() : Content()
)
```

**Show Error**:
```
import 'snackbar_utils.dart';

// Show error message:
SnackbarUtils.showError('Citation submission failed');

// Show success message:
SnackbarUtils.showSuccess('Citation created successfully');
```

### Debugging Tips

**Check API Response**:
- Open DevTools
- Look at console output - API client logs all requests/responses
- Response includes status code, response body, headers

**Check Local Storage**:
- Look at what's saved in GetStorage
- Inspect Hive database contents
- Use print statements to log values

**Check Network State**:
- Use `Connectivity().checkConnectivity()` to verify internet
- Verify API endpoints in [lib/app/core/constants/api_endpoints.dart](lib/app/core/constants/api_endpoints.dart)
- Check if any firewalls are blocking API

**Check Token/Auth**:
- AuthService manages tokens
- Check if token expired (compare timestamp)
- Look for 401 (unauthorized) errors in API response

**Mobile Device Testing**:
- iOS: Use Xcode simulator or real device
- Android: Use Android emulator or real device
- Check both portrait and landscape orientations
- Test on small (4") and large (6"+) phones

### Performance Optimization

**Image Handling**:
- Compress images before uploading
- Cache frequently-used images
- Use lazy loading for lists

**API Calls**:
- Cache data locally to reduce server load
- Don't refresh data if it hasn't changed
- Use pagination for large lists

**State Management**:
- Clean up controllers when feature closes (frees memory)
- Use Obx() only on widgets that actually change
- Don't store entire database in memory

---

---

## Summary

### What Makes This App Great

This Park Enforcement mobile app is built with modern technology and best practices to provide:

**✓ Works Everywhere**
- iOS and Android from the same code
- No need to maintain two separate apps
- Reach all officers on any device

**✓ Works Offline**
- Officers create citations anywhere, even underground garages
- Automatic syncing when internet returns
- No lost data ever

**✓ Serves Multiple Agencies**
- Same app for 40+ different parking companies
- Each agency has its own server and data
- Easy to add new agencies

**✓ Smart Forms**
- Forms adapt based on what violations are possible
- Dropdown menus have pre-defined options
- No wrong data entry

**✓ Rich Analytics**
- Officers see their daily performance
- Managers see team trends
- Visual charts and graphs
- Understand what's working

**✓ Secure**
- Officers have unique login credentials
- Passwords are protected
- Bot protection against automated attacks
- Token-based authentication

**✓ Fast to Build For**
- Same components used throughout app means less code
- New features can be added quickly
- Testing is easier
- Bugs are easier to fix

**✓ Professional Appearance**
- Consistent colors, fonts, sizes throughout
- Clean, modern design
- Works in bright sunlight (officer's work environment)
- Feels trustworthy and professional

**✓ Multiple Languages**
- English and Hindi fully supported
- Officers can switch languages instantly
- Can expand to more languages easily

### The Technology Under The Hood

**Framework**: Flutter
- One codebase, two platforms (iOS and Android)
- Built by Google, used by millions of apps
- Excellent performance and stability

**State Management**: GetX
- Manages app data and UI updates smartly
- MVCS pattern keeps code organized
- Reduces bugs and makes testing easier

**Local Storage**: GetStorage + Hive
- Fast access for frequently used data
- Powerful database for complex data
- Survives phone restarts
- Encrypted for security

**Security**: Bearer Token Authentication
- Unique identifier for each logged-in officer
- Auto-logout after inactivity
- Session management
- RecAPTCHA protection against bots

**Performance**:
- Optimized for slow networks (3G)
- Minimal data usage
- Responsive and snappy
- Battery efficient

### Current State of the Project

**Completed (Ready to Use)**:
- ✅ Brand Selection (Agency setup)
- ✅ Officer Authentication (Login system)
- ✅ Splash Screen (App initialization)
- ✅ Home Dashboard (Main interface)
- ✅ Core Services (All underlying systems)

**In Progress (Mostly Complete)**:
- 🟡 Citation Creation (80% - Main feature)
- 🟡 Activity Analytics (75% - Performance tracking)
- 🟡 Citation Lookup (70% - Search functionality)

**Planned for Future**:
- 🔵 Manual Scanning (License plate reader integration)
- 🔵 Municipal Citations (Alternative citation type)
- 🔵 Reports & Export (Data analysis)
- 🔵 Settings & Preferences (User customization)

### Why This Architecture Matters

**For You (Client)**:
- App works reliably for your officers
- Can handle thousands of citations daily
- Easy to maintain and update
- Future features can be added without breaking existing ones

**For Your Officers**:
- Works in any situation (online or offline)
- Fast and responsive
- Professional appearance builds trust with community
- Easy to learn and use

**For Your Developers**:
- Code is organized and easy to understand
- New developers can start coding quickly
- Patterns are consistent throughout
- Testing and debugging is straightforward

### Mobile App Industry Standards

This app follows industry best practices:

✓ Uses proven frameworks (Flutter, GetX)
✓ Tested architecture patterns (MVCS)
✓ Professional error handling
✓ Secure authentication
✓ Offline-first design
✓ Responsive UI for all devices
✓ Performance optimized
✓ Accessibility considerations
✓ Internationalization (multiple languages)
✓ Clean code structure

### Scale and Performance

The app is designed to handle:
- **10,000+ Citations Daily**: Can process high volume
- **Multiple Agencies**: Serves 40+ different organizations
- **Slow Networks**: Works on 3G and rural connections
- **Low Battery**: Minimal battery drain
- **Months of Data**: Caches efficiently without bloating

### Future Expansion

Ready to add:
- More languages (Spanish, Portuguese, etc.)
- Real-time GPS tracking
- Advanced filtering and reporting
- Third-party integrations
- Web dashboard
- Automated ticket fulfillment

---

---

**Version**: 1.0.0
**Last Updated**: February 2026
**Supported Platforms**: iOS 12+, Android 8+
**Maintainers**: Park Enforcement Development Team
