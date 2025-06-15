CREATE TABLE hotel (
  hotel_id      SERIAL       PRIMARY KEY,
  name          VARCHAR(255) NOT NULL,
  country       VARCHAR(30)  NOT NULL,
  city          VARCHAR(30)  NOT NULL,
  stars         SMALLINT,
  address       TEXT         NOT NULL,
  phone         VARCHAR(20)  NOT NULL,
  checkin_time  TIME         NOT NULL,
  checkout_time TIME         NOT NULL
);

CREATE TABLE room_type (
  room_type_id     SERIAL      PRIMARY KEY,
  name             VARCHAR(50) NOT NULL
);

CREATE TABLE room (
  room_id         SERIAL        PRIMARY KEY,
  hotel_id        INT           NOT NULL REFERENCES hotel,
  room_type_id    INT           NOT NULL REFERENCES room_type,
  room_number     VARCHAR(10)   NOT NULL,
  capacity        SMALLINT      NOT NULL,
  price_per_night NUMERIC(10,2) NOT NULL
);

CREATE TABLE room_log (
  room_log_id     SERIAL         PRIMARY KEY,
  room_id         INT            NOT NULL REFERENCES room,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  price_per_night NUMERIC(10,2)  NOT NULL
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
  hotel_id       INT            NOT NULL REFERENCES hotel,
  total_price    NUMERIC(10,2)  NOT NULL,
  status         VARCHAR(11)    NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE TABLE booking_room (
  booking_room_id SERIAL        PRIMARY KEY,
  booking_id      INT,
  room_id         INT           NOT NULL REFERENCES room,
  checkin_date    DATE          NOT NULL,
  checkout_date   DATE          NOT NULL
);

CREATE TABLE booking_log (
  booking_log_id SERIAL         PRIMARY KEY,
  booking_id     INT            NOT NULL REFERENCES booking,
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  total_price    NUMERIC(10,2)  NOT NULL,
  status         VARCHAR(11)    NOT NULL,
  booking_rooms  JSONB          NOT NULL
);