<!-- $Id: groupN.xsl 1964 2009-11-25 03:58:14Z boudreat $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<!-- 
     change group to match the group id 
     copy to new file, ex group2.xsl group3.xsl updating group in each
  -->
<xsl:variable name="group" select="'4'" />
<!--
   Below here does not need to change.
 -->
<xsl:template match="contestStandings">
    <HTML>
        <HEAD>
<TITLE>
	<xsl:for-each select="standingsHeader/groupList/group[@id = $group]">
<xsl:value-of select="@title"/> Full Info - <xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</xsl:for-each>
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
<IMG  style="width: 1200px; height: 200px;" SRC="regional-header-PacNW.png" align="center"/>
		    <h2><xsl:value-of select="/contestStandings/standingsHeader/@title"/></h2>
		    <h3><xsl:value-of select="/contestStandings/standingsHeader/groupList/group[@id = $group]/@title"/> per Site Standings</h3>
		    &#160;
		    <!-- XXX probably can remove these with the full title -->
		    <br/>
		    <!-- hmm, these comment add the ^M to the output -->
		    <xsl:comment>
		    Standings as of 1 hour to go.  Scoreboard is now frozen.&lt;br/>
		    </xsl:comment>
	    </center>
	</font>
	    <center>
<br/>
<br/>
        <a href="index2.html">Goto PacNW Division 2 Standings</a><br/><br/>
        <a href="index1.html">Goto PacNW Division 1 Standings</a><br/><br/>
            <TABLE cellspacing='0'>
                <tr><th><strong><u>Group Rank</u></strong></th><th><strong><u>Name</u></strong></th><th><strong><u>Solved</u></strong></th><th><strong><u>Time</u></strong></th></tr>
                <xsl:call-template name="teamStanding"/>
            </TABLE>
	    </center>
<div class="tail">

<span class="right">
       <A HREF="http://pc2.ecs.csus.edu/">PC^2 Homepage</A><br/>
       CSS by Tomas Cerny and Ray Holder
</span>
Created by <A HREF="http://pc2.ecs.csus.edu/">CSUS PC^2</A> version <xsl:value-of select="/contestStandings/standingsHeader/@systemVersion"/>
<br/>
Last updated
<xsl:value-of select="/contestStandings/standingsHeader/@currentDate"/>
</div>
        </BODY>
    </HTML>
</xsl:template>

        <xsl:template name="teamStanding">
            <xsl:for-each select="teamStanding[@teamGroupId = $group]">
                <tr>
<td><xsl:value-of select="@groupRank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@solved"/></td>
<td>
<xsl:attribute name="class">right</xsl:attribute>
<xsl:value-of select="@points"/></td>
                </tr>
            </xsl:for-each>
        </xsl:template>
</xsl:stylesheet>
