INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Bakersfield', '9001', 'Stockdale Highway', 'Bakersfield', '93311-1022');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('ChannelIslands', '1', 'University Drive', 'Camarillo', '93012');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Chico', '400', 'West First Street', 'Chico', '95929-0722');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('DominguezHills', '1000', 'E. Victoria Street', 'Carson',  '90747');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('EastBay', '25800', 'Carlos Bee Boulevard', 'Hayward',  '94542');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Fullerton','800', 'N. State College Blvd',  'Fullerton',  '92834');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('LongBeach', '1250', 'Bellflower Boulevard', 'Long Beach',  '90840');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('MontereyBay', '100', 'Campus Ctr', 'Seaside', '93955-8001');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Northridge', '18111', 'Nordhoff Street', 'Northridge',  '91330-8207');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanBernardino', '5500', 'University Parkway', 'San Bernardino',  '92407');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanMarcos', '333', 'South Twin Oaks Valley Road', 'San Marcos',  '92096-0001');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Maritime', '200', 'Maritime Academy Drive', 'Vallejo',  '94590');
INSERT INTO campus (id,streetNumber  ,streetName,city,postalCode) VALUES ('Humboldt', '1', 'Harpst Street', 'Arcata',  '95521');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Pomona', '3801', 'West Temple Avenue', 'Pomona',  '91768');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanLuisObispo', '1', 'Grand Ave', 'San Luis Obispo',  '93407-005');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('LosAngeles', '5151', 'State University Drive', 'Los Angeles',  '90032');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Fresno', '5241', 'N. Maple Avenue', 'Fresno',  '93740');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Sacramento', '6000', 'J Street','Sacramento',  '95819');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanDiego', '5500', 'Campanile Drive', 'San Diego',  '92182-7455');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanFrancisco', '1600', 'Holloway Avenue', 'San Francisco',  '94132');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('SanJose', '1', 'Washington Sq', 'San Jose',  '95192');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Sonoma', '1801', 'East Cotati Avenue', 'Rohnert Park',  '94928');
INSERT INTO campus (id,streetNumber ,streetName,city,postalCode) VALUES ('Stanislaus', '1', 'University Cir', 'Turlock',  '95382');




INSERT INTO degree (id, rootrequirement_id, name, owningcampus_id) VALUES  ( '8df3c440-3d0d-4dd4-87be-56c1196c6942' ,null,'Computer Science','DominguezHills');
INSERT INTO degree (id, rootrequirement_id, name, owningcampus_id) VALUES  ('eb2c229c-ebe4-47c7-85e8-981f1ff596d4',null,'Gender Studies','DominguezHills');
INSERT INTO requirement (id, owningdegree_id, parentrequirement_id, name, type) VALUES ('a4b3a79f-9cdd-45a8-8c4e-a782ab695101','8df3c440-3d0d-4dd4-87be-56c1196c6942',null,'Core Requirements','AND');

UPDATE degree SET  rootrequirement_id = 'a4b3a79f-9cdd-45a8-8c4e-a782ab695101' WHERE id = '8df3c440-3d0d-4dd4-87be-56c1196c6942';

INSERT INTO course (id,courseid,name,units,rootprerequisite_id,owningcampus_id) values ('3f971570-618a-40d8-9e61-b4f5455de6d3','CSC101','Intro to learning nothing',3,null,'DominguezHills');
INSERT INTO course (id,courseid,name,units,rootprerequisite_id,owningcampus_id) values ('8b60bb7f-ff90-42b6-b349-e02b8b6fe2a7','CSC103','Intro to learning useless facts',3,null,'DominguezHills');

INSERT INTO prerequisite (id, parentprereq_id, type) VALUES ('503ba096-48cb-4836-a47e-42c7eafe3d16',null,'OR');

INSERT INTO course (id,courseid,name,units,rootprerequisite_id,owningcampus_id) values  ('ef89b95f-afb3-414c-8df7-202c62b5f8eb','CSC121','Java programming',5,'503ba096-48cb-4836-a47e-42c7eafe3d16','DominguezHills');

insert into prerequisitecourse (prerequisite_id, childcourses_id) VALUES ('503ba096-48cb-4836-a47e-42c7eafe3d16','3f971570-618a-40d8-9e61-b4f5455de6d3');
insert into prerequisitecourse (prerequisite_id, childcourses_id) VALUES ('503ba096-48cb-4836-a47e-42c7eafe3d16','8b60bb7f-ff90-42b6-b349-e02b8b6fe2a7');

INSERT INTO requirementcourse (requirement_id, leafcourses_id) values ('a4b3a79f-9cdd-45a8-8c4e-a782ab695101','3f971570-618a-40d8-9e61-b4f5455de6d3');
INSERT INTO requirementcourse (requirement_id, leafcourses_id) values ('a4b3a79f-9cdd-45a8-8c4e-a782ab695101','ef89b95f-afb3-414c-8df7-202c62b5f8eb');
