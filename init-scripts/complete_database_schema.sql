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


CREATE SCHEMA IF NOT EXISTS "euk";


ALTER SCHEMA "euk" OWNER TO "postgres";


COMMENT ON SCHEMA "public" IS 'standard public schema';



CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";






CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";






CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";






CREATE OR REPLACE FUNCTION "euk"."get_formular_by_kategorija"("kategorija_id_param" integer) RETURNS TABLE("formular_id" integer, "naziv" character varying, "opis" "text", "datum_kreiranja" timestamp without time zone, "aktivna" boolean)
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    RETURN QUERY
    SELECT f.formular_id, f.naziv, f.opis, f.datum_kreiranja, f.aktivna
    FROM euk.formular f
    WHERE f.kategorija_id = kategorija_id_param AND f.aktivna = true
    ORDER BY f.datum_kreiranja DESC;
END;
$$;


ALTER FUNCTION "euk"."get_formular_by_kategorija"("kategorija_id_param" integer) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "euk"."get_formular_polja"("formular_id_param" integer) RETURNS TABLE("polje_id" integer, "naziv_polja" character varying, "label" character varying, "tip_polja" character varying, "obavezno" boolean, "redosled" integer, "placeholder" character varying, "opis" "text", "validacija" "text", "opcije" "text", "default_vrednost" "text", "readonly" boolean, "visible" boolean)
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "euk"."get_formular_polja"("formular_id_param" integer) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."can_access"("user_id" integer, "route_name" character varying) RETURNS boolean
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "public"."can_access"("user_id" integer, "route_name" character varying) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."can_user_access_route"("p_user_id" integer, "p_route_id" integer) RETURNS boolean
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "public"."can_user_access_route"("p_user_id" integer, "p_route_id" integer) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."check_global_license_validity"() RETURNS TABLE("license_id" integer, "license_key" character varying, "is_expired" boolean, "days_until_expiry" integer, "is_expiring_soon" boolean)
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "public"."check_global_license_validity"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."get_global_license_expiring_soon"() RETURNS TABLE("license_id" integer, "license_key" character varying, "end_date" timestamp without time zone, "days_until_expiry" integer)
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "public"."get_global_license_expiring_soon"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."grant_access"("user_id" integer, "route_name" character varying) RETURNS boolean
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    INSERT INTO user_routes (user_id, route_name) 
    VALUES (user_id, route_name)
    ON CONFLICT (user_id, route_name) DO NOTHING;
    RETURN TRUE;
END;
$$;


ALTER FUNCTION "public"."grant_access"("user_id" integer, "route_name" character varying) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."grant_route_access"("p_user_id" integer, "p_route_id" integer, "p_nivo_dozvola" integer) RETURNS boolean
    LANGUAGE "plpgsql"
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


ALTER FUNCTION "public"."grant_route_access"("p_user_id" integer, "p_route_id" integer, "p_nivo_dozvola" integer) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."revoke_access"("user_id" integer, "route_name" character varying) RETURNS boolean
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    DELETE FROM user_routes WHERE user_id = user_id AND route_name = route_name;
    RETURN FOUND;
END;
$$;


ALTER FUNCTION "public"."revoke_access"("user_id" integer, "route_name" character varying) OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."update_formular_polja_updated_at"() RETURNS "trigger"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION "public"."update_formular_polja_updated_at"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."update_formular_updated_at"() RETURNS "trigger"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    NEW.datum_azuriranja = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION "public"."update_formular_updated_at"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."update_global_license_updated_at"() RETURNS "trigger"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION "public"."update_global_license_updated_at"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"() RETURNS "trigger"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"() OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."update_updated_at_column"() RETURNS "trigger"
    LANGUAGE "plpgsql"
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION "public"."update_updated_at_column"() OWNER TO "postgres";

SET default_tablespace = '';

SET default_table_access_method = "heap";


