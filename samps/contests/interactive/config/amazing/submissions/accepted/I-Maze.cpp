/*
 * ACM GNYR 2016 - I - Amazing
 * Interactive maze solver.
 * Does not use dfs.  Does not use recursion.
 */
#include <stdio.h>
#include <stdlib.h>
#include<iostream>
#include<fstream>
#include<string>
#include<vector>
#include<ctime>
#include<stdarg.h>
#include<map>
using namespace std;

#ifndef WIN32
#define _vsnprintf      vsnprintf
#endif

/*
 * MazeNode.h
 */
enum EMazeDirs {
	MD_RIGHT = 0,
	MD_DOWN,
	MD_LEFT,
	MD_UP,
	NUM_MD
};

enum EMazeType {
	MT_UNKNOWN = 0,
	MT_OPEN,
	MT_WALL,
	MT_LOOP,
	NUM_MT
};

#define	MF_NEW		0x01

class CMazeNode  
{
public:
	CMazeNode(int x, int y);
	virtual ~CMazeNode();
public:
	EMazeDirs FindNextDir(void);
	void SetWall(EMazeDirs e);
	CMazeNode *SetNbor(EMazeDirs e, int nx, int ny);
	CMazeNode *SetNbor(EMazeDirs e, CMazeNode *pNode);
	CMazeNode *GetNode(EMazeDirs e) { return m_pNbor[e]; }
	EMazeType GetType(EMazeDirs e) { return m_nType[e]; }
	int GetInstance(void) { return m_nInst; }
	inline int GetX(void) { return m_nx; }
	inline int GetY(void) { return m_ny; }
public:
	static EMazeDirs sm_eOppDir[NUM_MD];
	static void ResetInstance(void) { sm_nInst = 0; }
	static int sm_nXInc[NUM_MD];
	static int sm_nYInc[NUM_MD];
private:
	int m_nInst;
	int m_nx;
	int m_ny;
	EMazeType m_nType[NUM_MD];
	bool m_bFree[NUM_MD];
	CMazeNode *m_pNbor[NUM_MD];
	EMazeDirs m_eDir;
	unsigned char m_ucFlags;
	static int sm_nInst;
};

/*
 * MazeSolver.h
 */
#pragma warning(disable:4786)

class CMazeSolver  
{
public:
	CMazeSolver();
	virtual ~CMazeSolver();
public:
	bool NextMove(void);
	void MoveResult(bool bWall);
	EMazeDirs GetDir(void) { return m_eLastDir; }
private:
	void DebugPrintf(const char *szFmt, ...);
private:
	CMazeNode *m_pNode;
	CMazeNode *m_pRoot;
	EMazeDirs m_eLastDir;
	vector<int> m_eBread;
	/*
	 * Hash table keeps track of what nodes are at what cells 
	 */
	map<int, CMazeNode *> m_maNodes;
	int m_nx;
	int m_ny;
};

/*
 * MazeNode.cpp
 */
/*
 * Opposite direction
 */
EMazeDirs CMazeNode::sm_eOppDir[] = {
	MD_LEFT,
	MD_UP,
	MD_RIGHT,
	MD_DOWN
};

