#!/bin/bash
#
# Display very curt textual error message
#
Error()
{
    echo "Content-type: text/plain"
    echo ""
    echo ERROR $*
    echo $now Error: $* >> $LOGFILE
}

Preamble()
{
	echo "Content-type: text/html"
	echo ""
}

Header()
{
cat << EOF
<html>
<meta http-equiv="Content-type" content="text/html" charset="utf-8" />
<head>
<title>PC&#xb2; Judge 1</title>
<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}

</style
</head>
<body>
<center>
<h1>PC<sup>2</sup> Judging Results for Judge 1</h1>
</center>
<p>
EOF
}

Trailer()
{
cat << EOF2
</body>
</html>
EOF2
}

StartTable()
{
	cat << EOF3
<p>
<table>
<tr>
<th>Run ID</th><th>Time Judged</th>
EOF3
}

EndTable()
{
	cat << EOF4
</table>
<p>
EOF4
}

TableRow()
{
	dir="$1"
	runid=${dir#../Run}
	runtime=`stat -c '%y' $dir`
	echo '<tr><td><a href="showrun.sh?run='$runid'">'"Run $runid</a></td><td>$runtime</td></tr>"
}

Preamble
Header

# Parse query string into dictionary args
sIFS="$IFS"
IFS='=&'
declare -a parm
parm=($QUERY_STRING)
IFS=$sIFS
declare -A args
for ((i=0; i<${#parm[@]}; i+=2))
do
    args[${parm[i]}]=${parm[i+1]}
    echo "${parm[i]} = ${args[${parm[i]}]}<br>"
done
# Done parsing

echo '<P>The run is '${args["run"]} in problem directory ${args["probdir"]}.  The execute folder is: ${args["dir"]}
Trailer
exit 0
