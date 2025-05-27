# Code Words API 🎮

A simple word guessing game built using **Spring Boot**. This API allows players to start games, make guesses, forfeit games, and view leaderboards.

## 🧪 How to Run Tests

Make sure you have [Maven](https://maven.apache.org/) installed.

To run all tests:

```bash
mvn test
```

## 🚀 How to Run the Application

Use the following Maven command to start the Spring Boot application:

```bash
mvn spring-boot:run
```

Once the application starts, it will be accessible at:

```
http://localhost:8080
```

## 📘 Swagger UI

To explore and test the API interactively, open the Swagger UI in your browser:

👉 [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

## 🎯 API Overview

### 🕹 Game Endpoints

| Method | Endpoint                    | Description                          |
|--------|-----------------------------|--------------------------------------|
| POST   | `/game`                     | Start a new game                     |
| POST   | `/game/{id}/guess`          | Make a guess in an existing game     |
| POST   | `/game/{id}/forfeit`        | Forfeit the game                     |
| GET    | `/game/{id}`                | Get game by ID                       |

### 👤 Player Endpoints

| Method | Endpoint                        | Description               |
|--------|----------------------------------|---------------------------|
| GET    | `/players`                      | Get player by username    |
| GET    | `/players/leaderboard`         | Get leaderboard           |

## 📂 Project Structure

```
src/
├── main/
│   ├── java/...
│   └── resources/
│       └── application.properties
└── test/
    └── java/...
```

## 🔧 Requirements

- Java 17 or later
- Maven 3.6 or later

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

Made with ❤️ using Spring Boot.
