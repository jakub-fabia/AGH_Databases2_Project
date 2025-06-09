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