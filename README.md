# Online Market

An e-commerce application with comprehensive order management, payment processing, and user authentication features.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Entity Relationships](#entity-relationships)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [API Documentation](#api-documentation)
  - [Authentication APIs](#authentication-apis)
  - [Product Management](#product-management)
  - [Order Management](#order-management)
  - [Payment Processing](#payment-processing)
- [Running Tests](#running-tests)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview

Online Market is a full-featured e-commerce platform built with Spring Boot, providing secure user authentication, product management, order processing, and integrated payment solutions via Flutterwave.

## Features

- **User Authentication**: Secure signup and login with JWT token-based authentication
- **Role-Based Access Control**: Different access levels for shoppers, buyers, and administrators
- **Product Management**: Complete CRUD operations for products and categories
- **Order Processing**: Order creation, tracking, and history
- **Payment Integration**: Seamless payment processing via Flutterwave
- **Email Notifications**: Automated emails for registration, order confirmation, and payment status
- **API Documentation**: Complete API documentation via Swagger/OpenAPI
- **Security**: Proper authentication and authorization mechanisms

## System Architecture

The application follows a standard Spring Boot architecture with:

- **Controller Layer**: REST APIs for client interaction
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access and persistence
- **Security Layer**: JWT-based authentication and authorization
- **Integration Layer**: Third-party integrations (Flutterwave, Email service)

## Entity Relationships

- **Users**: Core user entity with authentication details and role information
- **Categories**: Product categories
- **Products**: Belongs to a category
- **Orders**: Created by users and contains order items
- **Payments**: Linked to orders with payment status tracking

Key relationships:
- A Category has many Products
- A User can place many Orders
- An Order contains multiple Products
- A Payment is associated with one Order

## Setup Instructions

### Prerequisites

- JDK 11 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Git (optional, for cloning the repository)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/mariusbayizere/online-market.git
   cd online-market
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Create the PostgreSQL database:
   ```sql
   CREATE DATABASE online_market;
   ```

### Configuration

1. Configure the application properties in `src/main/resources/application.properties`:

   ```properties
spring.application.name=Project_Online_market
spring.datasource.url=jdbc:postgresql://localhost:5432/online_market
spring.datasource.username=postgres
spring.datasource.password=auca@2023
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
# spring.jpa.properties.hibernate.use_sql_comments=true
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=bayizeremarius119@gmail.com
spring.mail.password=kcxb hlwp rqkj eehn 
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
flutterwave.api.key=FLWSECK_TEST-3bd8451c8f2c33ab9422955ec4e296f5-X
flutterwave.testMode=true
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

# Swagger/OpenAPI Properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.enabled=true
springdoc.api-docs.path=/v3/api-docs
springdoc.pathsToMatch=/api/**
# 
logging.level.org.springdoc=DEBUG
logging.level.io.swagger.v3=DEBUG
springdoc.swagger-ui.csrf.enabled=false
spring.security.ignored=/swagger-ui/**/v3/api-docs/**
   ```

2. Replace the placeholder values with your actual credentials:
   - Database username and password
   - Email credentials (use an app password for Gmail)
   - Flutterwave API key

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   - API Endpoints: `http://localhost:8080/api/`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Documentation

The application uses Swagger/OpenAPI for API documentation. Once the application is running, you can access the API documentation at `http://localhost:8080/swagger-ui.html`.

### Authentication APIs

#### User Registration
```
POST /api/v1/auth/signup
```
- Registers a new user
- Required fields: email, password, confirmPassword, and userRole
- Returns a JWT token upon successful registration
- Sends a confirmation email to the registered email address

#### User Login
```
POST /api/v1/auth/signin
```
- Authenticates a user
- Required fields: email, password
- Returns a JWT token upon successful authentication

### Product Management

#### Categories
```
GET /api/categories - Get all categories
POST /api/categories/add - Create a new category
GET /api/categories/{id} - Get a specific category
PUT /api/categories/update/{id} - Update a category
DELETE /api/categories/delete/{id} - Delete a category
```
- Restricted to ADMIN role

#### Products
```
GET /api/products - Get all products
POST /api/products/add - Create a new product
GET /api/products/{id} - Get a specific product
PUT /api/products/update/{id} - Update a product
DELETE /api/products/delete/{id} - Delete a product
```
- Restricted to ADMIN role
- Creating a product requires a valid category ID

### Order Management

The order management system allows users with SHOPPER or ADMIN roles to place and manage orders.

```
GET /api/orders/history - Get all orders history
GET /api/orders/track/{id} - Get a specific order by ID
POST /api/orders/add - Create a new order
PUT /api/orders/update/{id} - Update an order
DELETE /api/orders/delete/{id} - Delete an order
GET /api/orders/oderbyuser/{userId} - Get all orders placed by a specific user
```

#### Order Creation Process
When creating a new order:
1. The system validates the user and product existence
2. Checks if the product is in stock
3. Verifies that the requested quantity is available
4. Prevents duplicate orders (same user and product)
5. Automatically updates the product inventory by reducing the quantity
6. Sends a confirmation email to the user with order details


### Review Management

```
GET /api/reviews - Get all reviews
GET /api/reviews/{id} - Get a specific review by ID
POST /api/reviews/add - Create a new review (Authenticated users only)
PUT /api/reviews/update/{id} - Update a review (Authenticated users only)
DELETE /api/reviews/delete/{id} - Delete a review (Admin access required)
```

#### Order Email Confirmation
After successfully placing an order, the system automatically sends an email confirmation to the user containing:
- Personal greeting with the user's name
- Order details (product name, quantity)
- Order ID for tracking
- Current order status
- Thank you message


#### Business Rules
- Users cannot order products that are out of stock
- Users cannot order quantities greater than available stock
- Users cannot place duplicate orders for the same product
- Order status is tracked and can be updated

### Payment Processing

```
POST /api/payments/initiate/{orderId} - Initiate payment for an order
GET /api/payments - Get all payments
GET /api/payments/{id} - Get a specific payment
PUT /api/payments/update/{id} - Update a payment
DELETE /api/payments/delete/{id} - Delete a payment
```
- Payment initiation requires a valid order ID
- Payment details must include email, phoneNumber, and paymentMethod
- Successful payment initiation generates a Flutterwave payment link
- After successful payment, the order status is automatically updated to "Shipped"
- Email confirmation is sent after payment processing


**#### Tags**
```
GET /api/tags - Get all tags
GET /api/tags/{id} - Get a specific tag
POST /api/tags/create - Create a new tag
PUT /api/tags/update/{id} - Update a tag
DELETE /api/tags/delete/{id} - Delete a tag
```


The coverage report will be available at `target/site/jacoco/index.html`.

## Security

The application implements the following security measures:

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Different permissions for different user roles
- **Password Encryption**: BCrypt password encoding
- **CORS Configuration**: Configured to allow specific origins
- **Session Management**: Stateless session management

Security annotations are used throughout the application to enforce access control:
- `@PreAuthorize("hasRole('SHOPPER') or hasRole('ADMIN')")` - Restricts access to SHOPPER or ADMIN roles
- `@SecurityRequirement(name = "bearerAuth")` - Indicates JWT token requirement in Swagger docs

## Troubleshooting

### Common Issues

1. **Database Connection Problems**:
   - Verify PostgreSQL is running
   - Check database credentials in application.properties
   - Ensure the database exists

2. **Email Sending Failures**:
   - Verify email credentials
   - For Gmail, use an app password instead of your account password
   - Check firewall settings for SMTP ports

3. **Payment Integration Issues**:
   - Verify Flutterwave API key
   - Ensure test mode is enabled for development
   - Check network connectivity to Flutterwave servers

4. **Order Processing Issues**:
   - Check if the user and product exist in the database
   - Verify product inventory levels
   - Ensure there are no duplicate orders for the same user and product

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.