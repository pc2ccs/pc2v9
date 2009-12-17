<!-- $Id$ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>
    <xsl:decimal-format decimal-separator="." grouping-separator="," />
    <xsl:variable name="median">
    <xsl:value-of select="/contestStandings/standingsHeader/@medianProblemsSolved" />
    </xsl:variable>
 <!-- 
     change group to match the group id 
     copy to new file, ex group1.xsl group2.xsl group3.xsl updating group
     in each one.
  -->
    <xsl:variable name="group" select="'N'" />
    <!--
    1st print ranks 1-12
        do we print ranks with 0 problems solved?
    2nd print rank 13 where numSolved = rank 12 solved && numSolved >= median
        blank out time, sort,  what rank is next?
    3rd print rank X where numSolved = rank13.numSolved -1 && numSolved >= median
    4th print rank Y where numSolved = rankX.numSolved -1 && numSolved >= median
    repeat until numSolved < median
    -->
<xsl:template match="contestStandings">
    <HTML>
        <HEAD>
<TITLE>
<xsl:for-each select="standingsHeader/groupList/group[@id = $group]">
<xsl:value-of select="@title"/> Scoreboard from <xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</xsl:for-each>
</TITLE>
<link rel="stylesheet" type="text/css" href="standings.css"/>
<META HTTP-EQUIV="REFRESH" CONTENT="60;"/>
<META HTTP-EQUIV="EXPIRES" CONTENT="0"/>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE"/>
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"/>
        </HEAD>
        <BODY>
<IMG WIDTH="1500" SRC="banner.png"/>
                    <xsl:comment>
                    Scoreboard as of 1 hour to go.  Scoreboard is now frozen.&lt;br/>
                    </xsl:comment>
<center>
<h2><xsl:for-each select="standingsHeader/groupList/group[@id = $group]">
<xsl:value-of select="@title"/> Scoreboard from <xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</xsl:for-each>
</h2>
	<a href="http://scoreboard/">Back to full Scoreboard</a>
                    <br/>

            <TABLE cellspacing="0">
		    <tr><th><strong><u>SuperRegion</u></strong></th><th><strong><u>Name</u></strong></th><th><strong><u>Solved</u></strong></th><th><strong><u>Time</u></strong></th>
                <xsl:call-template name="problemTitle"/>
<th>Total att/solv</th></tr>
<tr><th><strong><u>Rank</u></strong></th><td></td><td></td><td></td>
                <xsl:call-template name="problemColor"/>
</tr>
            <xsl:for-each select="teamStanding[@teamGroupId = $group]">
                <tr>
<xsl:if test="@groupRank mod 2 != '0'">
<xsl:attribute name="bgcolor">#eeeeff</xsl:attribute>
</xsl:if>
			<td><xsl:value-of select="@groupRank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td align="center"><xsl:value-of select="@solved"/></td>
<td align="right"><xsl:value-of select="@points"/></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
               </tr>
            </xsl:for-each>
<!--
                <xsl:call-template name="summary"/>
-->
            </TABLE>
</center>
            <!--  no honorable mention during the contest
		<table>
<tr><td>&#160;</td></tr>
<tr><th colspan="3" align="center">Honorable Mention</th></tr>
		<xsl:call-template name="teamStandingHM"/>
	</table>
	-->
<div class="tail">
<span class="right">
<A HREF="http://www.ecs.csus.edu/pc2/">PC^2 Homepage</A><br/>
CSS by Tomas Cerny and Ray Holder
</span>
<xsl:variable name="version">
<xsl:value-of select="/contestStandings/standingsHeader/@systemVersion"/>
</xsl:variable>
Created by <A HREF="http://www.ecs.csus.edu/pc2">CSUS PC^2 <xsl:value-of select="substring($version,0,6)"/></A><br/>
<A HREF="http://www.ecs.csus.edu/pc2/">http://www.ecs.csus.edu/pc2/</A><br/>
Last updated
<xsl:value-of select="/contestStandings/standingsHeader/@currentDate"/>
</div>
        </BODY>
    </HTML>
