
# Image Converter API
A simple Springboot application to convert images between different formats.
The following formats are supported:
* jpg
* gif
* png
* bmp

## Setup & run
The application has been implemented on Springboot 3, Gradle 8.7 and tested on Amazon Corretto Java 17.

##### Prerequisites
* Java 17
##### Run the app
* _./gradlew bootRun_ OR
* _./gradlew build_, followed by
	* _java -jar build/libs/spring-boot-imgconverter-0.0.1-SNAPSHOT.jar_

## Assumptions
- The API accepts a url pointing at an image to be converted, (as opposed to form data).
- The asynchronous endpoint publishes its result to a fictional integration point - this could be a queue, webhook or other event streaming service that the API client is subscribed to.

## Improvements
The following improvements could be made to bring this app closer to a production state.
- Add support for multipart form input in addition to image URL.
- Implement file size limt.
- Implement a proper data store.
- Implement support for asynchronous processing notification via queue, webhook etc..
- Add support for more image formats.
- Refactor to extract static references to javax.imageio.ImageIO so that mocks can be injected to facilitate more test scenarios.
- Add OpenAPI/Swagger annotations to describe the API + add Swagger UI or similar.
- Separate Unit test from integration tests so that they can be run separately in CI pipeline.
- Add error messages to failed API responses.

## Testing Considerations
• Consider how your solution will be tested and whether that testing could be automated.
• Consider how your solution will be deployed. Can this be automated and to what degree?