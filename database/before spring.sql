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
    booking_id integer NOT NULL,
    room_id integer NOT NULL,
    checkin_date date NOT NULL,
    checkout_date date NOT NULL
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
1	3	3	1950.00	COMPLETED	2025-04-27 00:00:00+00
2	7	4	670.00	COMPLETED	2025-03-14 00:00:00+00
3	5	5	1012.00	COMPLETED	2025-05-21 00:00:00+00
4	4	5	848.00	COMPLETED	2025-04-29 00:00:00+00
5	5	5	2135.00	COMPLETED	2025-04-17 00:00:00+00
6	11	6	1866.00	COMPLETED	2025-04-17 00:00:00+00
7	1	6	1480.00	COMPLETED	2025-03-14 00:00:00+00
8	12	3	1848.00	COMPLETED	2025-04-08 00:00:00+00
9	4	6	246.00	COMPLETED	2025-03-28 00:00:00+00
10	11	1	2155.00	COMPLETED	2025-05-08 00:00:00+00
11	3	5	496.00	CANCELLED	2025-04-16 00:00:00+00
12	2	2	858.00	COMPLETED	2025-04-25 00:00:00+00
13	9	4	844.00	COMPLETED	2025-04-22 00:00:00+00
14	12	1	220.00	COMPLETED	2025-03-20 00:00:00+00
15	3	3	518.00	COMPLETED	2025-04-11 00:00:00+00
16	1	2	61.00	COMPLETED	2025-05-03 00:00:00+00
17	9	3	928.00	COMPLETED	2025-04-15 00:00:00+00
18	11	5	257.00	CANCELLED	2025-04-01 00:00:00+00
19	13	3	1480.00	COMPLETED	2025-05-11 00:00:00+00
20	13	4	565.00	COMPLETED	2025-05-01 00:00:00+00
21	13	1	237.00	CANCELLED	2025-06-22 00:00:00+00
22	10	3	2868.00	CONFIRMED	2025-06-18 00:00:00+00
23	15	1	1240.00	PENDING	2025-06-19 00:00:00+00
24	4	3	650.00	CHECKED_IN	2025-06-17 00:00:00+00
25	8	2	1016.00	PENDING	2025-06-22 00:00:00+00
26	7	2	1552.00	CONFIRMED	2025-06-19 00:00:00+00
27	4	5	411.00	CHECKED_IN	2025-06-17 00:00:00+00
28	2	6	495.00	CONFIRMED	2025-06-19 00:00:00+00
29	2	3	2868.00	CANCELLED	2025-06-21 00:00:00+00
30	1	4	564.00	CHECKED_IN	2025-06-17 00:00:00+00
\.


