# File meant to be "source'd" to support generating web pages
TOOLTIP_UL_COLOR="blue"
TOOLTIP_TEXT_COLOR="white"
TOOLTIP_BG_COLOR="black"

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
PREEOF
}

Styles()
{
	cat << EOFSTYLE
<style>
body {
  font-family: "Arial", "Helvetica", "sans-serif";
}
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

.tooltip {
  position: relative;
  display: inline-block;
  border-bottom: 3px dashed ${TOOLTIP_UL_COLOR};
}

.tooltip .tooltiptext {
  visibility: hidden;
  width: 120px;
  background-color: ${TOOLTIP_BG_COLOR};
  color: ${TOOLTIP_TEXT_COLOR};
  text-align: center;
  border-radius: 6px;
  padding: 5px 0;

  /* Position the tooltip */
  position: absolute;
  z-index: 1;
}

.tooltip:hover .tooltiptext {
  visibility: visible;
}
EOFSTYLE
}

EndStyles()
{
	echo "</style>"
}

StartHTMLDoc()
{
	cat << EOFSHTML
</head>
<body>
EOFSHTML
}

TableSortScripts()
{
	cat << EOFSCR
<script src="/scripts/jquery-3.7.1.slim.min.js"> </script>
<script>
const getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

const comparer = (idx, asc) => (a, b) => ((v1, v2) =>
    v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2, undefined, {numeric: true})
    )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));

// do the work...
document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
    const table = th.closest('table');
    if (th.querySelector('span')){
	let order_icon = th.querySelector('span');
        // Awesome? hack: use the icon as a sort indicator for the column.  we convert
        // it to a URI and look at the UTF-8 encoding. Calm yourself.  I found this code
        // here: https://github.com/VFDouglas/HTML-Order-Table-By-Column/blob/main/index.html
        // and hacked it up further. -- JB
        let order = encodeURI(order_icon.innerHTML).includes('%E2%96%B2') ? 'desc' : 'asc';
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), order === 'asc'))
            .forEach(tr => table.appendChild(tr) );
        if(order === 'desc') {
            // down triangle
            order_icon.innerHTML = "&#x25bc;"
        } else {
            // up triangle
            order_icon.innerHTML = "&#x25b2;"
        }
    }
})));
</script>
EOFSCR
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

