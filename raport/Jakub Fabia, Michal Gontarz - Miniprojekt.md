# System rezerwacji hotelowych

**Autorzy:** Jakub Fabia · Michał Gontarz

---

## 1. Model danych

![Diagram ER](./img/Schema.png)

Kompletne definicje SQL znajdują się w pliku [`sql/tables.sql`](./sql/tables.sql).

---

<div style="page-break-after: always;"></div>

# NIEAKTUALNE DO POPRAWY!!!!!!!!!!!

### 1.1 Tabela `hotel`

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
| review_sum    | INT **NOT NULL**     | review_sum ≥ 0               | Suma ocen recenzji gości                   |
| review_count  | INT **NOT NULL**     | review_count ≥ 0             | Liczba recenzji gości                      |
| checkin_time  | TIME **NOT NULL**    | checkin_time > checkout_time | Standardowa godzina zameldowania           |
| checkout_time | TIME **NOT NULL**    |                              | Standardowa godzina wymeldowania           |

---

### 1.2 Tabela `room_type`

> Globalny katalog kategorii pokoi oferowanych na platformie (np. pokój hotelowy, apartament, domek, łóżko hostelowe).

| Kolumna     | Typ                   | Klucz / ograniczenia | Znaczenie                                       |
| ----------- | --------------------- | -------------------- | ----------------------------------------------- |
| type_id     | SERIAL                | **PK**               | Identyfikator typu                              |
| name        | VARCHAR **NOT NULL**  |                      | Nazwa kategorii ("Apartament", "Łóżko"...)      |
| capacity    | SMALLINT **NOT NULL** | capacity > 0         | Liczba osób, które można zakwaterować           |

---

<div style="page-break-after: always;"></div>

### 1.3 Tabela `hotel_room_type`

> Relacja N\:M między hotelami a typami pokoi. Pozwala ustalić cenę i wielkość inwentarza danego typu w konkretnym obiekcie.

| Kolumna         | Typ                   | Klucz / ograniczenia     | Znaczenie                          |
| --------------- | --------------------- | ------------------------ | ---------------------------------- |
| hotel_id        | INT                   | **PK**, FK → `hotel`     | Hotel                              |
| type_id         | INT                   | **PK**, FK → `room_type` | Typ pokoju                         |
| description     | TEXT **NOT NULL**     |                          | Opis pokoji tego typu w tym hotelu |
| price_per_night | NUMERIC **NOT NULL**  | price > 0                | Aktualna cena za noc               |
| total_rooms     | SMALLINT **NOT NULL** | total_rooms ≥ 0          | Liczba pokoi tego typu w hotelu    |

---

### 1.4 Tabela `room`

