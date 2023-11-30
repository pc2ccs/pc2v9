<!-- Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.  --> 
<!-- $Id: index.xsl 2717 2013-11-09 03:42:38Z boudreat $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:template match="contestStandings">
    <HTML>
        <HEAD>
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
<!--
		<IMG SRC="acm-icpc.gif" align="left" width="130" height="120"/><IMG SRC="acm.gif" align="right" width="100" height="100"/>
-->
		<center>
		    <h2><xsl:value-of select="/contestStandings/standingsHeader/@title"/></h2>
		    <h3></h3>
		    &#160;
		    <!-- XXX probably can remove these with the full title -->
		    <br/>
		    <xsl:value-of select="/contestStandings/standingsHeader/@scoreboardMessage"/>
		    <br/>
		    <br/>
		    <br/>
<!--
		    <xsl:for-each select="/contestStandings/standingsHeader/groupList/group">
		    <xsl:call-template name="groupLink">
		    <xsl:with-param name="group" select="@id"/>
		    </xsl:call-template>
		    </xsl:for-each>
-->
		    <br/>
	    </center>
	</font>
	    <center>
            <TABLE cellspacing="0">
                <tr><th><strong><u>Rank</u></strong></th>
<th><strong><u>Name</u></strong></th>
<th><strong><u>Solved</u></strong></th>
<th><strong><u>Time</u></strong></th>
                <xsl:call-template name="problemTitle"/>
<th>Total att/solv</th></tr>
<tr><td></td><td></td><td></td><td></td>
                <xsl:call-template name="problemColor"/>
</tr>
                <xsl:call-template name="teamStanding"/>
                <xsl:call-template name="summary"/>
            </TABLE>
	</center>
<div class="tail">

<span class="right">
       <A HREF="https://pc2ccs.github.io/">PC^2 Homepage</A><br/>
       CSS by Tomas Cerny and Ray Holder
</span>
Created by <A HREF="https://pc2ccs.github.io/">CSUS PC^2</A> version <xsl:value-of select="/contestStandings/standingsHeader/@systemVersion"/>
<br/>
Last updated
<xsl:value-of select="/contestStandings/standingsHeader/@currentDate"/>
</div>
        </BODY>
    </HTML>
</xsl:template>

        <xsl:template name="summary">
            <xsl:for-each select="standingsHeader">
                <tr>
<td></td>
<td>Submitted/1st Yes/Total Yes</td>
<td></td>
<td></td>
                <xsl:call-template name="problemsummary"/>
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@totalSolved"/></td>
                </tr>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemsummary">
            <xsl:for-each select="/contestStandings/standingsHeader/problem">
<!-- <problemsummary attempts="246" bestSolutionTime="25" id="1" lastsolutionTime="283" numberSolved="81" title="A+ Consanguine Calculations"/> -->
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@attempts"/>/<xsl:if test="@numberSolved &lt; '1'">--</xsl:if>
<xsl:if test="@bestSolutionTime"><xsl:value-of select="@bestSolutionTime"/></xsl:if>/<xsl:value-of select="@numberSolved"/>
</td>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStanding">
            <xsl:for-each select="teamStanding">
		<!-- index is 0 based  header and 1st team seperated by colors -->
		<xsl:choose>
		    <xsl:when test="@index mod 2 = 0">
			<tr class="even">
<td><xsl:value-of select="@rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@solved"/></td>
<td>
<xsl:attribute name="class">right</xsl:attribute>
<xsl:value-of select="@points"/></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
		</tr>
		    </xsl:when>
		    <xsl:otherwise>
		        <tr class="odd">
<td><xsl:value-of select="@rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@solved"/></td>
<td>
<xsl:attribute name="class">right</xsl:attribute>
<xsl:value-of select="@points"/></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td>
<xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
		</tr>
		    </xsl:otherwise>
		</xsl:choose>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemSummaryInfo">
            <xsl:for-each select="problemSummaryInfo">
<!-- <problemSummaryInfo attempts="1" index="1" problemid="1" score="73" solutionTime="73"/> -->
<td>
<xsl:if test="@isSolved = 'true'">
<xsl:attribute name="class">yes</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @isPending = 'true'">
<xsl:attribute name="class">pending</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @attempts &gt; '0' and @isPending = 'false'">
<xsl:attribute name="class">no</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @attempts = '0' and @isPending = 'false'">
<xsl:attribute name="class">center</xsl:attribute>
</xsl:if>
<xsl:value-of select="@attempts"/>/<xsl:if test="@isSolved = 'false'">--</xsl:if>
<xsl:if test="@isSolved = 'true'"><xsl:value-of select="@solutionTime"/></xsl:if>
</td>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemTitle">
            <xsl:for-each select="/contestStandings/standingsHeader/problem">
<th>&#160;&#160;&#160;&#160;<strong><u><xsl:number format="A" value="@id"/></u></strong>&#160;&#160;&#160;&#160;</th>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemColor">
	    <xsl:for-each select="/contestStandings/standingsHeader/colorList/colors[@siteNum = 1]/problem">
		<td><center><xsl:choose><xsl:when test="@color"> <xsl:value-of select="@color"/></xsl:when><xsl:otherwise>Color<xsl:value-of select="@id"/></xsl:otherwise></xsl:choose></center></td>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="groupLink">
        <xsl:param name="group"/>
        <xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@id = $group]">
<a href="group{$group}.html"><xsl:value-of select="@title"/> Per Site Standings
            </a>
        <br/>
            </xsl:for-each>
        </xsl:template>
        
</xsl:stylesheet>
