<!-- Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.  -->
<!-- vim: set tabstop=2 expandtab: -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:variable name="totalTeams" select="/contestStandings/standingsHeader/@totalTeams" />
<xsl:variable name="totalTeamCount" select="count(/contestStandings/teamStanding)" />
<xsl:variable name="totalIncluded" select="count(/contestStandings/standingsHeader/groupList/group[@included = 1])" />
<xsl:variable name="pageTitle">
  <xsl:choose>
    <xsl:when test="$totalTeamCount = $totalTeams">
      <xsl:value-of select="'All Sites'"/>
    </xsl:when>
    <xsl:when test="$totalIncluded = 1">
      <xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@included = 1]">
        <xsl:value-of select="@title" />
  <xsl:choose>
    <xsl:when test="@externalId &gt;= 10000 and not(contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'site'))"> Site Standings</xsl:when>
    <xsl:otherwise> Standings</xsl:otherwise>
  </xsl:choose>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'Any'" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>
<xsl:variable name="groupId">
  <xsl:choose>
    <xsl:when test="$totalTeamCount = $totalTeams">
      <xsl:value-of select="/contestStandings/standingsHeader/groupList/group[0]/@id"/>
    </xsl:when>
    <xsl:when test="$totalIncluded = 1">
      <xsl:value-of select="/contestStandings/standingsHeader/groupList/group[@included = 1]/@id" />
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="/contestStandings/standingsHeader/groupList/group[0]/@id" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>
<xsl:template match="contestStandings">
  <HTML>
    <HEAD>
      <TITLE>
        <xsl:value-of select="/contestStandings/standingsHeader/@title"/>
      </TITLE>
      <link rel="stylesheet" type="text/css" href="standings.css"/>
      <script type="text/javascript">
        <![CDATA[
          function rgbStringToArray(str) {
            // Remove 'rgb(' and ')' and split by comma
            return str
              .replace(/[^\d,]/g, '') // Remove non-digits and non-commas
              .split(',')
              .map(Number);           // Convert each to a number
          }
          function getLuminance(rgb) {
            function channel(c) {
              var v = c / 255;
              return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
            }
            return 0.2126 * channel(rgb[0]) + 0.7152 * channel(rgb[1]) + 0.0722 * channel(rgb[2]);
          }
          function getBestTextColor(rgb) {
            return getLuminance(rgb) > 0.179 ? 'black' : 'white';
          }
          window.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('th[style] a.problem-link-auto').forEach(function(a) {
              var th = a.closest('th');
              if (th && th.style.background) {
                var rgb = rgbStringToArray(th.style.background);
                a.style.color = getBestTextColor(rgb);
              }
            });
          });
        ]]>
      </script>
      <META HTTP-EQUIV="REFRESH" CONTENT="60;"/>
      <META HTTP-EQUIV="EXPIRES" CONTENT="0"/>
      <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE"/>
      <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"/>
    </HEAD>
    <BODY>
      <font face="verdana, arial, helvetica" align="right">
        <center>
          <IMG width="60%" SRC="header.svg" align="center"/>
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
          <h3>
            <xsl:value-of select="$pageTitle" />
          </h3>
          <xsl:if test="$totalTeamCount != $totalTeams">
            <a href="index.html">Full Contest Standings</a>
            <p/>
          </xsl:if>
          <table class="standings-grid-table">
            <xsl:for-each select="/contestStandings/standingsHeader/groupList/group[@teamCount &gt; 0 and $totalTeams != @teamCount]">
              <xsl:sort select="@title" order="ascending" data-type="text"/>
          
          
              <!-- 1. Start a new row for the 1st, 6th, 11th item, etc. -->
              <xsl:if test="position() mod 5 = 1">
                <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
              </xsl:if>
          
              <!-- 2. Display the cell content with the If/Else logic -->
              <td>
                <xsl:choose>
                  <!-- IF: The condition is met, show the active link -->
                  <xsl:when test="$groupId != @id">
                    <a href="index_{@title}.html">
                      <xsl:value-of select="@title"/>
                      <!-- Case-Insensitive Dynamic Text Insertion -->
                      <xsl:choose>
                        <xsl:when test="@externalId &gt;= 10000 and not(contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'site'))"> Site Standings</xsl:when>
                        <xsl:when test="@externalId &lt; 10000 and not(contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'region'))"> Region Standings</xsl:when>
                        <xsl:otherwise> Standings</xsl:otherwise>
                      </xsl:choose>
                    </a>
                  </xsl:when>
                  
                  <!-- ELSE: Highlight the text instead -->
                  <xsl:otherwise>
                    <span style="background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 4px; font-weight: bold; display: inline-block;">
                      <xsl:value-of select="@title"/>
                      <!-- Case-Insensitive Dynamic Text Insertion -->
                      <xsl:choose>
                        <xsl:when test="@externalId &gt;= 10000 and not(contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'site'))"> Site Standings</xsl:when>
                        <xsl:when test="@externalId &lt; 10000 and not(contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'region'))"> Region Standings</xsl:when>                        <xsl:otherwise> Standings</xsl:otherwise>
                      </xsl:choose>
                    </span>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
          
              <!-- 3. Close the row for the 5th, 10th, 15th item, or the absolute last item -->
              <xsl:if test="position() mod 5 = 0 or position() = last()">
                <xsl:if test="position() = last() and position() mod 5 != 0">
                  <xsl:call-template name="FillerCells">
                    <xsl:with-param name="count" select="5 - (position() mod 5)" />
                  </xsl:call-template>
                </xsl:if>
                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
              </xsl:if>
            </xsl:for-each>
          </table>
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
          <tr class="even">
            <td><xsl:attribute name="class">highest</xsl:attribute>Highest Honors</td>
          </tr>
          <tr class="even">
            <td><xsl:attribute name="class">high</xsl:attribute>High Honors</td>
          </tr>
          <tr class="even">
            <td><xsl:attribute name="class">honors</xsl:attribute>Honors</td>
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
          <A HREF="https://pc2ccs.github.io/">PC^2 Homepage</A>
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

<xsl:template name="FillerCells">
  <xsl:param name="count" />
  <xsl:if test="$count &gt; 0">
    <td>&#160;</td>
    <xsl:call-template name="FillerCells">
      <xsl:with-param name="count" select="$count - 1" />
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template name="problemTitle">
    <xsl:for-each select="/contestStandings/standingsHeader/problem">
    <xsl:variable name="i" select="position()" />
    <th>
      <xsl:attribute name="style">background: <xsl:value-of select="@rgb"/></xsl:attribute>
      <a>
        <xsl:attribute name="href">problems/<xsl:number format="A" value="@id"/>.pdf</xsl:attribute>
        <xsl:attribute name="target">_blank</xsl:attribute>
        <xsl:attribute name="class">problem-link-auto</xsl:attribute>
        <xsl:number format="A" value="@id"/>
      </a>
        </th>
    </xsl:for-each>
</xsl:template>
        
</xsl:stylesheet>
