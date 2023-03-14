# PC^2 Version 9

PC^ 2 (the **P**rogramming **C**ontest **C**ontrol system, pronounced "P-C-squared" or sometimes just "P-C-Two" for short) is a 
software system designed to support programming contest operations in a variety of computing environments including MS Windows, Mac OS/X,
and virtually any flavor of Unix-like platform. 
PC2 allows contestants (teams) to submit programs over a network to contest judges. 
The judges can recompile the submitted program, execute it, view the source code and/or execution results, 
and send a response back to the team. 
The system also supports an "automated judging" mode where judging is performed by software rather than by human judges.

The system automatically timestamps and archives submitted runs, maintains and displays current contest standings in a variety of ways, 
and allows the judges to retrieve and reexecute archived runs. 
It also provides a mechanism for contestants to submit clarification requests and queries to the judges, 
and for the judges to reply to queries and to issue broadcast bulletins to teams. 

In addition, PC2 supports contests being held simultaneously at multiple sites by automatically transmitting contest standing 
information between sites and generating a single contest-wide standings scoreboard at each remote site.

A wide variety of configurable options allow the contest administrator to tailor the system to specific contest operations. 

PC2 was developed at California State University, Sacramento (CSUS), and is Copyright (C) by the PC2 Development Team: John Clevenger,
Doug Lane, Samir Ashoo, and Troy Boudreau.  

# Getting Started  

To install PC2, go to https://pc2ccs.github.io/, download the latest distribution (either the .zip or .tar.gz), and unzip that file in any 
folder on your machine.  That's it -- PC2 is now installed and ready to run.

To learn how to run PC2 and configure it for a contest, go into the "doc" folder at the root of the unzipped PC2 installation,
where you will find a document named "pc2v9AdminGuide.pdf".  Chapter 2 in that doc provides an overview of how to set up PC2 for a contest.

If you are trying to set up PC2 so that contest teams can access it from a browser (rather than having to install PC2 on each team machine),
you will likely also be interested in the PC2 "Web Team Interface" (WTI); Appendix N in that same document provides complete instructions fpr 
setting up and running the WTI.


# Licensing Information
See the file [LICENSE.TXT](./LICENSE.TXT)

# Contributing to PC2
We welcome contributions intended to help improve PC2!  See the file [CONTRIBUTING.MD](./CONTRIBUTING.MD) for further information and
guidelines for contributing.  Substantial additional information can also be found on our GitHub Wiki, in particular 
[here](https://github.com/pc2ccs/pc2v9/wiki) and [here](https://github.com/pc2ccs/pc2v9/wiki/Contents).

# Contact Information
Website:  https://pc2ccs.github.io/  
mailto: pc2@ecs.csus.edu

# How to report bugs or request new features
We use [GitHub Issues](https://guides.github.com/features/issues/)
to track bug reports and feature/enhancement requests.  
Please see [this GitHub article](https://help.github.com/en/articles/creating-an-issue) 
for general information on how to file GitHub Issues.
See the [PC2v9 GitHub Issues page](https://github.com/pc2ccs/pc2v9/issues) to file bug reports or feature/enhancement requests
for PC2.

# Sign up for the announcement list
We maintain a mailing list of people who would like to receive announcements when new releases of PC2 occur, 
or when significant issues with published systems are found. 
Don't worry; we won't flood you with spam -- we only send announcements when there are significant issues or updates that
we think our users will want to know about.  Also, we **never** distribute our mailing list to anyone.

If you are a contest administrator setting up PC2, we recommend joining the PC2 announcement list so that you are notified 
if significant issues which might affect the integrity of your contest are found.

Please see [this page](http://hera.ecs.csus.edu/mailman/listinfo/pc2-announce "Join the Pc2 Mailing list") to join the PC2 mailing list.
