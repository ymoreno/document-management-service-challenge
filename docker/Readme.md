# Document Management Service Challenge

## Overview
This project is a Document Management Service that provides functionality for uploading, searching, and downloading documents efficiently. It supports large file uploads while maintaining memory constraints.

## Prerequisites
- Java 17+
- Maven 3+
- PostgreSQL 14+
- Git

## Database Setup

1. Start PostgreSQL and create the database:

   ```sql
   CREATE DATABASE document_management;
   ```

2. Run the following SQL script to set up the necessary tables:

   ```sh
   psql -U your_username -d document_management -f path/to/schema.sql
   ```

   Replace `your_username` with your PostgreSQL username and `path/to/schema.sql` with the actual path to your SQL script.

## Building and Running the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/ymoreno/document-management-service-challenge.git
   ```

2. Navigate to the project directory:
   ```sh
   cd document-management-service-challenge
   ```

3. Build the project using Maven:
   ```sh
   mvn clean install
   ```

4. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Running Tests
The test reports are generated in the `surefire-reports` folder.

Run the tests with:
   ```sh
   mvn test
   ```

After running the tests, you can find the reports in:
   ```sh
   target/surefire-reports/
   ```

## API Endpoints
### Upload Document
- **Endpoint:** `POST /upload`
- **Description:** Uploads a document.

### Search Documents
- **Endpoint:** `GET /search`
- **Description:** Searches documents with filtering, sorting, and pagination.

### Download Document
- **Endpoint:** `GET /download/{documentId}`
- **Description:** Downloads a document by its ID.

## Contributing
1. Fork the repository.
2. Create a new branch: `git checkout -b feature-branch`
3. Commit your changes: `git commit -m "feat: add new feature"`
4. Push the branch: `git push origin feature-branch`
5. Open a Pull Request.

## License
This project is licensed under the MIT License.

