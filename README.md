# ChipCycle


## E-Waste Management Made Simple

ChipCycle is an Android application designed to address the growing challenge of electronic waste management by connecting users with nearby e-waste collection centers. The app helps users locate, learn about, and responsibly dispose of electronic waste through an intuitive interface and educational resources.


## Features

- **Locate Nearby E-Waste Centers**: Find collection centers based on your current location or by searching specific states
- **Interactive Maps**: View centers on an interactive map with directions
- **Center Details**: Access important information about centers including:
  - Operating hours
  - Accepted items
  - Contact information
  - Address and directions
- **E-Waste Educational Guide**: Learn about proper disposal methods and environmental impact
- **Recycling Tips**: Get practical advice on extending device lifespan and responsible consumption
- **User Authentication**: Securely access personalized features with Google Sign-In or email
- **Bookmarks**: Save favorite recycling centers for quick access

## Technologies Used

### Frontend
- Kotlin with MVVM Architecture
- Material Design Components
- Jetpack Navigation Component
- Location Services (FusedLocationProviderClient)
- Firebase Authentication
- Retrofit for API communication
- SharedPreferences for local storage

### Backend
- Node.js with Express
- MongoDB for data storage
- Google Geocode API for location services
- JWT for secure authentication

## Installation

### Prerequisites
- Android Studio Arctic Fox or higher
- Minimum SDK: Android 8.0 (API level 26)
- Google Play Services installed on device

### Steps
1. Clone the repository
```bash
git clone https://github.com/SujalSharma-tech/ChipCycle.git
```

2. Open the project in Android Studio

3. Sync Gradle files and install dependencies

4. Create a `local.properties` file with your API keys:
```
MAPS_API_KEY=your_google_maps_key
```

5. Build and run the application on your device or emulator

## Usage

1. **Sign In**: Create an account or sign in using Google credentials
2. **Allow Location**: Grant location permissions for finding nearby centers
3. **Find Centers**: Browse the map or search by state to find recycling points
4. **Get Details**: Tap on a center to see full information and directions
5. **Learn**: Access the e-waste guide and tips sections for educational content

## API Endpoints

The ChipCycle backend provides the following main endpoints:

### `/location`
- **Method**: POST
- **Body**: 
  - `state` (string): State name to search for centers
- **Response**: Returns a list of e-waste collection centers in the specified state
- **Example**: `GET /location?state=California`

### `/searchbystate`
- **Method**: POST
- **Body**: 
  - `state` (string): State name to search for centers
- **Response**: Returns centers based on the state parameter
- **Example**: `GET /searchbystate?state=Texas`


## Contributing

We welcome contributions to ChipCycle! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


## Acknowledgements

- [Google Maps Platform](https://developers.google.com/maps)
- [Material Design](https://material.io/design)
- [Firebase](https://firebase.google.com/)
- All the e-waste collection centers partnering with us

---

**ChipCycle** - Responsible e-waste disposal at your fingertips.