> Każda fizyczna jednostka noclegowa (konkretny pokój 101, bungalow A‑3, łóżko #4 w hostelu).

| Kolumna     | Typ                      | Klucz / ograniczenia                  | Znaczenie                       |
| ----------- | ------------------------ | ------------------------------------- | ------------------------------- |
| room_id     | SERIAL                   | **PK**                                | Identyfikator pokoju            |
| hotel_id    | INT   **NOT NULL**       | FK części skł. PK → `hotel_room_type` | Hotel, do którego należy pokój  |
| type_id     | INT   **NOT NULL**       | FK części skł. PK → `hotel_room_type` | Kategoria pokoju w danym hotelu |
| room_number | VARCHAR **NOT NULL**     | UNIQUE(hotel_id, room_number)         | Numer "jednostki noclegowej"    |
| status      | VARCHAR **NOT NULL**     | DEFAULT 'AVAILABLE'                   | Aktualny stan pokoju            |

---

<div style="page-break-after: always;"></div>

### 1.5 Tabela `room_log`

> Historia zmian statusu dla każdego pokoju (dla raportów o poszczególnych pokojach).

| Kolumna       | Typ           | Klucz / ograniczenia | Znaczenie                     |
| ------------- | ------------- | -------------------- | ----------------------------- |
| room_log_id   | SERIAL        | **PK**               | Klucz sztuczny                |
| room_id       | INT           | FK → `room`          | Obiekt, którego dotyczy wpis  |
| created_at    | TIMESTAMPTZ   | DEFAULT now()        | Znacznik czasu zmiany         |
| status        | VARCHAR       |                      | Nowy status pokoju            |

---

### 1.6 Tabela `guest`

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

### 1.7 Tabela `booking`

> Nagłówek rezerwacji (dane wspólne dla wszystkich pokoi w jednym zamówieniu).

| Kolumna                      | Typ                  | Klucz / ograniczenia       | Znaczenie                      |
| ---------------------------- | -------------------- | -------------------------- | ------------------------------ |
| booking_id                   | SERIAL               | **PK**                     | Identyfikator zamówienia       |
| guest_id                     | INT   **NOT NULL**   | FK → `guest`               |                                |
| total_price                  | NUMERIC **NOT NULL** | total_price > 0            | Kwota za całe zamówienie       |
| status                       | VARCHAR **NOT NULL** | DEFAULT 'PENDING'          | Aktualny status rezerwacji     |
| created_at                   | TIMESTAMPTZ **NOT NULL** | DEFAULT now()              | Data utworzenia rezerwacji     |
| review                       | TEXT                 |                            | Opinia klienta po pobycie      |
| review_rating                | SMALLINT             | NULL OR 1 ≤ rating ≤ 5     | Ocena klienta (1-5)            |

---

### 1.8 Tabela `booking_room`

> Szczegóły rezerwacji — które pokoje wchodzą w skład jednego `booking` i z jakimi dodatkami.

| Kolumna       | Typ                  | Klucz / ograniczenia  | Znaczenie                             |
| ------------- | -------------------- | --------------------- | ------------------------------------- |
| booking_id    | INT                  | **PK** fk → `booking` |                                       |
| room_id       | INT                  | **PK** fk → `room`    |                                       |
| checkin_date  | DATE **NOT NULL**    | checkout > checkin    | Data zameldowania                     |
| checkout_date | DATE **NOT NULL**    | checkout > checkin    | Data wymeldowania                     |
| breakfast     | BOOLEAN **NOT NULL** | DEFAULT false         | Czy gość zamówił śniadanie            |
| late_checkout | BOOLEAN **NOT NULL** | DEFAULT false         | Czy gość zamówił późne wykwaterowanie |

---

<div style="page-break-after: always;"></div>

### 1.9 Tabela `booking_log`

> Migawka zmian rezerwacji obejmująca główne pola oraz listę pokojów (w formacie JSON).

| Kolumna                      | Typ            | Klucz / ograniczenia  | Znaczenie                                                                |
| ---------------------------- | -------------- | --------------------- | ------------------------------------------------------------------------ |
| booking_log_id               | SERIAL         | **PK**                |                                                                          |
| booking_id                   | INT            | FK → `booking`        |                                                                          |
| created_at                   | TIMESTAMPTZ    | DEFAULT now()         |                                                                          |
| status                       | VARCHAR        |                       | Nowy status zamówienia                                                   |
| booking_rooms                | JSONB          |                       | Nowe szczegóły zamówienia w formie `{room_id, checkin, checkout, breakfast, late_checkout}` |

---

### 1.10 Założenia i uproszczenia

* Ceny sezonowe nie są modelowane — zakładamy jedną stawkę `price_per_night` na typ w hotelu.
* Obsługa płatności nie będzie implementowana.
* `capacity` jest określona na poziomie `room_type`; w razie odstępstw można dodać kolumnę `capacity_override` w `room`.

### 1.12 Kod generujący tabele

```sql
CREATE TABLE hotel (
  hotel_id      SERIAL       PRIMARY KEY,
  name          VARCHAR(255) NOT NULL,
  country       VARCHAR(30)  NOT NULL,
  city          VARCHAR(30)  NOT NULL,
  address       TEXT         NOT NULL,
  phone         VARCHAR(20)  NOT NULL,
  email         VARCHAR(255) NOT NULL,
  stars         SMALLINT,
  review_sum    INT          NOT NULL DEFAULT 0,
  review_count  INT          NOT NULL DEFAULT 0,
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
  status       VARCHAR(11) NOT NULL DEFAULT 'AVAILABLE',
  FOREIGN KEY (hotel_id, type_id) REFERENCES hotel_room_type(hotel_id, type_id)
);

CREATE TABLE room_log (
  room_log_id SERIAL         PRIMARY KEY,
  room_id     INT            NOT NULL REFERENCES room,
  created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
  status      VARCHAR(11)    NOT NULL
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
  total_price    NUMERIC(10,2)  NOT NULL,
  status         VARCHAR(11)    NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  review         TEXT,
  review_rating  SMALLINT
);


CREATE TABLE booking_room (
  booking_id    INT            NOT NULL REFERENCES booking,
  room_id       INT            NOT NULL REFERENCES room,
  breakfast     BOOLEAN        NOT NULL DEFAULT false,
  late_checkout BOOLEAN        NOT NULL DEFAULT false,
  checkin_date   DATE          NOT NULL,
  checkout_date  DATE          NOT NULL,
  PRIMARY KEY (booking_id, room_id)
);


CREATE TABLE booking_log (
  booking_log_id SERIAL         PRIMARY KEY,
  booking_id     INT            NOT NULL REFERENCES booking,
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  status         VARCHAR(11)    NOT NULL,
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
  ADD UNIQUE(name, country, city, address),
  ADD UNIQUE(email),
  ADD UNIQUE(phone),
  ADD CHECK (stars IS NULL OR stars BETWEEN 1 AND 5),
  ADD CHECK (review_sum >= 0),
  ADD CHECK (review_count >= 0),
  ADD CHECK (checkin_time > checkout_time);

ALTER TABLE room_type
  ADD UNIQUE (name, capacity),
  ADD CHECK (capacity > 0);

ALTER TABLE hotel_room_type
  ADD CHECK (price_per_night > 0),
  ADD CHECK (total_rooms >= 0);

ALTER TABLE room
  ADD UNIQUE(hotel_id, room_number);

ALTER TABLE guest
  ADD UNIQUE(email),
  ADD UNIQUE(phone),
  ADD CHECK (date_of_birth < CURRENT_DATE),
  ADD CHECK (email IS NOT NULL OR phone IS NOT NULL);

ALTER TABLE booking
  ADD CHECK (total_price > 0),
  ADD CHECK (review_rating IS NULL OR review_rating BETWEEN 1 AND 5);

ALTER TABLE booking_room
  ADD CHECK (checkout_date > checkin_date);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE booking_room
  ADD CONSTRAINT no_room_overlap
  EXCLUDE USING gist (
       room_id                                  WITH =,
       daterange(checkin_date, checkout_date)   WITH &&
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
CREATE INDEX idx_hotel_room_type   ON hotel_room_type (type_id);
CREATE INDEX idx_booking_guest    ON booking         (guest_id);
CREATE INDEX idx_booking_room_room ON booking_room    (room_id);

CREATE INDEX idx_room_status_hotel_type ON room (status, hotel_id, type_id);

CREATE INDEX idx_booking_room_dates ON booking_room (checkin_date, checkout_date);

CREATE INDEX idx_room_log_room_ts ON room_log (room_id, created_at DESC);
CREATE INDEX idx_booking_log_booking_ts ON booking_log (booking_id, created_at DESC);
```

<div style="page-break-after: always;"></div>

## 3. Triggery

Do obsługi takich rzeczy jak:
* Tworzenie logów (wraz z tworzeniem JSON do `booking_log`),
* Zmiany statusów po przyjeździe gościa
* Obliczania wartości ocen hotelu
* Ilości pokojów danego typu w hotelu
Wykorzystano triggery.

```sql
CREATE OR REPLACE FUNCTION trg_room_status_log()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  INSERT INTO room_log(room_id, created_at, status)
  VALUES (NEW.room_id, now(), NEW.status);
  RETURN NEW;
END $$;

CREATE TRIGGER room_aiu_log
AFTER INSERT OR UPDATE ON room
FOR EACH ROW EXECUTE FUNCTION trg_room_status_log();

CREATE OR REPLACE FUNCTION f_insert_booking_log()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  b          booking;
  rooms_json jsonb;
BEGIN
  SELECT * INTO b FROM booking WHERE booking_id = NEW.booking_id;

  SELECT jsonb_agg(jsonb_build_object(
           'room_id',       room_id,
           'checkin_date',  checkin_date,
           'checkout_date', checkout_date,
           'breakfast',     breakfast,
           'late_checkout', late_checkout))
    INTO rooms_json
  FROM booking_room
  WHERE booking_id = NEW.booking_id;

  INSERT INTO booking_log(booking_id, created_at, status, booking_rooms)
  VALUES (b.booking_id, now(), b.status, COALESCE(rooms_json, '[]'::jsonb));
  RETURN NEW;
END $$;

CREATE TRIGGER booking_aiu_log
AFTER INSERT OR UPDATE ON booking
FOR EACH ROW EXECUTE FUNCTION f_insert_booking_log();

CREATE TRIGGER booking_room_aiud_log
AFTER INSERT OR UPDATE OR DELETE ON booking_room
FOR EACH ROW EXECUTE FUNCTION f_insert_booking_log();

CREATE OR REPLACE FUNCTION trg_booking_status_propagate()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF NEW.status IS DISTINCT FROM OLD.status THEN
    CASE NEW.status
      WHEN 'CHECKED_IN' THEN
        UPDATE room SET status = 'OCCUPIED'
        WHERE room_id IN (
          SELECT room_id FROM booking_room WHERE booking_id = NEW.booking_id
        );
      WHEN 'CHECKED_OUT' THEN
        UPDATE room SET status = 'DIRTY'
        WHERE room_id IN (
          SELECT room_id FROM booking_room WHERE booking_id = NEW.booking_id
        );
    END CASE;
  END IF;
  RETURN NEW;
END $$;

CREATE TRIGGER booking_status_aiu_rooms
AFTER UPDATE OF status ON booking
FOR EACH ROW EXECUTE FUNCTION trg_booking_status_propagate();

CREATE OR REPLACE FUNCTION trg_update_hotel_review_stats()
RETURNS trigger
LANGUAGE plpgsql AS
$$
DECLARE
    v_hotel_id INT;
    v_diff     INT;
BEGIN
    SELECT r.hotel_id
      INTO v_hotel_id
      FROM booking_room br
      JOIN room r ON r.room_id = br.room_id
      WHERE br.booking_id = NEW.booking_id
      LIMIT 1;

    IF v_hotel_id IS NULL THEN
        RETURN NEW;
    END IF;

    IF TG_OP = 'INSERT' AND NEW.review_rating IS NOT NULL THEN
        UPDATE hotel
           SET review_sum   = review_sum   + NEW.review_rating,
               review_count = review_count + 1
         WHERE hotel_id = v_hotel_id;

    ELSIF TG_OP = 'UPDATE' AND NEW.review_rating IS DISTINCT FROM OLD.review_rating THEN

        IF OLD.review_rating IS NULL THEN
            UPDATE hotel
               SET review_sum   = review_sum   + NEW.review_rating,
                   review_count = review_count + 1
             WHERE hotel_id = v_hotel_id;

        ELSIF NEW.review_rating IS NULL THEN
            UPDATE hotel
               SET review_sum   = review_sum   - OLD.review_rating,
                   review_count = review_count - 1
             WHERE hotel_id = v_hotel_id;

        ELSE
            v_diff := NEW.review_rating - OLD.review_rating;
            UPDATE hotel
               SET review_sum = review_sum + v_diff
             WHERE hotel_id = v_hotel_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;


CREATE TRIGGER booking_review_stats_trg
AFTER INSERT OR UPDATE OF review_rating
ON booking
FOR EACH ROW
EXECUTE FUNCTION trg_update_hotel_review_stats();

CREATE OR REPLACE FUNCTION trg_update_total_rooms()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
     UPDATE hotel_room_type
        SET total_rooms = total_rooms + 1
      WHERE hotel_id = NEW.hotel_id
        AND type_id  = NEW.type_id;
  ELSIF TG_OP = 'DELETE' THEN
     UPDATE hotel_room_type
        SET total_rooms = total_rooms - 1
      WHERE hotel_id = OLD.hotel_id
        AND type_id  = OLD.type_id;
  END IF;
  RETURN NULL;
END;
$$;

CREATE TRIGGER room_ai_del
AFTER INSERT OR DELETE ON room
FOR EACH ROW EXECUTE FUNCTION trg_update_total_rooms();
```

---


## 6. Możliwości technologii wykorzystanych w projekcie

- Zapisywanie plików JSON w tabeli SQL
- Użycie indeksu GiST do warunku integralnościowego nakładających się rezerwacji na ten sam pokój