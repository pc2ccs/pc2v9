<!-- $Id: results.xsl 190 2011-05-13 00:58:11Z boudreat $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:variable name="topX" select="'12'" /> <!-- XXX per CCS standard this needs to be passed in as a number of extra bronzes eg 12 + B -->
<xsl:variable name="median">
<xsl:value-of select="/contestStandings/standingsHeader/@medianProblemsSolved" />
</xsl:variable>
<xsl:template match="contestStandings"> <!-- this is version 1 of results.tsv -->
<xsl:text>results	1
</xsl:text>
<xsl:call-template name="exportStandingsMedal">
        <xsl:with-param name="A" select="1"/>
        <xsl:with-param name="B" select="4"/>
        <xsl:with-param name="award" select="'gold'"/>
    </xsl:call-template>
<xsl:call-template name="exportStandingsMedal">
        <xsl:with-param name="A" select="5"/>
        <xsl:with-param name="B" select="8"/>
        <xsl:with-param name="award" select="'silver'"/>
    </xsl:call-template>
<xsl:call-template name="exportStandingsMedal">
        <xsl:with-param name="A" select="9"/>
        <xsl:with-param name="B" select="$topX"/>
        <xsl:with-param name="award" select="'bronze'"/>
    </xsl:call-template>
<xsl:call-template name="exportStandingsRanked">
        <xsl:with-param name="A" select="$topX+1"/>
        <xsl:with-param name="median" select="$median"/>
        <xsl:with-param name="award" select="'ranked'"/>
    </xsl:call-template>
<xsl:call-template name="exportStandingsHonorable">
        <xsl:with-param name="median" select="$median"/>
        <xsl:with-param name="award" select="'honorable'"/>
    </xsl:call-template>
</xsl:template> <!-- contestStandings -->
<xsl:template name="groupname">
    <xsl:param name="group" />
<xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@id = $group]">
<xsl:text>&#9;</xsl:text><xsl:value-of select="@title"/>
</xsl:for-each>
</xsl:template>
<xsl:template name="exportStanding">
    <xsl:param name="award" />
<xsl:value-of select="@teamExternalId"/><xsl:text>&#9;</xsl:text><xsl:value-of select="@rank"/><xsl:text>&#9;</xsl:text><xsl:value-of select="$award"/><xsl:text>&#9;</xsl:text><xsl:value-of select="@solved"/><xsl:text>&#9;</xsl:text><xsl:value-of select="@points"/><xsl:text>&#9;</xsl:text><xsl:value-of select="@lastSolved"/>
<xsl:if test="@groupRank = '1'">
<xsl:call-template name="groupname">
            <xsl:with-param name="group" select="@teamGroupId"/>
        </xsl:call-template>
</xsl:if>
<xsl:text>
</xsl:text>
</xsl:template> <!-- exportStanding -->
<xsl:template name="exportStandingsMedal">
    <xsl:param name="A" />
    <xsl:param name="B" />
    <xsl:param name="award" />
<xsl:for-each select="teamStanding[@rank &gt;= $A and @rank &lt;= $B]">
<xsl:call-template name="exportStanding">
            <xsl:with-param name="award" select="$award"/>
        </xsl:call-template>
    </xsl:for-each>
</xsl:template> <!-- exportStandingMedal -->
<xsl:template name="exportStandingsRanked">
    <xsl:param name="A" />
    <xsl:param name="median" />
    <xsl:param name="award" />
    <xsl:for-each select="teamStanding[@rank &gt;= $A and @solved &gt;= $median]">
<xsl:call-template name="exportStanding">
            <xsl:with-param name="award" select="$award"/>
        </xsl:call-template>
    </xsl:for-each>
</xsl:template> <!-- exportStandingRanked -->
<xsl:template name="exportStandingsHonorable">
    <xsl:param name="median" />
    <xsl:param name="award" />
    <xsl:for-each select="teamStanding[@solved &lt; $median]">
<xsl:call-template name="exportStanding">
            <xsl:with-param name="award" select="$award"/>
        </xsl:call-template>
    </xsl:for-each>
</xsl:template> <!-- exportStandingHonorable -->
</xsl:stylesheet>
