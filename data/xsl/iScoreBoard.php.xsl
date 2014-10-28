<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
       <xsl:template name="problemTitle">
            <xsl:for-each select="/contestStandings/standingsHeader/problem">
				<th><xsl:value-of select="@title"/></th>
            </xsl:for-each>
        </xsl:template>
        <xsl:template name="teamStanding">
            <xsl:for-each select="teamStanding">
                <!-- index is 0 based  header and 1st team seperated by colors -->
                        <tr>
<td><xsl:value-of select="@rank"/></td>
<td><xsl:value-of select="@teamName"/></td>
<td><xsl:value-of select="@solved"/></td>
<td><xsl:value-of select="@points"/></td>
                <xsl:call-template name="problemSummaryInfo"/>
<!-- <teamStanding index="1" solved="8" problemsattempted="8" rank="1" score="1405" teamName="Warsaw University" timefirstsolved="13" timelastsolved="272" totalAttempts="19" userid="84" usersiteid="1"> -->
                </tr>
            </xsl:for-each>
        </xsl:template>
       <xsl:template name="problemSummaryInfo">
            <xsl:for-each select="problemSummaryInfo">
<!-- <problemSummaryInfo attempts="1" index="1" problemid="1" score="73" solutionTime="73"/> -->
<td>
<xsl:if test="@isSolved = 'true'">
<xsl:attribute name="style">text-align:center;background:#58FA58;border-color:white;</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @isPending = 'true'">
<xsl:attribute name="class">pending</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @attempts &gt; '0' and @isPending = 'false'">
<xsl:attribute name="style">text-align:center;background:#FF6666; border-color:white;</xsl:attribute>
</xsl:if>
<xsl:if test="@isSolved = 'false' and @attempts = '0' and @isPending = 'false'">
<xsl:attribute name="style">text-align:center;border-color:white;</xsl:attribute>
</xsl:if>
<xsl:value-of select="@attempts"/>/<xsl:if test="@isSolved = 'false'">--</xsl:if>
<xsl:if test="@isSolved = 'true'"><xsl:value-of select="@solutionTime"/></xsl:if>
</td>
            </xsl:for-each>
        </xsl:template>
<xsl:template match="contestStandings">
<html>
<script src="JQuery/jquery-1.9.1.js"></script>
<script src="JQuery/jquery-ui.js"></script>
<link href="table_sorter/css/theme.default.css" rel="stylesheet"/>
<script type="text/javascript" src="table_sorter/js/jquery.tablesorter.min.js"></script>
<script type="text/javascript" src="table_sorter/js/jquery.tablesorter.widgets.min.js"></script>
<script type="text/javascript">
$(function() {
	$( "#tabs" ).tabs();
	$('table').tablesorter({
			sortList: [[0,0]],
			widgets        : ['zebra', 'columns'],
			usNumberFormat : true,
			sortRestart    : true
	});
});
</script>
<body>
<table style="empty-cells:show;" id="veiwStandingTable" class="tablesorter" border="1px" >
		<thead>
			<tr>
				<th>Rank</th>
		 		<th>Team Name</th>
				<th>Solved</th>
				<th>Time</th>
<xsl:call-template name="problemTitle"/>
			</tr>
		</thead>
		<tbody>
			<xsl:call-template name="teamStanding"/>
		</tbody>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>

