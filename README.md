SmartShop - B2B Commercial Management System
Project Description
SmartShop is a backend REST API developed using Spring Boot for MicroTech Maroc, a B2B computer hardware distributor. This application manages a portfolio of clients, processes orders, and handles inventory. It features an automated loyalty system that assigns tiers to clients based on their purchase history and supports split payments across multiple methods (Cash, Check, Transfer).


The system is designed as an API-only solution without a graphical user interface, intended for testing via Postman or Swagger.

Key Features
Client Management
Create and manage client profiles with personal information.

Automatic tracking of total orders, total amount spent, and order dates .

Tiered loyalty system calculated automatically based on purchase history.

Loyalty System
The system automatically updates a client's status based on their confirmed order history :

Basic: Default level upon registration (0 orders).

Silver: Achieved after 3 orders or 1,000 DH spent.

Gold: Achieved after 10 orders or 5,000 DH spent.

Platinum: Achieved after 20 orders or 15,000 DH spent.

Discounts: Discounts are applied automatically based on the client's tier and the order subtotal :

Silver: 5% discount if subtotal > 500 DH.

Gold: 10% discount if subtotal > 800 DH.

Platinum: 15% discount if subtotal > 1,200 DH.

Order Processing
Multi-product orders with automatic stock validation.

Calculation of VAT (configurable, default 20%) on the discounted amount.

Support for Promo Codes (strictly formatted as PROMO-XXXX).


Order Lifecycle: Pending, Confirmed (only after full payment), Rejected (stock issues), or Canceled.

Payment System
Support for split payments (paying one order via multiple transactions).

Supported methods: Cash, Check, and Bank Transfer.

Payment tracking with statuses: Pending, Cashed, Rejected.

Strict validation ensures an order is only Confirmed when the remaining balance is zero.

Technical Architecture
Framework: Spring Boot.

Database: PostgreSQL or MySQL with Spring Data JPA.


Authentication: HTTP Session-based login/logout (No JWT or Spring Security).

Object Mapping: MapStruct for DTO/Entity conversion.


Error Handling: Centralized ControllerAdvice for consistent JSON error responses.

Prerequisites
Java 17 (or Java 8+ minimum).

Maven.

PostgreSQL or MySQL.

Postman (for API testing).

Installation and Setup
Clone the repository 

Configure the Database Open src/main/resources/application.properties and update your database credentials: spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop_db spring.datasource.username=your_username spring.datasource.password=your_password

Build the project mvn clean install

Run the application mvn spring-boot:run

Configuration
The application allows configuration of business rules in application.properties:

VAT Rate: Configurable percentage (default 20%).

Datasource: Database connection settings.

JPA: Hibernate ddl-auto settings (create/update).

API Endpoints Overview
Authentication
POST /api/auth/login: Authenticate user and start session.

POST /api/auth/logout: End session.

GET /api/auth/status: Check current authentication status.

Clients
POST /api/clients: Create a new client.

GET /api/clients/{id}: Get client details.

PUT /api/clients/{id}: Update client information.

Products
GET /api/products: List all products.

POST /api/products: Add a new product (Admin only).

GET /api/products/search: Search products by keyword.

Orders
POST /api/orders: Create a new order with items.

GET /api/orders/{id}: Retrieve order details.

GET /api/orders/client/{clientId}: List orders for a specific client.

Payments
POST /api/payments: Record a payment for an order.

PATCH /api/payments/{id}/cash: Mark a payment as encashed.

PATCH /api/payments/{id}/reject: Mark a payment as rejected.

Promo Codes
POST /api/promo-codes: Create a new promo code.

PATCH /api/promo-codes/{id}/activate: Activate a promo code.

User Roles and Permissions
ADMIN

Full CRUD access to all resources.

Can validate orders and payments.

Can manage products and promo codes.

CLIENT

Can view their own profile and order history.

Can view product list.

Cannot modify data or view other clients' information.

Error Handling
The API returns standardized JSON error responses including timestamp, status code, error type, and message. Common status codes used:

200: Success.

201: Resource Created.

400: Validation Error.

401: Unauthorized (Login required).

403: Forbidden (Insufficient permissions).

404: Resource Not Found.

422: Business Logic Violation (e.g., insufficient stock).

Testing
The application is designed to be tested using Postman. Ensure you log in via the /api/auth/login endpoint first to establish a session before accessing protected resources.
