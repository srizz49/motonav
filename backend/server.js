const http = require('http');
const url = require('url');

const PORT = 80; // or 3000, but app default base URL uses localhost (10.0.2.2 points to port 80/8080)

// In-memory data store
let users = [
    { id: "user_1", name: "Alex Rider", email: "alex@motonav.com", phone: "+1234567890", bio: "Adventure motorcyclist & tourer.", profileImageUrl: "", totalRides: 42, totalKm: 12500, savedPlacesCount: 8, bikesCount: 2 }
];

let bikes = [
    { id: "bike_1", name: "Royal Enfield Himalayan", brand: "Royal Enfield", model: "Himalayan 450", year: 2024, engineCc: 452, imageUrl: "" },
    { id: "bike_2", name: "KTM Adventure 390", brand: "KTM", model: "390 Adventure", year: 2023, engineCc: 373, imageUrl: "" }
];

let scenicPlaces = [
    { id: "place_1", title: "Blue Ridge Parkway", description: "Breathtaking mountain vistas and sweeping curves.", imageUrl: "", category: "Mountain Pass", rating: 4.9, distanceKm: 18.5, isSaved: true, latitude: 35.6131, longitude: -82.5511 },
    { id: "place_2", title: "Pacific Coast Highway", description: "Iconic coastal road with ocean breeze and sunset views.", imageUrl: "", category: "Coastal", rating: 4.8, distanceKm: 42.0, isSaved: false, latitude: 36.6002, longitude: -121.8947 }
];

let rides = [
    { id: "ride_1", title: "Weekend Mountain Pass Loop", date: "2025-05-12", distanceKm: 145.2, duration: "2h 45m", routeType: "Scenic", imageUrl: "" },
    { id: "ride_2", title: "Coastal Sunset Ride", date: "2025-05-10", distanceKm: 85.0, duration: "1h 30m", routeType: "Fastest", imageUrl: "" }
];

let photos = [
    { id: "photo_1", userId: "user_1", userName: "Alex Rider", userAvatar: "", imageUrl: "", caption: "Golden hour at the peak!", locationName: "Blue Ridge Summit", latitude: 35.6131, longitude: -82.5511, timestamp: "2 hours ago", likesCount: 24, tags: ["mountain", "sunset", "himalayan"] }
];

