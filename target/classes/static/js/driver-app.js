// Ethiopia Bus Driver App - JavaScript
let currentUser = null;
let currentBus = null;
let gpsWatchId = null;
let isTracking = false;
let ws = null;
let stompClient = null;
let currentLanguage = 'en';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication();
    initializeWebSocket();
    setupServiceWorker();
});

// Check if user is authenticated
function checkAuthentication() {
    const token = localStorage.getItem('driverToken');
    if (token) {
        const userData = JSON.parse(localStorage.getItem('driverData'));
        if (userData) {
            currentUser = userData;
            showMainApp();
            loadDriverData();
        }
    }
}

// Handle login
async function handleLogin(event) {
    event.preventDefault();
    
    const phoneNumber = document.getElementById('phoneNumber').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch('/api/auth/driver/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ phoneNumber, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            
            // Store authentication data
            localStorage.setItem('driverToken', data.token);
            localStorage.setItem('driverData', JSON.stringify(data));
            
            currentUser = data;
            showMainApp();
            loadDriverData();
            
            showNotification('Login successful!', 'success');
        } else {
            showNotification('Invalid phone number or password', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showNotification('Connection error. Please try again.', 'error');
    }
}

// Handle logout
function handleLogout() {
    if (confirm('Are you sure you want to logout?')) {
        // Stop GPS tracking
        if (isTracking) {
            toggleGPS();
        }
        
        // Clear storage
        localStorage.removeItem('driverToken');
        localStorage.removeItem('driverData');
        
        // Disconnect WebSocket
        if (stompClient) {
            stompClient.disconnect();
        }
        
        // Show login screen
        document.getElementById('mainApp').classList.add('hidden');
        document.getElementById('loginScreen').classList.remove('hidden');
        
        currentUser = null;
        currentBus = null;
    }
}

// Show main app
function showMainApp() {
    document.getElementById('loginScreen').classList.add('hidden');
    document.getElementById('mainApp').classList.remove('hidden');
}

// Load driver data
async function loadDriverData() {
    if (!currentUser) return;
    
    // Update UI with driver info
    document.getElementById('driverName').textContent = currentUser.fullName;
    document.getElementById('driverAvatar').textContent = currentUser.fullName.charAt(0).toUpperCase();
    
    if (currentUser.busId) {
        document.getElementById('busNumber').textContent = `Bus: ${currentUser.busNumber}`;
        document.getElementById('busNumberMain').textContent = currentUser.busNumber;
        
        // Load bus details
        await loadBusDetails(currentUser.busId);
        
        // Load reservations
        await loadReservations();
    } else {
        showNotification('No bus assigned. Please contact administrator.', 'warning');
    }
}

// Load bus details
async function loadBusDetails(busId) {
    try {
        const response = await fetch(`/api/buses/${busId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('driverToken')}`
            }
        });
        
        if (response.ok) {
            currentBus = await response.json();
            updateBusInfo();
        }
    } catch (error) {
        console.error('Error loading bus details:', error);
    }
}

// Update bus information
function updateBusInfo() {
    if (!currentBus) return;
    
    document.getElementById('busRoute').textContent = 
        currentBus.route ? `${currentBus.route.startPoint} - ${currentBus.route.endPoint}` : 'No route';
    
    document.getElementById('passengerCount').textContent = currentBus.currentPassengers || 0;
    document.getElementById('availableSeats').textContent = 
        currentBus.capacity - (currentBus.currentPassengers || 0);
    
    // Update status
    const statusElement = document.getElementById('busStatus');
    statusElement.className = `status-badge status-${currentBus.status.toLowerCase()}`;
    statusElement.innerHTML = currentLanguage === 'en' ? 
        currentBus.status : getAmharicStatus(currentBus.status);
}

// Toggle GPS tracking
function toggleGPS() {
    if (!isTracking) {
        startGPSTracking();
    } else {
        stopGPSTracking();
    }
}

