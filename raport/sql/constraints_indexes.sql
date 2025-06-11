ALTER TABLE hotel
  ADD UNIQUE(name, country, city, address),
  ADD UNIQUE(phone),
  ADD CHECK (stars IS NULL OR stars BETWEEN 1 AND 5),
  ADD CHECK (checkin_time > checkout_time);

ALTER TABLE room_type
  ADD UNIQUE (name);

ALTER TABLE room
  ADD UNIQUE(hotel_id, room_number),
  ADD CHECK (price_per_night > 0),
  ADD CHECK (capacity > 0);

ALTER TABLE guest
  ADD UNIQUE(email),
  ADD UNIQUE(phone),
  ADD CHECK (date_of_birth < CURRENT_DATE),
  ADD CHECK (email IS NOT NULL OR phone IS NOT NULL);

ALTER TABLE booking
  ADD CHECK (total_price > 0),
  ADD CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED IN', 'COMPLETED'));

ALTER TABLE booking_room
  ADD CHECK (checkout_date > checkin_date);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE booking_room
  ADD CONSTRAINT no_room_overlap
  EXCLUDE USING gist (
       room_id                                  WITH =,
       daterange(checkin_date, checkout_date)   WITH &&
  );

CREATE INDEX idx_room_hotel       ON room            (hotel_id);
CREATE INDEX idx_room_type        ON room            (room_type_id);
CREATE INDEX idx_booking_guest    ON booking         (guest_id);
CREATE INDEX idx_booking_room_room ON booking_room    (room_id);

CREATE INDEX idx_room_hotel_type ON room (hotel_id, room_type_id);

CREATE INDEX idx_booking_room_dates ON booking_room (checkin_date, checkout_date);

CREATE INDEX idx_room_log_room_ts ON room_log (room_id, created_at DESC);
CREATE INDEX idx_booking_log_booking_ts ON booking_log (booking_id, created_at DESC);