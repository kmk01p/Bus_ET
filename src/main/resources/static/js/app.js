// Ethiopia Bus Monitoring System - Frontend Application
let map;
let busMarkers = {};
let buses = [];
let selectedBusId = null;
let currentFilter = 'all';

// Initialize map
function initMap() {
    // Addis Ababa coordinates
    map = L.map('map').setView([9.03, 38.74], 12);
    
    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);
}

// Create bus icon based on status
function createBusIcon(status) {
    const colors = {
        'ACTIVE': '#4caf50',
        'INACTIVE': '#9e9e9e',
        'WAITING': '#2196f3',
        'MAINTENANCE': '#ff9800',
        'EMERGENCY': '#f44336',
        'DELAYED': '#ff5722'
    };
    
    const color = colors[status] || '#9e9e9e';
    
    return L.divIcon({
        html: `<div style="background: ${color}; width: 30px; height: 30px; border-radius: 50%; 
                border: 3px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3); 
                display: flex; align-items: center; justify-content: center;">
                <i class="fas fa-bus" style="color: white; font-size: 14px;"></i>
               </div>`,
        className: 'bus-marker',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    });
}

// Load buses from server
async function loadBuses() {
    try {
        const response = await fetch('/api/buses');
        buses = await response.json();
        updateBusList();
        updateMap();
        updateStatistics();
    } catch (error) {
        console.error('Error loading buses:', error);
    }
}

// Update bus list in sidebar
function updateBusList() {
    const busList = document.getElementById('busList');
    const searchTerm = document.getElementById('searchBox').value.toLowerCase();
    
    let filteredBuses = buses;
    
    // Apply status filter
    if (currentFilter !== 'all') {
        filteredBuses = buses.filter(bus => bus.status === currentFilter);
    }
    
    // Apply search filter
    if (searchTerm) {
        filteredBuses = filteredBuses.filter(bus => 
            bus.busNumber.toLowerCase().includes(searchTerm) ||
            (bus.driverName && bus.driverName.toLowerCase().includes(searchTerm))
        );
    }
    
    if (filteredBuses.length === 0) {
        busList.innerHTML = '<div class="loading">검색 결과가 없습니다.</div>';
        return;
    }
    
    busList.innerHTML = filteredBuses.map(bus => `
        <div class="bus-item ${bus.status.toLowerCase()}" onclick="selectBus(${bus.id})">
            <div class="bus-header">
                <span class="bus-number">${bus.busNumber}</span>
                <span class="bus-status status-${bus.status.toLowerCase()}">${getStatusText(bus.status)}</span>
            </div>
            <div class="bus-details">
                <div class="bus-detail">
                    <i class="fas fa-user"></i>
                    <span>${bus.driverName || 'N/A'}</span>
                </div>
                <div class="bus-detail">
                    <i class="fas fa-users"></i>
                    <span>${bus.currentPassengers || 0}/${bus.capacity}</span>
                </div>
                <div class="bus-detail">
                    <i class="fas fa-route"></i>
                    <span>${bus.route ? bus.route.routeNumber : 'No Route'}</span>
                </div>
                <div class="bus-detail">
                    <i class="fas fa-tachometer-alt"></i>
                    <span>${bus.speed ? bus.speed.toFixed(1) : '0'} km/h</span>
                </div>
            </div>
        </div>
    `).join('');
}

// Update map markers
function updateMap() {
    buses.forEach(bus => {
        if (bus.currentLatitude && bus.currentLongitude) {
            if (busMarkers[bus.id]) {
                // Update existing marker
                busMarkers[bus.id].setLatLng([bus.currentLatitude, bus.currentLongitude]);
                busMarkers[bus.id].setIcon(createBusIcon(bus.status));
            } else {
                // Create new marker
                const marker = L.marker([bus.currentLatitude, bus.currentLongitude], {
                    icon: createBusIcon(bus.status)
                }).addTo(map);
                
                marker.bindPopup(`
                    <strong>버스 번호:</strong> ${bus.busNumber}<br>
                    <strong>상태:</strong> ${getStatusText(bus.status)}<br>
                    <strong>운전자:</strong> ${bus.driverName || 'N/A'}<br>
                    <strong>탑승객:</strong> ${bus.currentPassengers || 0}/${bus.capacity}<br>
                    <strong>속도:</strong> ${bus.speed ? bus.speed.toFixed(1) : '0'} km/h
                `);
                
                marker.on('click', () => selectBus(bus.id));
                
                busMarkers[bus.id] = marker;
            }
        }
    });
    
    // Remove markers for buses that no longer exist
    Object.keys(busMarkers).forEach(busId => {
        if (!buses.find(bus => bus.id == busId)) {
            map.removeLayer(busMarkers[busId]);
            delete busMarkers[busId];
        }
    });
}

