<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" indent="no" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:template match="contestStandings">[<xsl:call-template name="teamStanding"/>]</xsl:template>
<xsl:template name="teamStanding"><xsl:for-each select="teamStanding">{"id":"<xsl:value-of select="@teamId"/>","rank":"<xsl:value-of select="@rank"/>","solved":"<xsl:value-of select="@solved"/>","points":"<xsl:value-of select="@points"/>","name":"<xsl:value-of select="@teamName"/>","group":"<xsl:value-of select="@teamGroupName"/>",<xsl:call-template name="problemSummaryInfo"/>}<xsl:if test="position() != last()">,</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="problemSummaryInfo"><xsl:for-each select="problemSummaryInfo[@attempts &gt; '0']">"<xsl:number format="A" value="@index"/>":{"a":"<xsl:value-of select="@attempts"/>",<xsl:if test="@isSolved = 'true'">"t":"<xsl:value-of select="@solutionTime"/>","s":<xsl:call-template name="status"><xsl:with-param name="id" select="@index"/><xsl:with-param name="solutionTime" select="@solutionTime"/></xsl:call-template>
</xsl:if><xsl:if test="@isSolved = 'false'">"s":"tried"</xsl:if>}<xsl:if test="position() != last()">,</xsl:if>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="status">
        <xsl:param name="id" />
        <xsl:param name="solutionTime" />
        <xsl:for-each select="/contestStandings/standingsHeader/problem[@id = $id]">
        <xsl:choose>
<xsl:when test="@bestSolutionTime = $solutionTime">"first"</xsl:when>
<xsl:otherwise>"solved"</xsl:otherwise>
</xsl:choose>
        </xsl:for-each>
        </xsl:template>
</xsl:stylesheet>
