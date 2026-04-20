# Real Estate Platform - RESTful API Design

## Overview
This document outlines the RESTful API design for the real estate platform with separate admin and user functionalities.

## Base URL
```
http://localhost:8080/api
```

## Authentication
The API uses JWT (JSON Web Token) for authentication. Admin endpoints require ADMIN role, while public endpoints are accessible without authentication.

## API Endpoints

### 1. Authentication Endpoints

#### User Registration
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "phoneNumber": "string"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00Z"
  }
}
```

#### User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}
```

### 2. Property Endpoints

#### Get All Properties (Public)
```http
GET /api/properties
Query Parameters:
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 10)
- priceMin (optional): Minimum price filter
- priceMax (optional): Maximum price filter
- location (optional): Location filter
- propertyType (optional): Property type filter (RESIDENTIAL, COMMERCIAL)
- listingType (optional): Listing type filter (SALE, RENT)
- status (optional): Property status filter (ACTIVE, PENDING, SOLD, WITHDRAWN)
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Properties retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Beautiful 3BR House",
        "description": "Spacious family home with garden",
        "price": 450000.00,
        "propertyType": "RESIDENTIAL",
        "listingType": "SALE",
        "address": "123 Main St",
        "city": "Springfield",
        "state": "IL",
        "zipCode": "62701",
        "area": 2500.0,
        "bedrooms": 3,
        "bathrooms": 2,
        "status": "ACTIVE",
        "imageUrls": ["image1.jpg", "image2.jpg"],
        "youtubeVideoUrl": "https://youtube.com/watch?v=xyz",
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

#### Get Property by ID (Public)
```http
GET /api/properties/{id}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Property retrieved successfully",
  "data": {
    "id": 1,
    "title": "Beautiful 3BR House",
    "description": "Spacious family home with garden",
    "price": 450000.00,
    "propertyType": "RESIDENTIAL",
    "listingType": "SALE",
    "address": "123 Main St",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "area": 2500.0,
    "bedrooms": 3,
    "bathrooms": 2,
    "status": "ACTIVE",
    "imageUrls": ["image1.jpg", "image2.jpg"],
    "youtubeVideoUrl": "https://youtube.com/watch?v=xyz",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
}
```

#### Create Property (Admin Only)
```http
POST /api/admin/properties
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Beautiful 3BR House",
  "description": "Spacious family home with garden",
  "price": 450000.00,
  "propertyType": "RESIDENTIAL",
  "listingType": "SALE",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701",
  "area": 2500.0,
  "bedrooms": 3,
  "bathrooms": 2,
  "imageUrls": ["image1.jpg", "image2.jpg"],
  "youtubeVideoUrl": "https://youtube.com/watch?v=xyz"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Property created successfully",
  "data": {
    "id": 1,
    "title": "Beautiful 3BR House",
    "description": "Spacious family home with garden",
    "price": 450000.00,
    "propertyType": "RESIDENTIAL",
    "listingType": "SALE",
    "address": "123 Main St",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "area": 2500.0,
    "bedrooms": 3,
    "bathrooms": 2,
    "status": "ACTIVE",
    "imageUrls": ["image1.jpg", "image2.jpg"],
    "youtubeVideoUrl": "https://youtube.com/watch?v=xyz",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
}
```

#### Update Property (Admin Only)
```http
PUT /api/admin/properties/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated Beautiful 3BR House",
  "description": "Updated spacious family home with garden",
  "price": 475000.00,
  "propertyType": "RESIDENTIAL",
  "listingType": "SALE",
  "address": "123 Main St",
  "city": "Springfield",
  "state": "IL",
  "zipCode": "62701",
  "area": 2500.0,
  "bedrooms": 3,
  "bathrooms": 2,
  "imageUrls": ["image1.jpg", "image2.jpg"],
  "youtubeVideoUrl": "https://youtube.com/watch?v=xyz"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Property updated successfully",
  "data": {
    "id": 1,
    "title": "Updated Beautiful 3BR House",
    "description": "Updated spacious family home with garden",
    "price": 475000.00,
    "propertyType": "RESIDENTIAL",
    "listingType": "SALE",
    "address": "123 Main St",
    "city": "Springfield",
    "state": "IL",
    "zipCode": "62701",
    "area": 2500.0,
    "bedrooms": 3,
    "bathrooms": 2,
    "status": "ACTIVE",
    "imageUrls": ["image1.jpg", "image2.jpg"],
    "youtubeVideoUrl": "https://youtube.com/watch?v=xyz",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T11:00:00Z"
  }
}
```

#### Delete Property (Admin Only)
```http
DELETE /api/admin/properties/{id}
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Property deleted successfully",
  "data": null
}
```

#### Update Property Status (Admin Only)
```http
PATCH /api/admin/properties/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "SOLD"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Property status updated successfully",
  "data": {
    "id": 1,
    "status": "SOLD",
    "updatedAt": "2024-01-01T12:00:00Z"
  }
}
```

### 3. Inquiry Endpoints

#### Submit Property Inquiry (Public)
```http
POST /api/properties/{propertyId}/inquiries
Content-Type: application/json

