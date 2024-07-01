# File meant to be "source'd" to support generating web pages

#
# Display very curt textual error message
#
Error()
{
    echo "Content-type: text/plain"
    echo ""
    echo ERROR $*
}


Preamble()
{
	echo "Content-type: text/html"
	echo ""
	if test $# -eq 0
	then
		headmsg="Judge"
	else
		headmsg="$*"
	fi
	cat << PREEOF
<html>
<meta http-equiv="Content-type" content="text/html" charset="utf-8" />
<head>
<title>PC&#xb2; $headmsg</title>
<style>
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  border-spacing: 10px;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

td.red {
  border-radius: 25px;
  text-align: center;
  background-color: #f0a0a0;
}
td.green {
  border-radius: 25px;
  text-align: center;
  background-color: #a0f0a0;
}

td.cent {
  text-align: center;
}

td.right {
  text-align: right;
}

th.cent {
  text-align: center;
}

th.right {
  text-align: right;
}

tr:nth-child(even) {
  background-color: #dddddd;
}

img {
  max-width:100%;
  max-height:100%;
  object-fit: contain;
}

.rspecial {
  border: 1px solid #ffffff;
  border-radius: 25px;
  text-align: center;
  background-color: 0xf0a0a0;
  margin: 2px;
}

.judgeicon {
  height: 36px;
  width: auto;
}

</style>
</head>
<body>
PREEOF
}

Header()
{
	# Make sure link to banner page is in a place apache can read
	if test ! -e ../${BANNER_FILE}
	then
		ln -s ${BANNER_IMAGE} ../${BANNER_FILE}
	fi
cat << HDREOF
<center>
<img src="/$BANNER_FILE">
<h1>PC<sup>2</sup> Judging Results</h1>
</center>
<p>
HDREOF
}

HeaderNoBanner()
{
	if test $# -eq 0
	then
		hdrmsg="Judging Results"
	else
		hdrmsg="$*"
	fi
cat << EOF
<center>
<h1>PC<sup>2</sup> $hdrmsg</h1>
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
EOF3
}

EndTable()
{
	cat << EOF4
</table>
<p>
EOF4
}

