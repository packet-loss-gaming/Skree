## General Project Structure

* Nitro
  - Packages for various Nitro general purpose MC libraries
* Skree
  - Content
    * Packages which represent game content, and things which are server specific. Anything which is used for a game mechanic, but otherwise not meaningful to a service should go here.
  - Service
    * Packages which define services, and their respective inner workings. Services should have one interface, and have their internal contents packaged inside a subpackage of service called 'internal.servicename'.
  - System
    * The initialization system, this is the part which initializes services, and uses them to create content from the content package.

## Building

To build the project you should use the command ````gradle build````.

### Open Boss

Skree depends on an internal Skelril library 'Open Boss', Open Boss is currently not available in any maven remote repository so you must build and install it manually. You can find instructions on how to do this by reading the [Open Boss](http://github.com/Skelril/Open-Boss) project's [README](https://github.com/Skelril/Open-Boss/blob/sponge/README.md).

## Database / jOOQ Schema Generation

The jOOQ Schema is generated via the gradle task ````codegen````. This task should only be run if you have a mariadb server setup locally running the proper database configuration.

Database development which does not require schema modification can be performed by simply using the generated jOOQ files. These are updated as the server's active schema is changed.

There is currently no system for creating the necessarily tables, they must currently be created manually. There is also a lack of documentation on the proper schema outside of the class files. This is an area which should be improved upon over time.
