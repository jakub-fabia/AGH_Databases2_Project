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
-- Name: booking_status; Type: TYPE; Schema: public; Owner: booking_user
--

CREATE TYPE public.booking_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'CHECKED_IN',
    'CHECKED_OUT',
    'CANCELLED',
    'NO_SHOW'
);


ALTER TYPE public.booking_status OWNER TO booking_user;

--
-- Name: payment_method; Type: TYPE; Schema: public; Owner: booking_user
--

CREATE TYPE public.payment_method AS ENUM (
    'CASH',
    'CARD',
    'ONLINE',
    'VOUCHER'
);


ALTER TYPE public.payment_method OWNER TO booking_user;

--
-- Name: payment_status; Type: TYPE; Schema: public; Owner: booking_user
--

CREATE TYPE public.payment_status AS ENUM (
    'PENDING',
    'CONFIRMED'
);


ALTER TYPE public.payment_status OWNER TO booking_user;

--
-- Name: room_status; Type: TYPE; Schema: public; Owner: booking_user
--

CREATE TYPE public.room_status AS ENUM (
    'AVAILABLE',
    'OCCUPIED',
    'DIRTY',
    'MAINTENANCE'
);


ALTER TYPE public.room_status OWNER TO booking_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: booking; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.booking (
    booking_id integer NOT NULL,
    guest_id integer NOT NULL,
    checkin_date date NOT NULL,
    checkout_date date NOT NULL,
    total_price numeric(10,2) NOT NULL,
    status public.booking_status DEFAULT 'PENDING'::public.booking_status NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    review text,
    review_rating smallint
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
-- Name: booking_room; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.booking_room (
    booking_id integer NOT NULL,
    room_id integer NOT NULL,
    breakfast boolean DEFAULT false NOT NULL,
    late_checkout boolean DEFAULT false NOT NULL
);


ALTER TABLE public.booking_room OWNER TO booking_user;

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
    email character varying(255)
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
    address text NOT NULL,
    phone character varying(20) NOT NULL,
    email character varying(255) NOT NULL,
    stars smallint NOT NULL,
    review_score numeric(3,2),
    checkin_time time without time zone NOT NULL,
    checkout_time time without time zone NOT NULL
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
-- Name: hotel_room_type; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.hotel_room_type (
    hotel_id integer NOT NULL,
    type_id integer NOT NULL,
    price_per_night numeric(10,2) NOT NULL,
    total_rooms smallint DEFAULT 0 NOT NULL
);


ALTER TABLE public.hotel_room_type OWNER TO booking_user;

--
-- Name: payment; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.payment (
    payment_id integer NOT NULL,
    booking_id integer NOT NULL,
    amount numeric(10,2) NOT NULL,
    payment_ts timestamp with time zone DEFAULT now() NOT NULL,
    method public.payment_method NOT NULL,
    status public.payment_status DEFAULT 'PENDING'::public.payment_status NOT NULL
);


ALTER TABLE public.payment OWNER TO booking_user;

--
-- Name: payment_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.payment_payment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payment_payment_id_seq OWNER TO booking_user;

--
-- Name: payment_payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.payment_payment_id_seq OWNED BY public.payment.payment_id;


--
-- Name: room; Type: TABLE; Schema: public; Owner: booking_user
--

CREATE TABLE public.room (
    room_id integer NOT NULL,
    hotel_id integer NOT NULL,
    type_id integer NOT NULL,
    room_number character varying(10) NOT NULL,
    status public.room_status DEFAULT 'AVAILABLE'::public.room_status NOT NULL
);


ALTER TABLE public.room OWNER TO booking_user;

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
    type_id integer NOT NULL,
    name character varying(50) NOT NULL,
    description text NOT NULL,
    capacity smallint NOT NULL
);


ALTER TABLE public.room_type OWNER TO booking_user;

--
-- Name: room_type_type_id_seq; Type: SEQUENCE; Schema: public; Owner: booking_user
--

CREATE SEQUENCE public.room_type_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.room_type_type_id_seq OWNER TO booking_user;

--
-- Name: room_type_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: booking_user
--

ALTER SEQUENCE public.room_type_type_id_seq OWNED BY public.room_type.type_id;


--
-- Name: booking booking_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking ALTER COLUMN booking_id SET DEFAULT nextval('public.booking_booking_id_seq'::regclass);


