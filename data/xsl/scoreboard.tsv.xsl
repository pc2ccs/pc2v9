<!-- $Id: wf.standings.xsl 2011 2009-12-17 20:11:32Z boudreat $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:variable name="median">
<xsl:value-of select="/contestStandings/standingsHeader/@medianProblemsSolved" />
</xsl:variable>
<!-- this sometimes varies 10, 12, 13 -->
<xsl:variable name="topX" select="'12'" />
<xsl:template match="contestStandings">file_version	1
team	reg	rank	prize	solved	time	last_solved<xsl:for-each select="standingsHeader/problem">
<xsl:text>	</xsl:text><xsl:value-of select="@id"/>
</xsl:for-each>
<xsl:text>
</xsl:text>
<xsl:call-template name="teamStandingTopX">
                    <xsl:with-param name="X" select="$topX"/>
                </xsl:call-template>
   </xsl:template>
	   <xsl:template name="teamStandingTopX">
			   <xsl:param name="X" />
	       <xsl:for-each select="teamStanding[@rank &lt;= $X]">
<xsl:call-template name="exportStanding">
		   <xsl:with-param name="Rank" select="@rank"/>
		   <xsl:with-param name="Solved" select="@solved"/>
   </xsl:call-template>
   <xsl:if test="last() = position()">
   <xsl:variable name="index" select="@index + 1"/>
   <xsl:variable name="rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
<xsl:call-template name="teamStandingRankNumSolved">
		   <xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		   <xsl:with-param name="Solved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
		   </xsl:call-template>
		   </xsl:if>
	       </xsl:for-each>
	   </xsl:template>
	   <xsl:template name="teamStandingRankNumSolved">
			   <xsl:param name="Rank" />
			   <xsl:param name="Solved" />
			   <xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $Solved]">
<xsl:call-template name="exportStanding">
		   <xsl:with-param name="Rank" select="$Rank"/>
		   <xsl:with-param name="Solved" select="$Solved"/>
   </xsl:call-template>
		       </xsl:for-each>
	       <!-- blah, now look thru the unsorted list to get the max index -->
	       <!-- TODO should be cleaner way of doing this using XPath -->
			   <xsl:for-each select="/contestStandings/teamStanding[@rank &gt;= $Rank and @solved = $Solved]">
   <xsl:if test="last() = position()">
   <xsl:variable name="index" select="@index + 1"/>
<xsl:call-template name="teamStandingRankNumSolved">
		   <xsl:with-param name="Rank" select="/contestStandings/teamStanding[@index = $index]/@rank"/>
		   <xsl:with-param name="Solved" select="/contestStandings/teamStanding[@index = $index]/@solved"/>
		   </xsl:call-template>
		   </xsl:if>
	       </xsl:for-each>
	   </xsl:template>
	   <xsl:template name="teamStandingHM">
			   <xsl:param name="Rank" />
			   <xsl:param name="Solved" />
<xsl:variable name="rankHM" select="/contestStandings/teamStanding[@solved &lt; $median]/@rank"/>
	       <xsl:for-each select="teamStanding[@solved &lt; $median]">
<!-- set it on the 1st one -->
<xsl:call-template name="exportStanding">
		   <xsl:with-param name="Rank" select="$rankHM"/>
		   <xsl:with-param name="Solved" select="@solved"/>
   </xsl:call-template>
	       </xsl:for-each>
	   </xsl:template>
<xsl:template name="exportStanding">
<xsl:param name="Rank" />
<xsl:param name="Solved" />
<xsl:value-of select="@teamName"/><xsl:text>	</xsl:text><xsl:value-of select="@teamExternalId"/><xsl:text>	</xsl:text><xsl:value-of select="$Rank"/><xsl:text>	</xsl:text>
<xsl:choose>
<xsl:when test="@rank &lt;= 4">
<xsl:value-of select="'gold'"/>
</xsl:when>
<xsl:when test="@rank &lt;= 8">
<xsl:value-of select="'silver'"/>
</xsl:when>
<xsl:when test="@rank &lt;= $topX">
<xsl:value-of select="'bronze'"/>
</xsl:when>
<xsl:when test="@solved &lt; $median">
<xsl:value-of select="'honorable'"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="''"/>
</xsl:otherwise>
</xsl:choose>
<xsl:text>	</xsl:text><xsl:value-of select="$Solved"/><xsl:text>	</xsl:text><xsl:value-of select="format-number(@points,'0.0')"/><xsl:text>	</xsl:text><xsl:value-of select="format-number(@lastSolved,'0.0')"/>
<xsl:call-template name="problemSummaryInfo"/>
<xsl:text>
</xsl:text>
</xsl:template>
<xsl:template name="problemSummaryInfo">
<xsl:for-each select="problemSummaryInfo">
<!-- <problemSummaryInfo attempts="1" index="1" problemid="1" score="73" solutionTime="73"/> -->
<xsl:text>	</xsl:text><xsl:if test="@attempts = 0">--</xsl:if><xsl:if test="@attempts &gt; 0"><xsl:value-of select="@attempts"/></xsl:if>/<xsl:if test="@isSolved = 'false'">--</xsl:if>
<xsl:if test="@isSolved = 'true'"><xsl:value-of select="@solutionTime"/></xsl:if>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
