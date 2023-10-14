CREATE TABLE IF NOT EXISTS job(
    id                  SERIAL NOT NULL PRIMARY KEY,
    job_status          VARCHAR(255),
    job_type            VARCHAR(255),
    start_time          timestamp,
    end_time            timestamp
);
CREATE TABLE IF NOT EXISTS job_to_section(
    id                  SERIAL NOT NULL PRIMARY KEY,
    section_id          INTEGER CONSTRAINT fk_job_to_section__section_id
                                            REFERENCES section
                                            ON UPDATE CASCADE ON DELETE CASCADE,
    job_id              INTEGER CONSTRAINT fk_job_to_section__job_id
                                            REFERENCES job
                                            ON UPDATE CASCADE ON DELETE CASCADE
)