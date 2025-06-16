--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Debian 17.5-1.pgdg120+1)
-- Dumped by pg_dump version 17.5 (Debian 17.5-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: btree_gist; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS btree_gist WITH SCHEMA public;


--
-- Name: EXTENSION btree_gist; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION btree_gist IS 'support for indexing common datatypes in GiST';


--
-- Name: check_booking_rooms_same_hotel(); Type: FUNCTION; Schema: public; Owner: booking_user
--

CREATE FUNCTION public.check_booking_rooms_same_hotel() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.check_booking_rooms_same_hotel() OWNER TO booking_user;

--
-- Name: f_insert_booking_log(); Type: FUNCTION; Schema: public; Owner: booking_user
--

CREATE FUNCTION public.f_insert_booking_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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


ALTER FUNCTION public.f_insert_booking_log() OWNER TO booking_user;

--
-- Name: trg_room_status_log(); Type: FUNCTION; Schema: public; Owner: booking_user
--

CREATE FUNCTION public.trg_room_status_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  INSERT INTO room_log(room_id, created_at, price_per_night)
  VALUES (NEW.room_id, now(), NEW.price_per_night);
  RETURN NEW;
END $$;


ALTER FUNCTION public.trg_room_status_log() OWNER TO booking_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: booking; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.booking (
    booking_id integer NOT NULL,
    guest_id integer NOT NULL,
    hotel_id integer NOT NULL,
    total_price numeric(10,2) NOT NULL,
    status character varying(11) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT booking_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying, 'CHECKED_IN'::character varying, 'COMPLETED'::character varying])::text[]))),
    CONSTRAINT booking_total_price_check CHECK ((total_price > (0)::numeric))
);


ALTER TABLE public.booking OWNER TO booking_user;

--
-- Name: booking_booking_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.booking_booking_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.booking_booking_id_seq OWNER TO booking_user;

--
-- Name: booking_booking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.booking_booking_id_seq OWNED BY public.booking.booking_id;


--
-- Name: booking_log; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.booking_log (
    booking_log_id integer NOT NULL,
    booking_id integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    total_price numeric(10,2) NOT NULL,
    status character varying(11) NOT NULL,
    booking_rooms jsonb NOT NULL
);


ALTER TABLE public.booking_log OWNER TO booking_user;

--
-- Name: booking_log_booking_log_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.booking_log_booking_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.booking_log_booking_log_id_seq OWNER TO booking_user;

--
-- Name: booking_log_booking_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.booking_log_booking_log_id_seq OWNED BY public.booking_log.booking_log_id;


--
-- Name: booking_room; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.booking_room (
    booking_room_id integer NOT NULL,
    booking_id integer,
    room_id integer NOT NULL,
    checkin_date date NOT NULL,
    checkout_date date NOT NULL,
    CONSTRAINT booking_room_check CHECK ((checkout_date > checkin_date))
);


ALTER TABLE public.booking_room OWNER TO booking_user;

--
-- Name: booking_room_booking_room_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.booking_room_booking_room_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.booking_room_booking_room_id_seq OWNER TO booking_user;

--
-- Name: booking_room_booking_room_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.booking_room_booking_room_id_seq OWNED BY public.booking_room.booking_room_id;


--
-- Name: guest; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.guest (
    guest_id integer NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    date_of_birth date NOT NULL,
    country character varying(30) NOT NULL,
    city character varying(30) NOT NULL,
    address text NOT NULL,
    phone character varying(20),
    email character varying(255),
    CONSTRAINT guest_check CHECK (((email IS NOT NULL) OR (phone IS NOT NULL))),
    CONSTRAINT guest_date_of_birth_check CHECK ((date_of_birth < CURRENT_DATE))
);


ALTER TABLE public.guest OWNER TO booking_user;

--
-- Name: guest_guest_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.guest_guest_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.guest_guest_id_seq OWNER TO booking_user;

--
-- Name: guest_guest_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.guest_guest_id_seq OWNED BY public.guest.guest_id;


--
-- Name: hotel; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.hotel (
    hotel_id integer NOT NULL,
    name character varying(255) NOT NULL,
    country character varying(30) NOT NULL,
    city character varying(30) NOT NULL,
    stars smallint,
    address text NOT NULL,
    phone character varying(20) NOT NULL,
    checkin_time time without time zone NOT NULL,
    checkout_time time without time zone NOT NULL,
    CONSTRAINT hotel_check CHECK ((checkin_time > checkout_time)),
    CONSTRAINT hotel_stars_check CHECK (((stars IS NULL) OR ((stars >= 1) AND (stars <= 5))))
);


ALTER TABLE public.hotel OWNER TO booking_user;

--
-- Name: hotel_hotel_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.hotel_hotel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.hotel_hotel_id_seq OWNER TO booking_user;

