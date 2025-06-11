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

CREATE TABLE room (
  room_id      SERIAL      PRIMARY KEY,
  hotel_id     INT         NOT NULL REFERENCES hotel,
  type_id      INT         NOT NULL REFERENCES room_type,
  room_number  VARCHAR(10) NOT NULL,
  price_per_night NUMERIC(10,2) NOT NULL,
  status       VARCHAR(11) NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE room_log (
  room_log_id     SERIAL         PRIMARY KEY,
  room_id         INT            NOT NULL REFERENCES room,
  created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
  price_per_night NUMERIC(10,2)  NOT NULL,
  status          VARCHAR(11)    NOT NULL
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
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
  review         TEXT,
  review_rating  SMALLINT
);

CREATE TABLE booking_room (
  booking_id    INT            NOT NULL REFERENCES booking,
  room_id       INT            NOT NULL REFERENCES room,
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