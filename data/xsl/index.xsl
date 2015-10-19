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
<style>
// based on
// $Id: standings.css 1983 2009-12-09 02:29:11Z boudreat $
table {
	border: 1px solid #ccc; 
	border-bottom: 0;
	width: 52.7em;
	margin-bottom: 2em;
}
body {
   font-family: verdana, arial, tahoma, sans-serif;
}
table th {
	text-align: center;
	background: #247eca;
	color: white;
	padding: 0em; 
	border: outset 2px #eee8aa;
}
table td {
	border-bottom: 1px solid #DDD; 
	padding: .0em .0em .0em .5em;
}
table tr td.rank {
	background: transparent; 
	border: 2px outset #ffffff;
}
table tr.gold td.rank {
	background: #f9d923;
	border: outset 2px #ffd700;
}
table tr.silver td.rank {
	background: Silver;
	border: 2px outset sILVER;
}
table tr.bronze td.rank {
	background: #c08e55; 
	border: outset 2px #c9960c;
}
table td.name {
	padding-left: 2em;
}
table th.name{
	padding-left: 3em; 
}
table tr.even td {
	background: #F7F7F7;
}
table tr td.r10 {
	background: #e9d923;
	border: 2px outset #DCDCDC;
}
table tr td.r9 {
	background: #e1d963;
	border: 2px outset #DCDCDC;
}
table tr td.r8 {
	background: #DDD7AA;
	border: 2px outset #DCDCDC;
}
table tr td.r7 {
	background: #d2d2d2;
	border: 2px outset #DCDCDC;
}
table tr td.r6 {
	background: #DDCDBD;
	border: 2px outset #DCDCDC;
}
table tr td.r5 {
	background: #e6e6e6;
	border: 2px outset #DCDCDC;
}
table tr td.r4 {
	background: #eee;
	border: 2px outset #f3f3f3;
}
table tr td.r3 {
	background: #F7f7f7;
	border: 2px outset #f7f7f7;
}
div.tail {
       font-size: .8em;
       color: #888;
//width: 65.875em;
width: 80%;
 border: 1px solid #ccc;
margin-left: auto;
margin-right: auto;
}
span.right {
     float: right;
}

// troy additions
table tr.odd {
        background-color: #0; color: black;
}
table tr.even {
        background-color: #EEEEFF; color: black;
}
table tr td.yes {
        background-color: #00ff00; color: black;
        text-align: center;
}
table tr td.pending {
        background-color: #ffff00; color: black;
        text-align: center;
}
table tr td.no {
        background-color: #ff0000; color: black;
        text-align: center;
}
table tr td.center {
        text-align: center;
}
table tr td.right {
        text-align: right;
}

</style>
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
		    <!-- hmm, these comment add the ^M to the output -->
		    Scoreboard will be shut down with one hour remaining in contest.
		    <xsl:comment>
		    Standings as of 1 hour to go.  Scoreboard is now frozen.&lt;br/>
		    </xsl:comment>
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