--
-- Name: hotel_hotel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.hotel_hotel_id_seq OWNED BY public.hotel.hotel_id;


--
-- Name: room; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.room (
    room_id integer NOT NULL,
    hotel_id integer NOT NULL,
    room_type_id integer NOT NULL,
    room_number character varying(10) NOT NULL,
    capacity smallint NOT NULL,
    price_per_night numeric(10,2) NOT NULL,
    CONSTRAINT room_capacity_check CHECK ((capacity > 0)),
    CONSTRAINT room_price_per_night_check CHECK ((price_per_night > (0)::numeric))
);


ALTER TABLE public.room OWNER TO booking_user;

--
-- Name: room_log; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.room_log (
    room_log_id integer NOT NULL,
    room_id integer NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    price_per_night numeric(10,2) NOT NULL
);


ALTER TABLE public.room_log OWNER TO booking_user;

--
-- Name: room_log_room_log_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.room_log_room_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.room_log_room_log_id_seq OWNER TO booking_user;

--
-- Name: room_log_room_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.room_log_room_log_id_seq OWNED BY public.room_log.room_log_id;


--
-- Name: room_room_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.room_room_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.room_room_id_seq OWNER TO booking_user;

--
-- Name: room_room_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.room_room_id_seq OWNED BY public.room.room_id;


