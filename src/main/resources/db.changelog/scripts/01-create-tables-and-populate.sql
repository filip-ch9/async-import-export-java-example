-- Create tables if they don't exist
CREATE TABLE IF NOT EXISTS section (
    id          SERIAL NOT NULL PRIMARY KEY,
    name        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS geological_class (
    id          SERIAL NOT NULL PRIMARY KEY,
    name        VARCHAR(255),
    code        VARCHAR(255),
    section_id  INT,
    FOREIGN KEY (section_id) REFERENCES section(id)
);

-- Insert data into the 'section' table
INSERT INTO section (name)
VALUES
    ('Section 1'),
    ('Section 2'),
    ('Section 3');

-- Insert data into the 'geological_class' table
INSERT INTO geological_class (name, code, section_id)
VALUES
    ('Geo Class 11', 'GC11', 1),
    ('Geo Class 12', 'GC12', 1),
    ('Geo Class 13', 'GC13', 1),
    ('Geo Class 14', 'GC14', 1),
    ('Geo Class 15', 'GC15', 1),
    ('Geo Class 21', 'GC21', 2),
    ('Geo Class 22', 'GC22', 2),
    ('Geo Class 23', 'GC23', 2),
    ('Geo Class 24', 'GC24', 2),
    ('Geo Class 25', 'GC25', 2),
    ('Geo Class 31', 'GC31', 3),
    ('Geo Class 32', 'GC32', 3),
    ('Geo Class 33', 'GC33', 3),
    ('Geo Class 34', 'GC34', 3),
    ('Geo Class 35', 'GC35', 3);