// Start GPS tracking
function startGPSTracking() {
    if (!navigator.geolocation) {
        showNotification('GPS not supported on this device', 'error');
        return;
    }
    
    // Request permission and start tracking
    gpsWatchId = navigator.geolocation.watchPosition(
        (position) => {
            sendLocationUpdate(position);
            updateGPSStatus(true);
            updateSpeed(position.coords.speed);
        },
        (error) => {
            console.error('GPS error:', error);
            showNotification('GPS error: ' + error.message, 'error');
            updateGPSStatus(false);
        },
        {
            enableHighAccuracy: true,
            timeout: 5000,
            maximumAge: 0
        }
    );
    
    isTracking = true;
    updateGPSUI(true);
    
    // Update bus status to ACTIVE
    updateBusStatus('ACTIVE');
}

// Stop GPS tracking
function stopGPSTracking() {
    if (gpsWatchId) {
        navigator.geolocation.clearWatch(gpsWatchId);
        gpsWatchId = null;
    }
    
    isTracking = false;
    updateGPSUI(false);
    
    // Update bus status to WAITING
    updateBusStatus('WAITING');
}

// Send location update via WebSocket
function sendLocationUpdate(position) {
    if (!stompClient || !stompClient.connected || !currentBus) return;
    
    const locationData = {
        busId: currentBus.id,
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        speed: position.coords.speed ? position.coords.speed * 3.6 : 0, // Convert m/s to km/h
        heading: position.coords.heading,
        accuracy: position.coords.accuracy,
        timestamp: new Date().toISOString()
    };
    
    stompClient.send('/app/update-location', {}, JSON.stringify(locationData));
    
    console.log('Location sent:', locationData);
}

// Update GPS UI
function updateGPSUI(active) {
    const indicator = document.getElementById('gpsIndicator');
    const statusText = document.getElementById('gpsStatusText');
    const toggleBtn = document.getElementById('gpsToggle');
    
    if (active) {
        indicator.className = 'gps-indicator gps-active';
        statusText.innerHTML = currentLanguage === 'en' ? 
            'GPS Active' : 'GPS ይሰራል';
        toggleBtn.className = 'gps-toggle stop';
        toggleBtn.innerHTML = '<i class="fas fa-stop"></i> ' + 
            (currentLanguage === 'en' ? 'Stop GPS Tracking' : 'GPS መከታተል አቁም');
    } else {
        indicator.className = 'gps-indicator gps-inactive';
        statusText.innerHTML = currentLanguage === 'en' ? 
            'GPS Inactive' : 'GPS አይሰራም';
        toggleBtn.className = 'gps-toggle';
        toggleBtn.innerHTML = '<i class="fas fa-satellite-dish"></i> ' + 
            (currentLanguage === 'en' ? 'Start GPS Tracking' : 'GPS መከታተል ጀምር');
    }
}

// Update GPS status
function updateGPSStatus(active) {
    const indicator = document.getElementById('gpsIndicator');
    if (active) {
        indicator.className = 'gps-indicator gps-active';
    } else {
        indicator.className = 'gps-indicator gps-inactive';
    }
}

// Update speed display
function updateSpeed(speedMs) {
    const speedKmh = speedMs ? Math.round(speedMs * 3.6) : 0;
    document.getElementById('currentSpeed').textContent = speedKmh;
}

// Update bus status
async function updateBusStatus(status) {
    if (!currentBus) return;
    
    try {
        const response = await fetch(`/api/buses/${currentBus.id}/status?status=${status}`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('driverToken')}`
            }
        });
        
        if (response.ok) {
            currentBus = await response.json();
            updateBusInfo();
        }
    } catch (error) {
        console.error('Error updating bus status:', error);
    }
}

// Load reservations
async function loadReservations() {
    if (!currentBus) return;
    
    try {
        const response = await fetch(`/api/reservations/bus/${currentBus.id}/today`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('driverToken')}`
            }
        });
        
        if (response.ok) {
            const reservations = await response.json();
            displayReservations(reservations);
        }
    } catch (error) {
        console.error('Error loading reservations:', error);
    }
}

