General Project Structure
-------------------------

* Nitro
  - Packages for various Nitro general purpose MC libraries
* Skree
  - Content
    * Packages which represent game content, things that are sever specific, and visible in game
  - Service
    * Packages which define services, services should have one Interface, and have their internal contents packaged inside a subpackage of service called 'internal.<service name>'
  - System
    * The initializition system, this is the part which initializes services, and uses them to create content from the content package