# B07Project

**Use [tempmailo.com](https://tempmailo.com/) to create emails for signup, login, email verification, password reset, and related testing scenarios.**

---

## Miklagard: Domestic Violence Prevention App

Miklagard is a mobile application designed to support individuals experiencing domestic violence. It provides immediate resources, personalized help, and secure data storage to ensure user safety and empowerment.

**Platform:** Android 16  
**Language:** Java  
**Backend:** Firebase (Auth, Database, Storage)

---

## Features

### 1. User Authentication & Security
- **Sign-Up & Login:** Secure authentication using Firebase Auth.
- **Email Verification:** Ensures user and system safety through verified email addresses.
- **Password Reset:** Users can reset their password via email.
- **PIN Protection:** Users create a PIN stored securely in SharedPreferences (using AndroidX Security), backed by the Android Keystore. PIN required for sensitive actions.
- **PIN Management:** Includes PIN setup, login, and recovery.

### 2. Personalized Questionnaire & Safety Plan
- **Initial Survey:** Users complete a questionnaire that analyzes their situation.
- **Personalized Advice:** Based on responses, the app provides tailored safety tips, planning suggestions, and local resources.
- **Editable Survey:** Users can update their answers, with the app generating new recommendations.
- **Data Storage:** Questionnaire data is securely stored as JSON files in Firebase.

### 3. Emergency Exit
- **Quick Escape:** An emergency button instantly closes the app and redirects the user to a neutral, inconspicuous website for safety.

### 4. Secure Information Storage
- **Document Storage:** Users can upload and manage important documents (court orders, IDs, passports, medication info, emergency contacts, etc.) in Firebase.
- **Cloud Access:** Secure access to critical information anytime, anywhere.
- **CRUD Operations:** Add, edit, and delete items in each category, with encryption for privacy.

### 5. Reminders & Notifications
- **Recurring Reminders:** Customizable, recurring reminders for safety-related tasks (e.g., medication, appointments).
- **Push Notifications:** Optional notifications for upcoming reminders.
- **Secure Access:** Tapping a notification prompts user login, protecting sensitive info.

### 6. Support Connection & Resources
- **Resource Directory:** In-app listing of local victim services, emergency hotlines, shelters, legal aid, and more.
- **Map Integration:** Visual map showing nearby resources and support centers.
- **Direct Links:** Easy access to external support services.

---

## Technical Highlights

- **Firebase Integration:** Real-time database and secure cloud storage for user data and resources.
- **AndroidX Security:** PIN and sensitive data protected through encrypted SharedPreferences and Android Keystore.
- **Modular UI:** Intuitive user interface designed for quick navigation and emergency actions.
- **Unit & Integration Testing:** Includes unit tests and Mockito-based tests to ensure reliability and security.

---

## Contribution

_Special thanks to our group members:_

- **Taha** (Scrum Master)
  - Firebase authentication (signup, login, password reset, email verification)
  - PIN setup, storage, and authentication
  - Questionnaire implementation
  - Git management, group leadership, Firebase configuration

- **Haowen** (h.santiago, Scrum Master backup)
  - Reminder and notification system
  - Git management, [add your work here...]

- **Herman** (herman77777717)
  - Emergency support connection features
  - [add your work here...]

- **Farheen**
  - PIN login and storage logic
  - Unit and Mockito tests
  - [add your work here...]

- **Gavriel** (Gravy)
  - Support connections and resource directory
  - [add your work here...]

_(Feel free to highlight your contributions!)_

---

## Demo

**Display video:** _To be announced_

---

^^chinese luigi save us
