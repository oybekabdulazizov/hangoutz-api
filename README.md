# HangoutZ

Event Management REST API

## Description

Our Event Management REST API is a robust and secure application
developed using Java, Spring Boot 3, Spring Boot Hibernate, Spring Boot Security, and MySQL.
Tailored for the seamless organization and participation in events,
this application empowers users to host and attend events with ease, whether online or onsite.
UPDATE: it is now integrates with JpaRepository, a powerful JPA specific extension
of [Repository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/Repository.html).

## Key Features:

User Authentication and Authorization:
The application ensures a secure environment by implementing Spring Boot Security.
Users can sign up, obtain authentication Json Web tokens, and utilize them to create, update, and remove events.
However, reading the events data is publicly available.

Efficient Database Integration:
Leveraging the power of MySQL, the application efficiently stores and retrieves data,
ensuring optimal performance and reliability.

Exception Handling:
exception handling mechanisms are in place to enhance the reliability and stability of the application.
This ensures that the user experience remains smooth even in unexpected scenarios.

RESTful Architecture:
Following RESTful principles, the API endpoints are designed for simplicity, scalability, and
ease of integration. This allows developers to interact seamlessly with the application and
integrate it into a variety of systems.

Event Creation and Management:
Users have the capability to effortlessly create, manage, and update events.
The API supports operations such as creating new events, retrieving event details,
updating event information, and removing events when necessary.

User Account Management:
Users can manage their accounts (password reset, details update, and request admins to delete their account)

Category:
They are created and managed by only admins, but can be publicly readable.

## Security Measures:

The application prioritizes the security of user data and interactions.
With Spring Boot Security, it implements robust authentication and authorization mechanisms,
ensuring that only authenticated users with the appropriate permissions can perform sensitive operations.

## Technological Stack:

- Java
- Spring Boot 3
- Spring Boot Hibernate
- Spring Boot Security
- MySQL

## Use cases

- Event hosts/organizers can create and manage events efficiently.
- Users can sign up and manage their accounts efficiently.
- Participants can easily discover, view details, and attend events.
- Only Hangoutz app Admins can:
    - delete users accounts.
    - create and manage categories.
      (To be an admin, a user can sign up using @hangoutz.com domain in their email)
- Developers can seamlessly integrate the API into their systems.

## Contact me

If you have any questions regarding the application, please email me at oybek.tulqinovich@gmail.com  