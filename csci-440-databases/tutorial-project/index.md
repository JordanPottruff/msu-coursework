# Understanding SQL Data Types & Constraints
![data-types-graphic](./images/data-types.png)

*Image courtesy of PostgreSQL Tutorial*
### Jordan Pottruff, Olexandr Matveyev, Ahmed Naji, Gage Oneill  


Difficulty: `Easy`  
Prerequisites: `Basic understanding of SQL syntax`

The purpose of this tutorial is to explore the different data types and constraints available in SQL and analyze how they can be used to design more effective database schema. We will look at the types of data and constraints that are found in nearly all SQL dialects/implementations. We will also explore the specific data types provided by some of the more popular dialects (SQLite, MySQL, SQLServer, and MSAccess). By the end of the tutorial, you should be comfortable in determining data types and constraints when working with SQL database schemas. 

Knowing how to apply constraints and specify data types prevents invalid queries from causing unforseen problems. While constraint and type checking can make a schema more complicated, it is often easier and less error-prone than doing it at the application layer. Therefore, applying the information from this tutorial will allow you to find problems quicker and easier.

## Contents
1. [**Common SQL Data Types**](#1-common-sql-data-types)
2. [**SQL Data Types by Dialect**](#2-sql-data-types-by-dialect)
3. [**SQL Constraints**](#3-sql-constraints)
4. [**Example Problems**](#4-example-problems)

# 1 Common SQL Data Types

While the specifics about data types largely depends on the SQL dialect (e.g. SQLite, MySQL, etc) being used, nearly all dialects share the following general types:  

1. **Integer** - a signed or unsigned numbers that do not require decimal/fraction representations.
    * _Memory:_ a standard integer uses around 4 bytes, but dialects often offer additional types that provide 1-8 bytes.
	* _Examples:_ 1, -301, 523032, etc.
2. **Real** - a signed or unsigned numbers that can be expressed as floating point values, i.e. decimals.
    * _Memory:_ a standard real will use around 4 bytes but additional types could provide up to 8 bytes or more.
    * _Examples:_ 1.135, -5.2, 10, etc.
3. **Character** - a number, character, or symbol that is generally found in ASCII or unicode.
    * _Memory:_ ASCII characters use around 1 byte, while unicode characters will use about 2 bytes.
	* _Examples:_ 'a', '9', '+', etc.
4. **Text** - a string of characters that can be used to represent combinationss of ASCII/unicode values.
    * _Memory:_ requires 1-2 bytes per character (see above), amount of characters is generally capped.
	* _Examples:_ "Hello world!", "<h1>hello!</h1>", "5", etc.
5. **BLOB** - a Binary Large Object, i.e. a collection of binary data.
    * _Memory:_ depends on data being stored and is generally capped at some max amount by the DBMS.
	* _Examples:_ images, audio, etc.
6. **Date/Time** - a date or time entity that stores information representing a point in time.
    * _Memory:_ within the range of a few bytes depending on the implementation.
	* _Examples:_ `TIMESTAMP` in MySQL or `date` in SQL Server
    
Data types are added after the name of columns inside the schema. For example:

```sql
CREATE TABLE Company(
    businessID Integer,
    businessName Text,
    --etc....
);
```

The above data types represent broad categories of types shared by most dialects. Between these dialects, the exact name, memory, and
even semantics can be slightly different. Next we will explore a few of the more popular dialects to see some of these differences.

# 2 SQL Data Types by Dialect

The following table represents a brief overview of the most common storage classes and its type keywords found in major dialects for each category listed in 
section 1.

| Dialect    | Integers                     | Reals                  | Characters      | Texts                             | Binary (BLOB)       | Date/Time                             | Other                           |
|------------|------------------------------|------------------------|-----------------|-----------------------------------|---------------------|---------------------------------------|---------------------------------|
| _SQLite_   | INT/INTEGER, TINYINT, BIGINT | REAL, DOUBLE, FLOAT    | CHARACTER       | TEXT, VARCHAR, NCHAR              | BLOB                | TEXT, INT, REAL is used for DATE/TIME | N/A                             |
| _MySQL_    | INT, TINYINT, BIGINT         | FLOAT, DOUBLE, DECIMAL | CHAR            | TEXT, TINYTEXT, VARCHAR, LONGTEXT | BLOB                | DATE, DATETIME, TIMESTAMP, TIME, YEAR | ENUM                            |
| _SQL Server_| int, tinyint, bigint        | float, real, decimal   | char            | text, ntext, varchar, nchar       | binary              | DATETIME, DATE, TIME, TIMESTAMP       | xml, money                      |
| _MS Access_| Integer, Long                | Single, Double         | N/A             | Text, Memo                        | Ole Object          | DATE/TIME                             | Hyperlink, AutoNumber, Currency |

Note that some of the keywords found in different dialects require additional information. For example, `TEXT` or `text` often requires an
argument that designates the max amount of characters allowed. To find more about this and other important factors when writing in one of
these dialects, always read the documentation such as that linked in our sources section.

# 3 SQL Constraints

Constraints provide additional limits on values that can't be expressed just by a data type. When a constraint is violated by an action 
on the database, the action is cancelled and some sort of error or exception is generally thrown by the system. Like types, constraints 
are defined in the schema of a database. There are a variety of constraints that are defined by SQL:

### (1) NOT NULL

Placing a NOT NULL constraint on a column enforces that any value in that column can never be NULL. Designating columns as NOT NULL ensures 
that necessary data cannot be omitted from the database when inserting or updating entries. However, this constraint may create unnecessary 
burdens when the column is not necessarily required.

To enforce the NOT NULL constraint, simply put the keyword after the data type of the column as so:

```sql
CREATE  TABLE Persons (
	FirstName text NOT NULL,
	LastName text NOT NULL
);
```

### (2) UNIQUE

The UNIQUE constraint ensures that no two values within a single column are the same. This is useful for preventing repeated values in 
non-primary key columns. If the NON NULL constraint is not used on a UNIQUE designated column, then only one entry can be NULL in that column. 
UNIQUE is added inline with the corresponding column like NOT NULL: 

```sql
CREATE TABLE Persons (
	ID int,
	SSN int UNIQUE
);
```

### (3) PRIMARY KEY

The PRIMARY KEY constraint designates a set of columns as the primary key of the table. In essence, it acts as a combination of the NOT NULL 
and UNIQUE constraints. If a single column is acting as the primary key, it can be described inline like the following:

```sql
CREATE TABLE Persons (
	ID int PRIMARY KEY,
	age int NOT NULL
);
```

If multiple columns make up the primary key, it should be described outside of the column like so:

```sql
CREATE TABLE Persons (
	FirstName text,
	LastName text,
	PRIMARY KEY (FirstName, LastName)
);
```

### (4) FOREIGN KEY

The FOREIGN KEY constraint means that a column is a foreign key, i.e. it points to the primary key of a row in the child table. While operations 
such as JOIN do not have to be done on foreign keys, constraining them with FOREIGN KEY prevents invalid data from being inserted into the column. Consider the 
following example of a database that has employees and persons:

```sql
CREATE TABLE Persons (
	ID int PRIMARY KEY,
	Name text
);

CREATE TABLE Employee (
	EID int,
	Hours int,
	FOREIGN KEY (EID) REFERENCES Persons(ID)
);
```

In the above example, any time an Employee entry is added or updated the EID value is verified to match with a corresponding ID in the Persons table. If no corresponding 
ID is found, then the action is aborted and an error state is returned. Note that the exact syntax of the constraint depends heavily upon the 
implementation of SQL.

### (5) CHECK

If a more advanced constraint is needed, verifying it can be done using CHECK. The CHECK constraint is followed by a SQL statement evaluating to a 
boolean value. An error occurs when an action causes the statement to return false, and the action is then aborted. Here is an example that constrains 
the integer data type to an even smaller range:

```sql  
CREATE TABLE Student (
	GPA real,
	CHECK (GPA >= 0 AND GPA <= 4)
);
```

Note that CHECK constraints can involve comparisons across columns and can take advantage of query operations that evaluate to true/false. Also note 
that the exact syntax depends greatly on the implementation of SQL.

### (6) DEFAULT

The DEFAULT constraint gives a column a default value if no other value is specified when inserting/updating. One advantage of DEFAULT is that it 
may be a less demanding method of eliminating NULL values compared to the NOT NULL constraint. Another advantage is that system values can be assigned 
to a column when an entry is created. For example, the default date for an entry can be when it is created (written in MySQL):

```sql
CREATE TABLE PizzaOrders (
	ID int PRIMARY KEY,
	OrderDate date DEFAULT GETDATE()
);
```

### Overview

While the word "constraint" often can have a negative connotation, in the context of database design they are a powerful tool that allow developers to 
prevent hard-to-solve problems by catching invalid data the moment it tries to enter the system. The more constraints that can be added to values
without interferring with the objective of the database means far less headaches in the future.

# 4 Example Problems

Below are a few example problems that help demonstrate the ideas covered in the previous sections. Answers can be found further down.

1. What does BLOB stand for and what kind of data can it store?
2. How many bytes is a typically sized `Real` value (single precision)?
3. Out of the common data types provided, which one would best be used for amounts of money in financial data?
4. Show how to change the following BarCustomer table to ensure that customers at the bar are over 21: `CREATE TABLE BarCustomer(SSN int PRIMARY KEY, name text, age int);`
5. What two constraints are essentially being applied when `PRIMARY KEY` is added to a column?
6. If you want to disallow missing values from a table, what constraint would you apply to the relevant columns?
7. What is the difference between a `PRIMARY KEY` and a `FOREIGN KEY`?
8. Give an example of an appropriate situation to use a `DEFAULT` constraint.

**Answers**

1. Binary Large Object; it stores binary data/any data.
2. 8 bytes.
3. Out of the common types in our tutorial, REAL would be an intuitive choice. However, floating point representations of money are often prone to rounding issues that can be harmful if precision is important. So in MySQL for example, the DECIMAL data type may be ideal as you can specify precision directly. 
4. `CREATE TABLE BarCustomer(SSN int PRIMARY KEY, name text, age int, CHECK (age >= 21));`
5. `UNIQUE` and `NOT NULL`.
6. `NOT NULL`
7. A primary key designates a column or a set of columns as the primary key, while the foreign key points from its table to the primary key of another.
8. Depends, but examples include: needing to avoid null values without enforcing NOT NULL, entries needing same starting value, value is a timestamp at creation, etc. 


# References

If you'd like to learn more about data types and constraints or get more practice with the concepts discussed above, check out one of the links below!

* [SQL Constraints](https://www.tutorialspoint.com/sql/sql-constraints.htm) by TutorialsPoint
* [Sqlite Data Types](https://www.sqlite.org/datatype3.html) on sqlite.org
* [MySQL Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html) on mysql.com
* [SQLServer Data Types](https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql?view=sql-server-2017) on docs.microsoft.com
* [General SQL Types](https://www.w3schools.com/sql/sql_datatypes.asp) by W3 Schools
* [Memory usage of data types in SQLWays](https://doc.ispirer.com/sqlways/Output/SQLWays-1-196.html) by Ispirer
* [Date & Time Types in SQLite](http://www.sqlitetutorial.net/sqlite-tutorial/sqlite-date/) by SQLiteTutorial

