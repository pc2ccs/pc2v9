# Contributing to PC^2

First, thanks for your interest in contributing to the PC^2 project -- we welcome new contributions!  

The following is a set of guidelines for contributing to PC^2, which is hosted in the **PC2CCS** Organization on GitHub. 
These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

#### Table of Contents

* [Things to know before you get started](#thingstoknow)  
  * [Code Organization](#codeorg)  
  * [Design Approach](#design) 
  * [Coding Style](#style)
* [How can I contribute?](#howtocontribute)
* [Issues and Bugs](#issue)
* [Feature Requests](#feature)
* [Improving Documentation](#docs)
* [Issue Submission Guidelines](#submit)
* [Pull Request Submission Guidelines](#submit-pr)
* [Signing the CLA](#cla)


## <a name="thingstoknow"></a> Things to know before you get started

### <a name="codeorg"></a> Code Organization 

PC^2 is written (mostly) in Java, and is organized as a set of modules.
There are two categories of modules:  **Server**s and **Client**s.
Each contest site runs a single **Server** module (in a multi-site contest each separate site can run its own Server module).

There are various types of Client modules:
  * Contest Administors run an **Admin** module.
  * Each Judge (human or automated) runs a **Judge** module.
  * Each contest team runs either a **Team** module or interfaces with the system via a browser using a **Web Client Team** module.
  * Scoreboards are displayed by a **Scoreboard** module.
  * An **Event Feed** module can be used to generate a [CLICS Event Feed](https://clics.ecs.baylor.edu/index.php?title=Main_Page).

Clients communicate _only_ with the Server module for their site -- never with any other client nor with any server but the one
designated as their "site server" (which is declared via a configuration file named **pc2v9.ini**).

All modules display a User Interface (UI) defined in subpackages of the **edu.csus.ecs.pc2.ui** package.  
The various UI packages share common tools and structures defined in packages such as **edu.csus.ecs.pc2.core**, 
**edu.csus.ecs.pc2.api**, **edu.csus.ecs.pc2.services**, and **edu.csus.ecs.pc2.tools**.

Further details on the Code Organization of PC^2 can be found on the [PC2 Developer's Wiki](need a link here).

### <a name="design"></a> Design Approach

We feel strongly that users benefit when we avoid making changes "just because someone likes _this_ better than _that_".  
In our experience it is very frustrating when a new version of software comes out and an experienced user suddenly can't find their
way around the system because (for example) the UI changed radically.
For this reason we strive very hard to keep the PC^2 user experience "backward-compatible".  That is, when we add new features
to the system we try our best to insure that an experienced user will not find themselves frustrated because the system doesn't 
work (or _appears_ not to work) the same way as it used to.

As an example, all UI classes consist of Java **_JTabbedPanes_** which utilize a PC^2 class named **_JPanePlugin_**.
The most common (and preferable) way to add a new feature is to implement it as a new **JPanePlugin** added as a new tab to an 
existing UI **JTabbedPane**.  This allows users to select the new feature if desired, but retains the previous UI elements in 
the existing tabs -- avoiding user confusion.

### <a name="style"></a>  Coding Style

Everyone has their own coding "style", and there are plenty examples of flame wars over _this_ or _that_ method, format, technique,
approach, etc.  However, without asserting that one particular style element is "better" or "worse" than another, we have found that
it's much more important to have a _consistent_ code style in a project 
than it is to argue about which particular approach is "better".  

For this reason, we have adopted a set of coding guidelines which we expect all contributors to PC^2 to follow.  (Believe us, not even
everyone on the original PC^2 core development team agreed on all the elements of our guidelines.  However, everyone did agree that
it was more important to _have a set of standard guidelines_ than it was to have unanimous agreement on what those guidelines are.)

Please see our [PC^2 Coding Style Guidelines document](../CodingStyle.md) for insight into what is expected of code contributions to PC^2.

## <a name="howtocontribute"></a>  How can I contribute?

### Reporting Bugs

* **Do not open up a GitHub issue if the bug is a security vulnerability
  in PC^2**; instead to refer to our [security policy](https://need.a.url.here).

* **Ensure the bug was not already reported** by searching on GitHub under [Issues](https://github.com/pc2ccs/pc2v9/issues).

* If you're unable to find an open issue addressing the problem, [open a new one](https://github.com/pc2ccs/pc2v9/issues/new).
Be sure to include a **title and clear description**, as much relevant information as possible, and a **code sample** or an **executable test case** 
demonstrating the expected behavior that is not occurring.


### Suggesting Enhancements

<Text here describing how to suggest an enhancement/new feature>
