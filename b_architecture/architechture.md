# PDF Viewer SDK Web App - Architecture

## Component Diagram
```text
+-------------------+       HTTP/REST         +-------------------------+
|   Client (Browser)| <---------------------> | Spring Boot API         |
|                   |                         |                         |
| +---------------+ |                         | +---------------------+ |
| | UI Layer      | |     Multipart Form      | | PdfController       | |
| | (HTML/CSS)    | | ----------------------> | | (Upload/Download)   | |
| +---------------+ |                         | +---------------------+ |
| +---------------+ |                         | +---------------------+ |
| | View Engine   | |                         | | File System         | |
| | (PDF.js)      | |                         | | (/uploads)          | |
| +---------------+ |                         | +---------------------+ |
| +---------------+ |                         +-------------------------+
| | Edit Engine   | |
| | (pdf-lib)     | |
| +---------------+ |
+-------------------+