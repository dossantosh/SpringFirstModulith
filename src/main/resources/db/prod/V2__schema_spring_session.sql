-- Spring Session (PROD ONLY)

CREATE TABLE public.spring_session (
    primary_id character(36) NOT NULL,
    session_id character(36) NOT NULL,
    creation_time bigint NOT NULL,
    last_access_time bigint NOT NULL,
    max_inactive_interval integer NOT NULL,
    expiry_time bigint NOT NULL,
    principal_name character varying(100)
);

CREATE TABLE public.spring_session_attributes (
    session_primary_id character(36) NOT NULL,
    attribute_name character varying(200) NOT NULL,
    attribute_bytes bytea NOT NULL
);

ALTER TABLE ONLY public.spring_session
    ADD CONSTRAINT spring_session_pk PRIMARY KEY (primary_id);

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name);

CREATE UNIQUE INDEX spring_session_ix1 ON public.spring_session USING btree (session_id);
CREATE INDEX spring_session_ix2 ON public.spring_session USING btree (expiry_time);
CREATE INDEX spring_session_ix3 ON public.spring_session USING btree (principal_name);

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_fk
    FOREIGN KEY (session_primary_id)
    REFERENCES public.spring_session(primary_id)
    ON DELETE CASCADE;
