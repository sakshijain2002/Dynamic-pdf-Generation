# Dynamic PDF Generator

This project is a **Spring Boot** service that generates and serves invoices in **PDF format**. When an invoice is requested, it either generates a new PDF or retrieves an already-generated one (if the same invoice data is provided). The PDF is generated using the **iText PDF** library and stored locally for future access.

## Features

- Generate an invoice PDF with seller, buyer, and item details.
- Store the generated PDF locally.
- Retrieve and serve the previously generated PDF if the same invoice data is provided (to avoid redundant generation).
- Download the generated PDF via an API.

## Technologies Used

- Java 17
- Spring Boot
- iText PDF (for generating PDFs)
- Maven (for dependency management)
- Postman (for testing API endpoints)

# **Generate or Download Invoice PDF**

Endpoint: /api/invoices/generate
Method: POST
Content-Type: application/json
Response: PDF file
Request Payload:
{
"seller": "XYZ Pvt. Ltd.",
"sellerGstin": "29AABBCCDD121ZD",
"sellerAddress": "New Delhi, India",
"buyer": "Vedant Computers",
"buyerGstin": "29AABBCCDD131ZD",
"buyerAddress": "New Delhi, India",
"items": [
{
"name": "Product 1",
"quantity": "12 Nos",
"rate": 123.00,
"amount": 1476.00
}
]
}

# Testing via Postman

1. Open Postman and create a POST request.
2. Set the URL to http://localhost:8080/api/invoices/generate.
3. Add the JSON payload in the Body section as described above.
4. Click on Send and Download to download the generated PDF file.