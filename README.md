# KLASS
A Spring Boot application that handles classifications for SSB.
Klass provides a REST api that clients can use to read classifications, and a Vaadin frontend for maintaining classifications.

## Usage

### Build
    mvn clean install
    
### Build (with REST api documentation)
    mvn clean install -Pdocumentation
    
### Run
    mvn spring-boot:run
    
Frontend may be accessed at:

    http://localhost:8080/klassui
    
REST api documentation may be accessed at (must build with -Pdocumentation):

    http://localhost:8080/api/klass/v1/api-guide.html
    

### known issues
    Frontend lacks localization