--
-- Data for Name: booking_log; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking_log (booking_log_id, booking_id, created_at, total_price, status, booking_rooms) FROM stdin;
1	1	2025-06-17 17:21:16.592217+00	1950.00	COMPLETED	[]
2	2	2025-06-17 17:21:16.592217+00	670.00	COMPLETED	[]
3	3	2025-06-17 17:21:16.592217+00	1012.00	COMPLETED	[]
4	4	2025-06-17 17:21:16.592217+00	848.00	COMPLETED	[]
5	5	2025-06-17 17:21:16.592217+00	2135.00	COMPLETED	[]
6	6	2025-06-17 17:21:16.592217+00	1866.00	COMPLETED	[]
7	7	2025-06-17 17:21:16.592217+00	1480.00	COMPLETED	[]
8	8	2025-06-17 17:21:16.592217+00	1848.00	COMPLETED	[]
9	9	2025-06-17 17:21:16.592217+00	246.00	COMPLETED	[]
10	10	2025-06-17 17:21:16.592217+00	2155.00	COMPLETED	[]
11	11	2025-06-17 17:21:16.592217+00	496.00	CANCELLED	[]
12	12	2025-06-17 17:21:16.592217+00	858.00	COMPLETED	[]
13	13	2025-06-17 17:21:16.592217+00	844.00	COMPLETED	[]
14	14	2025-06-17 17:21:16.592217+00	220.00	COMPLETED	[]
15	15	2025-06-17 17:21:16.592217+00	518.00	COMPLETED	[]
16	16	2025-06-17 17:21:16.592217+00	61.00	COMPLETED	[]
17	17	2025-06-17 17:21:16.592217+00	928.00	COMPLETED	[]
18	18	2025-06-17 17:21:16.592217+00	257.00	CANCELLED	[]
19	19	2025-06-17 17:21:16.592217+00	1480.00	COMPLETED	[]
20	20	2025-06-17 17:21:16.592217+00	565.00	COMPLETED	[]
21	21	2025-06-17 17:35:12.154005+00	237.00	CANCELLED	[]
22	22	2025-06-17 17:35:12.154005+00	2868.00	CONFIRMED	[]
23	23	2025-06-17 17:35:12.154005+00	1240.00	PENDING	[]
24	24	2025-06-17 17:35:12.154005+00	650.00	CHECKED_IN	[]
25	25	2025-06-17 17:35:12.154005+00	1016.00	PENDING	[]
26	26	2025-06-17 17:35:12.154005+00	1552.00	CONFIRMED	[]
27	27	2025-06-17 17:35:12.154005+00	411.00	CHECKED_IN	[]
28	28	2025-06-17 17:35:12.154005+00	495.00	CONFIRMED	[]
29	29	2025-06-17 17:35:12.154005+00	2868.00	CANCELLED	[]
30	30	2025-06-17 17:35:12.154005+00	564.00	CHECKED_IN	[]
\.


--
-- Data for Name: booking_room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking_room (booking_room_id, booking_id, room_id, checkin_date, checkout_date) FROM stdin;
1	1	16	2025-04-27	2025-05-02
2	1	13	2025-04-27	2025-05-02
3	1	15	2025-04-27	2025-05-02
4	2	20	2025-03-14	2025-03-16
5	3	24	2025-05-21	2025-05-25
6	4	26	2025-04-29	2025-05-03
7	5	23	2025-04-17	2025-04-22
8	5	24	2025-04-17	2025-04-22
9	6	27	2025-04-17	2025-04-20
10	6	30	2025-04-17	2025-04-20
11	7	29	2025-03-14	2025-03-18
12	7	27	2025-03-14	2025-03-18
13	7	28	2025-03-14	2025-03-18
14	8	15	2025-04-08	2025-04-11
15	8	13	2025-04-08	2025-04-11
16	8	12	2025-04-08	2025-04-11
17	9	28	2025-03-28	2025-03-29
18	10	5	2025-05-08	2025-05-13
19	10	1	2025-05-08	2025-05-13
20	11	26	2025-04-16	2025-04-17
21	11	22	2025-04-16	2025-04-17
22	12	11	2025-04-25	2025-04-28
23	13	18	2025-04-22	2025-04-26
24	14	2	2025-03-20	2025-03-21
25	15	16	2025-04-11	2025-04-12
26	15	15	2025-04-11	2025-04-12
27	15	14	2025-04-11	2025-04-12
28	16	8	2025-05-03	2025-05-04
29	17	13	2025-04-15	2025-04-19
30	17	15	2025-04-15	2025-04-19
31	18	25	2025-04-01	2025-04-02
32	19	16	2025-05-11	2025-05-16
33	20	20	2025-05-01	2025-05-02
34	20	19	2025-05-01	2025-05-02
35	21	2	2025-06-22	2025-06-23
36	22	16	2025-06-18	2025-06-22
37	22	14	2025-06-18	2025-06-22
38	22	12	2025-06-18	2025-06-22
39	23	1	2025-06-19	2025-06-21
40	23	3	2025-06-19	2025-06-21
41	23	4	2025-06-19	2025-06-21
42	24	12	2025-06-17	2025-06-19
43	25	9	2025-06-22	2025-06-26
44	25	10	2025-06-22	2025-06-26
45	26	8	2025-06-19	2025-06-23
46	26	6	2025-06-19	2025-06-23
47	26	10	2025-06-19	2025-06-23
48	27	26	2025-06-17	2025-06-20
49	28	27	2025-06-19	2025-06-22
50	29	16	2025-06-21	2025-06-25
51	29	15	2025-06-21	2025-06-25
52	29	12	2025-06-21	2025-06-25
53	30	19	2025-06-17	2025-06-18
54	30	17	2025-06-17	2025-06-18
\.


