--
-- PostgreSQL database dump
--

-- Dumped from database version 14.8
-- Dumped by pg_dump version 14.8

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: dd_sesion; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.dd_sesion (
    id integer NOT NULL,
    sesion_id character varying(64) NOT NULL,
    fecha_inicio timestamp without time zone DEFAULT now() NOT NULL,
    fecha_fin timestamp without time zone,
    punto_acceso character varying(64) NOT NULL,
    datos_cliente text NOT NULL,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.dd_sesion OWNER TO sia;

--
-- Name: dd_sesion_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.dd_sesion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dd_sesion_id_seq OWNER TO sia;

--
-- Name: dd_sesion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.dd_sesion_id_seq OWNED BY public.dd_sesion.id;


--
-- Name: si_adjunto; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_adjunto (
    id integer NOT NULL,
    uuid character varying(64),
    nombre character varying(1024),
    descripcion character varying(1024),
    tipo_archivo character varying(75),
    peso character varying(10),
    url text,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_adjunto OWNER TO sia;

--
-- Name: si_adjunto_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_adjunto_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_adjunto_id_seq OWNER TO sia;

--
-- Name: si_adjunto_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_adjunto_id_seq OWNED BY public.si_adjunto.id;


--
-- Name: si_modulo; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_modulo (
    id integer NOT NULL,
    nombre character varying(32),
    ruta character varying(64),
    icono character varying(128),
    rutaservlet character varying(128),
    tooltip character varying(128),
    extralinkrender text,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_modulo OWNER TO sia;

--
-- Name: si_modulo_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_modulo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_modulo_id_seq OWNER TO sia;

--
-- Name: si_modulo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_modulo_id_seq OWNED BY public.si_modulo.id;


--
-- Name: si_opcion; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_opcion (
    id integer NOT NULL,
    si_modulo integer,
    nombre character varying(64),
    pagina character varying(256),
    posicion integer,
    si_opcion integer,
    paginalistener character varying(256),
    icono character varying(64),
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_opcion OWNER TO sia;

--
-- Name: si_opcion_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_opcion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_opcion_id_seq OWNER TO sia;

--
-- Name: si_opcion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_opcion_id_seq OWNED BY public.si_opcion.id;


--
-- Name: si_parametro; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_parametro (
    id integer NOT NULL,
    upload_directory character varying(100),
    logo bytea,
    tipo_almacen_adjuntos character varying(3) DEFAULT 'LFS'::bpchar NOT NULL,
    gest_doc_url_base character varying(400),
    gest_doc_prop_adic character varying(512),
    gest_doc_usuario character varying(20),
    gest_doc_clave character varying(50),
    directorio_usuarios character varying(512),
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false,
    api_whatsapp character varying(1024)
);


ALTER TABLE public.si_parametro OWNER TO sia;

--
-- Name: si_parametro_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_parametro_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_parametro_id_seq OWNER TO sia;

--
-- Name: si_parametro_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_parametro_id_seq OWNED BY public.si_parametro.id;


--
-- Name: si_plantilla_html; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_plantilla_html (
    id integer NOT NULL,
    nombre character varying(20),
    descripcion character varying(150),
    inicio text,
    fin text,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_plantilla_html OWNER TO sia;

--
-- Name: si_plantilla_html_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_plantilla_html_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_plantilla_html_id_seq OWNER TO sia;

--
-- Name: si_plantilla_html_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_plantilla_html_id_seq OWNED BY public.si_plantilla_html.id;


--
-- Name: si_rel_rol_opcion; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_rel_rol_opcion (
    id integer NOT NULL,
    si_rol integer NOT NULL,
    si_opcion integer NOT NULL,
    acceso_rapido boolean DEFAULT false,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_rel_rol_opcion OWNER TO sia;

--
-- Name: si_rol; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_rol (
    id integer NOT NULL,
    nombre character varying(25),
    si_modulo integer NOT NULL,
    codigo character varying(8) DEFAULT ''::character varying NOT NULL,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_rol OWNER TO sia;

--
-- Name: si_rol_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_rol_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_rol_id_seq OWNER TO sia;

--
-- Name: si_rol_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_rol_id_seq OWNED BY public.si_rol.id;


--
-- Name: si_usuario_rol; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.si_usuario_rol (
    id integer NOT NULL,
    usuario integer NOT NULL,
    si_rol integer NOT NULL,
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false
);


ALTER TABLE public.si_usuario_rol OWNER TO sia;

--
-- Name: si_usuario_rol_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.si_usuario_rol_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.si_usuario_rol_id_seq OWNER TO sia;

--
-- Name: si_usuario_rol_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.si_usuario_rol_id_seq OWNED BY public.si_usuario_rol.id;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: sia
--

CREATE TABLE public.usuario (
    id integer NOT NULL,
    nombre character varying(128) NOT NULL,
    email character varying(120) NOT NULL,
    clave character varying(80) NOT NULL,
    telefono character varying(25) NOT NULL,
    fecha_nacimiento date NOT NULL,
    domicilio text,
    curp character varying(20),
    foto text,
    seccion character varying(10),
    genero integer NOT NULL,
    fecha_genero timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifico integer,
    fecha_modifico timestamp without time zone,
    eliminado boolean DEFAULT false NOT NULL,
    si_adjunto integer
);


ALTER TABLE public.usuario OWNER TO sia;

--
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: sia
--

CREATE SEQUENCE public.usuario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usuario_id_seq OWNER TO sia;

--
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sia
--

ALTER SEQUENCE public.usuario_id_seq OWNED BY public.usuario.id;


--
-- Name: dd_sesion id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.dd_sesion ALTER COLUMN id SET DEFAULT nextval('public.dd_sesion_id_seq'::regclass);


--
-- Name: si_adjunto id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_adjunto ALTER COLUMN id SET DEFAULT nextval('public.si_adjunto_id_seq'::regclass);


--
-- Name: si_modulo id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_modulo ALTER COLUMN id SET DEFAULT nextval('public.si_modulo_id_seq'::regclass);


--
-- Name: si_opcion id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion ALTER COLUMN id SET DEFAULT nextval('public.si_opcion_id_seq'::regclass);


--
-- Name: si_parametro id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_parametro ALTER COLUMN id SET DEFAULT nextval('public.si_parametro_id_seq'::regclass);


--
-- Name: si_plantilla_html id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_plantilla_html ALTER COLUMN id SET DEFAULT nextval('public.si_plantilla_html_id_seq'::regclass);


--
-- Name: si_rol id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rol ALTER COLUMN id SET DEFAULT nextval('public.si_rol_id_seq'::regclass);


--
-- Name: si_usuario_rol id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol ALTER COLUMN id SET DEFAULT nextval('public.si_usuario_rol_id_seq'::regclass);


--
-- Name: usuario id; Type: DEFAULT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.usuario ALTER COLUMN id SET DEFAULT nextval('public.usuario_id_seq'::regclass);


--
-- Data for Name: dd_sesion; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.dd_sesion (id, sesion_id, fecha_inicio, fecha_fin, punto_acceso, datos_cliente, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_adjunto; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_adjunto (id, uuid, nombre, descripcion, tipo_archivo, peso, url, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_modulo; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_modulo (id, nombre, ruta, icono, rutaservlet, tooltip, extralinkrender, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_opcion; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_opcion (id, si_modulo, nombre, pagina, posicion, si_opcion, paginalistener, icono, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_parametro; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_parametro (id, upload_directory, logo, tipo_almacen_adjuntos, gest_doc_url_base, gest_doc_prop_adic, gest_doc_usuario, gest_doc_clave, directorio_usuarios, genero, fecha_genero, modifico, fecha_modifico, eliminado, api_whatsapp) FROM stdin;
1	/ets/files/	\N	ALF	http://192.168.254.82/alfresco	{"site":"sia-documentos", "folder_name":"ETS"}	sia	sia123	mpg-ihsa.net	1	2023-02-20 17:40:04.804909	\N	\N	f	http://66.172.27.131:5001/whatsapp/send
2	/ets/files/	\N	ALF	http://192.168.254.57/alfresco	{"site":"sia-documentos", "folder_name":"ETS"}	sia	sia123	\N	1	2023-02-20 17:40:04.804909	\N	\N	f	http://66.172.27.131:5001/whatsapp/send
\.


--
-- Data for Name: si_plantilla_html; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_plantilla_html (id, nombre, descripcion, inicio, fin, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_rel_rol_opcion; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_rel_rol_opcion (id, si_rol, si_opcion, acceso_rapido, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_rol; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_rol (id, nombre, si_modulo, codigo, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: si_usuario_rol; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.si_usuario_rol (id, usuario, si_rol, genero, fecha_genero, modifico, fecha_modifico, eliminado) FROM stdin;
\.


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: sia
--

COPY public.usuario (id, nombre, email, clave, telefono, fecha_nacimiento, domicilio, curp, foto, seccion, genero, fecha_genero, modifico, fecha_modifico, eliminado, si_adjunto) FROM stdin;
3	Admin	admin@gmail.com	264c34ca1d5e413dc23e5f77c9319f468ded71b1	8110000000	1986-07-13	\N	\N	\N	0647	1	2023-02-21 13:03:58.918205	\N	\N	t	\N
1	SISTEMA	joel.rod.roj@hotmail.com	264c34ca1d5e413dc23e5f77c9319f468ded71b1	8110000000	1986-07-13	\N	\N	\N	0647	1	2023-02-17 12:02:56.788121	\N	\N	f	\N
2	Prueba	joel@gmail.com	264c34ca1d5e413dc23e5f77c9319f468ded71b1	8110000000	1986-07-13	\N	\N	\N	0647	1	2023-02-21 13:01:57.79958	\N	\N	f	\N
\.


--
-- Name: dd_sesion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.dd_sesion_id_seq', 1, false);


--
-- Name: si_adjunto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_adjunto_id_seq', 1, false);


--
-- Name: si_modulo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_modulo_id_seq', 1, false);


--
-- Name: si_opcion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_opcion_id_seq', 1, false);


--
-- Name: si_parametro_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_parametro_id_seq', 1, false);


--
-- Name: si_plantilla_html_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_plantilla_html_id_seq', 1, false);


--
-- Name: si_rol_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_rol_id_seq', 1, false);


--
-- Name: si_usuario_rol_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.si_usuario_rol_id_seq', 1, false);


--
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sia
--

SELECT pg_catalog.setval('public.usuario_id_seq', 42, true);


--
-- Name: dd_sesion dd_sesion_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.dd_sesion
    ADD CONSTRAINT dd_sesion_pkey PRIMARY KEY (id);


--
-- Name: si_adjunto si_adjunto_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_adjunto
    ADD CONSTRAINT si_adjunto_pkey PRIMARY KEY (id);


--
-- Name: si_modulo si_modulo_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_modulo
    ADD CONSTRAINT si_modulo_pkey PRIMARY KEY (id);


--
-- Name: si_opcion si_opcion_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion
    ADD CONSTRAINT si_opcion_pkey PRIMARY KEY (id);


--
-- Name: si_parametro si_parametro_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_parametro
    ADD CONSTRAINT si_parametro_pkey PRIMARY KEY (id);


--
-- Name: si_plantilla_html si_plantilla_html_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_plantilla_html
    ADD CONSTRAINT si_plantilla_html_pkey PRIMARY KEY (id);


--
-- Name: si_rel_rol_opcion si_rel_rol_opcion_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rel_rol_opcion
    ADD CONSTRAINT si_rel_rol_opcion_pkey PRIMARY KEY (id);


--
-- Name: si_rol si_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rol
    ADD CONSTRAINT si_rol_pkey PRIMARY KEY (id);


--
-- Name: si_usuario_rol si_usuario_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol
    ADD CONSTRAINT si_usuario_rol_pkey PRIMARY KEY (id);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: dd_sesion dd_sesion_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.dd_sesion
    ADD CONSTRAINT dd_sesion_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: dd_sesion dd_sesion_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.dd_sesion
    ADD CONSTRAINT dd_sesion_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_adjunto si_adjunto_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_adjunto
    ADD CONSTRAINT si_adjunto_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_adjunto si_adjunto_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_adjunto
    ADD CONSTRAINT si_adjunto_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_modulo si_modulo_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_modulo
    ADD CONSTRAINT si_modulo_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_modulo si_modulo_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_modulo
    ADD CONSTRAINT si_modulo_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_opcion si_opcion_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion
    ADD CONSTRAINT si_opcion_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_opcion si_opcion_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion
    ADD CONSTRAINT si_opcion_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_opcion si_opcion_si_modulo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion
    ADD CONSTRAINT si_opcion_si_modulo_fkey FOREIGN KEY (si_modulo) REFERENCES public.si_modulo(id);


--
-- Name: si_opcion si_opcion_si_opcion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_opcion
    ADD CONSTRAINT si_opcion_si_opcion_fkey FOREIGN KEY (si_opcion) REFERENCES public.si_modulo(id);


--
-- Name: si_parametro si_parametro_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_parametro
    ADD CONSTRAINT si_parametro_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_parametro si_parametro_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_parametro
    ADD CONSTRAINT si_parametro_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_plantilla_html si_plantilla_html_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_plantilla_html
    ADD CONSTRAINT si_plantilla_html_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_plantilla_html si_plantilla_html_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_plantilla_html
    ADD CONSTRAINT si_plantilla_html_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_rel_rol_opcion si_rel_rol_opcion_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rel_rol_opcion
    ADD CONSTRAINT si_rel_rol_opcion_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_rel_rol_opcion si_rel_rol_opcion_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rel_rol_opcion
    ADD CONSTRAINT si_rel_rol_opcion_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_rel_rol_opcion si_rel_rol_opcion_si_opcion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rel_rol_opcion
    ADD CONSTRAINT si_rel_rol_opcion_si_opcion_fkey FOREIGN KEY (si_opcion) REFERENCES public.si_opcion(id);


--
-- Name: si_rel_rol_opcion si_rel_rol_opcion_si_rol_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rel_rol_opcion
    ADD CONSTRAINT si_rel_rol_opcion_si_rol_fkey FOREIGN KEY (si_rol) REFERENCES public.si_rol(id);


--
-- Name: si_rol si_rol_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rol
    ADD CONSTRAINT si_rol_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_rol si_rol_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rol
    ADD CONSTRAINT si_rol_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_rol si_rol_si_modulo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_rol
    ADD CONSTRAINT si_rol_si_modulo_fkey FOREIGN KEY (si_modulo) REFERENCES public.si_modulo(id);


--
-- Name: si_usuario_rol si_usuario_rol_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol
    ADD CONSTRAINT si_usuario_rol_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: si_usuario_rol si_usuario_rol_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol
    ADD CONSTRAINT si_usuario_rol_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: si_usuario_rol si_usuario_rol_si_rol_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol
    ADD CONSTRAINT si_usuario_rol_si_rol_fkey FOREIGN KEY (si_rol) REFERENCES public.si_rol(id);


--
-- Name: si_usuario_rol si_usuario_rol_usuario_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.si_usuario_rol
    ADD CONSTRAINT si_usuario_rol_usuario_fkey FOREIGN KEY (usuario) REFERENCES public.usuario(id);


--
-- Name: usuario usuario_genero_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_genero_fkey FOREIGN KEY (genero) REFERENCES public.usuario(id);


--
-- Name: usuario usuario_modifico_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_modifico_fkey FOREIGN KEY (modifico) REFERENCES public.usuario(id);


--
-- Name: usuario usuario_si_adjunto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sia
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_si_adjunto_fkey FOREIGN KEY (si_adjunto) REFERENCES public.si_adjunto(id);


--
-- PostgreSQL database dump complete
--

