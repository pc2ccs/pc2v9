---
title: "Home"
description: "Programing Contest Control (PC^2) system implemented by the PC^2 Development Team for use at the ICPC World Finals and other programming contests"
date: Tuesday, March 16, 2021 4:03:24 PM GMT-07:00
draft: false
weight: 1
icon: fas fa-home
---

{{< figure src="/img/logo-full.png" alt="PC^2 logo" class="float-right" >}}

PC&sup2; (the **P**rogramming **C**ontest **C**ontrol system,
pronounced "P-C-squared" or sometimes just "P-C-Two" for short) is a
software system designed to support programming contest operations in
a variety of computing environments.
PC&sup2; allows contestants
(teams) to submit programs over a network to contest
judges. The judges can recompile the submitted program,
execute it, view the source code and/or execution results, and send a
response back to the team.   The system also supports an "automated judging"
mode where judging is performed by software rather than by human judges.

The system automatically timestamps and archives submitted runs,
maintains and displays current contest standings in a variety of ways,
and allows the judges to retrieve and reexecute archived runs.
It also provides a mechanism for contestants to submit clarification
requests and queries to the judges, and for the judges to reply to
queries and to issue broadcast bulletins to teams.

In addition, PC&sup2; supports contests being held
simultaneously at multiple sites by automatically transmitting
contest standing information between sites and generating a single
contest-wide standings scoreboard at each remote site.

A wide variety of configurable options allow the contest
administrator to tailor the system to specific contest operations.
For example, the number of teams, problems, and languages in the contest;
the scoring method being applied; which problems are handled by which
judges; whether teams are automatically notified of the result of a
submission; and the frequency of automatic scoreboard updates are all
configurable.
There are also mechanisms provided for editing the internal scoring
database, and for recovering from various types of soft and hard errors.
The system is designed to allow teams to use any language
development tool which can be invoked from a command line and generates
an executable file.

PC&sup2;  was developed at California State University,
Sacramento (**<a href="https://www.csus.edu/">CSUS</a>**),


{{< pc2row >}}
    {{< pc2block shortname=PC&sup2; name="PC&sup2;" description="Follow the links below to download PC&sup2;" toolname=pc2 doc=pc2v9AdminGuide page=current >}}
{{</ pc2row >}}

See the <a href="https://github.com/pc2ccs/pc2v9/wiki">PC&sup2; Wiki</a> for more information about PC&sup2;. See the <a href="https://github.com/pc2ccs/pc2v9/wiki/PC2-Developer%27s-Wiki">PC&sup2; Developer's page</a> for information on how you can contribute to PC&sup2;.

{{< lastbuilds >}}

### Disclaimer

The software available through this site is provided free and "as is", with the usual disclaimers: lack of guarantee of suitability
for any particular purpose, no stated or implied responsibility for the results of their use, etc.

In other words, we find this system to be very useful for managing programming contest operations, and we think you will too; but we
 do not guarantee that it will do exactly what you want for your programming contest. All of the code in the public distributions po
sted on our website is under the control of the PC&sup2; Development Team, and we take particular care to insure that there are no inten
tional bad things (malware) in it; however, we make no guarantees at all regarding the code.

For further information see the file [LICENSE.TXT](https://github.com/pc2ccs/pc2v9/blob/master/LICENSE.TXT) in the PC&sup2; distribution.

PC&sup2; is Copyright &copy; by the PC&sup2; Development Team.
