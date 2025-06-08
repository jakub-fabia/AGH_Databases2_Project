# System rezerwacji hotelowych

**Autorzy:** Jakub Fabia · Michał Gontarz

---

## 1. Model danych

![Diagram ER](./img/Schema.png)

Kompletne definicje SQL znajdują się w pliku [`sql/tables_enums.sql`](./sql/tables_enums.sql).

---

### 1.1 Enumy

| Enum                | Wartości                                                                    | Opis                          |
| ------------------- | --------------------------------------------------------------------------- | ----------------------------- |
| **room\_status**    | `AVAILABLE`, `OCCUPIED`, `DIRTY`, `MAINTENANCE`                             | Aktualny stan każdego pokoju  |
| **booking\_status** | `PENDING`, `CONFIRMED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`, `NO_SHOW` | Aktualny stan rezerwacji      |

<div style="page-break-after: always;"></div>

### 1.2 Tabela `hotel`

> Jednostka noclegowa widoczna w wynikach wyszukiwania (hotel, pensjonat, domek, hostel). Osoba zarządzająca hotelem ma dostęp do raportów sprzedaży i logów.

| Kolumna       | Typ                  | Klucz / ograniczenia         | Znaczenie                                  |
| ------------- | -------------------- | ---------------------------- | ------------------------------------------ |
| hotel_id      | SERIAL               | **PK**                       | Identyfikator hotelu                       |
| name          | VARCHAR **NOT NULL** |                              | Nazwa obiektu widoczna w ofercie           |
| country       | VARCHAR **NOT NULL** |                              | Kraj                                       |
| city          | VARCHAR **NOT NULL** |                              | Miasto / miejscowość                       |
| address       | TEXT **NOT NULL**    |                              | Ulica + nr domu/mieszkania                 |
| phone         | VARCHAR **NOT NULL** | UNIQUE                       | Telefon recepcji                           |
| email         | VARCHAR **NOT NULL** | UNIQUE                       | Adres e‑mail obiektu                       |
| stars         | SMALLINT             | NULL OR 1 ≤ stars ≤ 5        | Oficjalna kategoryzacja (1–5★)             |
| review_score  | NUMERIC              | NULL OR 0 ≤ score ≤ 5        | Średnia ocena z recenzji gości (np. 4.37)  |
| checkin_time  | TIME **NOT NULL**    | checkin_time < checkout_time | Standardowa godzina zameldowania           |
| checkout_time | TIME **NOT NULL**    |                              | Standardowa godzina wymeldowania           |

---

### 1.3 Tabela `room_type`

> Globalny katalog kategorii pokoi oferowanych na platformie (np. pokój hotelowy, apartament, domek, łóżko hostelowe).

| Kolumna     | Typ                   | Klucz / ograniczenia | Znaczenie                                       |
| ----------- | --------------------- | -------------------- | ----------------------------------------------- |
| type_id     | SERIAL                | **PK**               | Identyfikator typu                              |
| name        | VARCHAR **NOT NULL**  |                      | Nazwa kategorii ("Apartament", "Łóżko"...)      |
| capacity    | SMALLINT **NOT NULL** | capacity > 0         | Liczba osób, które można zakwaterować           |

---

<div style="page-break-after: always;"></div>

### 1.4 Tabela `hotel_room_type`

> Relacja N\:M między hotelami a typami pokoi. Pozwala ustalić cenę i wielkość inwentarza danego typu w konkretnym obiekcie.

| Kolumna         | Typ                   | Klucz / ograniczenia     | Znaczenie                          |
| --------------- | --------------------- | ------------------------ | ---------------------------------- |
| hotel_id        | INT                   | **PK**, FK → `hotel`     | Hotel                              |
| type_id         | INT                   | **PK**, FK → `room_type` | Typ pokoju                         |
| description     | TEXT **NOT NULL**     |                          | Opis pokoji tego typu w tym hotelu |
| price_per_night | NUMERIC **NOT NULL**  | price > 0                | Aktualna cena za noc               |
| total_rooms     | SMALLINT **NOT NULL** | total_rooms ≥ 0          | Liczba pokoi tego typu w hotelu    |

---

### 1.5 Tabela `room`

