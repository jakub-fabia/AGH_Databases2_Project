CREATE OR REPLACE FUNCTION trg_room_status_log()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  INSERT INTO room_log(room_id, created_at, price_per_night)
  VALUES (NEW.room_id, now(), NEW.price_per_night);
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
           'checkout_date', checkout_date))
    INTO rooms_json
  FROM booking_room
  WHERE booking_id = NEW.booking_id;

  INSERT INTO booking_log(booking_id, created_at, total_price, status, booking_rooms)
  VALUES (b.booking_id, now(), b.total_price, b.status, COALESCE(rooms_json, '[]'::jsonb));
  RETURN NEW;
END $$;

CREATE TRIGGER booking_aiu_log
AFTER INSERT OR UPDATE ON booking
FOR EACH ROW EXECUTE FUNCTION f_insert_booking_log();

CREATE OR REPLACE FUNCTION check_booking_rooms_same_hotel()
RETURNS TRIGGER AS $$
DECLARE
    existing_hotel_id INT;
    new_room_hotel_id INT;
BEGIN
    SELECT r.hotel_id INTO new_room_hotel_id
    FROM room r
    WHERE r.room_id = NEW.room_id;

    SELECT r.hotel_id INTO existing_hotel_id
    FROM booking_room br
    JOIN room r ON r.room_id = br.room_id
    WHERE br.booking_id = NEW.booking_id
    LIMIT 1;

    IF existing_hotel_id IS NOT NULL AND existing_hotel_id <> new_room_hotel_id THEN
        RAISE EXCEPTION 'All rooms in a single booking must belong to the same hotel.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_booking_room_hotel
BEFORE INSERT ON booking_room
FOR EACH ROW
EXECUTE FUNCTION check_booking_rooms_same_hotel();
