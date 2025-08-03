<!-- Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:variable name="totalTeams" select="/contestStandings/standingsHeader/@totalTeams" />
<xsl:variable name="totalTeamCount" select="count(/contestStandings/teamStanding)" />
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
					<xsl:value-of select="/contestStandings/standingsHeader/@scoreboardMessage"/>
					<br/>
					Generated: <xsl:value-of select="/contestStandings/standingsHeader/@currentDate"/>
					<br/>
					<xsl:choose>
						<xsl:when test="/contestStandings/standingsHeader/@remainingtime = '0:00:00' or starts-with(/contestStandings/standingsHeader/@remainingtime, '-')">
							The contest has ended
						</xsl:when>
						<xsl:otherwise>
							With: <xsl:value-of select="/contestStandings/standingsHeader/@remainingtime"/> Contest Time Remaining
						</xsl:otherwise>
					</xsl:choose>
					<br/>
					<xsl:choose>
						<xsl:when test="$totalTeamCount = $totalTeams">
							<h3>
								<xsl:value-of select="/contestStandings/standingsHeader/@title"/> Standings
							</h3>
						</xsl:when>
						<xsl:otherwise>
							<h3>
								<xsl:value-of select="/contestStandings/teamStanding[1]/@teamGroupName"/> Standings
							</h3>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$totalTeamCount != $totalTeams">
						<a href="index.html">Full Contest Standings</a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
					</xsl:if>
					<xsl:for-each select="/contestStandings/standingsHeader/groupList/group">
						<xsl:if test="@teamCount &gt; '0'">
							<xsl:if test="$totalTeamCount = $totalTeams or /contestStandings/teamStanding[1]/@teamGroupId != @id">
								<a href="index_{@title}.html">
									<xsl:value-of select="@title"/> Standings
								</a>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							</xsl:if>
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
						<th><strong><u>Score</u></strong></th>
						<th><strong><u>Time</u></strong></th>
						<th><strong><u>Solved</u></strong></th>
						<xsl:call-template name="problemTitle"/>
						<th>Total att/solv</th>
					</tr>
					<xsl:call-template name="teamStanding"/>
					<xsl:call-template name="summary"/>
				</TABLE>
			</center>
			<TABLE>
				<tr><th><strong><u>Legend</u></strong></th></tr>
				<xsl:if test="$totalTeamCount = $totalTeams">
					<tr class="even">
						<td><xsl:attribute name="class">gold</xsl:attribute>Gold Medalists</td>
					</tr>
					<tr class="even">
						<td><xsl:attribute name="class">silver</xsl:attribute>Silver Medalists</td>
					</tr>
					<tr class="even">
						<td><xsl:attribute name="class">bronze</xsl:attribute>Bronze Medalists</td>
					</tr>
				</xsl:if>
				<tr>
					<td><xsl:attribute name="class">yes</xsl:attribute>Solved</td>
				</tr>
				<tr>
					<td><xsl:attribute name="class">firstYes</xsl:attribute>First to Solve</td>
				</tr>
				<tr>
					<td><xsl:attribute name="class">pending</xsl:attribute>Pending</td>
				</tr>
				<tr>
					<td><xsl:attribute name="class">no</xsl:attribute>Wrong</td>
				</tr>
			</TABLE>
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
					<td>
						<xsl:if test="@isGold = 'true'">
							<xsl:attribute name="class">gold</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isSilver = 'true'">
							<xsl:attribute name="class">silver</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isBronze = 'true'">
							<xsl:attribute name="class">bronze</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isHighest = 'true'">
							<xsl:if test="not(@isGold) or @isGold = 'false'">
								<xsl:if test="not(@isSilver) or @isSilver = 'false'">
									<xsl:if test="not(@isBronze) or @isBronze = 'false'">
										<xsl:attribute name="class">highest</xsl:attribute>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHigh = 'true'">
							<xsl:if test="not(@isHighest) or @isHighest = 'false'">
								<xsl:if test="not(@isGold) or @isGold = 'false'">
									<xsl:if test="not(@isSilver) or @isSilver = 'false'">
										<xsl:if test="not(@isBronze) or @isBronze = 'false'">
											<xsl:attribute name="class">high</xsl:attribute>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHonors = 'true'">
							<xsl:if test="not(@isHigh) or @isHigh = 'false'">
								<xsl:if test="not(@isHighest) or @isHighest = 'false'">
									<xsl:if test="not(@isGold) or @isGold = 'false'">
										<xsl:if test="not(@isSilver) or @isSilver = 'false'">
											<xsl:if test="not(@isBronze) or @isBronze = 'false'">
												<xsl:attribute name="class">honors</xsl:attribute>
											</xsl:if>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:value-of select="@rank"/>
					</td>
					<td>
						<xsl:if test="@isGold = 'true'">
							<xsl:attribute name="class">gold</xsl:attribute>
							<img src="gold.png" alt="Gold Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isSilver = 'true'">
							<xsl:attribute name="class">silver</xsl:attribute>
							<img src="silver.png" alt="Silver Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isBronze = 'true'">
							<xsl:attribute name="class">bronze</xsl:attribute>
							<img src="bronze.png" alt="Bronze Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isHighest = 'true'">
							<xsl:if test="not(@isGold) or @isGold = 'false'">
								<xsl:if test="not(@isSilver) or @isSilver = 'false'">
									<xsl:if test="not(@isBronze) or @isBronze = 'false'">
										<xsl:attribute name="class">highest</xsl:attribute>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHigh = 'true'">
							<xsl:if test="not(@isHighest) or @isHighest = 'false'">
								<xsl:if test="not(@isGold) or @isGold = 'false'">
									<xsl:if test="not(@isSilver) or @isSilver = 'false'">
										<xsl:if test="not(@isBronze) or @isBronze = 'false'">
											<xsl:attribute name="class">high</xsl:attribute>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHonors = 'true'">
							<xsl:if test="not(@isHigh) or @isHigh = 'false'">
								<xsl:if test="not(@isHighest) or @isHighest = 'false'">
									<xsl:if test="not(@isGold) or @isGold = 'false'">
										<xsl:if test="not(@isSilver) or @isSilver = 'false'">
											<xsl:if test="not(@isBronze) or @isBronze = 'false'">
												<xsl:attribute name="class">honors</xsl:attribute>
											</xsl:if>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:value-of select="@teamName"/>
					</td>
					<td>
						<xsl:attribute name="class">center</xsl:attribute>
						<xsl:value-of select="@score"/>
					</td>
					<td>
						<xsl:attribute name="class">right</xsl:attribute>
						<xsl:value-of select="@lastSolved"/>
					</td>
					<td>
						<xsl:attribute name="class">center</xsl:attribute>
						<xsl:value-of select="@solved"/>
					</td>
					<xsl:call-template name="problemSummaryInfo"/>
					<!-- <teamStanding firstSolved="24" groupRank="1" index="0" isGold="true" lastSolved="294" overallRank="1" points="1471" problemsAttempted="13" rank="1" scoringAdjustment="0" shortSchoolName="U Illinois U-C" solved="12" teamAlias="University of Illinois Urbana-Champaign (not aliasesd)" teamExternalId="1041938" teamGroupExternalId="39415" teamGroupId="8" teamGroupName="ICPC NAC Central Division" teamId="41" teamKey="1TEAM41" teamName="41 University of Illinois Urbana-Champaign" teamSiteId="1" totalAttempts="17"> -->
					<td><xsl:value-of select="@totalAttempts"/>/<xsl:value-of select="@solved"/></td>
				</tr>
		    </xsl:when>
		    <xsl:otherwise>
		        <tr class="odd">
					<td>
						<xsl:if test="@isGold = 'true'">
							<xsl:attribute name="class">gold</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isSilver = 'true'">
							<xsl:attribute name="class">silver</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isBronze = 'true'">
							<xsl:attribute name="class">bronze</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isHighest = 'true'">
							<xsl:if test="not(@isGold) or @isGold = 'false'">
								<xsl:if test="not(@isSilver) or @isSilver = 'false'">
									<xsl:if test="not(@isBronze) or @isBronze = 'false'">
										<xsl:attribute name="class">highest</xsl:attribute>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHigh = 'true'">
							<xsl:if test="not(@isHighest) or @isHighest = 'false'">
								<xsl:if test="not(@isGold) or @isGold = 'false'">
									<xsl:if test="not(@isSilver) or @isSilver = 'false'">
										<xsl:if test="not(@isBronze) or @isBronze = 'false'">
											<xsl:attribute name="class">high</xsl:attribute>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHonors = 'true'">
							<xsl:if test="not(@isHigh) or @isHigh = 'false'">
								<xsl:if test="not(@isHighest) or @isHighest = 'false'">
									<xsl:if test="not(@isGold) or @isGold = 'false'">
										<xsl:if test="not(@isSilver) or @isSilver = 'false'">
											<xsl:if test="not(@isBronze) or @isBronze = 'false'">
												<xsl:attribute name="class">honors</xsl:attribute>
											</xsl:if>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:value-of select="@rank"/>
					</td>
					<td>
						<xsl:if test="@isGold = 'true'">
							<xsl:attribute name="class">gold</xsl:attribute>
							<img src="gold.png" alt="Gold Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isSilver = 'true'">
							<xsl:attribute name="class">silver</xsl:attribute>
							<img src="silver.png" alt="Silver Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isBronze = 'true'">
							<xsl:attribute name="class">bronze</xsl:attribute>
							<img src="bronze.png" alt="Bronze Medal" style="height:1em;vertical-align:middle;"/>
						</xsl:if>
						<xsl:if test="@isHighest = 'true'">
							<xsl:if test="not(@isGold) or @isGold = 'false'">
								<xsl:if test="not(@isSilver) or @isSilver = 'false'">
									<xsl:if test="not(@isBronze) or @isBronze = 'false'">
										<xsl:attribute name="class">highest</xsl:attribute>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHigh = 'true'">
							<xsl:if test="not(@isHighest) or @isHighest = 'false'">
								<xsl:if test="not(@isGold) or @isGold = 'false'">
									<xsl:if test="not(@isSilver) or @isSilver = 'false'">
										<xsl:if test="not(@isBronze) or @isBronze = 'false'">
											<xsl:attribute name="class">high</xsl:attribute>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:if test="@isHonors = 'true'">
							<xsl:if test="not(@isHigh) or @isHigh = 'false'">
								<xsl:if test="not(@isHighest) or @isHighest = 'false'">
									<xsl:if test="not(@isGold) or @isGold = 'false'">
										<xsl:if test="not(@isSilver) or @isSilver = 'false'">
											<xsl:if test="not(@isBronze) or @isBronze = 'false'">
												<xsl:attribute name="class">honors</xsl:attribute>
											</xsl:if>
										</xsl:if>
									</xsl:if>
								</xsl:if>
							</xsl:if>
						</xsl:if>
						<xsl:value-of select="@teamName"/>
					</td>
					<td>
						<xsl:attribute name="class">center</xsl:attribute>
						<xsl:value-of select="@score"/>
					</td>
					<td>
						<xsl:attribute name="class">right</xsl:attribute>
						<xsl:value-of select="@lastSolved"/>
					</td>
					<td>
						<xsl:attribute name="class">center</xsl:attribute>
						<xsl:value-of select="@solved"/>
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
			<xsl:value-of select="@attempts"/>/
			<xsl:choose>
				<xsl:when test="@attempts &gt; '0' and @score = '0.0'">0</xsl:when>
				<xsl:otherwise><xsl:value-of select="@score"/></xsl:otherwise>
			</xsl:choose>
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
				<xsl:if test="@maxScore"> (<xsl:value-of select="@maxScore"/>)</xsl:if>
			</a>
			&#160;&#160;&#160;&#160;<br/>
        </th>
    </xsl:for-each>
</xsl:template>
        
</xsl:stylesheet>