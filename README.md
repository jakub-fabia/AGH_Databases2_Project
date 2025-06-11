# Booking-like database system WIP!!!
Hotel room reservation system, using Java Spring Boot and PostgreSQL.

# Authors

- [Jakub Fabia](https://github.com/jakub-fabia)
- [Michał Gontarz](https://github.com/gontarsky04)

# Komendy do bazy

**Nazwa kontenera może się różnić, sprawdź za pomocą `mongo ps`**

```bash
docker exec -d hotel-app-db-1 pg_dump --host=localhost --port=5432 --dbname=booking --username=booking_user --file=/home/snapshot.sql
```

Komenda do przywracania stanu bazy:

```bash
docker exec -d hotel-app-db-1 psql --username=booking_user --dbname=booking --file=/home/snapshot.sql
```