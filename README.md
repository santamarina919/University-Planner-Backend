# University Planner
University Planner is a project aimed at prospective students of any university that would provide them a tool that allows them
to plan how they can obtain their degree in any fashion they would like. This README focuses on the backend implementation which handles 
the logic related to students plan.

## How It is Implemented
The backend of University Planner is implemented by first accepting the requirements of a degree through a specific syntax. The syntax
essentially describes all the relationships between requirements and courses. After this is defined that applications is able 
to construct of graph that represents the relationships. **When a user creates a plan for a degree the application will query the 
associated graph for any action the user takes.** For example, if a user wants to see what courses they can take they graph will look for 
course nodes that have all prerequisites competed. The degree graph can also represent sophisticated relationships between courses and degrees. 
This relationships can be the option to take one of two different courses to fulfill a prerequisite which the application will enforce.


## Technologies Used
The technologies were used to implement this project were Spring, Hibernate and PostgreSQL. Spring was chosen because it offered solutions
to common problems when developing a web application. Hibernate was chosen as it allowed for easier data access into the graph that 
represents all degrees. 

## Deployment
As a learning exercise this application was deployed onto a cloud service. It was deployed using A GCP virtual machine and running an 
Apache Tomcat server. The built java application was copied onto the VM where the Apache server deployed it. 