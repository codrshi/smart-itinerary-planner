# Smart Itinerary Planner API
Smart Itinerary Planner API is a Spring Boot–based RESTful service designed to generate optimized travel itineraries for users based on location and date range.
The application integrates with multiple third-party APIs to collect real-time data, like day-wise weather forecasts and attractions (attraction sites and local events).

Using this data, the system intelligently generates an itinerary by:
- Prioritizing days with favorable weather conditions
- Evenly distributing activities across the trip duration
- Avoiding activity clustering while maintaining a balanced daily schedule

The Itinerary is the core domain entity of the system. Users can perform full CRUD (Create, Read, Update, Delete) operations on itineraries through well-defined REST APIs.
The API is designed with extensibility, modular integration, and real-world usability in mind, making it suitable for travel planning platforms and intelligent trip management applications.

## Languages Used
- **Language**: Java
- **Framework**: Spring Boot
- **Persistence**: MongoDB
- **Caching & Rate Limiting**: Redis

## Third-party Integration
- **Ticketmaster Discovery API**: used to fetch local events happening in the selected location during the specified period
- **Geoapify Places API**: used to fetch attraction sites in the selected location
- **Virtual Crossing Weather API**: used to fetch day-wise weather forecasts for the specified period

## AI Assistant Support
In addition to standard REST functionality, the project includes a LLM-based assistant that enhances user interaction. The AI assistant can:
- Generate itineraries based on user-provided location and travel period
- Answer user queries related to existing itineraries
- Delete itineraries on user request
- Generate a natural-language summary of an itinerary and send it to the user via email

## External Links
- [Smart Itinerary Planner] (https://smart-itinerary-planner.onrender.com/itinerary)
- [Swagger Documentation] (https://smart-itinerary-planner.onrender.com/swagger-ui/index.html)
