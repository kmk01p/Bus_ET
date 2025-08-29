# Ethiopia Real-Time Bus Monitoring Platform 🚌
# የኢትዮጵያ የአውቶብስ ክትትል ስርዓት

A comprehensive real-time bus monitoring and reservation system built with eGovFrame 4.3.0 for Ethiopia's public transportation infrastructure.

## 🌟 Features

### Three Integrated Platforms

#### 1. **Admin Dashboard**
- Real-time monitoring of all buses and routes
- User management (drivers and passengers)
- Revenue and payment tracking
- System statistics and analytics
- Bus and driver assignment management

#### 2. **Passenger Mobile App**
- User registration with OTP verification
- Real-time bus tracking on map
- Seat selection and reservation
- Payment integration (Telebirr/CBE ready)
- Multi-language support (Amharic/English)
- Bus arrival notifications
- Route search and planning

#### 3. **Driver Mobile App**
- Driver authentication
- Real-time GPS location sharing
- Route navigation
- Passenger count management
- Status updates (Active/Break/Offline)
- Trip management

## 🚀 Technology Stack

- **Backend Framework:** eGovFrame 4.3.0 (Korean e-Government Standard Framework)
- **Core:** Spring Boot 2.7.18
- **Security:** Spring Security with JWT Authentication
- **Database:** H2 (In-memory) - easily switchable to PostgreSQL/MySQL
- **ORM:** JPA/Hibernate
- **Real-time:** WebSocket with STOMP protocol
- **Build Tool:** Maven
- **Process Manager:** PM2
- **Maps:** Leaflet.js
- **UI:** Responsive HTML5, CSS3, JavaScript

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 14+ (for PM2)
- Git

## 🛠️ Installation

### 1. Clone the repository
```bash
git clone https://github.com/kmk01p/Bus_ET.git
cd Bus_ET
```

### 2. Build the application
```bash
./mvnw clean package
```

### 3. Run the application

#### Option A: Direct Java execution
```bash
java -jar target/egov-bus-0.0.1-SNAPSHOT.jar
```

#### Option B: Using PM2 (Recommended for production)
```bash
npm install -g pm2
pm2 start ecosystem.config.js
```

### 4. Access the platforms
- **Admin Dashboard:** http://localhost:8080/
- **Passenger App:** http://localhost:8080/passenger-app.html
- **Driver App:** http://localhost:8080/driver-app.html

## 🔐 Default Credentials

### Admin Account
- **Username:** admin
- **Password:** Admin@2024#Ethiopia

### Sample Driver Accounts
- **Phone:** +251911111111
- **Password:** driver123

## 📡 API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@2024#Ethiopia"
}
```

#### Passenger Signup
```http
POST /api/auth/signup
Content-Type: application/json

{
  "fullName": "John Doe",
  "phoneNumber": "+251912345678",
  "password": "Password123!",
  "confirmPassword": "Password123!",
  "acceptTerms": true
}
```

### Admin Endpoints (Requires Admin Token)

#### Get Dashboard Statistics
```http
GET /api/admin/dashboard/stats
Authorization: Bearer {token}
```

#### Manage Users
```http
GET /api/admin/users
PUT /api/admin/users/{id}/activate
PUT /api/admin/users/{id}/deactivate
```

### Bus & Route Endpoints

#### Get Active Buses
```http
GET /api/buses/active
```

#### Get All Routes
```http
GET /api/routes
```

## 🗂️ Project Structure

```
Bus_ET/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/egovbus/
│       │       ├── config/         # Configuration classes
│       │       ├── controller/     # REST controllers
│       │       ├── dto/           # Data Transfer Objects
│       │       ├── model/         # Entity models
│       │       ├── repository/    # JPA repositories
│       │       ├── security/      # Security configuration
│       │       ├── service/       # Business logic
│       │       └── websocket/     # WebSocket handlers
│       └── resources/
│           ├── static/            # Frontend files
│           │   ├── index.html     # Admin dashboard
│           │   ├── passenger-app.html
│           │   └── driver-app.html
│           └── application.properties
├── pom.xml                        # Maven configuration
├── ecosystem.config.js            # PM2 configuration
└── README.md
```

## 🌍 Ethiopian Context Features

- **Phone Format:** Ethiopian format (+251)
- **Payment Systems:** Telebirr and CBE integration ready
- **Languages:** Amharic and English support
- **Routes:** Major Addis Ababa routes pre-configured
- **License Plates:** Ethiopian format (AA-XXXXX)

## 🔧 Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
# For PostgreSQL (Production)
spring.datasource.url=jdbc:postgresql://localhost:5432/busdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### JWT Configuration
```properties
jwt.secret=YourSecretKey
jwt.expiration=86400000  # 24 hours in milliseconds
```

## 📱 Progressive Web App (PWA)

The passenger and driver apps are PWA-enabled, allowing:
- Offline functionality
- Home screen installation
- Push notifications
- Native app-like experience

## 🚦 WebSocket Real-time Events

The platform uses WebSocket for real-time updates:
- Bus location updates
- Passenger count changes
- Route status updates
- System notifications

Connect to WebSocket endpoint: `ws://localhost:8080/ws-bus`

## 🧪 Testing

### Run unit tests
```bash
./mvnw test
```

### Test API endpoints
```bash
# Test admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2024#Ethiopia"}'
```

## 📈 Monitoring

### PM2 Monitoring
```bash
pm2 status              # Check application status
pm2 logs               # View logs
pm2 monit              # Real-time monitoring
```

### Application Logs
Logs are stored in:
- `logs/out.log` - Standard output
- `logs/err.log` - Error logs

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with eGovFrame 4.3.0 - Korean e-Government Standard Framework
- Designed for Ethiopia's public transportation needs
- Supports Ethiopian payment systems and phone formats

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Contact: [Your Email]

## 🚀 Deployment

### Docker Deployment (Coming Soon)
```bash
docker build -t ethiopia-bus .
docker run -p 8080:8080 ethiopia-bus
```

### Cloud Deployment
The application is cloud-ready and can be deployed to:
- AWS Elastic Beanstalk
- Google Cloud Platform
- Azure App Service
- Heroku

---

Made with ❤️ for Ethiopia's public transportation system 🇪🇹