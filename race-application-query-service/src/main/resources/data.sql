INSERT INTO race (id, name, distance)
VALUES ('9c4b074e-2eaa-436b-9918-9ec9be867a04', 'Croatian marathon', 'MARATHON'),
       ('bc3f3f76-3f29-4148-b75a-f8622d74a3af', 'Spanish half marathon', 'HALF_MARATHON'),
       ('a6e7b27d-9b97-411d-817c-636f33ef2258', 'Portugal 5k', 'FIVE_K'),
       ('8e9176aa-1a06-4927-80bd-eef430c80585', 'Turkey 10k', 'TEN_K');

INSERT INTO race_application (id, first_name, last_name, club, race_id)
VALUES ('56f2d867-b612-4471-9e2a-31ecc94d968f', 'Marie', 'Ann', 'Slick club', '9c4b074e-2eaa-436b-9918-9ec9be867a04'),
       ('bc3f3f76-3f29-4148-b75a-f8622d74a3af', 'Marie', 'Ann', 'Armored Core', 'bc3f3f76-3f29-4148-b75a-f8622d74a3af');