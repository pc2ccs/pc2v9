# Functions to support generation of HTML
TESTING = False

if TESTING :
    BANNER_FILE="banner.png"
    TABLE_SORT_JS="tablesort.js"
    JQUERY="jquery-3.7.1.slim.min.js"
else :
    BANNER_FILE="/banner.png"
    TABLE_SORT_JS="/scripts/tablesort.js"
    JQUERY="/scripts/jquery-3.7.1.slim.min.js"
    
def Preamble(hdr) :
    if TESTING == False :
        print("Content-type: text/html")
        print("")
    if hdr == None :
        headmsg = "Judge"
    else :
        headmsg = hdr;
    print("<html>")
    print('<meta http-equiv="Content-type" content="text/html" charset="utf-8" />')
    print("<head>")
    print(f"<title>PC&#xb2; {headmsg}</title>")
   
def Styles(css) :
    print(f'<link rel="stylesheet" href="{css}">')
    
def StartHTMLDoc() :
    print("</head>")
    print("<body>")
   

def Scripts() :
    print(f'<script src="{JQUERY}"> </script>')
    print(f'<script src="{TABLE_SORT_JS}"> </script>')
    
def Header() :
    # Need to link bannerfile if not present
    print("<center>")
    print(f' <img src="{BANNER_FILE}">')
    print(' <h1>PC<sup>2</sup> Judging Results</h1>')
    print("</center>")
    print("<p>")
    
def HeaderNoBanner(header) :
    if header == None :
        hdrmsg = "Judging Results"
    else :
        hdrmsg = header
    print("<center>")
    print(" <h1>PC<sup>2</sup> {hdrmsg}</h1>")
    print("</center>")
    print("<p>")
    
def Trailer() :
    print("</body>")
    print("</html>")
    
def StartTable() :
    print("<p><table>")
    
def EndTable() :
    print("</table><p>")
    


    
