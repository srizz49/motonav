const http = require('http');
const url = require('url');
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');
const crypto = require('crypto');

const PORT = 3000;

// MySQL Pool Configuration for XAMPP / Local MySQL
const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    port: process.env.DB_PORT || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'motonav_db',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
};

const pool = mysql.createPool(dbConfig);

// Helper function to query MySQL
async function queryDb(sql, params = []) {
    try {
        const [rows] = await pool.execute(sql, params);
        return rows;
    } catch (err) {
        console.error("MySQL Query Error:", err.message, "SQL:", sql);
        throw err;
    }
}

// Strict password verification supporting BCrypt ($2y$, $2a$, $2b$), SHA256, and plain text
function verifyPassword(inputPassword, storedHash) {
    if (!storedHash) return false;
    if (storedHash.startsWith('$2a$') || storedHash.startsWith('$2b$') || storedHash.startsWith('$2y$')) {
        try {
            if (bcrypt.compareSync(inputPassword, storedHash)) return true;
        } catch (e) {}
    }
    const sha256 = crypto.createHash('sha256').update(inputPassword).digest('hex');
    if (storedHash.toLowerCase() === sha256.toLowerCase()) return true;
    if (storedHash === inputPassword) return true;
    return false;
}

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

    req.on('end', async () => {
        let jsonBody = {};
        try {
            if (body) jsonBody = JSON.parse(body);
        } catch (e) {}

        const sendResponse = (success, message, data, statusCode = 200) => {
            res.writeHead(statusCode, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ success, message, data }));
        };

        console.log(`[${req.method}] ${pathname}`, query, jsonBody);

        try {
            // 1. Auth: Login
            if (pathname === '/motonav_api/api/auth/login.php' && req.method === 'POST') {
                const { email, password } = jsonBody;
                if (!email || !password) {
                    return sendResponse(false, "Email and password are required.", null);
                }

                const rows = await queryDb(
                    `SELECT id, name, email, password_hash, profile_image_url as profileImageUri, bio,
                            DATE_FORMAT(created_at, '%M %Y') as memberSince
                     FROM users
                     WHERE LOWER(email) = LOWER(?)`,
                    [email.trim()]
                );

                if (rows.length === 0) {
                    return sendResponse(false, "No account found with this email. Please click Sign Up to register.", null);
                }

                const user = rows[0];
                const isValid = verifyPassword(password, user.password_hash);
                if (!isValid) {
                    return sendResponse(false, "Incorrect password. Please try again.", null);
                }

                delete user.password_hash;

                const stats = await queryDb(
                    `SELECT COUNT(*) as ridesCount, COALESCE(SUM(distance_km), 0) as totalKm
                     FROM rides_history WHERE user_id = ?`,
                    [user.id]
                );

                user.totalRidesCount = Number(stats[0].ridesCount) || 0;
                user.totalDistanceKm = Number(stats[0].totalKm) || 0.0;

                return sendResponse(true, "Login successful!", user);
            }

            // 2. Auth: Register
            if (pathname === '/motonav_api/api/auth/register.php' && req.method === 'POST') {
                const { name, email, password } = jsonBody;
                if (!name || !email || !password || password.length < 6) {
                    return sendResponse(false, "Please provide a valid name, email, and password (min 6 chars).", null);
                }

                const existing = await queryDb(
                    `SELECT id FROM users WHERE LOWER(email) = LOWER(?)`,
                    [email.trim()]
                );

                if (existing.length > 0) {
                    return sendResponse(false, "An account with this email address already exists. Please log in.", null);
                }

                const userId = "user_" + Date.now();
                const passwordHash = bcrypt.hashSync(password, 10);

                await queryDb(
                    `INSERT INTO users (id, name, email, password_hash, bio) VALUES (?, ?, ?, ?, ?)`,
                    [userId, name.trim(), email.trim().toLowerCase(), passwordHash, "MotoNav Rider"]
                );

                const newUser = {
                    id: userId,
                    name: name.trim(),
                    email: email.trim().toLowerCase(),
                    profileImageUri: null,
                    bio: "MotoNav Rider",
                    memberSince: "May 2025",
                    totalRidesCount: 0,
                    totalDistanceKm: 0.0
                };

                return sendResponse(true, "Account created successfully in MySQL database!", newUser);
            }

            // 3. Profile: Get
            if (pathname === '/motonav_api/api/profile/get.php' && req.method === 'GET') {
                const userId = query.user_id || "user_1";
                const rows = await queryDb(
                    `SELECT id, name, email, profile_image_url as profileImageUri, bio,
                            DATE_FORMAT(created_at, '%M %Y') as memberSince
                     FROM users WHERE id = ? OR email = ?`,
                    [userId, userId]
                );

                if (rows.length === 0) {
                    return sendResponse(false, "User not found", null);
                }

                const user = rows[0];
                const stats = await queryDb(
                    `SELECT COUNT(*) as ridesCount, COALESCE(SUM(distance_km), 0) as totalKm
                     FROM rides_history WHERE user_id = ?`,
                    [user.id]
                );

                user.totalRidesCount = Number(stats[0].ridesCount) || 0;
                user.totalDistanceKm = Number(stats[0].totalKm) || 0.0;

                return sendResponse(true, "Success", user);
            }

            // 4. Profile: Update
            if (pathname === '/motonav_api/api/profile/update.php' && req.method === 'POST') {
                const { id, bio, name } = jsonBody;
                const userId = id || query.user_id;

                if (bio !== undefined && name !== undefined) {
                    await queryDb(`UPDATE users SET bio = ?, name = ? WHERE id = ?`, [bio, name, userId]);
                } else if (bio !== undefined) {
                    await queryDb(`UPDATE users SET bio = ? WHERE id = ?`, [bio, userId]);
                }

                return sendResponse(true, "Profile updated", null);
            }

            // 5. Bikes: Get / Add / Delete
            if (pathname === '/motonav_api/api/bikes/index.php') {
                const userId = query.user_id || jsonBody.userId || "user_1";

                if (req.method === 'GET') {
                    const rows = await queryDb(
                        `SELECT id, make, model, year,
                                mileage_kmpl as mileageKmpl,
                                fuel_tank_capacity_litres as fuelTankCapacityLitres,
                                is_default as isDefault
                         FROM bikes WHERE user_id = ? ORDER BY created_at DESC`,
                        [userId]
                    );

                    const bikes = rows.map(r => ({
                        id: r.id,
                        make: r.make,
                        model: r.model,
                        year: r.year,
                        mileageKmpl: Number(r.mileageKmpl) || 30.0,
                        fuelTankCapacityLitres: Number(r.fuelTankCapacityLitres) || 15.0,
                        isDefault: Boolean(r.isDefault)
                    }));

                    return sendResponse(true, "Success", bikes);
                }

                if (req.method === 'POST') {
                    const bikeId = "bike_" + Date.now();
                    const isDef = jsonBody.isDefault ? 1 : 0;

                    if (isDef) {
                        await queryDb(`UPDATE bikes SET is_default = 0 WHERE user_id = ?`, [userId]);
                    }

                    await queryDb(
                        `INSERT INTO bikes (id, user_id, make, model, year, mileage_kmpl, fuel_tank_capacity_litres, is_default)
                         VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
                        [
                            bikeId,
                            userId,
                            jsonBody.make || "Custom Make",
                            jsonBody.model || "Custom Model",
                            jsonBody.year || 2024,
                            jsonBody.mileageKmpl || 30.0,
                            jsonBody.fuelTankCapacityLitres || 15.0,
                            isDef
                        ]
                    );

                    const newBike = {
                        id: bikeId,
                        make: jsonBody.make || "Custom Make",
                        model: jsonBody.model || "Custom Model",
                        year: jsonBody.year || 2024,
                        mileageKmpl: jsonBody.mileageKmpl || 30.0,
                        fuelTankCapacityLitres: jsonBody.fuelTankCapacityLitres || 15.0,
                        isDefault: Boolean(isDef)
                    };

                    return sendResponse(true, "Bike added to garage in MySQL", newBike);
                }

                if (req.method === 'DELETE') {
                    const bikeId = query.id;
                    await queryDb(`DELETE FROM bikes WHERE id = ?`, [bikeId]);
                    return sendResponse(true, "Bike deleted", null);
                }
            }

            // 6. Places: Index & Toggle Save
            if (pathname === '/motonav_api/api/places/index.php' && req.method === 'GET') {
                const userId = query.user_id || "user_1";
                const rows = await queryDb(
                    `SELECT p.id, p.name, p.category, p.description, p.image_url as imageUrl,
                            p.rating, p.review_count as reviewCount, p.distance_km as distanceKm,
                            p.latitude, p.longitude,
                            (CASE WHEN s.place_id IS NOT NULL THEN 1 ELSE 0 END) as isSaved
                     FROM scenic_places p
                     LEFT JOIN saved_places s ON p.id = s.place_id AND s.user_id = ?`,
                    [userId]
                );

                const places = rows.map(r => ({
                    id: r.id,
                    name: r.name,
                    category: r.category,
                    description: r.description,
                    imageUrl: r.imageUrl,
                    rating: Number(r.rating),
                    reviewCount: Number(r.reviewCount),
                    distanceKm: Number(r.distanceKm),
                    latitude: Number(r.latitude),
                    longitude: Number(r.longitude),
                    isSaved: Boolean(r.isSaved)
                }));

                return sendResponse(true, "Success", places);
            }

            if (pathname === '/motonav_api/api/places/toggle_save.php' && req.method === 'POST') {
                const { userId = "user_1", placeId } = jsonBody;
                if (!placeId) return sendResponse(false, "placeId is required", null);

                const saved = await queryDb(
                    `SELECT * FROM saved_places WHERE user_id = ? AND place_id = ?`,
                    [userId, placeId]
                );

                if (saved.length > 0) {
                    await queryDb(`DELETE FROM saved_places WHERE user_id = ? AND place_id = ?`, [userId, placeId]);
                    return sendResponse(true, "Place removed from saved", { isSaved: false });
                } else {
                    await queryDb(`INSERT INTO saved_places (user_id, place_id) VALUES (?, ?)`, [userId, placeId]);
                    return sendResponse(true, "Place saved", { isSaved: true });
                }
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
                const userId = query.user_id || jsonBody.userId || "user_1";

                if (req.method === 'GET') {
                    const rows = await queryDb(
                        `SELECT id, title, date, distance_km as distanceKm, duration_minutes as durationMinutes,
                                avg_speed_kmh as avgSpeedKmh, fuel_cost_inr as fuelCostInr,
                                route_type as routeType, bike_model as bikeModel
                         FROM rides_history WHERE user_id = ? ORDER BY created_at DESC`,
                        [userId]
                    );

                    const rides = rows.map(r => ({
                        id: r.id,
                        title: r.title,
                        date: r.date,
                        distanceKm: Number(r.distanceKm),
                        durationMinutes: Number(r.durationMinutes),
                        avgSpeedKmh: Number(r.avgSpeedKmh),
                        fuelCostInr: Number(r.fuelCostInr),
                        routeType: r.routeType,
                        bikeModel: r.bikeModel
                    }));

                    return sendResponse(true, "Success", rides);
                }

                if (req.method === 'POST') {
                    const rideId = "ride_" + Date.now();
                    const dist = Number(jsonBody.distanceKm) || 50.0;
                    const dur = Number(jsonBody.durationMinutes) || 60;
                    const avgSpeed = dur > 0 ? (dist / (dur / 60.0)) : 50.0;

                    await queryDb(
                        `INSERT INTO rides_history (id, user_id, title, date, distance_km, duration_minutes, avg_speed_kmh, fuel_cost_inr, route_type, bike_model)
                         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
                        [
                            rideId,
                            userId,
                            jsonBody.title || "Rider Cruise",
                            "Today",
                            dist,
                            dur,
                            avgSpeed,
                            Number(jsonBody.fuelCostInr) || 200.0,
                            jsonBody.routeType || "SCENIC",
                            jsonBody.bikeModel || "Custom Motorcycle"
                        ]
                    );

                    const newRide = {
                        id: rideId,
                        title: jsonBody.title || "Rider Cruise",
                        date: "Today",
                        distanceKm: dist,
                        durationMinutes: dur,
                        avgSpeedKmh: avgSpeed,
                        fuelCostInr: Number(jsonBody.fuelCostInr) || 200.0,
                        routeType: jsonBody.routeType || "SCENIC",
                        bikeModel: jsonBody.bikeModel || "Custom Motorcycle"
                    };

                    return sendResponse(true, "Ride recorded in MySQL database", newRide);
                }
            }

            // 9. Photos: Get & Upload
            if (pathname === '/motonav_api/api/photos/index.php' && req.method === 'GET') {
                const rows = await queryDb(
                    `SELECT p.id, p.image_url as imageUri, p.caption, p.location_name as locationName,
                            p.latitude, p.longitude, p.tags, p.timestamp_display as timestamp,
                            COALESCE(u.name, 'Rider') as uploaderName
                     FROM photo_posts p
                     LEFT JOIN users u ON p.user_id = u.id
                     ORDER BY p.created_at DESC`
                );

                const photos = rows.map(r => ({
                    id: r.id,
                    imageUri: r.image_url,
                    caption: r.caption,
                    locationName: r.locationName,
                    latitude: Number(r.latitude),
                    longitude: Number(r.longitude),
                    tags: r.tags ? r.tags.split(',') : ["ride", "motonav"],
                    timestamp: r.timestamp,
                    isUploadedRemote: true,
                    uploaderName: r.uploaderName
                }));

                return sendResponse(true, "Success", photos);
            }

            if (pathname === '/motonav_api/api/photos/upload.php' && req.method === 'POST') {
                const photoId = "photo_" + Date.now();
                const userId = jsonBody.userId || "user_1";
                const tagsStr = Array.isArray(jsonBody.tags) ? jsonBody.tags.join(',') : (jsonBody.tags || "ride,motonav");

                await queryDb(
                    `INSERT INTO photo_posts (id, user_id, image_url, caption, location_name, latitude, longitude, tags, timestamp_display)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
                    [
                        photoId,
                        userId,
                        jsonBody.imageUri || "https://picsum.photos/id/1018/600/600",
                        jsonBody.caption || "Riding through scenic routes!",
                        jsonBody.locationName || "Scenic Viewpoint",
                        Number(jsonBody.latitude) || 18.5204,
                        Number(jsonBody.longitude) || 73.8567,
                        tagsStr,
                        "Just now"
                    ]
                );

                const newPhoto = {
                    id: photoId,
                    imageUri: jsonBody.imageUri || "https://picsum.photos/id/1018/600/600",
                    caption: jsonBody.caption || "Riding through scenic routes!",
                    locationName: jsonBody.locationName || "Scenic Viewpoint",
                    latitude: Number(jsonBody.latitude) || 18.5204,
                    longitude: Number(jsonBody.longitude) || 73.8567,
                    tags: Array.isArray(jsonBody.tags) ? jsonBody.tags : ["ride", "motonav"],
                    timestamp: "Just now",
                    isUploadedRemote: true,
                    uploaderName: "Rider"
                };

                return sendResponse(true, "Photo posted to MySQL database", newPhoto);
            }

            // 10. Fuel: Stations & Petrol Price
            if (pathname === '/motonav_api/api/fuel/stations.php' && req.method === 'GET') {
                const prices = await queryDb(`SELECT price_per_litre FROM fuel_prices ORDER BY updated_at DESC LIMIT 1`);
                const petrolPrice = prices.length > 0 ? Number(prices[0].price_per_litre) : 104.50;

                const stationRows = await queryDb(
                    `SELECT id, name, brand, address, distance_km as distanceKm,
                            is_open_24_hours as isOpen24Hours, has_air_water as hasAirWater
                     FROM fuel_stations`
                );

                const stations = stationRows.map(s => ({
                    id: s.id,
                    name: s.name,
                    brand: s.brand,
                    address: s.address,
                    distanceKm: Number(s.distanceKm),
                    isOpen24Hours: Boolean(s.isOpen24Hours),
                    hasAirWater: Boolean(s.hasAirWater)
                }));

                return sendResponse(true, "Success", { petrolPrice, stations });
            }

            res.writeHead(404, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ success: false, message: "Endpoint not found: " + pathname, data: null }));

        } catch (err) {
            console.error("API Processing Error:", err);
            sendResponse(false, "MySQL Server Error: " + err.message, null, 500);
        }
    });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`MotoNav MySQL Backend running on port ${PORT} (http://localhost:${PORT}/motonav_api/) connected to motonav_db!`);
});