{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phoneNumber": "+1234567890",
  "message": "I'm interested in this property. Can we schedule a viewing?"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Inquiry submitted successfully",
  "data": {
    "id": 1,
    "propertyId": 1,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phoneNumber": "+1234567890",
    "message": "I'm interested in this property. Can we schedule a viewing?",
    "status": "PENDING",
    "submittedAt": "2024-01-01T14:00:00Z"
  }
}
```

#### Get All Inquiries (Admin Only)
```http
GET /api/admin/inquiries
Authorization: Bearer {token}
Query Parameters:
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 10)
- status (optional): Inquiry status filter (PENDING, IN_PROGRESS, RESOLVED, CLOSED)
- propertyId (optional): Filter by property ID
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Inquiries retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "propertyId": 1,
        "propertyTitle": "Beautiful 3BR House",
        "name": "Jane Smith",
        "email": "jane@example.com",
        "phoneNumber": "+1234567890",
        "message": "I'm interested in this property. Can we schedule a viewing?",
        "status": "PENDING",
        "submittedAt": "2024-01-01T14:00:00Z",
        "updatedAt": "2024-01-01T14:00:00Z"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

#### Update Inquiry Status (Admin Only)
```http
PATCH /api/admin/inquiries/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "RESOLVED",
  "adminNotes": "Viewing scheduled for tomorrow at 2 PM"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Inquiry updated successfully",
  "data": {
    "id": 1,
    "propertyId": 1,
    "propertyTitle": "Beautiful 3BR House",
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phoneNumber": "+1234567890",
    "message": "I'm interested in this property. Can we schedule a viewing?",
    "status": "RESOLVED",
    "adminNotes": "Viewing scheduled for tomorrow at 2 PM",
    "submittedAt": "2024-01-01T14:00:00Z",
    "updatedAt": "2024-01-01T15:00:00Z"
  }
}
```

### 4. Admin User Management Endpoints

#### Get All Users (Admin Only)
```http
GET /api/admin/users
Authorization: Bearer {token}
Query Parameters:
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 10)
- role (optional): User role filter (USER, ADMIN)
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "phoneNumber": "+1234567890",
        "role": "USER",
        "active": true,
        "createdAt": "2024-01-01T10:00:00Z",
        "lastLoginAt": "2024-01-01T12:00:00Z"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

## Error Responses

All endpoints return standardized error responses:

### Validation Error (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email should be valid"
    },
    {
      "field": "price",
      "message": "Price must be greater than 0"
    }
  ],
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/properties"
}
```

### Unauthorized (401 Unauthorized)
```json
{
  "success": false,
  "message": "Authentication required",
  "error": "Unauthorized",
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/admin/properties"
}
```

### Forbidden (403 Forbidden)
```json
{
  "success": false,
  "message": "Access denied",
  "error": "Forbidden",
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/admin/properties"
}
```

### Not Found (404 Not Found)
```json
{
  "success": false,
  "message": "Property not found",
  "error": "Not Found",
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/properties/999"
}
```

### Internal Server Error (500 Internal Server Error)
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "error": "Internal Server Error",
  "timestamp": "2024-01-01T10:00:00Z",
  "path": "/api/properties"
}
```

## Status Codes Summary

- **200 OK**: Successful GET, PUT, PATCH requests
- **201 Created**: Successful POST requests
- **400 Bad Request**: Validation errors, malformed requests
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Access denied (insufficient permissions)
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server errors

## Security Considerations

1. **JWT Authentication**: All admin endpoints require valid JWT tokens
2. **Role-based Access Control**: Admin endpoints are restricted to ADMIN role users
3. **Input Validation**: All input data is validated using Bean Validation
4. **SQL Injection Prevention**: Using JPA/Hibernate with parameterized queries
5. **CORS Configuration**: Properly configured for frontend integration
6. **Rate Limiting**: Consider implementing rate limiting for public endpoints
7. **HTTPS**: Use HTTPS in production environments
8. **Sensitive Data**: Never log or expose sensitive information like passwords
