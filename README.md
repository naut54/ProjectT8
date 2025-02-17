# ProjectT8 - Product Administration System

## Overview
ProjectT8 is a Java-based desktop application for product inventory management, built using Swing for the graphical user interface. It provides a comprehensive solution for managing products, categories, stock levels, and generating reports with an intuitive user interface.

## Features
- **Product Management**
  - Add, edit, and delete products
  - Categorize products with custom categories
  - Track product details including name, description, price, and SKU

- **Category System**
  - Create and manage product categories
  - Organize products hierarchically
  - Easy category assignment and modification

- **Inventory Control**
  - Real-time stock tracking
  - Low stock alerts
  - Stock adjustment history
  - Batch stock updates

- **Report Generation**
  - Stock level reports
  - Sales and inventory analytics
  - Category-wise product distribution
  - Custom report generation
  - Export reports in various formats

- **User-Friendly Interface**
  - Intuitive Swing-based GUI
  - Quick search functionality
  - Sortable and filterable data tables
  - Responsive design

## System Requirements
- Java Runtime Environment (JRE) 8 or higher
- Minimum 2GB RAM
- Windows/Linux/macOS operating system
- Screen resolution: 1024x768 or higher

## Installation
1. Download the latest release from the releases page
2. Extract the downloaded archive
3. Run `ProjectT8.jar` file
```bash
java -jar ProjectT8.jar
```

## Quick Start
1. Launch the application
2. Log in with default credentials (admin/admin)
3. Navigate to the desired module using the main menu
4. Start managing your products and inventory

## Configuration
The application can be configured through the `config.properties` file located in the installation directory. Key configurations include:
- Database connection settings
- Report export directory
- Backup settings
- Alert thresholds

## Database Setup
ProjectT8 uses a relational database to store all information. The application supports:
- MySQL
- PostgreSQL
- SQLite (for standalone deployment)

Default database configuration is SQLite for easy setup.

## Development
### Prerequisites
- Java Development Kit (JDK) 16 or higher
- Apache Maven
- Your preferred IDE (Eclipse/IntelliJ IDEA recommended)

### Building from Source
```bash
git clone https://github.com/naut54/ProjectT8.git
cd ProjectT8
mvn clean install
```

### Project Structure
```
ProjectT8/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docs/
├── config/
└── pom.xml
```

## Contributing
1. Fork the repository
2. Create a new branch for your feature
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## Support
- Check the [documentation](docs/) for detailed information
- Submit bug reports and feature requests through the issue tracker
- Contact support at: support@projectt8.com

## License
This project is licensed under the MIT License - see the LICENSE file for details

## Acknowledgments
- Java Swing library
- Apache Commons
- Log4j for logging
- JUnit for testing

## Version History
- v1.0.0 - Initial release
  - Basic product management
  - Category system
  - Stock control
  - Report generation

---
© 2025 ProjectT8. All rights reserved.
