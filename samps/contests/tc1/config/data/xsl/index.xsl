<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:template match="contestStandings">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<TITLE>
<xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</TITLE>
<link rel="stylesheet" type="text/css" href="standings.css"/>
<META HTTP-EQUIV="REFRESH" CONTENT="60;"/>
<META HTTP-EQUIV="EXPIRES" CONTENT="0"/>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE"/>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"/>
</HEAD>
<BODY>
<font face="verdana, arial, helvetica" align="right">
<center>
<IMG  style="width: 1200px; height: 200px;" SRC="pac2022-banner.png" align="center"/>

<h2>
<xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</h2>
<h3></h3>
		    &#160;
		    
<h3>
<a href="index1.html">PacNW Division 1 Standings</a>

<br/>
<br/>
<a href="index2.html">PacNW Division 2 Standings</a>

<br/>
<br/>
<a href="index3.html">Rocky Mountain Standings</a>

<br/>
<br/>
<A HREF="TechNotes.pdf">Technical Notes</A>

</h3>

<br/>
<br/>
<br/>
</center>
</font>
</BODY>
</HTML>
</xsl:template>
</xsl:stylesheet>