--
-- Data for Name: guest; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.guest (guest_id, first_name, last_name, date_of_birth, country, city, address, phone, email) FROM stdin;
1	Jan	Kowalski	1990-03-15	Polska	Warszawa	ul. Marszałkowska 10/5	+48 501 234 567	\N
2	Anna	Nowak	1985-07-22	Polska	Kraków	ul. Karmelicka 12/3	\N	anna.nowak@example.com
3	Marek	Wiśniewski	1978-11-05	Polska	Gdańsk	ul. Długa 45	+48 502 789 321	\N
4	Ewa	Zielińska	1995-01-30	Polska	Poznań	ul. Święty Marcin 20	\N	ewa.zielinska@example.com
5	Tomasz	Lewandowski	1988-06-17	Polska	Wrocław	ul. Piłsudskiego 8	+48 503 456 789	t.lewandowski@example.com
6	Julia	Kaczmarek	1993-04-12	Polska	Katowice	ul. Słowackiego 7	\N	julia.k@example.com
7	Kamil	Dąbrowski	1982-09-09	Polska	Łódź	ul. Piotrkowska 99	+48 504 678 901	\N
8	Magdalena	Sikora	1997-12-25	Polska	Lublin	ul. Narutowicza 13	\N	magda.s@example.com
9	Jakub	Wójcik	1991-08-04	Polska	Rzeszów	ul. 3 Maja 15	+48 505 789 123	\N
10	Zofia	Michalska	1989-02-14	Polska	Szczecin	ul. Wojska Polskiego 88	\N	zofia.michalska@example.com
11	Lukas	Müller	1984-10-10	Niemcy	Berlin	Alexanderplatz 1	+49 170 1234567	\N
12	Emma	Schneider	1990-05-20	Niemcy	Hamburg	Reeperbahn 12	\N	emma.schneider@beispiel.de
13	Claire	Dubois	1986-03-03	Francja	Lyon	Rue de la République 5	\N	cdubois@example.fr
14	Liam	OConnor	1992-07-01	Irlandia	Dublin	O’Connell Street 18	+353 86 123 4567	\N
15	Sofia	Martínez	1994-11-11	Hiszpania	Barcelona	Carrer de Balmes 101	\N	sofia.martinez@example.es
\.


--
-- Data for Name: hotel; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.hotel (hotel_id, name, country, city, stars, address, phone, checkin_time, checkout_time) FROM stdin;
2	Hotel Kaiserhof	Niemcy	Berlin	5	Friedrichstraße 100, 10117 Berlin	+49 30 11223344	14:00:00	11:00:00
3	Dragonfly Hostel	Polska	Kraków	2	ul. Szewska 10, 31-009 Kraków	+48 12 345 6789	14:00:00	11:00:00
5	Tatra View Hotel	Polska	Zakopane	4	ul. Krupówki 20, 34-500 Zakopane	+48 18 123 4567	15:00:00	12:00:00
1	Berlin Backpackers	Niemcy	Berlin	\N	Leipziger Str. 45, 10117 Berlin	+49 30 987654321	15:00:00	10:00:00
4	Woodland Cabin Kraków	Polska	Kraków	\N	ul. Wolska 3, 30-307 Kraków	+48 12 987 6543	13:00:00	10:00:00
6	Tatra Mountain Hut	Polska	Zakopane	\N	Droga do Daniela 5, 34-500 Zakopane	+48 18 987 6543	13:00:00	10:00:00
\.


