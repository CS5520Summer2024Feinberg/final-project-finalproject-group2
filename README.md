# GetUp - Android Wake-Up and Photo Sharing App

## Overview

GetUp is an Android application designed to help users establish a healthy wake-up routine and share their morning experiences. The app combines user authentication, personalized wake-up suggestions based on location and sunrise times, and a photo sharing feature to encourage users to start their day positively.

## Features

1. **User Authentication**
   - Sign up and login functionality
   - Integration with Firebase Authentication

2. **Personalized Wake-Up Advice**
   - Location-based sunrise time calculation
   - Customized wake-up suggestions based on user's geographical location

3. **Photo Sharing**
   - Capture photos using the device camera
   - Select photos from the device gallery
   - View captured/selected photos in a scrollable list
   - Share photos to Instagram or other platforms

4. **Data Persistence**
   - Store user data and preferences using Firebase Firestore
   - Local storage of photo URIs

## Technical Stack

- Language: Java
- Architecture: MVVM (Model-View-ViewModel)
- Authentication: Firebase Authentication
- Database: Firebase Firestore
- Location Services: Google Location Services API
- HTTP Requests: Retrofit
- Dependency Injection: Manual (potential for Hilt integration)
- UI Components: RecyclerView, AlertDialog, ConstraintLayout

## Project Structure

- `application/`: Contains the main Application class
- `entity/`: Data models
- `onboarding/`: User authentication related classes
- `photoSharing/`: Photo capture, display, and sharing functionality
- `repository/`: Data access layer
- `suggestion/`: Wake-up suggestion generation and management

## Setup and Installation

1. Clone the repository
   ```
   git clone https://github.com/your-username/getup-android.git
   ```
2. Open the project in Android Studio
3. Set up a Firebase project and add the `google-services.json` file to the app directory
4. Build and run the application on an Android device or emulator

## Future Improvements

- Implement Hilt for dependency injection
- Break down large activities into smaller components or fragments
- Enhance error handling and user feedback
- Implement unit and integration tests
- Add more social sharing options
