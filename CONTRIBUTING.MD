# Contributing to PC^2

First, thanks for your interest in contributing to the PC^2 project -- we welcome new contributions!  

The following is a set of guidelines for contributing to PC^2, which is hosted in the **PC2CCS** Organization on GitHub. 
Substantial additional information can also be found on our GitHub Wiki, in particular 
[here](https://github.com/pc2ccs/pc2v9/wiki) and [here](https://github.com/pc2ccs/pc2v9/wiki/Contents).

#### Table of Contents

* [Things to know before you get started](#thingstoknow)
  * [Code Organization](#codeorg)
  * [Design Approach](#design)
  * [Coding Style](#style)
* [Providing Feedback](#feedback)
  * [Reporting Bugs](#reportingbugs)
  * [Suggesting New Features](#suggestingfeatures)
* [How can I contribute?](#howtocontribute)
  * [Your First Code Contribution](#firstcodecontribution)
  * [Pull Request Submission Guidelines](#submit-pr)


## <a name="thingstoknow"></a> Things to know before you get started

### <a name="codeorg"></a> Code Organization 

PC^2 is written (mostly) in Java, and is organized as a set of modules.
There are two categories of modules:  **Server**s and **Client**s.
Each contest site runs a single **Server** module (in a multi-site contest each separate site can run its own Server module).

There are various types of Client modules:
  * Contest Administors run an **Admin** module.
  * Each contest Judge (human or automated) runs a **Judge** module.
  * Each contest Team runs either a **Team** module or interfaces with the system via a browser using a **Web Client Team** module.
  * Scoreboards are displayed by a **Scoreboard** module.
  * An **Event Feed** module can be used to generate a [CLICS Event Feed](https://clics.ecs.baylor.edu/index.php?title=Main_Page).

Clients communicate _only_ with the Server module for their site -- never with any other client nor with any server but the one
designated as their "site server" (which is declared via a configuration file named **pc2v9.ini**).

All modules display a User Interface (UI) defined in a corresponding subpackage of the **edu.csus.ecs.pc2.ui** package.  
The various UI packages share common tools and structures defined in packages such as **edu.csus.ecs.pc2.core**, 
**edu.csus.ecs.pc2.api**, **edu.csus.ecs.pc2.services**, and **edu.csus.ecs.pc2.tools**.

Further details on the Code Organization of PC^2 can be found on the
[PC2 Developer's Wiki](https://github.com/pc2ccs/pc2v9/wiki/PC2-Developer%27s-Wiki).

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
than it is to argue about which particular code style is "better".  

For this reason, we have adopted a set of coding guidelines which we expect all contributors to PC^2 to follow.  (Believe us, not even
everyone on the original PC^2 core development team agreed on all the elements of our guidelines.  However, everyone did agree that
it was more important to _have a set of standard guidelines_ than it was to have unanimous agreement on what those guidelines are.)

Please see our [PC^2 Coding Style Guidelines document](./CodingStyle.md) for insight into what is expected of code contributions to PC^2.

## <a name="feedback"></a> Providing Feedback

There are several ways that PC^2 users can provide feedback to help improve the system.

### <a name="reportingbugs"></a> Reporting Bugs

* **Do not open up a GitHub issue if the bug is a security vulnerability
  in PC^2**; instead to refer to our [security policy](./SecurityPolicy.md).

* **Ensure the bug was not already reported** by searching on GitHub under [PC2V9 Issues](https://github.com/pc2ccs/pc2v9/issues).

* If you're unable to find an open issue addressing the problem, [open a new one](https://github.com/pc2ccs/pc2v9/issues/new/choose).
Choose the "Bug Report" template and click "Get Started", then fill in each of the template sections.  Be sure to include a **title and clear description**, **steps to reproduce the bug**, and a description of what you _expected_ to happen (but didn't) when those "steps to reproduce" are followed.  Please also include relevant PC^2 **log** information (either inline in the report, or as an attachment if the relevant log data is long), as well as attachments showing any relevant screenshots, along with a description of the environment in which the bug manifested.

### <a name="suggestingfeatures"></a>  Suggesting New Features

If you have an idea for a new feature, or a suggestion for a better way to handle an existing operation, we'd love to hear about it!
Please feel free to [open a new-feature request](https://github.com/pc2ccs/pc2v9/issues/new/choose) by selecting the **feature request** issue template and filling in the relevant information.

 
## <a name="howtocontribute"></a>  How can I contribute?

If you are interested in doing more than providing feedback -- you want to contribute code to PC^2 -- then keep reading!

### <a name="firstcodecontribution"></a>  Your First Code Contribution

Unsure how to begin contributing to PC^2? You can start by looking through the PC^2 [Issues List](https://github.com/pc2ccs/pc2v9/issues) for
issues labeled `beginner` and `help-wanted`.  

* `Beginner` issues should only require a few lines of code, and a test or two.
* `Help-wanted` issues are items which are likely to be a bit more involved than `beginner` issues.

You can also learn a lot about how to contribute to PC2 by spending some time looking through the [PC2V9 Wiki](https://github.com/pc2ccs/pc2v9/wiki).

Regardless of what issue you choose to work on, the overall process is the same.  To understand the process, you should
start by reading about the [PC2 Development Environment](https://github.com/pc2ccs/pc2v9/wiki/Development-Environment) and then take a look at the [PC2 Development Flow](https://github.com/pc2ccs/pc2v9/wiki/Development-Flow) details. The process can be summarized as:

* Create your own [fork](https://help.github.com/en/articles/fork-a-repo) of the pc2v9 repository.
* [Sync your fork](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/syncing-a-fork) with the upstream (PC2V9) repository.
* Clone your fork onto your own machine (it's usually easier to work on a clone on your own machine,
    using your own IDE/Tools).
* Create a Git Branch (called a _topic branch_ or _feature branch_) for the work, following the _Branch Naming Guidelines_ in the PC2v9 [Development Environment](https://github.com/pc2ccs/pc2v9/wiki/Development-Environment) page.
* Develop the code to implement your bug fix or feature.
* Create automated tests (e.g. Java JUnits) to demonstrate that your code works as intended.
* If the branch work was done on a clone of your fork, push the branch code from your local clone to your PC2v9 fork on GitHub.
  Do NOT merge your topic branch into your master branch.
* Submit a _Pull Request_ following the PR Submission Guidelines (outlined below).

### <a name="submit-pr"></a> Pull Request Submission Guidelines

When you have completed the above steps and come up with a bug fix or enhancement, the final step is to submit a [Pull Request](https://help.github.com/en/articles/about-pull-requests).  Pull Requests (PR's) allow you to submit a request to the PC^2 maintainers to merge your code into the PC^2 master branch and thus make it part of an upcoming public distribution of PC^2.

Prior to submitting a PR, be sure that you have complied with all the preceding steps, including following the [PC^2 Coding Style Guidelines](./CodingStyle.md) and the creation of automated tests (e.g. JUnits) which demonstrate that your code works as intended (failure to adhere to these steps will almost certainly result in rejection of your PR, regardless of how "cool" the new feature might be).

To submit a PR, follow the steps in the GitHub [Creating a Pull Request](https://help.github.com/en/articles/creating-a-pull-request-from-a-fork) guide. Specifically, these steps are:

* Navigate to the original PC2v9 project repository [Pull Requests](https://github.com/pc2ccs/pc2v9/pulls).
* Click the **New Pull Request** button.
* In the **Compare changes** paragraph, click the **Compare across forks** link.
* Insure that the **base:repository** dropdown shows **pc2ccs/pc2v9** and that the **base** (branch) dropdown shows **master**.
* Click the **head repository** dropdown and enter the name of your forked repository.
* Click the **compare** dropdown and enter the name of the branch in your forked repository.
* Click the **Create pull request** button.
* Scroll down in the Pull Request Template **Write** tab area and fill in the following information:
  * Description (describe what changes your Pull Request makes to the PC^2 system)
  * Issue number (enter the Issue number from the [PC2v9 Issues list](https://github.com/pc2ccs/pc2v9/issues); for example,
    enter "Fixes #1234").
  * Environment (describe the environment in which your Pull Request code was created and tested).
* Click the **Create pull request** button.