--
-- Name: room_type; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.room_type (
    room_type_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.room_type OWNER TO booking_user;

--
-- Name: room_type_room_type_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.room_type_room_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.room_type_room_type_id_seq OWNER TO booking_user;

--
-- Name: room_type_room_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.room_type_room_type_id_seq OWNED BY public.room_type.room_type_id;


--
-- Name: booking booking_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking ALTER COLUMN booking_id SET DEFAULT nextval('public.booking_booking_id_seq'::regclass);


--
-- Name: booking_log booking_log_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_log ALTER COLUMN booking_log_id SET DEFAULT nextval('public.booking_log_booking_log_id_seq'::regclass);


--
-- Name: booking_room booking_room_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room ALTER COLUMN booking_room_id SET DEFAULT nextval('public.booking_room_booking_room_id_seq'::regclass);


--
-- Name: guest guest_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.guest ALTER COLUMN guest_id SET DEFAULT nextval('public.guest_guest_id_seq'::regclass);


--
-- Name: hotel hotel_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel ALTER COLUMN hotel_id SET DEFAULT nextval('public.hotel_hotel_id_seq'::regclass);


--
-- Name: room room_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room ALTER COLUMN room_id SET DEFAULT nextval('public.room_room_id_seq'::regclass);


--
-- Name: room_log room_log_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_log ALTER COLUMN room_log_id SET DEFAULT nextval('public.room_log_room_log_id_seq'::regclass);


--
-- Name: room_type room_type_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_type ALTER COLUMN room_type_id SET DEFAULT nextval('public.room_type_room_type_id_seq'::regclass);


--
-- Data for Name: booking; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking (booking_id, guest_id, hotel_id, total_price, status, created_at) FROM stdin;
\.


--
-- Data for Name: booking_log; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking_log (booking_log_id, booking_id, created_at, total_price, status, booking_rooms) FROM stdin;
\.


--
-- Data for Name: booking_room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking_room (booking_room_id, booking_id, room_id, checkin_date, checkout_date) FROM stdin;
\.


--
-- Data for Name: guest; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.guest (guest_id, first_name, last_name, date_of_birth, country, city, address, phone, email) FROM stdin;
\.


--
-- Data for Name: hotel; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.hotel (hotel_id, name, country, city, stars, address, phone, checkin_time, checkout_time) FROM stdin;
\.


--
-- Data for Name: room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room (room_id, hotel_id, room_type_id, room_number, capacity, price_per_night) FROM stdin;
\.


--
-- Data for Name: room_log; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room_log (room_log_id, room_id, created_at, price_per_night) FROM stdin;
\.


--
-- Data for Name: room_type; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room_type (room_type_id, name) FROM stdin;
\.


--
-- Name: booking_booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_booking_id_seq', 1, false);


--
-- Name: booking_log_booking_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_log_booking_log_id_seq', 1, false);


--
-- Name: booking_room_booking_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_room_booking_room_id_seq', 1, false);


--
-- Name: guest_guest_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.guest_guest_id_seq', 1, false);


--
-- Name: hotel_hotel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.hotel_hotel_id_seq', 1, false);


--
-- Name: room_log_room_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_log_room_log_id_seq', 1, false);


--
-- Name: room_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_room_id_seq', 1, false);


--
-- Name: room_type_room_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_type_room_type_id_seq', 1, false);


--
-- Name: booking_log booking_log_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_log
    ADD CONSTRAINT booking_log_pkey PRIMARY KEY (booking_log_id);


--
-- Name: booking booking_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_pkey PRIMARY KEY (booking_id);


--
-- Name: booking_room booking_room_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room
    ADD CONSTRAINT booking_room_pkey PRIMARY KEY (booking_room_id);


--
-- Name: guest guest_email_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.guest
    ADD CONSTRAINT guest_email_key UNIQUE (email);


--
-- Name: guest guest_phone_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.guest
    ADD CONSTRAINT guest_phone_key UNIQUE (phone);


--
-- Name: guest guest_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.guest
    ADD CONSTRAINT guest_pkey PRIMARY KEY (guest_id);


--
-- Name: hotel hotel_name_country_city_address_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_name_country_city_address_key UNIQUE (name, country, city, address);


--
-- Name: hotel hotel_phone_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_phone_key UNIQUE (phone);


--
-- Name: hotel hotel_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_pkey PRIMARY KEY (hotel_id);


--
-- Name: booking_room no_room_overlap; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room
    ADD CONSTRAINT no_room_overlap EXCLUDE USING gist (room_id WITH =, daterange(checkin_date, checkout_date) WITH &&);


--
-- Name: room room_hotel_id_room_number_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_hotel_id_room_number_key UNIQUE (hotel_id, room_number);


--
-- Name: room_log room_log_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_log
    ADD CONSTRAINT room_log_pkey PRIMARY KEY (room_log_id);


--
-- Name: room room_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (room_id);


--
-- Name: room_type room_type_name_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_type
    ADD CONSTRAINT room_type_name_key UNIQUE (name);


--
-- Name: room_type room_type_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_type
    ADD CONSTRAINT room_type_pkey PRIMARY KEY (room_type_id);


--
-- Name: idx_booking_guest; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_booking_guest ON public.booking USING btree (guest_id);


--
-- Name: idx_booking_log_booking_ts; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_booking_log_booking_ts ON public.booking_log USING btree (booking_id, created_at DESC);


--
-- Name: idx_booking_room_dates; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_booking_room_dates ON public.booking_room USING btree (checkin_date, checkout_date);


--
-- Name: idx_booking_room_room; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_booking_room_room ON public.booking_room USING btree (room_id);


--
-- Name: idx_room_hotel; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_room_hotel ON public.room USING btree (hotel_id);


--
-- Name: idx_room_hotel_type; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_room_hotel_type ON public.room USING btree (hotel_id, room_type_id);


--
-- Name: idx_room_log_room_ts; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_room_log_room_ts ON public.room_log USING btree (room_id, created_at DESC);


--
-- Name: idx_room_type; Type: INDEX; Schema: public; Owner: booking_user
--

CREATE INDEX idx_room_type ON public.room USING btree (room_type_id);


--
-- Name: booking booking_aiu_log; Type: TRIGGER; Schema: public; Owner: booking_user
--

CREATE TRIGGER booking_aiu_log AFTER INSERT OR UPDATE ON public.booking FOR EACH ROW EXECUTE FUNCTION public.f_insert_booking_log();


--
-- Name: room room_aiu_log; Type: TRIGGER; Schema: public; Owner: booking_user
--

CREATE TRIGGER room_aiu_log AFTER INSERT OR UPDATE ON public.room FOR EACH ROW EXECUTE FUNCTION public.trg_room_status_log();


--
-- Name: booking_room trg_check_booking_room_hotel; Type: TRIGGER; Schema: public; Owner: booking_user
--

CREATE TRIGGER trg_check_booking_room_hotel BEFORE INSERT ON public.booking_room FOR EACH ROW EXECUTE FUNCTION public.check_booking_rooms_same_hotel();


--
-- Name: booking booking_guest_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_guest_id_fkey FOREIGN KEY (guest_id) REFERENCES public.guest(guest_id);


--
-- Name: booking booking_hotel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id);


--
-- Name: booking_log booking_log_booking_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_log
    ADD CONSTRAINT booking_log_booking_id_fkey FOREIGN KEY (booking_id) REFERENCES public.booking(booking_id);


--
-- Name: booking_room booking_room_room_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room
    ADD CONSTRAINT booking_room_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.room(room_id);


--
-- Name: booking_log fk_booking; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_log
    ADD CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES public.booking(booking_id) ON DELETE SET NULL;


--
-- Name: room room_hotel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id);


--
-- Name: room_log room_log_room_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_log
    ADD CONSTRAINT room_log_room_id_fkey FOREIGN KEY (room_id) REFERENCES public.room(room_id);


--
-- Name: room room_room_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_room_type_id_fkey FOREIGN KEY (room_type_id) REFERENCES public.room_type(room_type_id);


--
-- PostgreSQL database dump complete
--