</xsl:template>
        <xsl:template name="teamStandingTopX">
			<xsl:param name="X" />
            <xsl:for-each select="teamStanding[@rank &lt;= $X and @teamGroupId = $group]">
                <tr>
<xsl:if test="@rank mod 2 = '0'">
<xsl:attribute name="bgcolor">#eeeeff</xsl:attribute>
</xsl:if>
			<td><xsl:value-of select="@rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td align="center"><xsl:value-of select="@solved"/></td>
<td align="right"><xsl:value-of select="@points"/></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
                </tr>
<xsl:if test="last() = position()">
<xsl:variable name="index" select="@index + 1"/>
<xsl:variable name="rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
<xsl:call-template name="teamStandingRankNumSolved">
		<xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		<xsl:with-param name="NumSolved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
                    <xsl:with-param name="Color" select="'0'"/>
                </xsl:call-template>
		</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStandingRankNumSolved">
			<xsl:param name="Rank" />
			<xsl:param name="NumSolved" />
			<xsl:param name="Color" />
			<xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $NumSolved and @teamGroupId = $group]">
<!-- backed out after Finals 2008 practice
				<xsl:sort select="@teamName"/>
-->
               <xsl:choose>
                   <xsl:when test="@index mod 2 = 0">
                       <tr class="even">
			<td><xsl:value-of select="$Rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@solved"/>
</td>
<td>
<xsl:attribute name="class">right</xsl:attribute>
<xsl:value-of select="@points"/>
</td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
                </tr>
</xsl:when>
<xsl:otherwise>
<tr class="odd">
			<td><xsl:value-of select="$Rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td>
<xsl:attribute name="class">center</xsl:attribute>
<xsl:value-of select="@solved"/>
</td>
<td>
<xsl:attribute name="class">right</xsl:attribute>
<xsl:value-of select="@points"/>
</td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
                </tr>
</xsl:otherwise>
</xsl:choose>
		    </xsl:for-each>

            <!-- blah, now look thru the unsorted list to get the max index -->
            <!-- TODO should be cleaner way of doing this using XPath -->
			<xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $NumSolved and @teamGroupId = $group]">
<!--  XXX rank them thru the end
			<xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $NumSolved and @solved &gt;= $median]">
-->
<xsl:if test="last() = position()">
<xsl:variable name="index" select="@index + 1"/>
<xsl:call-template name="teamStandingRankNumSolved">
		<xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		<xsl:with-param name="NumSolved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
                <xsl:with-param name="Color" select="$Color + 1"/>
                </xsl:call-template>
		</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStandingHM">

            <xsl:for-each select="teamStanding[@solved &lt; $median]">
				<xsl:sort select="@teamName"/>
                <tr>
<td><xsl:value-of select="@teamName"/></td><td></td><td></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
</tr>
	    </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemSummaryInfo">
            <xsl:for-each select="problemSummaryInfo">
<!-- <problemSummaryInfo attempts="1" index="1" problemid="1" score="73" solutionTime="73"/> -->
<td align="center">
<xsl:if test="@isSolved = 'true'">
<xsl:attribute name="bgcolor">#00ff00</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @isPending = 'true'">
<xsl:attribute name="bgcolor">#ffff00</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @attempts &gt; '0' and @isPending = 'false'">
<xsl:attribute name="bgcolor">#ff0000</xsl:attribute>
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
<xsl:value-of select="@attempts"/>/<xsl:if test="@numberSolved &lt; '1'">--</xsl:if>
<xsl:if test="@bestSolutionTime"><xsl:value-of select="@bestSolutionTime"/></xsl:if>/<xsl:value-of select="@numberSolved"/>
</td>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="groupLink">
        <xsl:param name="group"/>
        <xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@id = $group]">
<a href="group{$group}.html"><xsl:value-of select="@title"/> SuperRegion Scoreboard
            </a>
        <br/>
            </xsl:for-each>
        </xsl:template>
</xsl:stylesheet>
