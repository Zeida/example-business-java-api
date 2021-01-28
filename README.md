# Example Business API

The assignment requires to build an API project using a technology between the choices we recommend in the next sections, although there is no objection if another JAVA framework is used instead.

The objective of this assignment is to build a small software for the management of goods in a company.  Read through the following business case to capture the data requirements,  build the corresponding class diagram and develop the software solution.

## Business requisites

This company is focused in buying and selling products.

The information needed for each item is:
- **Item code**: Must be a unique value. This should be a numeric value.
- **Description**
- **Price**
- **State**: This can either be ‘Active’ or ‘Discontinued’.
- **Suppliers**: Each item can be supplied by a set of suppliers. Likewise, a supplier can provide more than one item.
- **Price reductions**: An item can have several price reductions associated to it. No control for overlapping dates is required but recommended (look at the section ['Extra points'](#extra-points)).
- **Creation date**
- **Creator (User)**

The information needed for each supplier is:
- **Name**
- **Country**

The information needed for price reductions is:
- **Reduced price**
- **Start date**
- **End date**

## Tasks

Here are the tasks that need to be implemented for the API:

1. Login: The API should have an access window where the user can sign in with its corresponding username and password. This should be implemented using an authentication mechanism.

2. List of items: The API should represent the list of items offered by the company. An option to filter the records by the item’s state should be implemented.
   This list should include the following information:
    - Item code:
    - Description
    - State
    - Price
    - Creation date
    - Creator

3. **Item’s information**: The detailed information of an item selected from the list should be represented in a window. The suppliers and price reductions associated to the item must be included too. It is your own choice, how to represent this information.

4. **Create an item**: An action to create an item must be developed.
    - The only mandatory fields are the *Item code* and *Description*.
    - The value ‘Active’ should be set as the default state.
    - The current date should be set as the creation date.

5. **Edit an item’s data**: *Active* items should be modifiable.
    - All the fields (excluding the item code) should be editable.
    - An option to associate a supplier should be included. The system should verify that the supplier is  not associated to the item already.
    - An option to insert price reductions should be implemented.

6. **Deactivate an item**: Implement an option to deactivate an item by changing its state to *Discontinued*.
    - The user has to specify the reason for the deactivation.
    - The user performing the deactivation has to be registered.

You have to perform these tasks satisfying also the next points:

1. **Java Framework**: develop the solution using either *Spring Boot* or *Play Java* frameworks

2. **Hibernate**: It is required to include *Hibernate ORM*

3. **Models**: Implement the domain models with *JPA* annotations

4. **REST**: Design the API entries for *REST* methods exclusively

5. It is convenient to use libraries and tools provided by the platform selected.

## Extra points

The following tasks can be done optionally:

1. **Admin**: Implement a role for *Adminstrators*, so they can:

  - Manage user information
      - Create users
      - Delete users
      - List users

  - Delete items.

2. Control of overlapping dates when inserting a price reduction. An item cannot have two active price reductions at the same time.

3. SQLs: Build the corresponding SQL queries to obtain the following results

  - List of cheapest item per supplier.
  - List of suppliers associated to items whose price has been reduced.

4. Documentation: Include the following software documentation:
  - Class diagram
  - User manual

## Technical extra assessments

1. Not only the efficiency of the final solution will be assessed but also code guide style, simplicity, usage of standards and design patterns

2. Java 8 and above compliant JDK

3. Asynchronous and non blocking Java and Javascript methods

4. Implementation of unit tests

5. SQL statements optimized for either *PostgreSQL* or *H2 RDBM*

6. Adoption of security APIs (eg *JWT*)

7. Rich GUI through making use of *React/Redux* like frameworks
