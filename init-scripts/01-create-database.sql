--
-- PostgreSQL database dump
--

-- Dumped from database version 16.9
-- Dumped by pg_dump version 16.9

-- Started on 2025-10-29 16:23:42

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

--
-- TOC entry 9 (class 2615 OID 17244)
-- Name: euk; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA euk;


-- Schema ownership will be handled by the database user

--
-- TOC entry 8 (class 2615 OID 16415)
-- Name: extensions; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA extensions;


ALTER SCHEMA extensions OWNER TO euk_user;

--
-- TOC entry 2 (class 3079 OID 17245)
-- Name: pg_stat_statements; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_stat_statements WITH SCHEMA extensions;


--
-- TOC entry 5105 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pg_stat_statements; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_stat_statements IS 'track planning and execution statistics of all SQL statements executed';


--
-- TOC entry 3 (class 3079 OID 17276)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA extensions;


--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 4 (class 3079 OID 17313)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA extensions;


--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 4
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- TOC entry 301 (class 1255 OID 17324)
-- Name: get_formular_by_kategorija(integer); Type: FUNCTION; Schema: euk; Owner: postgres
--

CREATE FUNCTION euk.get_formular_by_kategorija(kategorija_id_param integer) RETURNS TABLE(formular_id integer, naziv character varying, opis text, datum_kreiranja timestamp without time zone, aktivna boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT f.formular_id, f.naziv, f.opis, f.datum_kreiranja, f.aktivna
    FROM euk.formular f
    WHERE f.kategorija_id = kategorija_id_param AND f.aktivna = true
    ORDER BY f.datum_kreiranja DESC;
END;
$$;


ALTER FUNCTION euk.get_formular_by_kategorija(kategorija_id_param integer) OWNER TO euk_user;

--
-- TOC entry 302 (class 1255 OID 17325)
-- Name: get_formular_polja(integer); Type: FUNCTION; Schema: euk; Owner: postgres
--

CREATE FUNCTION euk.get_formular_polja(formular_id_param integer) RETURNS TABLE(polje_id integer, naziv_polja character varying, label character varying, tip_polja character varying, obavezno boolean, redosled integer, placeholder character varying, opis text, validacija text, opcije text, default_vrednost text, readonly boolean, visible boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT fp.polje_id, fp.naziv_polja, fp.label, fp.tip_polja, fp.obavezno, fp.redosled,
           fp.placeholder, fp.opis, fp.validacija, fp.opcije, fp.default_vrednost, fp.readonly, fp.visible
    FROM euk.formular_polja fp
    WHERE fp.formular_id = formular_id_param
    ORDER BY fp.redosled;
END;
$$;


ALTER FUNCTION euk.get_formular_polja(formular_id_param integer) OWNER TO euk_user;

--
-- TOC entry 303 (class 1255 OID 17326)
-- Name: can_access(integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.can_access(user_id integer, route_name character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    user_role VARCHAR(20);
    route_requires_admin BOOLEAN;
BEGIN
    -- Proveri rolu korisnika
    SELECT role INTO user_role FROM users WHERE id = user_id;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Proveri da li ruta zahteva admin rolu
    SELECT zahteva_admin_role INTO route_requires_admin FROM rute WHERE naziv = route_name;
    IF NOT FOUND THEN RETURN FALSE; END IF;
    
    -- Ako ruta zahteva admin rolu, korisnik mora biti admin
    IF route_requires_admin AND user_role != 'ADMIN' THEN
        RETURN FALSE;
    END IF;
    
    -- Proveri eksplicitni pristup
    IF EXISTS (SELECT 1 FROM user_routes WHERE user_id = user_id AND route_name = route_name AND can_access = true) THEN
        RETURN TRUE;
    END IF;
    
    -- Ako nema eksplicitnog pristupa, dozvoli pristup EUK rutama
    RETURN NOT route_requires_admin;
END;
$$;


ALTER FUNCTION public.can_access(user_id integer, route_name character varying) OWNER TO euk_user;

--
-- TOC entry 304 (class 1255 OID 17327)
-- Name: can_user_access_route(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.can_user_access_route(p_user_id integer, p_route_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_user_nivo INTEGER;
    v_route_nivo_min INTEGER;
    v_route_nivo_max INTEGER;
    v_route_aktivna BOOLEAN;
    v_user_nivo_dozvola INTEGER;
BEGIN
    -- Proveri da li korisnik postoji i dohvati njegov nivo
    SELECT nivo_pristupa INTO v_user_nivo
    FROM users WHERE id = p_user_id;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Korisnik ne postoji
    END IF;
    
    -- Proveri da li ruta postoji i dohvati njene nivoe
    SELECT nivo_min, nivo_max, aktivna INTO v_route_nivo_min, v_route_nivo_max, v_route_aktivna
    FROM rute WHERE id = p_route_id;
    
    IF NOT FOUND THEN
        RETURN FALSE; -- Ruta ne postoji
    END IF;
    
    -- Proveri da li je ruta aktivna
    IF NOT v_route_aktivna THEN
        RETURN FALSE;
    END IF;
    
    -- Proveri eksplicitnu dozvolu u user_routes
    SELECT nivo_dozvola INTO v_user_nivo_dozvola
    FROM user_routes 
    WHERE user_id = p_user_id AND route_id = p_route_id;
    
    -- Ako postoji eksplicitna dozvola, koristi je
    IF v_user_nivo_dozvola IS NOT NULL THEN
        RETURN v_user_nivo_dozvola >= v_route_nivo_min AND v_user_nivo_dozvola <= v_route_nivo_max;
    END IF;
    
    -- Ako nema eksplicitne dozvole, koristi nivo korisnika
    RETURN v_user_nivo >= v_route_nivo_min AND v_user_nivo <= v_route_nivo_max;
END;
$$;


ALTER FUNCTION public.can_user_access_route(p_user_id integer, p_route_id integer) OWNER TO euk_user;

--
-- TOC entry 305 (class 1255 OID 17328)
-- Name: check_global_license_validity(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_global_license_validity() RETURNS TABLE(license_id integer, license_key character varying, is_expired boolean, days_until_expiry integer, is_expiring_soon boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gl.id,
        gl.license_key,
        (CURRENT_TIMESTAMP > gl.end_date) as is_expired,
        EXTRACT(DAY FROM (gl.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry,
        (gl.end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')) as is_expiring_soon
    FROM global_license gl
    WHERE gl.is_active = true
    LIMIT 1;
END;
$$;


ALTER FUNCTION public.check_global_license_validity() OWNER TO euk_user;

--
-- TOC entry 306 (class 1255 OID 17329)
-- Name: get_global_license_expiring_soon(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_global_license_expiring_soon() RETURNS TABLE(license_id integer, license_key character varying, end_date timestamp without time zone, days_until_expiry integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT 
        gl.id,
        gl.license_key,
        gl.end_date,
        EXTRACT(DAY FROM (gl.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
    FROM global_license gl
    WHERE gl.is_active = true 
    AND gl.end_date BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + INTERVAL '30 days')
    AND gl.notification_sent = false
    LIMIT 1;
END;
$$;


ALTER FUNCTION public.get_global_license_expiring_soon() OWNER TO euk_user;

--
-- TOC entry 307 (class 1255 OID 17330)
-- Name: grant_access(integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.grant_access(user_id integer, route_name character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO user_routes (user_id, route_name) 
    VALUES (user_id, route_name)
    ON CONFLICT (user_id, route_name) DO NOTHING;
    RETURN TRUE;
END;
$$;


ALTER FUNCTION public.grant_access(user_id integer, route_name character varying) OWNER TO euk_user;

--
-- TOC entry 308 (class 1255 OID 17331)
-- Name: grant_route_access(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Proveri da li korisnik postoji
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'Korisnik sa ID % ne postoji', p_user_id;
    END IF;
    
    -- Proveri da li ruta postoji
    IF NOT EXISTS (SELECT 1 FROM rute WHERE id = p_route_id) THEN
        RAISE EXCEPTION 'Ruta sa ID % ne postoji', p_route_id;
    END IF;
    
    -- Dodaj ili ažuriraj pristup
    INSERT INTO user_routes (user_id, route_id, nivo_dozvola)
    VALUES (p_user_id, p_route_id, p_nivo_dozvola)
    ON CONFLICT (user_id, route_id) 
    DO UPDATE SET
        nivo_dozvola = p_nivo_dozvola;
    
    RETURN TRUE;
END;
$$;


ALTER FUNCTION public.grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer) OWNER TO euk_user;

--
-- TOC entry 309 (class 1255 OID 17332)
-- Name: revoke_access(integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.revoke_access(user_id integer, route_name character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM user_routes WHERE user_id = user_id AND route_name = route_name;
    RETURN FOUND;
END;
$$;


ALTER FUNCTION public.revoke_access(user_id integer, route_name character varying) OWNER TO euk_user;

--
-- TOC entry 310 (class 1255 OID 17333)
-- Name: update_formular_polja_updated_at(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_formular_polja_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_formular_polja_updated_at() OWNER TO euk_user;

--
-- TOC entry 311 (class 1255 OID 17334)
-- Name: update_formular_updated_at(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_formular_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.datum_azuriranja = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_formular_updated_at() OWNER TO euk_user;

--
-- TOC entry 312 (class 1255 OID 17335)
-- Name: update_global_license_updated_at(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_global_license_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_global_license_updated_at() OWNER TO euk_user;

--
-- TOC entry 313 (class 1255 OID 17336)
-- Name: update_ugrozeno_lice_t2_updated_at(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_ugrozeno_lice_t2_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_ugrozeno_lice_t2_updated_at() OWNER TO euk_user;

--
-- TOC entry 314 (class 1255 OID 17337)
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_updated_at_column() OWNER TO euk_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 17338)
-- Name: kategorija; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.kategorija (
    kategorija_id integer NOT NULL,
    naziv character varying(255) NOT NULL,
    skracenica character varying(10) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE euk.kategorija OWNER TO euk_user;

--
-- TOC entry 5120 (class 0 OID 0)
-- Dependencies: 222
-- Name: TABLE kategorija; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON TABLE euk.kategorija IS 'Tabela za kategorije predmeta';


--
-- TOC entry 223 (class 1259 OID 17342)
-- Name: kategorija_kategorija_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.kategorija_kategorija_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.kategorija_kategorija_id_seq OWNER TO euk_user;

--
-- TOC entry 5121 (class 0 OID 0)
-- Dependencies: 223
-- Name: kategorija_kategorija_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.kategorija_kategorija_id_seq OWNED BY euk.kategorija.kategorija_id;


--
-- TOC entry 224 (class 1259 OID 17343)
-- Name: obrasci_vrste; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.obrasci_vrste (
    id bigint NOT NULL,
    naziv character varying(100) NOT NULL,
    opis text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE euk.obrasci_vrste OWNER TO euk_user;

--
-- TOC entry 225 (class 1259 OID 17350)
-- Name: obrasci_vrste_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.obrasci_vrste_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.obrasci_vrste_id_seq OWNER TO euk_user;

--
-- TOC entry 5122 (class 0 OID 0)
-- Dependencies: 225
-- Name: obrasci_vrste_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.obrasci_vrste_id_seq OWNED BY euk.obrasci_vrste.id;


--
-- TOC entry 226 (class 1259 OID 17351)
-- Name: organizaciona_struktura; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.organizaciona_struktura (
    id bigint NOT NULL,
    naziv character varying(100) NOT NULL,
    opis text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE euk.organizaciona_struktura OWNER TO euk_user;

--
-- TOC entry 227 (class 1259 OID 17358)
-- Name: organizaciona_struktura_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.organizaciona_struktura_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.organizaciona_struktura_id_seq OWNER TO euk_user;

--
-- TOC entry 5123 (class 0 OID 0)
-- Dependencies: 227
-- Name: organizaciona_struktura_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.organizaciona_struktura_id_seq OWNED BY euk.organizaciona_struktura.id;


--
-- TOC entry 228 (class 1259 OID 17359)
-- Name: predmet; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.predmet (
    predmet_id integer NOT NULL,
    datum_kreiranja date DEFAULT CURRENT_DATE NOT NULL,
    naziv_predmeta character varying(255) NOT NULL,
    status character varying(50) NOT NULL,
    odgovorna_osoba character varying(255) NOT NULL,
    prioritet character varying(20) NOT NULL,
    rok_za_zavrsetak date,
    kategorija_id integer NOT NULL,
    kategorija_skracenica character varying(10),
    template_file_path character varying(500),
    template_generated_at timestamp without time zone,
    template_status character varying(50) DEFAULT 'pending'::character varying,
    odbija_se_nsp_dokument bytea,
    odbija_se_nsp_dokument_naziv character varying(255),
    odbija_se_nsp_dokument_datum timestamp without time zone
);


ALTER TABLE euk.predmet OWNER TO euk_user;

--
-- TOC entry 5124 (class 0 OID 0)
-- Dependencies: 228
-- Name: TABLE predmet; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON TABLE euk.predmet IS 'Tabela za predmete/slucajeve';


--
-- TOC entry 5125 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN predmet.datum_kreiranja; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.predmet.datum_kreiranja IS 'Datum kreiranja predmeta - defaultno danasnji datum';


--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN predmet.rok_za_zavrsetak; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.predmet.rok_za_zavrsetak IS 'Rok za zavrsetak predmeta - moze biti NULL';


--
-- TOC entry 5127 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN predmet.odbija_se_nsp_dokument; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument IS 'Generisani Word dokument (ODBIJA SE NSP,UNSP,DD,UDTNP) u .docx formatu';


--
-- TOC entry 5128 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN predmet.odbija_se_nsp_dokument_naziv; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument_naziv IS 'Naziv generisanog dokumenta';


--
-- TOC entry 5129 (class 0 OID 0)
-- Dependencies: 228
-- Name: COLUMN predmet.odbija_se_nsp_dokument_datum; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.predmet.odbija_se_nsp_dokument_datum IS 'Datum i vreme kada je dokument generisan';


--
-- TOC entry 229 (class 1259 OID 17366)
-- Name: predmet_predmet_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.predmet_predmet_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.predmet_predmet_id_seq OWNER TO euk_user;

--
-- TOC entry 5130 (class 0 OID 0)
-- Dependencies: 229
-- Name: predmet_predmet_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.predmet_predmet_id_seq OWNED BY euk.predmet.predmet_id;


--
-- TOC entry 230 (class 1259 OID 17367)
-- Name: ugrozeno_lice_t1_ugrozeno_lice_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq OWNER TO euk_user;

--
-- TOC entry 231 (class 1259 OID 17368)
-- Name: ugrozeno_lice_t1; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.ugrozeno_lice_t1 (
    ugrozeno_lice_id integer DEFAULT nextval('euk.ugrozeno_lice_t1_ugrozeno_lice_id_seq'::regclass) NOT NULL,
    redni_broj character varying(20),
    ime character varying(100),
    prezime character varying(100),
    jmbg character varying(50),
    ptt_broj character varying(10),
    grad_opstina character varying(100),
    mesto character varying(100),
    ulica_i_broj character varying(200),
    broj_clanova_domacinstva integer,
    osnov_sticanja_statusa character varying(10),
    ed_broj_broj_mernog_uredjaja character varying(100),
    potrosnja_i_povrsina_combined character varying(200),
    iznos_umanjenja_sa_pdv numeric(12,2),
    broj_racuna character varying(50),
    datum_izdavanja_racuna date,
    datum_trajanja_prava date,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE euk.ugrozeno_lice_t1 OWNER TO euk_user;

--
-- TOC entry 232 (class 1259 OID 17376)
-- Name: ugrozeno_lice_t1_new_ugrozeno_lice_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.ugrozeno_lice_t1_new_ugrozeno_lice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.ugrozeno_lice_t1_new_ugrozeno_lice_id_seq OWNER TO euk_user;

--
-- TOC entry 5131 (class 0 OID 0)
-- Dependencies: 232
-- Name: ugrozeno_lice_t1_new_ugrozeno_lice_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.ugrozeno_lice_t1_new_ugrozeno_lice_id_seq OWNED BY euk.ugrozeno_lice_t1.ugrozeno_lice_id;


--
-- TOC entry 233 (class 1259 OID 17377)
-- Name: ugrozeno_lice_t2; Type: TABLE; Schema: euk; Owner: postgres
--

CREATE TABLE euk.ugrozeno_lice_t2 (
    ugrozeno_lice_id integer NOT NULL,
    redni_broj character varying(20) NOT NULL,
    ime character varying(100) NOT NULL,
    prezime character varying(100) NOT NULL,
    jmbg character(13) NOT NULL,
    ptt_broj character varying(10),
    grad_opstina character varying(100),
    mesto character varying(100),
    ulica_i_broj character varying(200),
    ed_broj character varying(100),
    pok_vazenja_resenja_o_statusu character varying(200),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE euk.ugrozeno_lice_t2 OWNER TO euk_user;

--
-- TOC entry 5132 (class 0 OID 0)
-- Dependencies: 233
-- Name: TABLE ugrozeno_lice_t2; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON TABLE euk.ugrozeno_lice_t2 IS 'Tabela za čuvanje podataka o ugroženim licima - verzija T2';


--
-- TOC entry 5133 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.ugrozeno_lice_id; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.ugrozeno_lice_id IS 'Primarni ključ - ID ugroženog lica';


--
-- TOC entry 5134 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.redni_broj; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.redni_broj IS 'Redni broj ugroženog lica';


--
-- TOC entry 5135 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.ime; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.ime IS 'Ime ugroženog lica';


--
-- TOC entry 5136 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.prezime; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.prezime IS 'Prezime ugroženog lica';


--
-- TOC entry 5137 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.jmbg; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.jmbg IS 'JMBG ugroženog lica (13 cifara)';


--
-- TOC entry 5138 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.ptt_broj; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.ptt_broj IS 'PTT broj';


--
-- TOC entry 5139 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.grad_opstina; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.grad_opstina IS 'Grad/Opština';


--
-- TOC entry 5140 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.mesto; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.mesto IS 'Mesto';


--
-- TOC entry 5141 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.ulica_i_broj; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.ulica_i_broj IS 'Ulica i broj';


--
-- TOC entry 5142 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.ed_broj; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.ed_broj IS 'ED broj';


--
-- TOC entry 5143 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.pok_vazenja_resenja_o_statusu; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.pok_vazenja_resenja_o_statusu IS 'Period važenja rešenja o statusu';


--
-- TOC entry 5144 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.created_at; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.created_at IS 'Datum i vreme kreiranja zapisa';


--
-- TOC entry 5145 (class 0 OID 0)
-- Dependencies: 233
-- Name: COLUMN ugrozeno_lice_t2.updated_at; Type: COMMENT; Schema: euk; Owner: postgres
--

COMMENT ON COLUMN euk.ugrozeno_lice_t2.updated_at IS 'Datum i vreme poslednje izmene zapisa';


--
-- TOC entry 234 (class 1259 OID 17384)
-- Name: ugrozeno_lice_t2_ugrozeno_lice_id_seq; Type: SEQUENCE; Schema: euk; Owner: postgres
--

CREATE SEQUENCE euk.ugrozeno_lice_t2_ugrozeno_lice_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE euk.ugrozeno_lice_t2_ugrozeno_lice_id_seq OWNER TO euk_user;

--
-- TOC entry 5146 (class 0 OID 0)
-- Dependencies: 234
-- Name: ugrozeno_lice_t2_ugrozeno_lice_id_seq; Type: SEQUENCE OWNED BY; Schema: euk; Owner: postgres
--

ALTER SEQUENCE euk.ugrozeno_lice_t2_ugrozeno_lice_id_seq OWNED BY euk.ugrozeno_lice_t2.ugrozeno_lice_id;


--
-- TOC entry 235 (class 1259 OID 17385)
-- Name: global_license; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.global_license (
    id bigint NOT NULL,
    license_key character varying(255) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    end_date timestamp without time zone NOT NULL,
    is_active boolean DEFAULT true,
    notification_sent boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.global_license OWNER TO euk_user;

--
-- TOC entry 236 (class 1259 OID 17392)
-- Name: global_license_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.global_license_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.global_license_id_seq OWNER TO euk_user;

--
-- TOC entry 5148 (class 0 OID 0)
-- Dependencies: 236
-- Name: global_license_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.global_license_id_seq OWNED BY public.global_license.id;


--
-- TOC entry 237 (class 1259 OID 17393)
-- Name: user_sessions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_sessions (
    id integer NOT NULL,
    user_id integer,
    token character varying(500) NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.user_sessions OWNER TO euk_user;

--
-- TOC entry 238 (class 1259 OID 17399)
-- Name: user_sessions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_sessions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_sessions_id_seq OWNER TO euk_user;

--
-- TOC entry 5151 (class 0 OID 0)
-- Dependencies: 238
-- Name: user_sessions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_sessions_id_seq OWNED BY public.user_sessions.id;


--
-- TOC entry 239 (class 1259 OID 17400)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    role character varying(50) DEFAULT 'korisnik'::character varying,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (((role)::text = ANY (ARRAY[('admin'::character varying)::text, ('korisnik'::character varying)::text])))
);


ALTER TABLE public.users OWNER TO euk_user;

--
-- TOC entry 240 (class 1259 OID 17410)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO euk_user;

--
-- TOC entry 5154 (class 0 OID 0)
-- Dependencies: 240
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 4855 (class 2604 OID 17411)
-- Name: kategorija kategorija_id; Type: DEFAULT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.kategorija ALTER COLUMN kategorija_id SET DEFAULT nextval('euk.kategorija_kategorija_id_seq'::regclass);


--
-- TOC entry 4857 (class 2604 OID 17500)
-- Name: obrasci_vrste id; Type: DEFAULT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.obrasci_vrste ALTER COLUMN id SET DEFAULT nextval('euk.obrasci_vrste_id_seq'::regclass);


--
-- TOC entry 4860 (class 2604 OID 17511)
-- Name: organizaciona_struktura id; Type: DEFAULT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.organizaciona_struktura ALTER COLUMN id SET DEFAULT nextval('euk.organizaciona_struktura_id_seq'::regclass);


--
-- TOC entry 4863 (class 2604 OID 17414)
-- Name: predmet predmet_id; Type: DEFAULT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.predmet ALTER COLUMN predmet_id SET DEFAULT nextval('euk.predmet_predmet_id_seq'::regclass);


--
-- TOC entry 4869 (class 2604 OID 17415)
-- Name: ugrozeno_lice_t2 ugrozeno_lice_id; Type: DEFAULT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.ugrozeno_lice_t2 ALTER COLUMN ugrozeno_lice_id SET DEFAULT nextval('euk.ugrozeno_lice_t2_ugrozeno_lice_id_seq'::regclass);


--
-- TOC entry 4872 (class 2604 OID 17777)
-- Name: global_license id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.global_license ALTER COLUMN id SET DEFAULT nextval('public.global_license_id_seq'::regclass);


--
-- TOC entry 4877 (class 2604 OID 17417)
-- Name: user_sessions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_sessions ALTER COLUMN id SET DEFAULT nextval('public.user_sessions_id_seq'::regclass);


--
-- TOC entry 4879 (class 2604 OID 17418)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4886 (class 2606 OID 17420)
-- Name: kategorija kategorija_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.kategorija
    ADD CONSTRAINT kategorija_pkey PRIMARY KEY (kategorija_id);


--
-- TOC entry 4889 (class 2606 OID 17422)
-- Name: obrasci_vrste obrasci_vrste_naziv_key; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.obrasci_vrste
    ADD CONSTRAINT obrasci_vrste_naziv_key UNIQUE (naziv);


--
-- TOC entry 4891 (class 2606 OID 17502)
-- Name: obrasci_vrste obrasci_vrste_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.obrasci_vrste
    ADD CONSTRAINT obrasci_vrste_pkey PRIMARY KEY (id);


--
-- TOC entry 4894 (class 2606 OID 17426)
-- Name: organizaciona_struktura organizaciona_struktura_naziv_key; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.organizaciona_struktura
    ADD CONSTRAINT organizaciona_struktura_naziv_key UNIQUE (naziv);


--
-- TOC entry 4896 (class 2606 OID 17513)
-- Name: organizaciona_struktura organizaciona_struktura_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.organizaciona_struktura
    ADD CONSTRAINT organizaciona_struktura_pkey PRIMARY KEY (id);


--
-- TOC entry 4901 (class 2606 OID 17430)
-- Name: predmet predmet_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.predmet
    ADD CONSTRAINT predmet_pkey PRIMARY KEY (predmet_id);


--
-- TOC entry 4917 (class 2606 OID 17432)
-- Name: ugrozeno_lice_t1 ugrozeno_lice_t1_new_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.ugrozeno_lice_t1
    ADD CONSTRAINT ugrozeno_lice_t1_new_pkey PRIMARY KEY (ugrozeno_lice_id);


--
-- TOC entry 4929 (class 2606 OID 17803)
-- Name: ugrozeno_lice_t2 ugrozeno_lice_t2_jmbg_key; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.ugrozeno_lice_t2
    ADD CONSTRAINT ugrozeno_lice_t2_jmbg_key UNIQUE (jmbg);


--
-- TOC entry 4931 (class 2606 OID 17436)
-- Name: ugrozeno_lice_t2 ugrozeno_lice_t2_pkey; Type: CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.ugrozeno_lice_t2
    ADD CONSTRAINT ugrozeno_lice_t2_pkey PRIMARY KEY (ugrozeno_lice_id);


--
-- TOC entry 4933 (class 2606 OID 17438)
-- Name: global_license global_license_license_key_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.global_license
    ADD CONSTRAINT global_license_license_key_key UNIQUE (license_key);


--
-- TOC entry 4935 (class 2606 OID 17779)
-- Name: global_license global_license_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.global_license
    ADD CONSTRAINT global_license_pkey PRIMARY KEY (id);


--
-- TOC entry 4941 (class 2606 OID 17442)
-- Name: user_sessions user_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_pkey PRIMARY KEY (id);


--
-- TOC entry 4945 (class 2606 OID 17444)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 4947 (class 2606 OID 17446)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4949 (class 2606 OID 17448)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4902 (class 1259 OID 17449)
-- Name: idx_euk_ugrozeno_lice_t1_created_at; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_created_at ON euk.ugrozeno_lice_t1 USING btree (created_at);


--
-- TOC entry 4903 (class 1259 OID 17450)
-- Name: idx_euk_ugrozeno_lice_t1_datum_racuna; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna ON euk.ugrozeno_lice_t1 USING btree (datum_izdavanja_racuna);


--
-- TOC entry 4904 (class 1259 OID 17451)
-- Name: idx_euk_ugrozeno_lice_t1_datum_racuna_iznos; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_datum_racuna_iznos ON euk.ugrozeno_lice_t1 USING btree (datum_izdavanja_racuna, iznos_umanjenja_sa_pdv);


--
-- TOC entry 4905 (class 1259 OID 17542)
-- Name: idx_euk_ugrozeno_lice_t1_ed_broj; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_ed_broj ON euk.ugrozeno_lice_t1 USING btree (ed_broj_broj_mernog_uredjaja);


--
-- TOC entry 4906 (class 1259 OID 17628)
-- Name: idx_euk_ugrozeno_lice_t1_grad_mesto; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_mesto ON euk.ugrozeno_lice_t1 USING btree (grad_opstina, mesto);


--
-- TOC entry 4907 (class 1259 OID 17565)
-- Name: idx_euk_ugrozeno_lice_t1_grad_opstina; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_grad_opstina ON euk.ugrozeno_lice_t1 USING btree (grad_opstina);


--
-- TOC entry 4908 (class 1259 OID 17693)
-- Name: idx_euk_ugrozeno_lice_t1_ime_prezime; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_ime_prezime ON euk.ugrozeno_lice_t1 USING btree (ime, prezime);


--
-- TOC entry 4909 (class 1259 OID 17627)
-- Name: idx_euk_ugrozeno_lice_t1_mesto; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_mesto ON euk.ugrozeno_lice_t1 USING btree (mesto);


--
-- TOC entry 4910 (class 1259 OID 17649)
-- Name: idx_euk_ugrozeno_lice_t1_osnov_statusa; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_osnov_statusa ON euk.ugrozeno_lice_t1 USING btree (osnov_sticanja_statusa);


--
-- TOC entry 4911 (class 1259 OID 17672)
-- Name: idx_euk_ugrozeno_lice_t1_potrosnja_povrsina_combined; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_potrosnja_povrsina_combined ON euk.ugrozeno_lice_t1 USING btree (potrosnja_i_povrsina_combined);


--
-- TOC entry 4912 (class 1259 OID 17651)
-- Name: idx_euk_ugrozeno_lice_t1_status_ed; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_status_ed ON euk.ugrozeno_lice_t1 USING btree (osnov_sticanja_statusa, ed_broj_broj_mernog_uredjaja);


--
-- TOC entry 4913 (class 1259 OID 17460)
-- Name: idx_euk_ugrozeno_lice_t1_umanjenje; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_umanjenje ON euk.ugrozeno_lice_t1 USING btree (iznos_umanjenja_sa_pdv);


--
-- TOC entry 4914 (class 1259 OID 17461)
-- Name: idx_euk_ugrozeno_lice_t1_updated_at; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t1_updated_at ON euk.ugrozeno_lice_t1 USING btree (updated_at);


--
-- TOC entry 4918 (class 1259 OID 17462)
-- Name: idx_euk_ugrozeno_lice_t2_created_at; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_created_at ON euk.ugrozeno_lice_t2 USING btree (created_at);


--
-- TOC entry 4919 (class 1259 OID 17463)
-- Name: idx_euk_ugrozeno_lice_t2_ed_broj; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_ed_broj ON euk.ugrozeno_lice_t2 USING btree (ed_broj);


--
-- TOC entry 4920 (class 1259 OID 17464)
-- Name: idx_euk_ugrozeno_lice_t2_grad_opstina; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_grad_opstina ON euk.ugrozeno_lice_t2 USING btree (grad_opstina);


--
-- TOC entry 4921 (class 1259 OID 17465)
-- Name: idx_euk_ugrozeno_lice_t2_ime_prezime; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_ime_prezime ON euk.ugrozeno_lice_t2 USING btree (ime, prezime);


--
-- TOC entry 4922 (class 1259 OID 17804)
-- Name: idx_euk_ugrozeno_lice_t2_jmbg; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE UNIQUE INDEX idx_euk_ugrozeno_lice_t2_jmbg ON euk.ugrozeno_lice_t2 USING btree (jmbg);


--
-- TOC entry 4923 (class 1259 OID 17467)
-- Name: idx_euk_ugrozeno_lice_t2_mesto; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_mesto ON euk.ugrozeno_lice_t2 USING btree (mesto);


--
-- TOC entry 4924 (class 1259 OID 17468)
-- Name: idx_euk_ugrozeno_lice_t2_pok_vazenja; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_pok_vazenja ON euk.ugrozeno_lice_t2 USING btree (pok_vazenja_resenja_o_statusu);


--
-- TOC entry 4925 (class 1259 OID 17469)
-- Name: idx_euk_ugrozeno_lice_t2_ptt_broj; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_ptt_broj ON euk.ugrozeno_lice_t2 USING btree (ptt_broj);


--
-- TOC entry 4926 (class 1259 OID 17470)
-- Name: idx_euk_ugrozeno_lice_t2_redni_broj; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_redni_broj ON euk.ugrozeno_lice_t2 USING btree (redni_broj);


--
-- TOC entry 4927 (class 1259 OID 17471)
-- Name: idx_euk_ugrozeno_lice_t2_updated_at; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_euk_ugrozeno_lice_t2_updated_at ON euk.ugrozeno_lice_t2 USING btree (updated_at);


--
-- TOC entry 4887 (class 1259 OID 17472)
-- Name: idx_obrasci_vrste_naziv; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_obrasci_vrste_naziv ON euk.obrasci_vrste USING btree (naziv);


--
-- TOC entry 4892 (class 1259 OID 17473)
-- Name: idx_organizaciona_struktura_naziv; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_organizaciona_struktura_naziv ON euk.organizaciona_struktura USING btree (naziv);


--
-- TOC entry 4897 (class 1259 OID 17474)
-- Name: idx_predmet_kategorija_id; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_predmet_kategorija_id ON euk.predmet USING btree (kategorija_id);


--
-- TOC entry 4898 (class 1259 OID 17475)
-- Name: idx_predmet_kategorija_skracenica; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_predmet_kategorija_skracenica ON euk.predmet USING btree (kategorija_skracenica);


--
-- TOC entry 4899 (class 1259 OID 17476)
-- Name: idx_predmet_template_status; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_predmet_template_status ON euk.predmet USING btree (template_status);


--
-- TOC entry 4915 (class 1259 OID 17650)
-- Name: idx_ugrozeno_lice_osnov_status; Type: INDEX; Schema: euk; Owner: postgres
--

CREATE INDEX idx_ugrozeno_lice_osnov_status ON euk.ugrozeno_lice_t1 USING btree (osnov_sticanja_statusa);


--
-- TOC entry 4936 (class 1259 OID 17478)
-- Name: idx_global_license_active; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_global_license_active ON public.global_license USING btree (is_active);


--
-- TOC entry 4937 (class 1259 OID 17479)
-- Name: idx_global_license_end_date; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_global_license_end_date ON public.global_license USING btree (end_date);


--
-- TOC entry 4938 (class 1259 OID 17480)
-- Name: idx_sessions_token; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sessions_token ON public.user_sessions USING btree (token);


--
-- TOC entry 4939 (class 1259 OID 17481)
-- Name: idx_sessions_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_sessions_user_id ON public.user_sessions USING btree (user_id);


--
-- TOC entry 4942 (class 1259 OID 17482)
-- Name: idx_users_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_users_email ON public.users USING btree (email);


--
-- TOC entry 4943 (class 1259 OID 17483)
-- Name: idx_users_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_users_username ON public.users USING btree (username);


--
-- TOC entry 4952 (class 2620 OID 17484)
-- Name: ugrozeno_lice_t2 trigger_update_ugrozeno_lice_t2_updated_at; Type: TRIGGER; Schema: euk; Owner: postgres
--

CREATE TRIGGER trigger_update_ugrozeno_lice_t2_updated_at BEFORE UPDATE ON euk.ugrozeno_lice_t2 FOR EACH ROW EXECUTE FUNCTION public.update_ugrozeno_lice_t2_updated_at();


--
-- TOC entry 4953 (class 2620 OID 17485)
-- Name: global_license update_global_license_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER update_global_license_updated_at BEFORE UPDATE ON public.global_license FOR EACH ROW EXECUTE FUNCTION public.update_global_license_updated_at();


--
-- TOC entry 4950 (class 2606 OID 17486)
-- Name: predmet fk_predmet_kategorija; Type: FK CONSTRAINT; Schema: euk; Owner: postgres
--

ALTER TABLE ONLY euk.predmet
    ADD CONSTRAINT fk_predmet_kategorija FOREIGN KEY (kategorija_id) REFERENCES euk.kategorija(kategorija_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4951 (class 2606 OID 17491)
-- Name: user_sessions user_sessions_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_sessions
    ADD CONSTRAINT user_sessions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 5104 (class 0 OID 0)
-- Dependencies: 10
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT USAGE ON SCHEMA public TO euk_user;
-- GRANT USAGE ON SCHEMA public TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT USAGE ON SCHEMA public TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT USAGE ON SCHEMA public TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 303
-- Name: FUNCTION can_access(user_id integer, route_name character varying); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.can_access(user_id integer, route_name character varying) TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.can_access(user_id integer, route_name character varying) TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.can_access(user_id integer, route_name character varying) TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5109 (class 0 OID 0)
-- Dependencies: 304
-- Name: FUNCTION can_user_access_route(p_user_id integer, p_route_id integer); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.can_user_access_route(p_user_id integer, p_route_id integer) TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.can_user_access_route(p_user_id integer, p_route_id integer) TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.can_user_access_route(p_user_id integer, p_route_id integer) TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5110 (class 0 OID 0)
-- Dependencies: 305
-- Name: FUNCTION check_global_license_validity(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.check_global_license_validity() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.check_global_license_validity() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.check_global_license_validity() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5111 (class 0 OID 0)
-- Dependencies: 306
-- Name: FUNCTION get_global_license_expiring_soon(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.get_global_license_expiring_soon() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.get_global_license_expiring_soon() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.get_global_license_expiring_soon() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5112 (class 0 OID 0)
-- Dependencies: 307
-- Name: FUNCTION grant_access(user_id integer, route_name character varying); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.grant_access(user_id integer, route_name character varying) TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.grant_access(user_id integer, route_name character varying) TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.grant_access(user_id integer, route_name character varying) TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 308
-- Name: FUNCTION grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer) TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer) TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.grant_route_access(p_user_id integer, p_route_id integer, p_nivo_dozvola integer) TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 309
-- Name: FUNCTION revoke_access(user_id integer, route_name character varying); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.revoke_access(user_id integer, route_name character varying) TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.revoke_access(user_id integer, route_name character varying) TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.revoke_access(user_id integer, route_name character varying) TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 310
-- Name: FUNCTION update_formular_polja_updated_at(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.update_formular_polja_updated_at() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_formular_polja_updated_at() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_formular_polja_updated_at() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 311
-- Name: FUNCTION update_formular_updated_at(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.update_formular_updated_at() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_formular_updated_at() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_formular_updated_at() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 312
-- Name: FUNCTION update_global_license_updated_at(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.update_global_license_updated_at() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_global_license_updated_at() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_global_license_updated_at() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 313
-- Name: FUNCTION update_ugrozeno_lice_t2_updated_at(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.update_ugrozeno_lice_t2_updated_at() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_ugrozeno_lice_t2_updated_at() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_ugrozeno_lice_t2_updated_at() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 314
-- Name: FUNCTION update_updated_at_column(); Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON FUNCTION public.update_updated_at_column() TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_updated_at_column() TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTION public.update_updated_at_column() TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5147 (class 0 OID 0)
-- Dependencies: 235
-- Name: TABLE global_license; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON TABLE public.global_license TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.global_license TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.global_license TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5149 (class 0 OID 0)
-- Dependencies: 236
-- Name: SEQUENCE global_license_id_seq; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON SEQUENCE public.global_license_id_seq TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.global_license_id_seq TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.global_license_id_seq TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5150 (class 0 OID 0)
-- Dependencies: 237
-- Name: TABLE user_sessions; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON TABLE public.user_sessions TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.user_sessions TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.user_sessions TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5152 (class 0 OID 0)
-- Dependencies: 238
-- Name: SEQUENCE user_sessions_id_seq; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON SEQUENCE public.user_sessions_id_seq TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.user_sessions_id_seq TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.user_sessions_id_seq TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5153 (class 0 OID 0)
-- Dependencies: 239
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON TABLE public.users TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.users TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLE public.users TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 5155 (class 0 OID 0)
-- Dependencies: 240
-- Name: SEQUENCE users_id_seq; Type: ACL; Schema: public; Owner: postgres
--

-- GRANT ALL ON SEQUENCE public.users_id_seq TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.users_id_seq TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCE public.users_id_seq TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 2155 (class 826 OID 17496)
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE euk_user IN SCHEMA public GRANT ALL ON SEQUENCES TO euk_user;
-- GRANT ALL ON SEQUENCES TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCES TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON SEQUENCES TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 2156 (class 826 OID 17497)
-- Name: DEFAULT PRIVILEGES FOR FUNCTIONS; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE euk_user IN SCHEMA public GRANT ALL ON FUNCTIONS TO euk_user;
-- GRANT ALL ON FUNCTIONS TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTIONS TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON FUNCTIONS TO service_role; (commented out - roles do not exist in standard PostgreSQL)


--
-- TOC entry 2157 (class 826 OID 17498)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE euk_user IN SCHEMA public GRANT ALL ON TABLES TO euk_user;
-- GRANT ALL ON TABLES TO anon; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLES TO authenticated; (commented out - roles do not exist in standard PostgreSQL)
-- GRANT ALL ON TABLES TO service_role; (commented out - roles do not exist in standard PostgreSQL)


-- Completed on 2025-10-29 16:23:43

--
-- PostgreSQL database dump complete
--