> Każda fizyczna jednostka noclegowa (konkretny pokój 101, bungalow A‑3, łóżko #4 w hostelu).

| Kolumna     | Typ                      | Klucz / ograniczenia                  | Znaczenie                       |
| ----------- | ------------------------ | ------------------------------------- | ------------------------------- |
| room_id     | SERIAL                   | **PK**                                | Identyfikator pokoju            |
| hotel_id    | INT   **NOT NULL**       | FK części skł. PK → `hotel_room_type` | Hotel, do którego należy pokój  |
| type_id     | INT   **NOT NULL**       | FK części skł. PK → `hotel_room_type` | Kategoria pokoju w danym hotelu |
| room_number | VARCHAR **NOT NULL**     | UNIQUE(hotel_id, room_number)         | Numer "jednostki noclegowej"    |
| status      | room_status **NOT NULL** | DEFAULT 'AVAILABLE'                   | Aktualny stan pokoju            |

---

<div style="page-break-after: always;"></div>

### 1.6 Tabela `room_log`

> Historia zmian statusu dla każdego pokoju (dla raportów o poszczególnych pokojach).

| Kolumna       | Typ           | Klucz / ograniczenia | Znaczenie                     |
| ------------- | ------------- | -------------------- | ----------------------------- |
| room_log_id   | SERIAL        | **PK**               | Klucz sztuczny                |
| room_id       | INT           | FK → `room`          | Obiekt, którego dotyczy wpis  |
| created_at    | TIMESTAMPTZ   | DEFAULT now()        | Znacznik czasu zmiany         |
| status        | room_status   |                      | Nowy status pokoju            |

---

### 1.7 Tabela `guest`

> Dane kontaktowe klienta składającego rezerwację.

| Kolumna                | Typ                  | Klucz / ograniczenia    | Znaczenie                  |
| ---------------------- | -------------------- | ----------------------- | -------------------------- |
| guest_id               | SERIAL               | **PK**                  | Identyfikator gościa       |
| first_name / last_name | VARCHAR **NOT NULL** |                         | Imię i nazwisko            |
| date_of_birth          | DATE **NOT NULL**    | date_of_birth < today   | Data urodzenia             |
| country / city         | VARCHAR **NOT NULL** |                         | Adres zamieszkania         |
| address                | TEXT **NOT NULL**    |                         | Ulica + nr domu/mieszkania |
| phone                  | VARCHAR              | UNIQUE                  | Telefon kontaktowy         |
| email                  | VARCHAR              | UNIQUE                  | E‑mail                     |

---

<div style="page-break-after: always;"></div>

### 1.8 Tabela `booking`

> Nagłówek rezerwacji (dane wspólne dla wszystkich pokoi w jednym zamówieniu).

| Kolumna                      | Typ                  | Klucz / ograniczenia       | Znaczenie                      |
| ---------------------------- | -------------------- | -------------------------- | ------------------------------ |
| booking_id                   | SERIAL               | **PK**                     | Identyfikator zamówienia       |
| guest_id                     | INT   **NOT NULL**   | FK → `guest`               |                                |
| checkin_date / checkout_date | DATE **NOT NULL**    | CHECK checkout > checkin   | Data zameldowania/wymeldowania |
| total_price                  | NUMERIC **NOT NULL** | total_price > 0            | Kwota za całe zamówienie       |
| status                       | booking_status       | DEFAULT 'PENDING'          | Aktualny status rezerwacji     |
| created_at                   | TIMESTAMPTZ          | DEFAULT now()              | Data utworzenia rezerwacji     |
| review                       | TEXT                 |                            | Opinia klienta po pobycie      |
| review_rating                | SMALLINT             | NULL OR 1 ≤ rating ≤ 5     | Ocena klienta (1-5)            |

---

### 1.9 Tabela `booking_room`

> Szczegóły rezerwacji — które pokoje wchodzą w skład jednego `booking` i z jakimi dodatkami.

| Kolumna       | Typ     | Klucz / ograniczenia  | Znaczenie                             |
| ------------- | ------- | --------------------- | ------------------------------------- |
| booking_id    | INT     | **PK** fk → `booking` |                                       |
| room_id       | INT     | **PK** fk → `room`    |                                       |
| breakfast     | BOOLEAN | DEFAULT false         | Czy gość zamówił śniadanie            |
| late_checkout | BOOLEAN | DEFAULT false         | Czy gość zamówił późne wykwaterowanie |

---

<div style="page-break-after: always;"></div>

### 1.10 Tabela `booking_log`

> Migawka zmian rezerwacji obejmująca główne pola oraz listę pokojów (w formacie JSON).

| Kolumna                      | Typ            | Klucz / ograniczenia  | Znaczenie                                                                |
| ---------------------------- | -------------- | --------------------- | ------------------------------------------------------------------------ |
| booking_log_id               | SERIAL         | **PK**                |                                                                          |
| booking_id                   | INT            | FK → `booking`        |                                                                          |
| created_at                   | TIMESTAMPTZ    | DEFAULT now()         |                                                                          |
| checkin_date / checkout_date | DATE           |                       | Nowe daty zameldowania/wymeldowania                                      | 
| status                       | booking_status |                       | Nowy status zamówienia                                                   |
| booking_rooms                | JSONB          |                       | Nowe szczegóły zamówienia w formie `{room_id, breakfast, late_checkout}` |

---

### 1.11. Założenia i uproszczenia

* Ceny sezonowe nie są modelowane — zakładamy jedną stawkę `price_per_night` na typ w hotelu.
* Obsługa płatności nie będzie implementowana.
* `capacity` jest określona na poziomie `room_type`; w razie odstępstw można dodać kolumnę `capacity_override` w `room`.

### 1.12 Kod generujący tabele

```sql
CREATE TYPE room_status AS ENUM (
  'AVAILABLE',
  'OCCUPIED',
  'DIRTY',
  'MAINTENANCE'
);
CREATE TYPE booking_status as ENUM (
  'PENDING', 
  'CONFIRMED', 
  'CHECKED_IN', 
  'CHECKED_OUT', 
  'CANCELLED', 
  'NO_SHOW'
);

CREATE TABLE hotel (
  hotel_id      SERIAL       PRIMARY KEY,
  name          VARCHAR(255) NOT NULL,
  country       VARCHAR(30)  NOT NULL,
  city          VARCHAR(30)  NOT NULL,
  address       TEXT         NOT NULL,
  phone         VARCHAR(20)  NOT NULL,
  email         VARCHAR(255) NOT NULL,
  stars         SMALLINT,
  review_score  NUMERIC(3,2),
  checkin_time  TIME         NOT NULL,
  checkout_time TIME         NOT NULL
);

CREATE TABLE room_type (
  type_id     SERIAL      PRIMARY KEY,
  name        VARCHAR(50) NOT NULL,
  capacity    SMALLINT    NOT NULL
);

CREATE TABLE hotel_room_type (
  hotel_id        INT           REFERENCES hotel,
  type_id         INT           REFERENCES room_type,
  description     TEXT          NOT NULL,
  price_per_night NUMERIC(10,2) NOT NULL,
  total_rooms     SMALLINT      NOT NULL,
  PRIMARY KEY (hotel_id, type_id)
);

CREATE TABLE room (
  room_id      SERIAL      PRIMARY KEY,
  hotel_id     INT         NOT NULL,
  type_id      INT         NOT NULL,
  room_number  VARCHAR(10) NOT NULL,
  status       room_status NOT NULL DEFAULT 'AVAILABLE',
  FOREIGN KEY (hotel_id, type_id) REFERENCES hotel_room_type(hotel_id, type_id),
);

CREATE TABLE room_log (
  room_log_id SERIAL         PRIMARY KEY,
  room_id     INT            NOT NULL REFERENCES room,
  created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
  status      room_status    NOT NULL
);

CREATE TABLE guest (
  guest_id      SERIAL       PRIMARY KEY,
  first_name    VARCHAR(50)  NOT NULL,
  last_name     VARCHAR(50)  NOT NULL,
  date_of_birth DATE         NOT NULL,
  country       VARCHAR(30)  NOT NULL,
  city          VARCHAR(30)  NOT NULL,
  address       TEXT         NOT NULL,
  phone         VARCHAR(20),
  email         VARCHAR(255)
);

CREATE TABLE booking (
  booking_id     SERIAL         PRIMARY KEY,
  guest_id       INT            NOT NULL REFERENCES guest,
  checkin_date   DATE           NOT NULL,
  checkout_date  DATE           NOT NULL,
  total_price    NUMERIC(10,2)  NOT NULL,
  status         booking_status NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  review         TEXT,
  review_rating  SMALLINT
);

CREATE TABLE booking_room (
  booking_id    INT            NOT NULL REFERENCES booking,
  room_id       INT            NOT NULL REFERENCES room,
  breakfast     BOOLEAN        NOT NULL DEFAULT false,
  late_checkout BOOLEAN        NOT NULL DEFAULT false,
  PRIMARY KEY (booking_id, room_id)
);

CREATE TABLE booking_log (
  booking_log_id SERIAL         PRIMARY KEY,
  booking_id     INT            NOT NULL REFERENCES booking,
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  checkin_date   DATE           NOT NULL,
  checkout_date  DATE           NOT NULL,
  status         booking_status NOT NULL,
  booking_rooms  JSONB          NOT NULL
);
```

<div style="page-break-after: always;"></div>

## 2. Reguły integralności & indeksy

Reguły integralności oraz indeksy poprawiające wydajność opisano w pliku [`sql/constraints_indexes.sql`](./sql/constraints_indexes.sql).


Poza wypisanymi w tabelce ograniczeniami dodano:
* Wymaganie co najmniej jednego kontaktu (telefon lub e‑mail) w tabeli `guest`.
* Brak nakładających się rezerwacji na ten sam pokój.

```sql
ALTER TABLE hotel
  ADD CHECK (email IS UNIQUE),
  ADD CHECK (phone IS UNIQUE),
  ADD CHECK (stars IS NULL OR stars BETWEEN 1 AND 5),
  ADD CHECK (review_score IS NULL OR review_score BETWEEN 0 AND 5),
  ADD CHECK (checkin_time < checkout_time);

ALTER TABLE room_type
  ADD CHECK (capacity > 0);

ALTER TABLE hotel_room_type
  ADD CHECK (price_per_night > 0),
  ADD CHECK (total_rooms >= 0);

ALTER TABLE room
  ADD CHECK ((hotel_id, room_number) IS UNIQUE);

ALTER TABLE guest
  ADD CHECK (email IS UNIQUE),
  ADD CHECK (phone IS UNIQUE),
  ADD CHECK (date_of_birth < CURRENT_DATE),
  ADD CHECK (email IS NOT NULL OR phone IS NOT NULL);

ALTER TABLE booking
  ADD CHECK (checkout_date > checkin_date),
  ADD CHECK (total_price > 0),
  ADD CHECK (review_rating IS NULL OR review_rating BETWEEN 1 AND 5);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE booking_room
  ADD CONSTRAINT no_room_overlap
  EXCLUDE USING gist (
       room_id          WITH =,     -- sprawdzamy tylko dla tych samych pokojow
       daterange(                   -- tworze przedzial [check-in, check-out)
           (SELECT checkin_date  FROM booking b WHERE b.booking_id = booking_room.booking_id),
           (SELECT checkout_date FROM booking b WHERE b.booking_id = booking_room.booking_id)
       ) WITH &&                    -- operator przecinania się przedzialow
  );
```

<div style="page-break-after: always;"></div>

Utworzyliśmy także indeksy poprawiające wydajność bazy danych:
* Na klucze obce (PostgreSQL nie tworzy ich automatycznie).
* Do wyszukiwania dostępnych pokoi (`room.status + hotel_id + type_id`).
* Do sprawdzania rezerwacji między datami.
* Do logów po dacie utworzenia.


```sql
CREATE INDEX idx_room_hotel       ON room            (hotel_id);
CREATE INDEX idx_room_type        ON room            (type_id);
CREATE INDEX idx_hotelroom_type   ON hotel_room_type (type_id);
CREATE INDEX idx_booking_guest    ON booking         (guest_id);
CREATE INDEX idx_bookingroom_room ON booking_room    (room_id);

CREATE INDEX idx_room_status_hotel_type ON room (status, hotel_id, type_id);

CREATE INDEX idx_booking_dates ON booking (checkin_date, checkout_date);

CREATE INDEX idx_roomlog_room_ts ON room_log (room_id, created_at DESC);
CREATE INDEX idx_bookinglog_booking_ts ON booking_log (booking_id, created_at DESC);
```

---


## 6. Możliwości technologii wykorzystanych w projekcie

- Zapisywanie plików JSON w tabeli SQL
- Użycie indeksu GiST do warunku integralnościowego nakładających się rezerwacji na ten sam pokój