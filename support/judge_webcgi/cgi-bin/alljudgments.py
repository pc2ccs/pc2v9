#!/usr/bin/python3
import json
import urllib.request
import operator

import HtmlFunctions

TESTING = False

if TESTING :
    CORRECT_PNG = "Correct.png"
    COMPILE_ERROR_PNG = "Warning.png"
    WRONG_PNG = "Wrong.png"
    JUDGE_STYLES="judgestyles.css"
else :
    CORRECT_PNG = "../Correct.png"
    COMPILE_ERROR_PNG = "../Warning.png"
    WRONG_PNG = "../Wrong.png"
    JUDGE_STYLES="/css/judgestyles.css"
    
JUDGMENTS_SCRIPT="getjudgments.sh"

JUDGE_HOSTS = ['pc2-aj1', 'pc2-aj2', 'pc2-aj3' ]

def MyTableStyles() :
    print("<style>")
    print("th {")
    print("    cursor: pointer;")
    print("}")
    print("</style>")
    
def TableHeader():
    print('<tr>');
    print('<th>Run ID <span>&#x25b2;</span></th>');
    print('<th class="cent">Disp</th>');
    print('<th class="cent">Judgment <span>&#x25b2;</span></th>');
    print('<th>Problem <span>&#x25b2;</span></th>');
    print('<th>Team <span>&#x25b2;</span></th>');
    print('<th class="cent">Test Cases <span>&#x25b2;</span></th>');
    print('<th class="cent">Language <span>&#x25b2;</span></th>');
    print('<th class="cent">Judge <span>&#x25b2;</span></th>');
    print('<th>Time Judged <span>&#x25b2;</span></th>');
    print('</tr>');

def getJudgmentsFromHost(host) :
    try :
        with urllib.request.urlopen(f"http://{host}/cgi-bin/{JUDGMENTS_SCRIPT}") as url :
            return(json.load(url))
    except Exception as err :
        if TESTING :
            print(f"getJudgmentsFromHost: Exception: {err}")
    return None

def addDataToTable(data) :
    for j in data :
        print('  <tr>')
        print(f'   <td><a href="{j["href"]}">Run {j["runid"]}</a></td>')
        if j["judgment"] == "AC" :
            icon = CORRECT_PNG
        elif j["judgment"] == "CE" :
            icon = COMPILE_ERROR_PNG
        else :
            icon = WRONG_PNG
        print(f'   <td class="cent"><img class="judgeicon" src="{icon}"></td>')
        print(f'   <td class="cent">{j["judgment"]}</td>')
        print(f'   <td>{j["problem"]}</td>')
        print(f'   <td>team{j["team_number"]}</td>')
        print(f'   <td class="cent">{j["test_info"]}</td>')
        print(f'   <td class="cent">{j["language_id"]}</td>')
        print(f'   <td class="cent">{j["judge"]}</td>')
        print(f'   <td>{j["runtime"]}</td>')
        print('  </tr>')

HtmlFunctions.Preamble(None)
HtmlFunctions.Styles(JUDGE_STYLES)
MyTableStyles()
HtmlFunctions.StartHTMLDoc()
HtmlFunctions.Header()
HtmlFunctions.StartTable()
TableHeader()

alldata=[]
for jh in JUDGE_HOSTS :
    newData = getJudgmentsFromHost(jh)
    if newData != None :
        alldata = alldata + newData

#alldata.sort(key = operator.itemgetter('runid'), reverse=True)
alldata.sort(key = lambda n: int(n['runid']), reverse=True)
addDataToTable(alldata)

HtmlFunctions.EndTable()
HtmlFunctions.Scripts()
HtmlFunctions.Trailer()

