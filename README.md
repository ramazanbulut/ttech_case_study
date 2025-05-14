# Aviation Route Planner

## Project Overview
Aviation Route Planner is a full-stack web application designed to help users plan and manage aviation routes. The system provides a modern, user-friendly interface for route planning and management, backed by a robust backend system. This application enables users to:

- Search and filter airports worldwide
- Plan optimal routes between locations

## Tech Stack

### Frontend
- **React 19.1.0 with TypeScript**
  - Modern component-based architecture
  - Type safety and better development experience
  - Enhanced code maintainability and scalability

- **UI Framework**
  - Material-UI (MUI) v7.0.2
    - Comprehensive set of pre-built components
    - Responsive design components
    - Date picker components
    - Icon library

- **Routing & State Management**
  - React Router v7.5.3
    - Client-side routing
    - Nested routes support
    - Route guards and authentication

- **API & Data Handling**
  - Axios
    - HTTP client for API requests
    - Request/response interceptors
    - Error handling middleware

- **Styling & UI Enhancement**
  - Styled Components
    - CSS-in-JS solution
    - Dynamic styling
  - React Toastify
    - Toast messages

- **Date Handling**
  - Date-fns
    - Date manipulation
    - Timezone handling

### Backend
- **Java 17 & Spring Boot 3.2.3**
  - Latest LTS Java version
  - Spring Boot for rapid development
  - Auto-configuration and dependency injection

- **Database & ORM**
  - PostgreSQL Database
    - Relational database
    - ACID compliance
    - Advanced querying capabilities
  - Spring Data JPA (Hibernate)
    - Object-Relational Mapping
    - Repository pattern
    - Query optimization

- **API & Security**
  - Spring Web (REST API)
    - RESTful endpoints
    - Request/Response handling
    - Content negotiation
  - Spring Validation
    - Input validation
    - Custom validators
    - Error handling

- **Documentation & Development**
  - Swagger/OpenAPI
    - API documentation
    - Interactive API testing
    - Schema generation
  - Lombok
    - Reduced boilerplate code
    - Automatic getter/setter generation
    - Builder pattern support

## Project Structure
```
.
├── frontend/               # React frontend application
│   ├── src/               # Source code
│   │   ├── components/    # Reusable UI components
│   │   ├── pages/        # Page components
│   │   ├── services/     # API services
│   │   ├── utils/        # Utility functions
│   │   ├── hooks/        # Custom React hooks
│   │   ├── context/      # React context providers
│   │   ├── types/        # TypeScript type definitions
│   │   └── styles/       # Global styles and themes
│   ├── public/           # Static files
│   └── package.json      # Frontend dependencies
├── backend/              # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/    # Java source code
│   │   │   │   ├── controllers/  # REST controllers
│   │   │   │   ├── services/     # Business logic
│   │   │   │   ├── repositories/ # Data access
│   │   │   │   ├── models/       # Entity classes
│   │   │   │   ├── dto/          # Data transfer objects
│   │   │   │   └── config/       # Configuration classes
│   │   │   └── resources/        # Application properties
│   │   └── test/        # Test classes
│   └── pom.xml          # Backend dependencies
├── airports.csv         # Airport data
└── add_airports.py      # Airport data import script
```

## Getting Started

### Prerequisites
- **Node.js** (Latest LTS version)
  - Required for frontend development
  - Includes npm package manager
- **Java 17 or higher**
  - Required for backend development
  - JDK installation
- **PostgreSQL**
  - Database server
  - Latest stable version recommended
- **Maven**
  - Java build tool
  - Required for backend dependencies

### Environment Setup

#### Database Configuration
1. Install PostgreSQL
2. Create a new database:
   ```sql
   CREATE DATABASE aviation_route_planner;
   ```
3. Update `application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/aviation_route_planner
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

#### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Create a `.env` file:
   ```env
   REACT_APP_API_URL=http://localhost:3000
   ```
4. Start the development server:
   ```bash
   npm start
   ```
   The frontend will be available at http://localhost:3000

#### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   The backend will be available at http://localhost:8080
   API documentation will be available at http://localhost:8080/swagger-ui.html

### Development Workflow
1. Start the PostgreSQL database
2. Run the backend application
3. Start the frontend development server
4. Access the application at http://localhost:3000
5. Use Swagger UI at http://localhost:8080/swagger-ui.html for API testing

## API Documentation
The API documentation is available through Swagger UI when the backend is running. You can access it at:
http://localhost:8080/swagger-ui.html

The API includes the following main endpoints:
- `/api/airports` - Airport management
- `/api/routes` - Route planning and management
- `/api/flights` - Flight information and calculations

## Additional Tools

### Postman Collection
A Postman collection (`Ttech_API_Collection.json`) is available for testing the API endpoints. The collection includes:

- **Locations API**
  - Get all locations (with pagination, sorting, and search)
  - Get location by ID
  - Create new location
  - Update location
  - Delete location

- **Transportations API**
  - Get all transportations (with filtering and pagination)
  - Get transportation by ID
  - Create new transportation
  - Update transportation
  - Delete transportation

- **Routes API**
  - Search routes between locations (GET request with query parameters)

To use the collection:
1. Import `Ttech_API_Collection.json` into Postman
2. The collection uses `http://localhost:8080` as the base URL
3. All endpoints are prefixed with `/api`
4. Example requests are included for each endpoint

### Airport Data Management
The `add_airports.py` script is used to import airport data from `airports.csv`:
1. Ensure PostgreSQL is running
2. Update database credentials in the script
3. Run the script:
   ```bash
   python add_airports.py
   ```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details. 