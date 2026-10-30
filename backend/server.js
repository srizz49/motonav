const http = require('http');
const url = require('url');

const PORT = 3000;

// In-memory real-time data stores
let users = [
    {
        id: "user_1",
        name: "Alex Rider",
        email: "alex@motonav.com",
        profileImageUri: null,
        bio: "Avid motorcycle explorer & twisty road enthusiast.",
        memberSince: "March 2024",
        totalRidesCount: 28,
        totalDistanceKm: 3450.0
    }
];

let bikes = [
    {
        id: "bike_1",
        make: "Royal Enfield",
        model: "Himalayan 450",
        year: 2024,
        mileageKmpl: 30.0,
        fuelTankCapacityLitres: 17.0,
        isDefault: true
    },
    {
        id: "bike_2",
        make: "KTM",
        model: "390 Adventure",
        year: 2023,
        mileageKmpl: 28.5,
        fuelTankCapacityLitres: 14.5,
        isDefault: false
    }
];

let scenicPlaces = [
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
];

let rides = [
    {
        id: "ride_1",
        title: "Sunday Western Ghats Explorer",
        date: "Oct 24, 2024",
        distanceKm: 142.5,
        durationMinutes: 195,
        avgSpeedKmh: 43.8,
        fuelCostInr: 486.5,
        routeType: "SCENIC",
        bikeModel: "Royal Enfield Himalayan 450"
    },
    {
        id: "ride_2",
        title: "Coastal Highway Cruise",
        date: "Oct 12, 2024",
        distanceKm: 210.0,
        durationMinutes: 260,
        avgSpeedKmh: 48.4,
        fuelCostInr: 717.5,
        routeType: "FASTEST",
        bikeModel: "Royal Enfield Himalayan 450"
    }
];

let photos = [
    {
        id: "photo_1",
        imageUri: "https://picsum.photos/id/1018/600/600",
        caption: "Early morning mist over Western Ghats curves!",
        locationName: "Western Ghats Loop",
        latitude: 18.5204,
        longitude: 73.8567,
        tags: ["Himalayan450", "TwistyRoads", "MorningRide"],
        timestamp: "2 days ago",
        isUploadedRemote: true,
        uploaderName: "Alex Rider"
    }
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
            // non-json body fallback
        }

        const sendResponse = (success, message, data) => {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ success, message, data }));
        };

        console.log(`[${req.method}] ${pathname}`, query, jsonBody);

        // 1. Auth: Login
        if (pathname === '/motonav_api/api/auth/login.php' && req.method === 'POST') {
            const { email, password } = jsonBody;
            let user = users.find(u => u.email.toLowerCase() === (email || '').toLowerCase());
            if (user) {
                return sendResponse(true, "Login successful", user);
            } else {
                // Auto-create user on login if not found for seamless real-time usage
                const newUser = {
                    id: "user_" + Date.now(),
                    name: (email || 'Rider').split('@')[0].replace(/^\w/, c => c.toUpperCase()),
                    email: email || "rider@motonav.com",
                    profileImageUri: null,
                    bio: "MotoNav Explorer",
                    memberSince: "May 2025",
                    totalRidesCount: 0,
                    totalDistanceKm: 0.0
                };
                users.push(newUser);
                return sendResponse(true, "Account created and logged in", newUser);
            }
        }

        // 2. Auth: Register
        if (pathname === '/motonav_api/api/auth/register.php' && req.method === 'POST') {
            const { name, email } = jsonBody;
            let existing = users.find(u => u.email.toLowerCase() === (email || '').toLowerCase());
            if (existing) {
                return sendResponse(true, "Welcome back", existing);
            }
            const newUser = {
                id: "user_" + Date.now(),
                name: name || "New Rider",
                email: email || "rider@motonav.com",
                profileImageUri: null,
                bio: "New MotoNav Explorer",
                memberSince: "May 2025",
                totalRidesCount: 0,
                totalDistanceKm: 0.0
            };
            users.push(newUser);
            return sendResponse(true, "Registration successful", newUser);
        }

        // 3. Profile: Get
        if (pathname === '/motonav_api/api/profile/get.php' && req.method === 'GET') {
            const userId = query.user_id;
            const user = users.find(u => u.id === userId || u.email === userId) || users[0];
            return sendResponse(true, "Success", user);
        }

        // 4. Profile: Update
        if (pathname === '/motonav_api/api/profile/update.php' && req.method === 'POST') {
            const { id, bio, name } = jsonBody;
            const user = users.find(u => u.id === id || u.id === query.user_id) || users[0];
            if (user) {
                if (bio !== undefined) user.bio = bio;
                if (name !== undefined) user.name = name;
                return sendResponse(true, "Profile updated", user);
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
                    id: "bike_" + Date.now(),
                    make: jsonBody.make || jsonBody.brand || "Custom Make",
                    model: jsonBody.model || "Custom Model",
                    year: jsonBody.year || 2024,
                    mileageKmpl: jsonBody.mileageKmpl || 30.0,
                    fuelTankCapacityLitres: jsonBody.fuelTankCapacityLitres || 15.0,
                    isDefault: jsonBody.isDefault || false
                };
                if (newBike.isDefault) {
                    bikes.forEach(b => b.isDefault = false);
                }
                bikes.push(newBike);
                return sendResponse(true, "Bike added", newBike);
            }
            if (req.method === 'DELETE') {
                const bikeId = query.id;
                bikes = bikes.filter(b => b.id !== bikeId);
                return sendResponse(true, "Bike deleted", null);
            }
        }

        // 6. Places: Get & Toggle Save
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
                return sendResponse(true, "Success", rides);
            }
            if (req.method === 'POST') {
                const newRide = {
                    id: "ride_" + Date.now(),
                    title: jsonBody.title || "New Ride",
                    date: "Today",
                    distanceKm: jsonBody.distanceKm || 50.0,
                    durationMinutes: jsonBody.durationMinutes || 60,
                    avgSpeedKmh: jsonBody.durationMinutes > 0 ? (jsonBody.distanceKm / (jsonBody.durationMinutes / 60.0)) : 50.0,
                    fuelCostInr: jsonBody.fuelCostInr || 200.0,
                    routeType: jsonBody.routeType || "SCENIC",
                    bikeModel: jsonBody.bikeModel || "Custom Bike"
                };
                rides.unshift(newRide);

                // Update user ride stats
                if (users.length > 0) {
                    users[0].totalRidesCount += 1;
                    users[0].totalDistanceKm += newRide.distanceKm;
                }

                return sendResponse(true, "Ride recorded", newRide);
            }
        }

        // 9. Photos: Get & Upload
        if (pathname === '/motonav_api/api/photos/index.php' && req.method === 'GET') {
            return sendResponse(true, "Success", photos);
        }

        if (pathname === '/motonav_api/api/photos/upload.php' && req.method === 'POST') {
            const newPhoto = {
                id: "photo_" + Date.now(),
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
            photos.unshift(newPhoto);
            return sendResponse(true, "Photo uploaded", newPhoto);
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
    console.log(`MotoNav Realtime Backend running on port ${PORT} (http://localhost:${PORT}/motonav_api/)`);
});
