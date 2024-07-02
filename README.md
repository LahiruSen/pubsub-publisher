# Pub/Sub Message Publisher

This project is a Java application that publishes messages to a Google Cloud Pub/Sub topic. It is designed to test data integration pipelines by generating a series of messages from an XML template file.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- A Google Cloud Platform (GCP) project with Pub/Sub API enabled.
- Java Development Kit (JDK) installed on your machine.
- Maven for dependency management.

## Installation

1. Clone this repository to your local machine:
    ```bash
    git clone https://github.com/LahiruSen/pubsub-publisher
    cd pubsub-publisher
    ```

2. Create a `pom.xml` file in the project's root directory and add the following dependencies:

    ```xml
    <dependencies>
        <dependency>
            <groupId>com.google.cloud</groupId>
            <artifactId>google-cloud-pubsub</artifactId>
            <version>1.113.7</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.32</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.32</version>
        </dependency>
    </dependencies>
    ```

3. Ensure the XML template file `appointment.xml` is placed in the `resources` directory of your project.

## Usage

1. Update the `Main` class with your GCP project ID and Pub/Sub topic ID:
    ```java
    String projectId = "your-gcp-project-id";
    String topicId = "your-pubsub-topic";
    ```

2. Run the application. This will read the XML file, extract the sequence number, and publish messages to the specified Pub/Sub topic.

    ```bash
    mvn compile
    mvn exec:java -Dexec.mainClass="org.example.Main"
    ```

## XML Template

Here is a sample `appointment.xml` file that the application reads and uses for publishing messages:

```xml
<Appointment>
    <AppointmentId>12345</AppointmentId>
    <Sequence_Number>1</Sequence_Number>
    <PatientName>John Doe</PatientName>
    <AppointmentDate>2024-07-01</AppointmentDate>
    <DoctorName>Dr. Jane Smith</DoctorName>
    <Department>Cardiology</Department>
    <Status>Confirmed</Status>
</Appointment>
```

## Code Overview

### Main Class

The `Main` class contains the main logic of the application:

- Reads the XML file.
- Extracts the sequence number.
- Publishes messages to the Pub/Sub topic.

### Methods

- `readFile(String filePath)`: Reads the content of the specified XML file.
- `extractTagValue(String xmlString, String tagName)`: Extracts the value of a specified tag from the XML string.
- `publishMessageWithRetry(String message)`: Publishes a message to the Pub/Sub topic with error handling and logging.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Google Cloud Pub/Sub](https://cloud.google.com/pubsub/docs)
- [SLF4J - Simple Logging Facade for Java](http://www.slf4j.org/)

---
