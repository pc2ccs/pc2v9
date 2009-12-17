<!-- $Id$ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>
    <xsl:decimal-format decimal-separator="." grouping-separator="," />
    <xsl:variable name="median">
    <xsl:value-of select="/contestStandings/standingsHeader/@medianProblemsSolved" />
    </xsl:variable>
    <!-- this sometimes varies 10, 12, 13 -->
    <xsl:variable name="topX" select="'12'" />
    <!--
	WARNING: Standings do not exist until the contest is over.
           There is no honorable mention until the contest is over.
           And there are no SuperRegion Champions until the contest
           is over.
      -->
    <!--
        What this XSL does:
    1st print ranks 1-12 (where topX is 12)
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
<xsl:value-of select="/contestStandings/standingsHeader/@title"/>
</TITLE>
        </HEAD>
        <BODY>
<IMG WIDTH="1500" SRC="banner.png"/>
<center>
            <TABLE border="0">
		    <tr><th><strong><u>Rank</u></strong></th><th><strong><u>Name</u></strong></th><th><strong><u>Solved</u></strong></th><th><strong><u>Time</u></strong></th></tr>
		    <!-- start the recursion -->
                <xsl:call-template name="teamStandingTopX">
                    <xsl:with-param name="X" select="$topX"/>
                </xsl:call-template>
            </TABLE>
		<table>
<tr><td>&#160;</td></tr>
<tr><th colspan="3" align="center">Honorable Mention</th></tr>
		<xsl:call-template name="teamStandingHM"/>
	</table>
	<br/>
	<table>
		<tr><th align="left">SuperRegion</th><th align="left">Champion</th></tr>
		<xsl:call-template name="groupChamps"/>
	</table>
</center>
<p>
<xsl:variable name="version">
<xsl:value-of select="/contestStandings/standingsHeader/@systemVersion"/>
</xsl:variable>
Created by <A HREF="http://www.ecs.csus.edu/pc2">CSUS PC^2 <xsl:value-of select="substring($version,0,6)"/></A><br/>
<A HREF="http://www.ecs.csus.edu/pc2/">http://www.ecs.csus.edu/pc2/</A><br/>
Last updated
<xsl:value-of select="/contestStandings/standingsHeader/@currentDate"/>
</p>
        </BODY>
    </HTML>
</xsl:template>
        <xsl:template name="teamStandingTopX">
			<xsl:param name="X" />
            <xsl:for-each select="teamStanding[@rank &lt;= $X]">
                <tr>
			<td><xsl:value-of select="@rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td align="center"><xsl:value-of select="@solved"/></td>
<td align="right"><xsl:value-of select="@points"/></td>
                </tr>
<xsl:if test="last() = position()">
<xsl:variable name="index" select="@index + 1"/>
<xsl:variable name="rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
<xsl:call-template name="teamStandingRankNumSolved">
		<xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		<xsl:with-param name="NumSolved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
                </xsl:call-template>
		</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStandingRankNumSolved">
			<xsl:param name="Rank" />
			<xsl:param name="NumSolved" />
			<xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $NumSolved and @solved &gt;= $median]">
<!--  backed out after world finals 2008 practice
				<xsl:sort select="@teamName"/>
-->
                <tr>
			<td><xsl:value-of select="$Rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td align="center"><xsl:value-of select="@solved"/></td>
<td>&#160;</td>
                </tr>
		    </xsl:for-each>

            <!-- blah, now look thru the unsorted list to get the max index -->
            <!-- TODO should be cleaner way of doing this using XPath -->
			<xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $NumSolved and @solved &gt;= $median]">
<xsl:if test="last() = position()">
<xsl:variable name="index" select="@index + 1"/>
<xsl:call-template name="teamStandingRankNumSolved">
		<xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		<xsl:with-param name="NumSolved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
                </xsl:call-template>
		</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStandingHM">

            <xsl:for-each select="teamStanding[@solved &lt; $median]">
				<xsl:sort select="@teamName"/>
                <tr>
<td><xsl:value-of select="@teamName"/></td>
</tr>
	    </xsl:for-each>
        </xsl:template>
        <xsl:template name="groupChamps">
		<xsl:for-each select="/contestStandings/standingsHeader/groupList/group">
<xsl:call-template name="groupChampsX">
                    <xsl:with-param name="X" select="@id"/>
                </xsl:call-template>

		</xsl:for-each>
	</xsl:template>
        <xsl:template name="groupChampsX">
			<xsl:param name="X" />
			<xsl:for-each select="/contestStandings/teamStanding[@groupRank = 1 and @teamGroupId = $X]">
                <tr>
			<td><xsl:value-of select="@teamGroupName"/></td>
<td><xsl:value-of select="@teamName"/></td>
		</tr>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
