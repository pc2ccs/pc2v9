<!-- Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:variable name="divStop" select="10"/>
<xsl:variable name="divStart" select="7"/>
<xsl:variable name="totalTeams" select="52" />
<xsl:variable name="teamCount" select="count(/contestStandings/teamStanding)" />
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
				<center>
					<IMG width="60%" SRC="regional-header.png" align="center"/>
					<h2>
						<xsl:value-of select="/contestStandings/standingsHeader/@title"/>
					</h2>
					&#160;
					<!-- XXX probably can remove these with the full title -->
					<br/>
					<table style="width:100%">
						<tr>
							<td class="remaining">
								<h2>Remaining: <xsl:value-of select="/contestStandings/standingsHeader/@remainingtime"/></h2>
							</td>
							<td class="freeze">
								<xsl:value-of select="/contestStandings/standingsHeader/@scoreboardMessage"/>
							</td>
							<td class="elapsed">
								<h2>Elapsed: <xsl:value-of select="/contestStandings/standingsHeader/@elapsedtime"/></h2>
							</td>
						</tr>
					</table>
					<br/>
					<xsl:choose>
						<xsl:when test="$teamCount = $totalTeams">
							<h3>NAC 2025 Contest Standings</h3>
						</xsl:when>
						<xsl:otherwise>
							<h3>
								<xsl:value-of select="/contestStandings/teamStanding[1]/@teamGroupName"/> Standings
							</h3>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$teamCount != $totalTeams">
						<a href="index.html">Full Contest Standings</a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
					</xsl:if>
					<xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@id &gt;= $divStart and @id &lt;= $divStop]">
						<xsl:if test="$teamCount = $totalTeams or /contestStandings/teamStanding[1]/@teamGroupId != @id">
							<a href="index_{@title}.html">
								<xsl:value-of select="@title"/> Standings
							</a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						</xsl:if>
					</xsl:for-each>
					<br/>
					<br/>
				</center>
			</font>
			<center>
				<TABLE>
					<tr>
						<th><strong><u>Rank</u></strong></th>
						<th><strong><u>Name</u></strong></th>
						<th><strong><u>Solved</u></strong></th>
						<th><strong><u>Time</u></strong></th>
						<xsl:call-template name="problemTitle"/>
						<th>Total att/solv</th>
					</tr>
					<!-- <tr>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<xsl:call-template name="problemColor"/>
					</tr> -->
					<xsl:call-template name="teamStanding"/>
					<xsl:call-template name="summary"/>
				</TABLE>
			</center>
			<div class="tail">
				<span class="right">
					<A HREF="https://pc2ccs.github.io/">PC^2 Homepage</A><br/>
					CSS by Tomas Cerny and Ray Holder
				</span>
				Created by <A HREF="https://pc2ccs.github.io/">CSUS PC^2</A> version 
				<xsl:value-of select="/contestStandings/standingsHeader/@systemVersion"/>
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
		<!-- <problem attempts="66" bestSolutionTime="21" color="orange" id="1" lastSolutionTime="286" numberSolved="44" rgb="#C14A17" title="A Totient Quotient"/> -->
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
						<xsl:value-of select="@solved"/>
					</td>
					<td>
						<xsl:attribute name="class">right</xsl:attribute>
						<xsl:value-of select="@points"/>
					</td>
					<xsl:call-template name="problemSummaryInfo"/>
					<!-- <teamStanding firstSolved="24" groupRank="1" index="0" isGold="true" lastSolved="294" overallRank="1" points="1471" problemsAttempted="13" rank="1" scoringAdjustment="0" shortSchoolName="U Illinois U-C" solved="12" teamAlias="University of Illinois Urbana-Champaign (not aliasesd)" teamExternalId="1041938" teamGroupExternalId="39415" teamGroupId="8" teamGroupName="ICPC NAC Central Division" teamId="41" teamKey="1TEAM41" teamName="41 University of Illinois Urbana-Champaign" teamSiteId="1" totalAttempts="17"> -->
					<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
				</tr>
		    </xsl:when>
		    <xsl:otherwise>
		        <tr class="odd">
					<td><xsl:value-of select="@rank"/></td>
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
					<td>
						<xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/>
					</td>
				</tr>
		    </xsl:otherwise>
		</xsl:choose>
    </xsl:for-each>
</xsl:template>

<xsl:template name="problemSummaryInfo">
    <xsl:for-each select="problemSummaryInfo">
		<!-- <problemSummaryInfo attempts="1" fts="false" index="1" isPending="false" isSolved="true" points="73" problemId="atotientquotient\-\-3468153913115378318" shortName="atotientquotient" solutionTime="73"/> -->
		<td>
			<xsl:if test="@isSolved = 'true' and @fts = 'true'">
				<xsl:attribute name="class">firstYes</xsl:attribute>
			</xsl:if>
			<xsl:if test="@isSolved = 'true' and @fts = 'false'">
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
		<xsl:variable name="i" select="position()" />
		<th>
			<xsl:attribute name="style">background: <xsl:value-of select="@rgb"/></xsl:attribute>
			&#160;&#160;&#160;&#160;
			<a>
				<xsl:attribute name="href">problems/<xsl:number format="A" value="@id"/>.pdf</xsl:attribute>
				<xsl:attribute name="target">_blank</xsl:attribute>
				<xsl:number format="A" value="@id"/>
			</a>
			&#160;&#160;&#160;&#160;<br/>
        </th>
    </xsl:for-each>
</xsl:template>

<!-- <xsl:template name="problemColor">
	<xsl:for-each select="/contestStandings/standingsHeader/colorList/colors[@siteNum = 1]/problem">
		<td>
			<center>
				<xsl:choose>
					<xsl:when test="@colorName">
						<xsl:value-of select="@colorName"/>
					</xsl:when>
					<xsl:otherwise>
						Color<xsl:value-of select="@letter"/>
					</xsl:otherwise>
				</xsl:choose>
			</center>
		</td>
    </xsl:for-each>
</xsl:template> -->

<xsl:template name="groupLink">
    <xsl:param name="group"/>
    <xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@title = $group]">
		<a href="group_{$group}.html"><xsl:value-of select="@title"/> Per Site Standings</a>
        <br/>
    </xsl:for-each>
</xsl:template>
        
</xsl:stylesheet>