CREATE TABLE IF NOT EXISTS "euk"."kategorija" (
    "kategorija_id" integer NOT NULL,
    "naziv" character varying(255) NOT NULL,
    "skracenica" character varying(10) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE "euk"."kategorija" OWNER TO "postgres";


COMMENT ON TABLE "euk"."kategorija" IS 'Tabela za kategorije predmeta';



CREATE SEQUENCE IF NOT EXISTS "euk"."kategorija_kategorija_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."kategorija_kategorija_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."kategorija_kategorija_id_seq" OWNED BY "euk"."kategorija"."kategorija_id";



CREATE TABLE IF NOT EXISTS "euk"."obrasci_vrste" (
    "id" integer NOT NULL,
    "naziv" character varying(100) NOT NULL,
    "opis" "text",
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "euk"."obrasci_vrste" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "euk"."obrasci_vrste_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."obrasci_vrste_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."obrasci_vrste_id_seq" OWNED BY "euk"."obrasci_vrste"."id";



CREATE TABLE IF NOT EXISTS "euk"."organizaciona_struktura" (
    "id" integer NOT NULL,
    "naziv" character varying(100) NOT NULL,
    "opis" "text",
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "euk"."organizaciona_struktura" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "euk"."organizaciona_struktura_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."organizaciona_struktura_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."organizaciona_struktura_id_seq" OWNED BY "euk"."organizaciona_struktura"."id";



CREATE TABLE IF NOT EXISTS "euk"."predmet" (
    "predmet_id" integer NOT NULL,
    "datum_kreiranja" "date" DEFAULT CURRENT_DATE NOT NULL,
    "naziv_predmeta" character varying(255) NOT NULL,
    "status" character varying(50) NOT NULL,
    "odgovorna_osoba" character varying(255) NOT NULL,
    "prioritet" character varying(20) NOT NULL,
    "rok_za_zavrsetak" "date",
    "kategorija_id" integer NOT NULL,
    "kategorija_skracenica" character varying(10),
    "template_file_path" character varying(500),
    "template_generated_at" timestamp without time zone,
    "template_status" character varying(50) DEFAULT 'pending'::character varying
);


ALTER TABLE "euk"."predmet" OWNER TO "postgres";


COMMENT ON TABLE "euk"."predmet" IS 'Tabela za predmete/slucajeve';



COMMENT ON COLUMN "euk"."predmet"."datum_kreiranja" IS 'Datum kreiranja predmeta - defaultno danasnji datum';



COMMENT ON COLUMN "euk"."predmet"."rok_za_zavrsetak" IS 'Rok za zavrsetak predmeta - moze biti NULL';



CREATE SEQUENCE IF NOT EXISTS "euk"."predmet_predmet_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."predmet_predmet_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."predmet_predmet_id_seq" OWNED BY "euk"."predmet"."predmet_id";



CREATE SEQUENCE IF NOT EXISTS "euk"."ugrozeno_lice_t1_ugrozeno_lice_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."ugrozeno_lice_t1_ugrozeno_lice_id_seq" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "euk"."ugrozeno_lice_t1" (
    "ugrozeno_lice_id" integer DEFAULT "nextval"('"euk"."ugrozeno_lice_t1_ugrozeno_lice_id_seq"'::"regclass") NOT NULL,
    "redni_broj" "text",
    "ime" "text",
    "prezime" "text",
    "jmbg" "text",
    "ptt_broj" "text",
    "grad_opstina" "text",
    "mesto" "text",
    "ulica_i_broj" "text",
    "broj_clanova_domacinstva" integer,
    "osnov_sticanja_statusa" "text",
    "ed_broj_broj_mernog_uredjaja" "text",
    "potrosnja_i_povrsina_combined" "text",
    "iznos_umanjenja_sa_pdv" numeric(12,2),
    "broj_racuna" "text",
    "datum_izdavanja_racuna" "date",
    "datum_trajanja_prava" "date",
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "euk"."ugrozeno_lice_t1" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "euk"."ugrozeno_lice_t1_new_ugrozeno_lice_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."ugrozeno_lice_t1_new_ugrozeno_lice_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."ugrozeno_lice_t1_new_ugrozeno_lice_id_seq" OWNED BY "euk"."ugrozeno_lice_t1"."ugrozeno_lice_id";



CREATE TABLE IF NOT EXISTS "euk"."ugrozeno_lice_t2" (
    "ugrozeno_lice_id" integer NOT NULL,
    "redni_broj" character varying(20) NOT NULL,
    "ime" character varying(100) NOT NULL,
    "prezime" character varying(100) NOT NULL,
    "jmbg" character(13) NOT NULL,
    "ptt_broj" character varying(10),
    "grad_opstina" character varying(100),
    "mesto" character varying(100),
    "ulica_i_broj" character varying(200),
    "ed_broj" character varying(100),
    "pok_vazenja_resenja_o_statusu" character varying(200),
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "euk"."ugrozeno_lice_t2" OWNER TO "postgres";


COMMENT ON TABLE "euk"."ugrozeno_lice_t2" IS 'Tabela za čuvanje podataka o ugroženim licima - verzija T2';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."ugrozeno_lice_id" IS 'Primarni ključ - ID ugroženog lica';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."redni_broj" IS 'Redni broj ugroženog lica';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."ime" IS 'Ime ugroženog lica';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."prezime" IS 'Prezime ugroženog lica';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."jmbg" IS 'JMBG ugroženog lica (13 cifara)';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."ptt_broj" IS 'PTT broj';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."grad_opstina" IS 'Grad/Opština';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."mesto" IS 'Mesto';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."ulica_i_broj" IS 'Ulica i broj';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."ed_broj" IS 'ED broj';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."pok_vazenja_resenja_o_statusu" IS 'Period važenja rešenja o statusu';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."created_at" IS 'Datum i vreme kreiranja zapisa';



COMMENT ON COLUMN "euk"."ugrozeno_lice_t2"."updated_at" IS 'Datum i vreme poslednje izmene zapisa';



CREATE SEQUENCE IF NOT EXISTS "euk"."ugrozeno_lice_t2_ugrozeno_lice_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "euk"."ugrozeno_lice_t2_ugrozeno_lice_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "euk"."ugrozeno_lice_t2_ugrozeno_lice_id_seq" OWNED BY "euk"."ugrozeno_lice_t2"."ugrozeno_lice_id";



CREATE TABLE IF NOT EXISTS "public"."global_license" (
    "id" integer NOT NULL,
    "license_key" character varying(255) NOT NULL,
    "start_date" timestamp without time zone NOT NULL,
    "end_date" timestamp without time zone NOT NULL,
    "is_active" boolean DEFAULT true,
    "notification_sent" boolean DEFAULT false,
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "public"."global_license" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."global_license_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."global_license_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."global_license_id_seq" OWNED BY "public"."global_license"."id";



CREATE TABLE IF NOT EXISTS "public"."user_sessions" (
    "id" integer NOT NULL,
    "user_id" integer,
    "token" character varying(500) NOT NULL,
    "expires_at" timestamp without time zone NOT NULL,
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "public"."user_sessions" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."user_sessions_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."user_sessions_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."user_sessions_id_seq" OWNED BY "public"."user_sessions"."id";



CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" bigint NOT NULL,
    "username" character varying(50) NOT NULL,
    "email" character varying(100) NOT NULL,
    "password_hash" character varying(255) NOT NULL,
    "first_name" character varying(50) NOT NULL,
    "last_name" character varying(50) NOT NULL,
    "role" character varying(20) DEFAULT 'korisnik'::character varying,
    "is_active" boolean DEFAULT true,
    "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "chk_user_role" CHECK ((("role")::"text" = ANY ((ARRAY['admin'::character varying, 'korisnik'::character varying])::"text"[])))
);


ALTER TABLE "public"."users" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."users_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."users_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."users_id_seq" OWNED BY "public"."users"."id";



ALTER TABLE ONLY "euk"."kategorija" ALTER COLUMN "kategorija_id" SET DEFAULT "nextval"('"euk"."kategorija_kategorija_id_seq"'::"regclass");



ALTER TABLE ONLY "euk"."obrasci_vrste" ALTER COLUMN "id" SET DEFAULT "nextval"('"euk"."obrasci_vrste_id_seq"'::"regclass");



ALTER TABLE ONLY "euk"."organizaciona_struktura" ALTER COLUMN "id" SET DEFAULT "nextval"('"euk"."organizaciona_struktura_id_seq"'::"regclass");



ALTER TABLE ONLY "euk"."predmet" ALTER COLUMN "predmet_id" SET DEFAULT "nextval"('"euk"."predmet_predmet_id_seq"'::"regclass");



ALTER TABLE ONLY "euk"."ugrozeno_lice_t2" ALTER COLUMN "ugrozeno_lice_id" SET DEFAULT "nextval"('"euk"."ugrozeno_lice_t2_ugrozeno_lice_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."global_license" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."global_license_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."user_sessions" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."user_sessions_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."users" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."users_id_seq"'::"regclass");



ALTER TABLE ONLY "euk"."kategorija"
    ADD CONSTRAINT "kategorija_pkey" PRIMARY KEY ("kategorija_id");



ALTER TABLE ONLY "euk"."obrasci_vrste"
    ADD CONSTRAINT "obrasci_vrste_naziv_key" UNIQUE ("naziv");



ALTER TABLE ONLY "euk"."obrasci_vrste"
    ADD CONSTRAINT "obrasci_vrste_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "euk"."organizaciona_struktura"
    ADD CONSTRAINT "organizaciona_struktura_naziv_key" UNIQUE ("naziv");



ALTER TABLE ONLY "euk"."organizaciona_struktura"
    ADD CONSTRAINT "organizaciona_struktura_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "euk"."predmet"
    ADD CONSTRAINT "predmet_pkey" PRIMARY KEY ("predmet_id");



ALTER TABLE ONLY "euk"."ugrozeno_lice_t1"
    ADD CONSTRAINT "ugrozeno_lice_t1_new_pkey" PRIMARY KEY ("ugrozeno_lice_id");



ALTER TABLE ONLY "euk"."ugrozeno_lice_t2"
    ADD CONSTRAINT "ugrozeno_lice_t2_jmbg_key" UNIQUE ("jmbg");



ALTER TABLE ONLY "euk"."ugrozeno_lice_t2"
    ADD CONSTRAINT "ugrozeno_lice_t2_pkey" PRIMARY KEY ("ugrozeno_lice_id");



ALTER TABLE ONLY "public"."global_license"
    ADD CONSTRAINT "global_license_license_key_key" UNIQUE ("license_key");



ALTER TABLE ONLY "public"."global_license"
    ADD CONSTRAINT "global_license_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."user_sessions"
    ADD CONSTRAINT "user_sessions_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_email_key" UNIQUE ("email");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_username_key" UNIQUE ("username");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_created_at" ON "euk"."ugrozeno_lice_t1" USING "btree" ("created_at");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_datum_racuna" ON "euk"."ugrozeno_lice_t1" USING "btree" ("datum_izdavanja_racuna");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_datum_racuna_iznos" ON "euk"."ugrozeno_lice_t1" USING "btree" ("datum_izdavanja_racuna", "iznos_umanjenja_sa_pdv");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_ed_broj" ON "euk"."ugrozeno_lice_t1" USING "btree" ("ed_broj_broj_mernog_uredjaja");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_grad_mesto" ON "euk"."ugrozeno_lice_t1" USING "btree" ("grad_opstina", "mesto");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_grad_opstina" ON "euk"."ugrozeno_lice_t1" USING "btree" ("grad_opstina");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_ime_prezime" ON "euk"."ugrozeno_lice_t1" USING "btree" ("ime", "prezime");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_mesto" ON "euk"."ugrozeno_lice_t1" USING "btree" ("mesto");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_osnov_status" ON "euk"."ugrozeno_lice_t1" USING "btree" ("osnov_sticanja_statusa");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_potrosnja_povrsina_combined" ON "euk"."ugrozeno_lice_t1" USING "btree" ("potrosnja_i_povrsina_combined");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_status_ed" ON "euk"."ugrozeno_lice_t1" USING "btree" ("osnov_sticanja_statusa", "ed_broj_broj_mernog_uredjaja");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_umanjenje" ON "euk"."ugrozeno_lice_t1" USING "btree" ("iznos_umanjenja_sa_pdv");



CREATE INDEX "idx_euk_ugrozeno_lice_t1_updated_at" ON "euk"."ugrozeno_lice_t1" USING "btree" ("updated_at");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_created_at" ON "euk"."ugrozeno_lice_t2" USING "btree" ("created_at");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_ed_broj" ON "euk"."ugrozeno_lice_t2" USING "btree" ("ed_broj");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_grad_opstina" ON "euk"."ugrozeno_lice_t2" USING "btree" ("grad_opstina");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_ime_prezime" ON "euk"."ugrozeno_lice_t2" USING "btree" ("ime", "prezime");



CREATE UNIQUE INDEX "idx_euk_ugrozeno_lice_t2_jmbg" ON "euk"."ugrozeno_lice_t2" USING "btree" ("jmbg");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_mesto" ON "euk"."ugrozeno_lice_t2" USING "btree" ("mesto");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_pok_vazenja" ON "euk"."ugrozeno_lice_t2" USING "btree" ("pok_vazenja_resenja_o_statusu");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_ptt_broj" ON "euk"."ugrozeno_lice_t2" USING "btree" ("ptt_broj");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_redni_broj" ON "euk"."ugrozeno_lice_t2" USING "btree" ("redni_broj");



CREATE INDEX "idx_euk_ugrozeno_lice_t2_updated_at" ON "euk"."ugrozeno_lice_t2" USING "btree" ("updated_at");



CREATE INDEX "idx_obrasci_vrste_naziv" ON "euk"."obrasci_vrste" USING "btree" ("naziv");



CREATE INDEX "idx_organizaciona_struktura_naziv" ON "euk"."organizaciona_struktura" USING "btree" ("naziv");



CREATE INDEX "idx_predmet_kategorija_id" ON "euk"."predmet" USING "btree" ("kategorija_id");



CREATE INDEX "idx_predmet_kategorija_skracenica" ON "euk"."predmet" USING "btree" ("kategorija_skracenica");



CREATE INDEX "idx_predmet_template_status" ON "euk"."predmet" USING "btree" ("template_status");



CREATE INDEX "idx_ugrozeno_lice_osnov_status" ON "euk"."ugrozeno_lice_t1" USING "btree" ("osnov_sticanja_statusa");



CREATE INDEX "idx_global_license_active" ON "public"."global_license" USING "btree" ("is_active");



CREATE INDEX "idx_global_license_end_date" ON "public"."global_license" USING "btree" ("end_date");



CREATE INDEX "idx_sessions_token" ON "public"."user_sessions" USING "btree" ("token");



CREATE INDEX "idx_sessions_user_id" ON "public"."user_sessions" USING "btree" ("user_id");



CREATE INDEX "idx_users_email" ON "public"."users" USING "btree" ("email");



CREATE INDEX "idx_users_username" ON "public"."users" USING "btree" ("username");



CREATE OR REPLACE TRIGGER "trigger_update_ugrozeno_lice_t2_updated_at" BEFORE UPDATE ON "euk"."ugrozeno_lice_t2" FOR EACH ROW EXECUTE FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"();



CREATE OR REPLACE TRIGGER "update_global_license_updated_at" BEFORE UPDATE ON "public"."global_license" FOR EACH ROW EXECUTE FUNCTION "public"."update_global_license_updated_at"();



ALTER TABLE ONLY "euk"."predmet"
    ADD CONSTRAINT "fk_predmet_kategorija" FOREIGN KEY ("kategorija_id") REFERENCES "euk"."kategorija"("kategorija_id") ON UPDATE CASCADE ON DELETE RESTRICT;



ALTER TABLE ONLY "public"."user_sessions"
    ADD CONSTRAINT "user_sessions_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;





ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";


GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";

























































































































































GRANT ALL ON FUNCTION "public"."can_access"("user_id" integer, "route_name" character varying) TO "anon";
GRANT ALL ON FUNCTION "public"."can_access"("user_id" integer, "route_name" character varying) TO "authenticated";
GRANT ALL ON FUNCTION "public"."can_access"("user_id" integer, "route_name" character varying) TO "service_role";



GRANT ALL ON FUNCTION "public"."can_user_access_route"("p_user_id" integer, "p_route_id" integer) TO "anon";
GRANT ALL ON FUNCTION "public"."can_user_access_route"("p_user_id" integer, "p_route_id" integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."can_user_access_route"("p_user_id" integer, "p_route_id" integer) TO "service_role";



GRANT ALL ON FUNCTION "public"."check_global_license_validity"() TO "anon";
GRANT ALL ON FUNCTION "public"."check_global_license_validity"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."check_global_license_validity"() TO "service_role";



GRANT ALL ON FUNCTION "public"."get_global_license_expiring_soon"() TO "anon";
GRANT ALL ON FUNCTION "public"."get_global_license_expiring_soon"() TO "anon";
GRANT ALL ON FUNCTION "public"."get_global_license_expiring_soon"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."get_global_license_expiring_soon"() TO "service_role";



GRANT ALL ON FUNCTION "public"."grant_access"("user_id" integer, "route_name" character varying) TO "anon";
GRANT ALL ON FUNCTION "public"."grant_access"("user_id" integer, "route_name" character varying) TO "authenticated";
GRANT ALL ON FUNCTION "public"."grant_access"("user_id" integer, "route_name" character varying) TO "service_role";



GRANT ALL ON FUNCTION "public"."grant_route_access"("p_user_id" integer, "p_route_id" integer, "p_nivo_dozvola" integer) TO "anon";
GRANT ALL ON FUNCTION "public"."grant_route_access"("p_user_id" integer, "p_route_id" integer, "p_nivo_dozvola" integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."grant_route_access"("p_user_id" integer, "p_route_id" integer, "p_nivo_dozvola" integer) TO "service_role";



GRANT ALL ON FUNCTION "public"."revoke_access"("user_id" integer, "route_name" character varying) TO "anon";
GRANT ALL ON FUNCTION "public"."revoke_access"("user_id" integer, "route_name" character varying) TO "authenticated";
GRANT ALL ON FUNCTION "public"."revoke_access"("user_id" integer, "route_name" character varying) TO "service_role";



GRANT ALL ON FUNCTION "public"."update_formular_polja_updated_at"() TO "anon";
GRANT ALL ON FUNCTION "public"."update_formular_polja_updated_at"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."update_formular_polja_updated_at"() TO "service_role";



GRANT ALL ON FUNCTION "public"."update_formular_updated_at"() TO "anon";
GRANT ALL ON FUNCTION "public"."update_formular_updated_at"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."update_formular_updated_at"() TO "service_role";



GRANT ALL ON FUNCTION "public"."update_global_license_updated_at"() TO "anon";
GRANT ALL ON FUNCTION "public"."update_global_license_updated_at"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."update_global_license_updated_at"() TO "service_role";



GRANT ALL ON FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"() TO "anon";
GRANT ALL ON FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."update_ugrozeno_lice_t2_updated_at"() TO "service_role";



GRANT ALL ON FUNCTION "public"."update_updated_at_column"() TO "anon";
GRANT ALL ON FUNCTION "public"."update_updated_at_column"() TO "authenticated";
GRANT ALL ON FUNCTION "public"."update_updated_at_column"() TO "service_role";


















GRANT ALL ON TABLE "public"."global_license" TO "anon";
GRANT ALL ON TABLE "public"."global_license" TO "authenticated";
GRANT ALL ON TABLE "public"."global_license" TO "service_role";



GRANT ALL ON SEQUENCE "public"."global_license_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."global_license_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."global_license_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."user_sessions" TO "anon";
GRANT ALL ON TABLE "public"."user_sessions" TO "authenticated";
GRANT ALL ON TABLE "public"."user_sessions" TO "service_role";



GRANT ALL ON SEQUENCE "public"."user_sessions_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."user_sessions_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."user_sessions_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."users" TO "anon";
GRANT ALL ON TABLE "public"."users" TO "authenticated";
GRANT ALL ON TABLE "public"."users" TO "service_role";



GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."users_id_seq" TO "service_role";









ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "service_role";































RESET ALL;