// Display reservations
function displayReservations(reservations) {
    const listElement = document.getElementById('passengerList');
    
    if (reservations.length === 0) {
        listElement.innerHTML = `
            <div class="passenger-item">
                <div class="passenger-info">
                    <div class="passenger-name">No reservations for today</div>
                </div>
            </div>
        `;
        return;
    }
    
    listElement.innerHTML = reservations.map(reservation => `
        <div class="passenger-item">
            <div class="passenger-info">
                <div class="passenger-name">${reservation.passenger.fullName}</div>
                <div class="passenger-details">
                    ${reservation.boardingStop} → ${reservation.alightingStop} | 
                    Seat: ${reservation.seatNumber}
                </div>
            </div>
            ${reservation.status === 'CONFIRMED' ? 
                `<button class="check-in-btn" onclick="checkInPassenger('${reservation.confirmationCode}')">
                    Check In
                </button>` : 
                `<span style="color: #4caf50;">✓</span>`
            }
        </div>
    `).join('');
}

// Check in passenger
async function checkInPassenger(confirmationCode) {
    try {
        const response = await fetch(`/api/reservations/checkin/${confirmationCode}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('driverToken')}`
            }
        });
        
        if (response.ok) {
            showNotification('Passenger checked in successfully', 'success');
            loadReservations();
            
            // Update passenger count
            currentBus.currentPassengers = (currentBus.currentPassengers || 0) + 1;
            updateBusInfo();
        } else {
            showNotification('Check-in failed', 'error');
        }
    } catch (error) {
        console.error('Check-in error:', error);
        showNotification('Check-in error', 'error');
    }
}

// Initialize WebSocket connection
function initializeWebSocket() {
    const socket = new SockJS('/ws-bus');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, (frame) => {
        console.log('WebSocket connected:', frame);
        
        // Subscribe to notifications
        if (currentUser) {
            stompClient.subscribe(`/queue/user-${currentUser.userId}/notifications`, (message) => {
                const notification = JSON.parse(message.body);
                showNotification(notification.message, 'info');
            });
        }
    }, (error) => {
        console.error('WebSocket error:', error);
        setTimeout(initializeWebSocket, 5000); // Retry after 5 seconds
    });
}

// Toggle language
function toggleLanguage() {
    currentLanguage = currentLanguage === 'en' ? 'am' : 'en';
    
    document.querySelectorAll('.lang-en').forEach(el => {
        el.classList.toggle('hidden', currentLanguage === 'am');
    });
    
    document.querySelectorAll('.lang-am').forEach(el => {
        el.classList.toggle('hidden', currentLanguage === 'en');
    });
    
    // Update dynamic content
    if (isTracking) {
        updateGPSUI(true);
    }
}

// Get Amharic status
function getAmharicStatus(status) {
    const statusMap = {
        'ACTIVE': 'ንቁ',
        'INACTIVE': 'አይሰራም',
        'WAITING': 'በመጠበቅ ላይ',
        'MAINTENANCE': 'ጥገና'
    };
    return statusMap[status] || status;
}

// Show notification
function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        padding: 1rem 2rem;
        background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        z-index: 1000;
        animation: slideDown 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Setup service worker for PWA
function setupServiceWorker() {
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/sw.js')
            .then(registration => console.log('ServiceWorker registered'))
            .catch(error => console.error('ServiceWorker registration failed:', error));
    }
}

// Add SockJS and STOMP libraries dynamically
if (!window.SockJS) {
    const sockjsScript = document.createElement('script');
    sockjsScript.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
    document.head.appendChild(sockjsScript);
}

if (!window.Stomp) {
    const stompScript = document.createElement('script');
    stompScript.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
    document.head.appendChild(stompScript);
}