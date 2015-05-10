General Project Structure
-------------------------

* Nitro
  - Packages for various Nitro general purpose MC libraries
* Skree
  - Content
    * Packages which represent game content, and things which are server specific. Anything which is used for a game mechanic, but otherwise not meaningful to a service should go here.
  - Service
    * Packages which define services, and their respective inner workings. Services should have one interface, and have their internal contents packaged inside a subpackage of service called 'internal.service name'.
  - System
    * The initialization system, this is the part which initializes services, and uses them to create content from the content package.