--
-- Name: guest guest_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.guest ALTER COLUMN guest_id SET DEFAULT nextval('public.guest_guest_id_seq'::regclass);


--
-- Name: hotel hotel_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel ALTER COLUMN hotel_id SET DEFAULT nextval('public.hotel_hotel_id_seq'::regclass);


--
-- Name: payment payment_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.payment ALTER COLUMN payment_id SET DEFAULT nextval('public.payment_payment_id_seq'::regclass);


--
-- Name: room room_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room ALTER COLUMN room_id SET DEFAULT nextval('public.room_room_id_seq'::regclass);


--
-- Name: room_type type_id; Type: DEFAULT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_type ALTER COLUMN type_id SET DEFAULT nextval('public.room_type_type_id_seq'::regclass);


--
-- Data for Name: booking; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking (booking_id, guest_id, checkin_date, checkout_date, total_price, status, created_at, review, review_rating) FROM stdin;
\.


--
-- Data for Name: booking_room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.booking_room (booking_id, room_id, breakfast, late_checkout) FROM stdin;
\.


--
-- Data for Name: guest; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.guest (guest_id, first_name, last_name, date_of_birth, country, city, address, phone, email) FROM stdin;
\.


--
-- Data for Name: hotel; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.hotel (hotel_id, name, country, city, address, phone, email, stars, review_score, checkin_time, checkout_time) FROM stdin;
\.


--
-- Data for Name: hotel_room_type; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.hotel_room_type (hotel_id, type_id, price_per_night, total_rooms) FROM stdin;
\.


--
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.payment (payment_id, booking_id, amount, payment_ts, method, status) FROM stdin;
\.


--
-- Data for Name: room; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room (room_id, hotel_id, type_id, room_number, status) FROM stdin;
\.


--
-- Data for Name: room_type; Type: TABLE DATA; Schema: public; Owner: booking_user
--

COPY public.room_type (type_id, name, description, capacity) FROM stdin;
\.


--
-- Name: booking_booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.booking_booking_id_seq', 1, false);


--
-- Name: guest_guest_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.guest_guest_id_seq', 1, false);


--
-- Name: hotel_hotel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.hotel_hotel_id_seq', 1, false);


--
-- Name: payment_payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.payment_payment_id_seq', 1, false);


--
-- Name: room_room_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_room_id_seq', 1, false);


--
-- Name: room_type_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: booking_user
--

SELECT pg_catalog.setval('public.room_type_type_id_seq', 1, false);


--
-- Name: booking booking_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_pkey PRIMARY KEY (booking_id);


--
-- Name: booking_room booking_room_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking_room
    ADD CONSTRAINT booking_room_pkey PRIMARY KEY (booking_id, room_id);


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
-- Name: hotel hotel_email_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel
    ADD CONSTRAINT hotel_email_key UNIQUE (email);


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
-- Name: hotel_room_type hotel_room_type_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel_room_type
    ADD CONSTRAINT hotel_room_type_pkey PRIMARY KEY (hotel_id, type_id);


--
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (payment_id);


--
-- Name: room room_hotel_id_room_number_key; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_hotel_id_room_number_key UNIQUE (hotel_id, room_number);


--
-- Name: room room_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (room_id);


--
-- Name: room_type room_type_pkey; Type: CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room_type
    ADD CONSTRAINT room_type_pkey PRIMARY KEY (type_id);


--
-- Name: booking booking_guest_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_guest_id_fkey FOREIGN KEY (guest_id) REFERENCES public.guest(guest_id);


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
-- Name: hotel_room_type hotel_room_type_hotel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel_room_type
    ADD CONSTRAINT hotel_room_type_hotel_id_fkey FOREIGN KEY (hotel_id) REFERENCES public.hotel(hotel_id);


--
-- Name: hotel_room_type hotel_room_type_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.hotel_room_type
    ADD CONSTRAINT hotel_room_type_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.room_type(type_id);


--
-- Name: payment payment_booking_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_booking_id_fkey FOREIGN KEY (booking_id) REFERENCES public.booking(booking_id);


--
-- Name: room room_hotel_id_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: booking_user
--

ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_hotel_id_type_id_fkey FOREIGN KEY (hotel_id, type_id) REFERENCES public.hotel_room_type(hotel_id, type_id);


--
-- PostgreSQL database dump complete
--

