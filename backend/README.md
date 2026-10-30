# MotoNav Backend Server 🚀

This is a lightweight Node.js backend server implementing all API endpoints required by the MotoNav Android app (`MotoNavApiService.kt`).

## Running the Backend

1. Make sure [Node.js](https://nodejs.org/) is installed on your machine.
2. Open a terminal in this `backend` directory.
3. Run the server using Node:
   ```bash
   node server.js
   ```
   *(Note: The server listens on port 80, which matches `http://10.0.2.2/motonav_api/` used by the Android emulator).*

## Endpoints Implemented

- `POST /motonav_api/api/auth/login.php`
- `POST /motonav_api/api/auth/register.php`
- `GET /motonav_api/api/profile/get.php`
- `POST /motonav_api/api/profile/update.php`
- `GET /motonav_api/api/bikes/index.php`
- `POST /motonav_api/api/bikes/index.php`
- `DELETE /motonav_api/api/bikes/index.php`
- `GET /motonav_api/api/places/index.php`
- `POST /motonav_api/api/places/toggle_save.php`
- `GET /motonav_api/api/routes/index.php`
- `GET /motonav_api/api/rides/index.php`
- `POST /motonav_api/api/rides/index.php`
- `GET /motonav_api/api/photos/index.php`
- `POST /motonav_api/api/photos/upload.php`
- `GET /motonav_api/api/fuel/stations.php`
