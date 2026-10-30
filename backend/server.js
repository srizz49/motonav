const http = require('http');
const url = require('url');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const DB_FILE = path.join(__dirname, 'data', 'db.json');

// Ensure database folder exists
if (!fs.existsSync(path.join(__dirname, 'data'))) {
    fs.mkdirSync(path.join(__dirname, 'data'), { recursive: true });
}

// Initial default database structure if file doesn't exist
const initialDb = {
    users: [
        {
            id: "user_1001",
            name: "Rider One",
            email: "rider@motonav.com",
            passwordHash: "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", // SHA-256 for 'password'
            profileImageUri: null,
            bio: "Avid motorcycle explorer & twisty road enthusiast.",
            memberSince: "May 2025",
            totalRidesCount: 2,
            totalDistanceKm: 352.5
        }
    ],
    bikes: [
        {
            id: "bike_1001",
            userId: "user_1001",
            make: "Royal Enfield",
            model: "Himalayan 450",
            year: 2024,
            mileageKmpl: 30.0,
            fuelTankCapacityLitres: 17.0,
            isDefault: true
        }
    ],
    scenicPlaces: [
        {
            id: "place_1",
            name: "Western Ghats Twisty Loop",
            category: "Mountain Pass",
            description: "Sweeping curves, hairpin turns, and dense fog during monsoons. Premier motorcycle destination.",
            imageUrl: "https://picsum.photos/id/1018/800/400",
            rating: 4.9,
            reviewCount: 312,
            distanceKm: 48.0,
            latitude: 18.5204,
            longitude: 73.8567,
            isSaved: true
        },
        {
            id: "place_2",
            name: "Malshej Ghat Viewpoint",
            category: "Viewpoint",
            description: "Waterfall cascading directly onto the mountain road. Epic photography spots.",
            imageUrl: "https://picsum.photos/id/1015/800/400",
            rating: 4.8,
            reviewCount: 245,
            distanceKm: 85.0,
            latitude: 19.3400,
            longitude: 73.7800,
            isSaved: true
        },
        {
            id: "place_3",
            name: "Tamhini Ghat Canyon",
            category: "Waterfall Pass",
            description: "Lush valley road with cliffside views and continuous winding tarmac.",
            imageUrl: "https://picsum.photos/id/1043/800/400",
            rating: 4.7,
            reviewCount: 189,
            distanceKm: 62.0,
            latitude: 18.4700,
            longitude: 73.4300,
            isSaved: false
        }
    ],
    rides: [
        {
            id: "ride_1001",
            userId: "user_1001",
            title: "Sunday Western Ghats Explorer",
            date: "May 10, 2025",
            distanceKm: 142.5,
            durationMinutes: 195,
            avgSpeedKmh: 43.8,
            fuelCostInr: 486.5,
            routeType: "SCENIC",
            bikeModel: "Royal Enfield Himalayan 450"
        }
    ],
    photos: [
        {
            id: "photo_1001",
            userId: "user_1001",
            imageUri: "https://picsum.photos/id/1018/600/600",
            caption: "Early morning mist over Western Ghats curves!",
            locationName: "Western Ghats Loop",
            latitude: 18.5204,
            longitude: 73.8567,
            tags: ["Himalayan450", "TwistyRoads", "MorningRide"],
            timestamp: "2 days ago",
            isUploadedRemote: true,
            uploaderName: "Rider One"
        }
    ]
};

function loadDb() {
    try {
        if (fs.existsSync(DB_FILE)) {
            const data = fs.readFileSync(DB_FILE, 'utf8');
            return JSON.parse(data);
        }
    } catch (e) {
        console.error("Error loading db.json:", e);
    }
    fs.writeFileSync(DB_FILE, JSON.stringify(initialDb, null, 2));
    return initialDb;
}

function saveDb(db) {
    try {
        fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2));
    } catch (e) {
        console.error("Error saving db.json:", e);
    }
}

let db = loadDb();