// Select bus and center map
function selectBus(busId) {
    selectedBusId = busId;
    const bus = buses.find(b => b.id === busId);
    
    if (bus && bus.currentLatitude && bus.currentLongitude) {
        map.setView([bus.currentLatitude, bus.currentLongitude], 15);
        
        if (busMarkers[busId]) {
            busMarkers[busId].openPopup();
        }
    }
    
    // Highlight selected bus in list
    document.querySelectorAll('.bus-item').forEach(item => {
        item.style.border = '1px solid #e0e0e0';
    });
    
    const selectedElement = document.querySelector(`.bus-item:nth-child(${buses.findIndex(b => b.id === busId) + 1})`);
    if (selectedElement) {
        selectedElement.style.border = '2px solid #667eea';
    }
}

// Get status text in Korean
function getStatusText(status) {
    const statusMap = {
        'ACTIVE': '운행중',
        'INACTIVE': '운행종료',
        'WAITING': '대기중',
        'MAINTENANCE': '정비중',
        'EMERGENCY': '긴급상황',
        'DELAYED': '지연'
    };
    return statusMap[status] || status;
}

// Update statistics
function updateStatistics() {
    const totalBuses = buses.length;
    const activeBuses = buses.filter(b => b.status === 'ACTIVE').length;
    const waitingBuses = buses.filter(b => b.status === 'WAITING').length;
    
    document.getElementById('totalBuses').textContent = totalBuses;
    document.getElementById('activeBuses').textContent = activeBuses;
    document.getElementById('waitingBuses').textContent = waitingBuses;
}

// Update current time
function updateTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit' 
    });
    document.getElementById('currentTime').textContent = timeString;
}

// Filter buttons event handlers
document.addEventListener('DOMContentLoaded', () => {
    // Initialize map
    initMap();
    
    // Load initial data
    loadBuses();
    
    // Set up auto-refresh (every 10 seconds)
    setInterval(loadBuses, 10000);
    
    // Update time every second
    updateTime();
    setInterval(updateTime, 1000);
    
    // Search box event
    document.getElementById('searchBox').addEventListener('input', updateBusList);
    
    // Filter buttons
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
            currentFilter = e.target.dataset.filter;
            updateBusList();
        });
    });
});

// Initialize sample data if no buses exist
async function initializeSampleData() {
    try {
        // Check if buses exist
        const response = await fetch('/api/buses');
        const existingBuses = await response.json();
        
        if (existingBuses.length === 0) {
            // Create sample routes
            const routes = [
                {
                    routeNumber: "R001",
                    routeName: "Bole - Merkato",
                    startPoint: "Bole",
                    endPoint: "Merkato",
                    stops: ["Bole", "Mexico Square", "Meskel Square", "Piassa", "Merkato"],
                    totalDistance: 12.5,
                    estimatedTime: 45,
                    fare: 15.0,
                    operatingHours: "05:00 - 22:00",
                    isActive: true
                },
                {
                    routeNumber: "R002",
                    routeName: "Kality - CMC",
                    startPoint: "Kality",
                    endPoint: "CMC",
                    stops: ["Kality", "Saris", "Gotera", "Mexico", "CMC"],
                    totalDistance: 15.0,
                    estimatedTime: 50,
                    fare: 20.0,
                    operatingHours: "05:30 - 21:30",
                    isActive: true
                }
            ];
            
            // Create routes
            for (const route of routes) {
                await fetch('/api/routes', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(route)
                });
            }
            
            // Create sample buses
            const sampleBuses = [
                {
                    busNumber: "AA-101",
                    licensePlate: "3-A-12345",
                    driverName: "Abebe Kebede",
                    driverPhone: "+251911234567",
                    capacity: 50,
                    currentPassengers: 32,
                    status: "ACTIVE"
                },
                {
                    busNumber: "AA-102",
                    licensePlate: "3-A-12346",
                    driverName: "Tadesse Haile",
                    driverPhone: "+251911234568",
                    capacity: 45,
                    currentPassengers: 28,
                    status: "ACTIVE"
                },
                {
                    busNumber: "AA-103",
                    licensePlate: "3-A-12347",
                    driverName: "Getachew Molla",
                    driverPhone: "+251911234569",
                    capacity: 50,
                    currentPassengers: 15,
                    status: "WAITING"
                },
                {
                    busNumber: "AA-104",
                    licensePlate: "3-A-12348",
                    driverName: "Solomon Bekele",
                    driverPhone: "+251911234570",
                    capacity: 45,
                    currentPassengers: 0,
                    status: "INACTIVE"
                }
            ];
            
            for (const bus of sampleBuses) {
                await fetch('/api/buses', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(bus)
                });
            }
            
            // Reload buses after initialization
            setTimeout(loadBuses, 1000);
        }
    } catch (error) {
        console.error('Error initializing sample data:', error);
    }
}

// Initialize sample data on first load
setTimeout(initializeSampleData, 2000);