const server = http.createServer((req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;
    const query = parsedUrl.query;

    // Enable CORS
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (req.method === 'OPTIONS') {
        res.writeHead(204);
        res.end();
        return;
    }

    let body = '';
    req.on('data', chunk => {
        body += chunk.toString();
    });

    req.on('end', () => {
        let jsonBody = {};
        try {
            if (body) jsonBody = JSON.parse(body);
        } catch (e) {
            // ignore JSON parse error for non-json
        }

        const sendResponse = (success, message, data) => {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ success, message, data }));
        };

        console.log(`[${req.method}] ${pathname}`, query, jsonBody);

        // 1. Auth: Login
        if (pathname === '/motonav_api/api/auth/login.php' && req.method === 'POST') {
            const { email, password } = jsonBody;
            const user = users.find(u => u.email === email);
            if (user) {
                return sendResponse(true, "Login successful", user);
            } else {
                return sendResponse(false, "User not found. Please sign up.", null);
            }
        }

        // 2. Auth: Register
        if (pathname === '/motonav_api/api/auth/register.php' && req.method === 'POST') {
            const { name, email, password } = jsonBody;
            if (users.find(u => u.email === email)) {
                return sendResponse(false, "Email already registered.", null);
            }
            const newUser = {
                id: "user_" + (users.length + 1),
                name: name || "Rider",
                email: email,
                phone: "",
                bio: "MotoNav Rider",
                profileImageUrl: "",
                totalRides: 0,
                totalKm: 0,
                savedPlacesCount: 0,
                bikesCount: 0
            };
            users.push(newUser);
            return sendResponse(true, "Registration successful", newUser);
        }

        // 3. Profile: Get
        if (pathname === '/motonav_api/api/profile/get.php' && req.method === 'GET') {
            const userId = query.user_id || "user_1";
            const user = users.find(u => u.id === userId) || users[0];
            return sendResponse(true, "Success", user);
        }

        // 4. Profile: Update
        if (pathname === '/motonav_api/api/profile/update.php' && req.method === 'POST') {
            const { userId, name, phone, bio } = jsonBody;
            const user = users.find(u => u.id === userId);
            if (user) {
                if (name) user.name = name;
                if (phone) user.phone = phone;
                if (bio) user.bio = bio;
                return sendResponse(true, "Profile updated", null);
            }
            return sendResponse(false, "User not found", null);
        }

        // 5. Bikes: Get / Add / Delete
        if (pathname === '/motonav_api/api/bikes/index.php') {
            if (req.method === 'GET') {
                return sendResponse(true, "Success", bikes);
            }
            if (req.method === 'POST') {
                const newBike = {
                    id: "bike_" + (bikes.length + 1),
                    name: jsonBody.name || "My Bike",
                    brand: jsonBody.brand || "Brand",
                    model: jsonBody.model || "Model",
                    year: jsonBody.year || 2024,
                    engineCc: jsonBody.engineCc || 500,
                    imageUrl: jsonBody.imageUrl || ""
                };
                bikes.push(newBike);
                return sendResponse(true, "Bike added", newBike);
            }
            if (req.method === 'DELETE') {
                const bikeId = query.id;
                bikes = bikes.filter(b => b.id !== bikeId);
                return sendResponse(true, "Bike deleted", null);
            }
        }

        // 6. Places: Index & Toggle Save
        if (pathname === '/motonav_api/api/places/index.php' && req.method === 'GET') {
            return sendResponse(true, "Success", scenicPlaces);
        }

        if (pathname === '/motonav_api/api/places/toggle_save.php' && req.method === 'POST') {
            const placeId = jsonBody.placeId;
            const place = scenicPlaces.find(p => p.id === placeId);
            if (place) {
                place.isSaved = !place.isSaved;
                return sendResponse(true, "Success", { isSaved: place.isSaved });
            }
            return sendResponse(false, "Place not found", { isSaved: false });
        }

        // 7. Routes: Index
        if (pathname === '/motonav_api/api/routes/index.php' && req.method === 'GET') {
            const routeData = {
                scenic: {
                    title: "Scenic Mountain Ridge Route",
                    distanceKm: 124.5,
                    estimatedDuration: "2h 15m",
                    elevationGainM: 1420,
                    twistinessScore: "9.2/10",
                    description: "Winding asphalt through high altitude mountain passes with scenic overlooks.",
                    waypointsCount: 5
                },
                fastest: {
                    title: "Fastest Highway Corridor",
                    distanceKm: 98.0,
                    estimatedDuration: "1h 10m",
                    elevationGainM: 320,
                    twistinessScore: "3.5/10",
                    description: "Direct multi-lane highway for quick transit.",
                    waypointsCount: 2
                }
            };
            return sendResponse(true, "Success", routeData);
        }

        // 8. Rides: Index & Record
        if (pathname === '/motonav_api/api/rides/index.php') {
            if (req.method === 'GET') {
                return sendResponse(true, "Success", rides);
            }
            if (req.method === 'POST') {
                const newRide = {
                    id: "ride_" + (rides.length + 1),
                    title: jsonBody.title || "Morning Ride",
                    date: jsonBody.date || new Date().toISOString().split('T')[0],
                    distanceKm: jsonBody.distanceKm || 50.0,
                    duration: jsonBody.duration || "1h 00m",
                    routeType: jsonBody.routeType || "Scenic",
                    imageUrl: jsonBody.imageUrl || ""
                };
                rides.push(newRide);
                return sendResponse(true, "Ride recorded", newRide);
            }
        }

        // 9. Photos: Index & Upload
        if (pathname === '/motonav_api/api/photos/index.php' && req.method === 'GET') {
            return sendResponse(true, "Success", photos);
        }

        if (pathname === '/motonav_api/api/photos/upload.php' && req.method === 'POST') {
            const newPhoto = {
                id: "photo_" + (photos.length + 1),
                userId: jsonBody.userId || "user_1",
                userName: jsonBody.userName || "Alex Rider",
                userAvatar: "",
                imageUrl: jsonBody.imageUrl || "",
                caption: jsonBody.caption || "Amazing ride!",
                locationName: jsonBody.locationName || "Scenic Viewpoint",
                latitude: jsonBody.latitude || 35.6131,
                longitude: jsonBody.longitude || -82.5511,
                timestamp: "Just now",
                likesCount: 0,
                tags: jsonBody.tags || ["ride", "motonav"]
            };
            photos.push(newPhoto);
            return sendResponse(true, "Photo uploaded", newPhoto);
        }

        // 10. Fuel: Stations
        if (pathname === '/motonav_api/api/fuel/stations.php' && req.method === 'GET') {
            const fuelData = {
                petrolPrice: 1.45,
                stations: [
                    { id: "st_1", name: "Shell Express", brand: "Shell", address: "124 Highway 101", distanceKm: 2.1, isOpen24Hours: true, hasAirWater: true },
                    { id: "st_2", name: "BP Fuel & Go", brand: "BP", address: "502 Mountain Pass Rd", distanceKm: 5.4, isOpen24Hours: true, hasAirWater: false },
                    { id: "st_3", name: "Chevron Station", brand: "Chevron", address: "89 Valley Way", distanceKm: 8.0, isOpen24Hours: false, hasAirWater: true }
                ]
            };
            return sendResponse(true, "Success", fuelData);
        }

        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: "Endpoint not found: " + pathname, data: null }));
    });
});

server.listen(80, '0.0.0.0', () => {
    console.log('MotoNav Mock Backend running on port 80 (http://localhost/motonav_api/)');
});