const server = http.createServer((req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;
    const query = parsedUrl.query;

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
        } catch (e) {}

        const sendResponse = (success, message, data) => {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ success, message, data }));
        };

        console.log(`[${req.method}] ${pathname}`, query);

        // 1. Auth: Login
        if (pathname === '/motonav_api/api/auth/login.php' && req.method === 'POST') {
            const { email, password } = jsonBody;
            if (!email || !password) {
                return sendResponse(false, "Email and password are required.", null);
            }
            const user = db.users.find(u => u.email.toLowerCase() === email.trim().toLowerCase());
            if (!user) {
                return sendResponse(false, "No account found with this email. Please register.", null);
            }
            return sendResponse(true, "Login successful", user);
        }

        // 2. Auth: Register
        if (pathname === '/motonav_api/api/auth/register.php' && req.method === 'POST') {
            const { name, email, password } = jsonBody;
            if (!name || !email || !password || password.length < 6) {
                return sendResponse(false, "Please provide a valid name, email, and password (min 6 chars).", null);
            }
            const existing = db.users.find(u => u.email.toLowerCase() === email.trim().toLowerCase());
            if (existing) {
                return sendResponse(false, "An account with this email already exists. Please log in.", null);
            }
            const newUser = {
                id: "user_" + Date.now(),
                name: name.trim(),
                email: email.trim().toLowerCase(),
                profileImageUri: null,
                bio: "MotoNav Explorer",
                memberSince: "May 2025",
                totalRidesCount: 0,
                totalDistanceKm: 0.0
            };
            db.users.push(newUser);
            saveDb(db);
            return sendResponse(true, "Account created successfully!", newUser);
        }

        // 3. Profile: Get
        if (pathname === '/motonav_api/api/profile/get.php' && req.method === 'GET') {
            const userId = query.user_id;
            const user = db.users.find(u => u.id === userId) || db.users[0];
            return sendResponse(true, "Success", user);
        }

        // 4. Profile: Update
        if (pathname === '/motonav_api/api/profile/update.php' && req.method === 'POST') {
            const { id, bio, name } = jsonBody;
            const user = db.users.find(u => u.id === id || u.id === query.user_id) || db.users[0];
            if (user) {
                if (bio !== undefined) user.bio = bio;
                if (name !== undefined) user.name = name;
                saveDb(db);
                return sendResponse(true, "Profile updated successfully", user);
            }
            return sendResponse(false, "User not found", null);
        }

        // 5. Bikes: Get / Add / Delete
        if (pathname === '/motonav_api/api/bikes/index.php') {
            if (req.method === 'GET') {
                const userId = query.user_id;
                const userBikes = db.bikes.filter(b => b.userId === userId || !b.userId);
                return sendResponse(true, "Success", userBikes);
            }
            if (req.method === 'POST') {
                const userId = query.user_id || jsonBody.userId || "user_1001";
                const newBike = {
                    id: "bike_" + Date.now(),
                    userId: userId,
                    make: jsonBody.make || "Custom Make",
                    model: jsonBody.model || "Custom Model",
                    year: jsonBody.year || 2024,
                    mileageKmpl: jsonBody.mileageKmpl || 30.0,
                    fuelTankCapacityLitres: jsonBody.fuelTankCapacityLitres || 15.0,
                    isDefault: jsonBody.isDefault || false
                };
                if (newBike.isDefault) {
                    db.bikes.forEach(b => { if (b.userId === userId) b.isDefault = false; });
                }
                db.bikes.push(newBike);
                saveDb(db);
                return sendResponse(true, "Bike added to garage", newBike);
            }
            if (req.method === 'DELETE') {
                const bikeId = query.id;
                db.bikes = db.bikes.filter(b => b.id !== bikeId);
                saveDb(db);
                return sendResponse(true, "Bike removed", null);
            }
        }

        // 6. Places: Get & Toggle Save
        if (pathname === '/motonav_api/api/places/index.php' && req.method === 'GET') {
            return sendResponse(true, "Success", db.scenicPlaces);
        }

        if (pathname === '/motonav_api/api/places/toggle_save.php' && req.method === 'POST') {
            const placeId = jsonBody.placeId;
            const place = db.scenicPlaces.find(p => p.id === placeId);
            if (place) {
                place.isSaved = !place.isSaved;
                saveDb(db);
                return sendResponse(true, "Success", { isSaved: place.isSaved });
            }
            return sendResponse(false, "Place not found", { isSaved: false });
        }

        // 7. Routes: Index
        if (pathname === '/motonav_api/api/routes/index.php' && req.method === 'GET') {
            const routeData = {
                scenic: {
                    id: "route_scenic",
                    title: "Western Ghats Twisties & Waterfalls",
                    startLocationName: "Pune City",
                    destinationName: "Mahabaleshwar Peak",
                    type: "SCENIC",
                    distanceKm: 142.5,
                    durationMinutes: 195,
                    scenicScore: 9,
                    twistyScore: 9,
                    waypoints: [
                        { id: "wp_1", name: "Lonavala Viewpoint", address: "NH 48, Lonavala", latitude: 18.7557, longitude: 73.4091, isStopover: true, isScenicViewpoint: true },
                        { id: "wp_2", name: "Khandala Hairpin Bend", address: "Ghat Section", latitude: 18.7610, longitude: 73.3762, isStopover: false, isScenicViewpoint: true },
                        { id: "wp_3", name: "Tiger Point Refreshments", address: "Amby Valley Rd", latitude: 18.6942, longitude: 73.3812, isStopover: true, isScenicViewpoint: true }
                    ],
                    estimatedFuelCostInr: 486.5,
                    roadConditions: "Smooth mountain curves, 12 hairpin bends, lush scenic vistas",
                    description: "Selected for maximum riding enjoyment with sweeping valley views and motorcycling hotspots."
                },
                fastest: {
                    id: "route_fastest",
                    title: "NH-48 Express Corridor",
                    startLocationName: "Pune City",
                    destinationName: "Mahabaleshwar Peak",
                    type: "FASTEST",
                    distanceKm: 120.0,
                    durationMinutes: 145,
                    scenicScore: 5,
                    twistyScore: 4,
                    waypoints: [],
                    estimatedFuelCostInr: 410.0,
                    roadConditions: "4-lane dual carriageway highway, bypass roads",
                    description: "Optimized for minimal transit time and straight highway cruising."
                }
            };
            return sendResponse(true, "Success", routeData);
        }

        // 8. Rides: Get & Record
        if (pathname === '/motonav_api/api/rides/index.php') {
            if (req.method === 'GET') {
                const userId = query.user_id;
                const userRides = db.rides.filter(r => r.userId === userId || !r.userId);
                return sendResponse(true, "Success", userRides);
            }
            if (req.method === 'POST') {
                const userId = query.user_id || jsonBody.userId || "user_1001";
                const newRide = {
                    id: "ride_" + Date.now(),
                    userId: userId,
                    title: jsonBody.title || "Rider Cruise",
                    date: "Today",
                    distanceKm: jsonBody.distanceKm || 50.0,
                    durationMinutes: jsonBody.durationMinutes || 60,
                    avgSpeedKmh: jsonBody.durationMinutes > 0 ? (jsonBody.distanceKm / (jsonBody.durationMinutes / 60.0)) : 50.0,
                    fuelCostInr: jsonBody.fuelCostInr || 200.0,
                    routeType: jsonBody.routeType || "SCENIC",
                    bikeModel: jsonBody.bikeModel || "Custom Motorcycle"
                };
                db.rides.unshift(newRide);

                const user = db.users.find(u => u.id === userId);
                if (user) {
                    user.totalRidesCount += 1;
                    user.totalDistanceKm += newRide.distanceKm;
                }

                saveDb(db);
                return sendResponse(true, "Ride recorded in history", newRide);
            }
        }

        // 9. Photos: Get & Upload
        if (pathname === '/motonav_api/api/photos/index.php' && req.method === 'GET') {
            return sendResponse(true, "Success", db.photos);
        }

        if (pathname === '/motonav_api/api/photos/upload.php' && req.method === 'POST') {
            const newPhoto = {
                id: "photo_" + Date.now(),
                userId: jsonBody.userId || "user_1001",
                imageUri: jsonBody.imageUri || "https://picsum.photos/id/1018/600/600",
                caption: jsonBody.caption || "Riding through scenic routes!",
                locationName: jsonBody.locationName || "Scenic Viewpoint",
                latitude: jsonBody.latitude || 18.5204,
                longitude: jsonBody.longitude || 73.8567,
                tags: jsonBody.tags || ["ride", "motonav"],
                timestamp: "Just now",
                isUploadedRemote: true,
                uploaderName: jsonBody.uploaderName || "Rider"
            };
            db.photos.unshift(newPhoto);
            saveDb(db);
            return sendResponse(true, "Photo posted to community map", newPhoto);
        }

        // 10. Fuel: Stations
        if (pathname === '/motonav_api/api/fuel/stations.php' && req.method === 'GET') {
            const fuelData = {
                petrolPrice: 104.50,
                stations: [
                    { id: "fs_1", name: "Shell Highway Station", brand: "Shell", address: "NH-48 Km 34", distanceKm: 3.2, isOpen24Hours: true, hasAirWater: true },
                    { id: "fs_2", name: "HP Auto Energy Point", brand: "HPCL", address: "Lonavala Bypass Rd", distanceKm: 8.5, isOpen24Hours: true, hasAirWater: true },
                    { id: "fs_3", name: "Indian Oil Express Fuel", brand: "IOCL", address: "Mountain Pass Junction", distanceKm: 14.1, isOpen24Hours: true, hasAirWater: false },
                    { id: "fs_4", name: "BPCL Rider Oasis", brand: "BPCL", address: "Ghat Exit Km 92", distanceKm: 22.0, isOpen24Hours: false, hasAirWater: true }
                ]
            };
            return sendResponse(true, "Success", fuelData);
        }

        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: "Endpoint not found: " + pathname, data: null }));
    });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`MotoNav Persistent Realtime Backend running on port ${PORT} (http://localhost:${PORT}/motonav_api/)`);
});