int CMazeNode::sm_nInst = 0;
int CMazeNode::sm_nXInc[] = {
	1, 0, -1, 0
};
int CMazeNode::sm_nYInc[] = {
	0, 1, 0, -1
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CMazeNode::CMazeNode(int x, int y)
{
	int i;

	for(i = 0; i < NUM_MD; i++){
		m_pNbor[i] = NULL;
		m_nType[i] = MT_UNKNOWN;
		m_bFree[i] = false;
	}
	m_ucFlags = MF_NEW;
	m_eDir = MD_RIGHT;
	m_nx = x;
	m_ny = y;
	m_nInst = sm_nInst++;
}

CMazeNode::~CMazeNode()
{
	int i;

	for(i = 0; i < NUM_MD; i++){
		if(m_bFree[i] == true && m_pNbor[i] != NULL){
			delete m_pNbor[i];
		}
	}
}

EMazeDirs CMazeNode::FindNextDir()
{
	while(m_eDir < NUM_MD){
		if(m_nType[m_eDir] == MT_UNKNOWN){
			/*
			 * First time trying this direction
			 */
			break;
		}
		m_eDir = (EMazeDirs)(m_eDir + 1);
	}
	return(m_eDir);
}

CMazeNode *CMazeNode::SetNbor(EMazeDirs e, CMazeNode *pNode)
{
	m_nType[e] = MT_OPEN;
	m_pNbor[e] = pNode;
	m_pNbor[e]->m_nType[sm_eOppDir[e]] = MT_OPEN;
	m_pNbor[e]->m_pNbor[sm_eOppDir[e]] = this;
	return(pNode);
}

void CMazeNode::SetWall(EMazeDirs e)
{
	m_nType[e] = MT_WALL;
}

CMazeNode *CMazeNode::SetNbor(EMazeDirs e, int nx, int ny)
{
	m_nType[e] = MT_OPEN;
	m_pNbor[e] = new CMazeNode(nx, ny);
	m_bFree[e] = true;
	/*
	 * Remember from whence we came
	 */
	m_pNbor[e]->m_nType[sm_eOppDir[e]] = MT_OPEN;
	m_pNbor[e]->m_pNbor[sm_eOppDir[e]] = this;
	return(m_pNbor[e]);
}

/*
 * MazeSolver.cpp
 */
//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CMazeSolver::CMazeSolver()
{
	m_pNode = NULL;
	m_nx = 0;
	m_ny = 0;
}

CMazeSolver::~CMazeSolver()
{
	if(m_pRoot != NULL){
		delete m_pRoot;
	}
}

bool CMazeSolver::NextMove()
{
	EMazeDirs eDir;
	CMazeNode *p;
	map<int, CMazeNode *>::iterator it;

	if(m_pNode == NULL){
		/*
		 * First move
		 */
		m_pNode = new CMazeNode(m_nx, m_ny);
		if(m_pNode == NULL){
			return(false);
		}
		m_maNodes.insert(pair<unsigned long, CMazeNode *>((m_nx<<16)|m_ny, m_pNode));
		m_pRoot = m_pNode;
	}
	m_eLastDir = m_pNode->FindNextDir();
	if(m_eLastDir == NUM_MD){
		DebugPrintf("Node %d: Tried all directions - %d breadcrumbs left\n",
			m_pNode->GetInstance(), m_eBread.size());
		/*
		 * All done with this node - dead end - no way out
		 * Back off to previous node
		 */
		if(m_eBread.empty() == true){
			DebugPrintf("No way out of maze\n");
			/*
			 * Really, no way out
			 */
			return(false);
		}
		eDir = (EMazeDirs)m_eBread.back();
		m_eBread.pop_back();
		p = m_pNode->GetNode(eDir);
		if(p == NULL){
			DebugPrintf("Can not back up using direction %d from node %d\n", eDir, m_pNode->GetInstance());
			return(false);
		} else {
			DebugPrintf("Backing up to node %d using direction %d\n", p->GetInstance(), eDir);
			m_eLastDir = eDir;
		}
	}
	DebugPrintf("Try direction %d from node %d\n", m_eLastDir, m_pNode->GetInstance());
	return(true);
}

void CMazeSolver::MoveResult(bool bWall)
{
	int ihash, i = m_pNode->GetInstance();
	EMazeType nt;
	map<int, CMazeNode *>::iterator it;

	DebugPrintf("Result for node %d at %d,%d moving %d is %s\n",
		i, m_pNode->GetX(), m_pNode->GetY(), m_eLastDir, bWall ? "WALL" : "OPEN");

	if(bWall == true){
		m_pNode->SetWall(m_eLastDir);
	} else {
		nt = m_pNode->GetType(m_eLastDir);
		if(nt == MT_UNKNOWN){
			/*
			 * Remember how to get back
			 */
			m_eBread.push_back(CMazeNode::sm_eOppDir[m_eLastDir]);
			m_nx += CMazeNode::sm_nXInc[m_eLastDir];
			m_ny += CMazeNode::sm_nYInc[m_eLastDir];
			/*
			 * See if neighbor exists (IE we looped back)
			 */
			ihash = ((m_nx & 0xffff) << 16) | (m_ny & 0xffff);
			DebugPrintf("New location: %d,%d hash=0x%08lx\n", m_nx, m_ny, ihash);
			it = m_maNodes.find(ihash);
			if(it != m_maNodes.end()){
				DebugPrintf("We have already visited node %d,%d, setting as neighbor\n", m_nx, m_ny);
				m_pNode = m_pNode->SetNbor(m_eLastDir, it->second);
			} else {
				m_pNode = m_pNode->SetNbor(m_eLastDir, m_nx, m_ny);
				m_maNodes.insert(pair<unsigned long, CMazeNode *>(ihash, m_pNode));
			}
			DebugPrintf("Saving breadcrumb %d to get back to node %d from new node %d (%d breadcrumbs)\n",
				CMazeNode::sm_eOppDir[m_eLastDir], i, m_pNode->GetInstance(),  m_eBread.size());
			m_eLastDir = MD_RIGHT;
		} else {
			/* Been here before, backing up -- later, check if doing same stupid move again */
			m_pNode = m_pNode->GetNode(m_eLastDir);
			m_nx = m_pNode->GetX();
			m_ny = m_pNode->GetY();
		}
	}
}

void CMazeSolver::DebugPrintf(const char *szFmt, ...)
{
#if 0
	va_list args;
	char szBuf[80];
	va_start(args, szFmt);

	::_vsnprintf(&(szBuf[0]), sizeof(szBuf), szFmt, args);
	//::fprintf(stderr, "%s", &(szBuf[0]));
    FILE *debug = ::fopen("/dev/tty", "w");
	::fprintf(debug, "%s", &(szBuf[0]));
    ::fclose(debug);
#endif
}

/*
 * mazeans.cpp
 */

const char *szDir[NUM_MD] = { "right", "down", "left", "up" };

int main(int argc, char **argv)
{
	CMazeSolver *ps;
	EMazeDirs eDir;
	char szBuf[64];
	
	ps = new CMazeSolver();
	if(ps == NULL){
		return(1);
	}
	setbuf(stdin, NULL);
	setbuf(stdout, NULL);
	/*
	* Keep going until no more mazes
	*/
	for(;;){
		if(ps->NextMove() == true){
			eDir = ps->GetDir();
			fprintf(stderr, "*** SUBMISSION: sending next move: %s\n", szDir[eDir]);
			printf("%s\n", szDir[eDir]);
		} else {
			fprintf(stderr, "*** SUBMISSION: sending no way out\n");
			printf("no way out\n");
			delete ps;
            break;
		}
		if(fgets(&(szBuf[0]), sizeof(szBuf), stdin) == NULL){
			break;
		}
		fprintf(stderr, "*** SUBMISSION %s: %s", szDir[eDir], &(szBuf[0]));
		if(szBuf[0] == 's'){
			/* Solved */
            fprintf(stderr, "*** SUBMISSION IS EXITING\n");
            return 0;
		} else 
		if(szBuf[0] == 'w' && szBuf[1] == 'r'){ // wrong
            return -1;  // bummer, exit
        } else {
			ps->MoveResult(szBuf[0] == 'w' && szBuf[1] == 'a');
		}
	}
	return(0);
}
