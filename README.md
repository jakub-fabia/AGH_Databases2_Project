# Hotel reservation management system

Hotel room reservation system, using `Java Spring Boot`, `PostgreSQL` and `ReactJS`.

# Authors

- [Jakub Fabia](https://github.com/jakub-fabia)
- [Michał Gontarz](https://github.com/gontarsky04)

# Quick-start guide

In the project directory execute

```bash
docker compose up --detach
```

Container ports are:
- 3000 for frontend,
- 8082 for backend,
- 5432 for database.

Login details for database are in the ['compose.yaml'](./compose.yaml) file.

# [Full documentation (in Polish)](./raport/Jakub%20Fabia,%20Michal%20Gontarz%20-%20Miniprojekt.md)

# DB state commands

```bash
docker exec -d \<db-container-name\> pg_dump --host=localhost --port=5432 --dbname=booking --username=booking_user --file=/home/snapshot.sql
```

```bash
docker exec -d \<db-container-name\> psql --username=booking_user --dbname=booking --file=/home/snapshot.sql
```