--
-- Data for Name: room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room (room_id, hotel_id, room_type_id, room_number, capacity, price_per_night) FROM stdin;
1	1	3	H1-B1	1	45.00
2	1	3	H1-B2	1	45.00
3	1	3	H1-B3	1	45.00
4	1	3	H1-B4	1	45.00
5	1	3	H1-B5	1	45.00
6	2	2	HK101	2	180.00
7	2	2	HK102	2	180.00
8	2	2	HK103	3	200.00
9	2	2	HK104	2	170.00
10	2	1	HK201	4	350.00
11	2	1	HK202	3	320.00
12	3	3	H3-B1	1	40.00
13	3	3	H3-B2	1	40.00
14	3	3	H3-B3	1	40.00
15	3	3	H3-B4	1	40.00
16	3	3	H3-B5	1	40.00
17	4	4	D4-01	4	250.00
18	4	4	D4-02	5	270.00
19	4	4	D4-03	4	250.00
20	4	4	D4-04	6	300.00
21	5	2	TV101	2	190.00
22	5	2	TV102	2	190.00
23	5	2	TV103	3	210.00
24	5	2	TV104	2	180.00
25	5	1	TV201	4	360.00
26	5	1	TV202	3	340.00
27	6	4	D6-01	5	260.00
28	6	4	D6-02	4	250.00
29	6	4	D6-03	6	280.00
30	6	4	D6-04	4	250.00
\.


--
-- Data for Name: room_log; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room_log (room_log_id, room_id, created_at, price_per_night) FROM stdin;
89	1	2025-06-17 17:08:23.26649+00	45.00
90	2	2025-06-17 17:08:23.26649+00	45.00
91	3	2025-06-17 17:08:23.26649+00	45.00
92	4	2025-06-17 17:08:23.26649+00	45.00
93	5	2025-06-17 17:08:23.26649+00	45.00
94	6	2025-06-17 17:08:23.316589+00	180.00
95	7	2025-06-17 17:08:23.316589+00	180.00
96	8	2025-06-17 17:08:23.316589+00	200.00
97	9	2025-06-17 17:08:23.316589+00	170.00
98	10	2025-06-17 17:08:23.316589+00	350.00
99	11	2025-06-17 17:08:23.316589+00	320.00
100	12	2025-06-17 17:08:23.339237+00	40.00
101	13	2025-06-17 17:08:23.339237+00	40.00
102	14	2025-06-17 17:08:23.339237+00	40.00
103	15	2025-06-17 17:08:23.339237+00	40.00
104	16	2025-06-17 17:08:23.339237+00	40.00
105	17	2025-06-17 17:08:23.361455+00	250.00
106	18	2025-06-17 17:08:23.361455+00	270.00
107	19	2025-06-17 17:08:23.361455+00	250.00
108	20	2025-06-17 17:08:23.361455+00	300.00
109	21	2025-06-17 17:08:23.380199+00	190.00
110	22	2025-06-17 17:08:23.380199+00	190.00
111	23	2025-06-17 17:08:23.380199+00	210.00
112	24	2025-06-17 17:08:23.380199+00	180.00
113	25	2025-06-17 17:08:23.380199+00	360.00
114	26	2025-06-17 17:08:23.380199+00	340.00
115	27	2025-06-17 17:08:23.407455+00	260.00
116	28	2025-06-17 17:08:23.407455+00	250.00
117	29	2025-06-17 17:08:23.407455+00	280.00
118	30	2025-06-17 17:08:23.407455+00	250.00
\.


--
-- Data for Name: room_type; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room_type (room_type_id, name) FROM stdin;
1	Apartament
2	Pokój hotelowy
3	Łóżko w pokoju współdzielonym
4	Domek
\.


--
-- Name: booking_booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_booking_id_seq', 1, false);


--
-- Name: booking_log_booking_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_log_booking_log_id_seq', 30, true);


--
-- Name: booking_room_booking_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_room_booking_room_id_seq', 54, true);


--
-- Name: guest_guest_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.guest_guest_id_seq', 15, true);


--
-- Name: hotel_hotel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.hotel_hotel_id_seq', 6, true);


--
-- Name: room_log_room_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_log_room_log_id_seq', 118, true);


--
-- Name: room_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_room_id_seq', 30, true);


--
-- Name: room_type_room_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_type_room_type_id_seq', 4, true);


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
-- Name: booking_room booking_room_booking_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room
    ADD CONSTRAINT booking_room_booking_id_fkey FOREIGN KEY (booking_id) REFERENCES public.booking(booking_id);


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

