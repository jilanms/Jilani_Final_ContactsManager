-- Create your initial database tables in this file
-- Example
CREATE TABLE contact (
  id INT NOT NULL AUTO_INCREMENT,
  firstName VARCHAR(20) NOT NULL,
  lastName VARCHAR(20) NOT NULL,
  email VARCHAR(50) NOT NULL,
  phone VARCHAR(20),
  address VARCHAR(100),
  companyName VARCHAR(20),
  companyPhone VARCHAR(20),
  notes VARCHAR(255) 
);