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

