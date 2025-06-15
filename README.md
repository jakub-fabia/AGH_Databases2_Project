# Hotel reservation management system

Hotel room reservation system, using `Java Spring Boot`, `PostgreSQL` and `ReactJS`.

# Authors

- [Jakub Fabia](https://github.com/jakub-fabia)
- [Michał Gontarz](https://github.com/gontarsky04)

# [Full documentation (in Polish)](./raport/Jakub%20Fabia,%20Michal%20Gontarz%20-%20Miniprojekt.md)

# DB commands

```bash
docker exec -d hotel-app-db-1 pg_dump --host=localhost --port=5432 --dbname=booking --username=booking_user --file=/home/snapshot.sql
```

```bash
docker exec -d hotel-app-db-1 psql --username=booking_user --dbname=booking --file=/home/snapshot.sql
```