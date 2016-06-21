/*-----------------------------------------*/
/* Wileman's C solution to Winning Windows */
/* 2015 ACM ICPC Finals                    */
/*-----------------------------------------*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAXLINE 80
#define MAXVAL 1000000000

#define OPEN_CMD 1
#define CLOSE_CMD 2
#define RESIZE_CMD 3
#define MOVE_CMD 4

#define min(a,b) ((a < b) ? a : b)
#define max(a,b) ((a > b) ? a : b)

int lineno;				/* input line number (to debug data) */
int cmdno;				/* command number: 1, 2, ... */
int xmax, ymax;				/* window dimensions */
int nwin;				/* # of open windows */
struct w {
    int cmdorg;				/* OPEN command number */
    int pos[2];				/* upper left corner (x,y) */
    int dim[2];				/* window dimensions (width, height) */
    struct w *prev;			/* ptr to previous window */
    struct w *next;			/* ptr to next window */
} *wlist, *tail;

void move2(struct w *p, int mvdist[2]);

/*------------------------------------------------------------*/
/* Get the next command, or return an end of file indication. */
/* Commands are named as specified above.                     */
/* The integer arguments for the command (up to 4) are        */
/* returned in the elements of the array argument, and the    */
/* command type (1..4) or EOF is returned by the function.    */
/* Syntax errors are not acceptable; the program terminates.  */
/* We assume there is a command on each input line.           */
/* We assume no input line is longer than 80 characters.      */
/* We assume the first line (xmax and ymax) is already read.  */
/*------------------------------------------------------------*/
int get_command(FILE *f, int *args)
{
    int cmd;
    int nargs;
    int i, n;
    char line[MAXLINE+1];
    char *p, *s, *ndptr;
    long lval;

    /*------------------------------------------------*/
    /* Get a line, checking for error or end of file. */
    /*------------------------------------------------*/
again:
    if (fgets(line,MAXLINE,f) == NULL)
	return EOF;
    /*-------------------------------------------*/
    /* Verify the last character stored in '\n'. */
    /* Otherwise the line is too long.           */
    /*-------------------------------------------*/
    n = strlen(line);
    if (n > 0 && line[n-1] != '\n') {
	fprintf(stderr,"ERROR: line %d is too long.\n", lineno);
	exit(1);
    }
    /*----------------------------------------------*/
    /* Extract the command name and validate it.    */
    /* If legal, set expected number of parameters. */
    /*----------------------------------------------*/
    p = line;
    s = strtok(p," \n");
    if (s == NULL) {		/* empty line - ignore it */
	lineno++;
	goto again;
    }
    if (!strcmp(s,"OPEN")) {
	cmd = OPEN_CMD;
	nargs = 4;
    } else if (!strcmp(s,"CLOSE")) {
	cmd = CLOSE_CMD;
	nargs = 2;
    } else if (!strcmp(s,"RESIZE")) {
	cmd = RESIZE_CMD;
	nargs = 4;
    } else if (!strcmp(s,"MOVE")) {
	cmd = MOVE_CMD;
	nargs = 4;
    } else {
	fprintf(stderr,"ERROR: bad command on line %d\n", lineno);
	exit(1);
    }
    /*-----------------------------------------------------*/
    /* Extract the appropriate number of parameter values. */
    /*-----------------------------------------------------*/
    for(i=0;i<nargs;i++) {
	s = strtok(NULL," \n");
	if (s == NULL) {
	    fprintf(stderr,"ERROR: missing value on line %d\n", lineno);
	    exit(1);
	}
	lval = strtol(s,&ndptr,10);
	if (*ndptr != '\0') {
	    fprintf(stderr,"ERROR: bad value on line %d\n", lineno);
	    exit(1);
	}
	args[i] = (int)lval;
    }
    /*------------------------------------------------------------------*/
    /* Verify that at least one of args[2] amd args[3] is 0 for a MOVE. */
    /*------------------------------------------------------------------*/
    if (cmd == MOVE_CMD) {
	if (args[2] != 0 && args[3] != 0) {
	    fprintf(stderr,"ERROR: parameters 3 and 4 on line %d"
		" are both non-zero.\n", lineno);
	    exit(1);
	}
    }
    /*--------------------------------------------*/
    /* Check for anything else on the input line. */
    /*--------------------------------------------*/
    s = strtok(NULL," \n");
    if (s != NULL) {
	fprintf(stderr,"ERROR: trailing trash on line %d\n", lineno);
	exit(1);
    }
    lineno++;
    return cmd;
}

/*-------------------------------------------------------------*/
/* Return a string pointer to a command name given its number. */
/*-------------------------------------------------------------*/
char *cmd_name(int cmd)
{
    switch(cmd) {
	case 1: return "OPEN";
	case 2: return "CLOSE";
	case 3: return "RESIZE";
	case 4: return "MOVE";
	default: return "Bah, humbug!\n";
    }
}

/*--------------------------------------------*/
/* Display window does not fit error message. */
/*--------------------------------------------*/
void err_no_fit(int cmd)
{
    printf("Command %d: %s - window does not fit\n", cmdno, cmd_name(cmd));
}

/*---------------------------------------*/
/* Display no such window error message. */
/*---------------------------------------*/
void err_no_win(int cmd)
{
    printf("Command %d: %s - no window at given position\n",
	cmdno, cmd_name(cmd));
}

void err_move_less(int ax, int dx)
{
    printf("Command %d: MOVE - moved %d instead of %d\n",
	cmdno, abs(ax), abs(dx));
}

/*------------------------------------------------------------*/
/* Predicate: does the range amin..amax intersect bmin..bmax? */
/*------------------------------------------------------------*/
int overlap(int amin, int amax, int bmin, int bmax)
{
    return ! ((amin > bmax) || (bmin > amax));
}

/*-----------------------------------------------------------*/
/* Predicate: does a window with upper left corner at (x,y), */
/* width w, and height h overlap any existing window EXCEPT  */
/* the window identified by skip (if not NULL)?              */
/*-----------------------------------------------------------*/
int winoverlap(int x, int y, int w, int h, struct w *skip)
{
    struct w *p;

    for(p=wlist;p!=NULL;p=p->next) {
	if (p == skip)
	    continue;
	if (overlap(x, x+w-1, p->pos[0], p->pos[0]+p->dim[0]-1) &&
	    overlap(y, y+h-1, p->pos[1], p->pos[1]+p->dim[1]-1))
	    return 1;
    }
    return 0;
}

/*----------------------------------------*/
/* Predicate: do windows a and b overlap? */
/*----------------------------------------*/
int winolap(struct w *a, struct w *b)
{
    return overlap(a->pos[0], a->pos[0]+a->dim[0]-1,
	              b->pos[0], b->pos[0]+b->dim[0]-1) &&
           overlap(a->pos[1], a->pos[1]+a->dim[1]-1,
		      b->pos[1], b->pos[1]+b->dim[1]-1);
}

/*-----------------------------------------------------------*/
/* Predicate: does a window with upper left corner at (x,y), */
/* width w, and height h fit on the display?                 */
/*-----------------------------------------------------------*/
int winfit(int x, int y, int w, int h)
{
    return (x >= 0 && y >= 0 && x+w-1 < xmax && y+h-1 < ymax);
}

/*------------------------------------------------------------*/
/* Find the window, if any, that includes the pixel at (x,y). */
/* If found, return a pointer to its structure.               */
/* If not found, return NULL.                                 */
/*------------------------------------------------------------*/
/* NAIVE IMPLEMENTATION                                       */
/*------------------------------------------------------------*/
struct w *findwin(int x, int y)
{
    struct w *p;

    for(p=wlist;p!=NULL;p=p->next) {
	if (x < p->pos[0] || x >= p->pos[0] + p->dim[0])
	    continue;
	if (y < p->pos[1] || y >= p->pos[1] + p->dim[1])
	    continue;
	return p;
    }
    return NULL;
}

/*----------------------------------------------------*/
/* Remove the entry at p from the doubly-linked list. */
/*----------------------------------------------------*/
void win_remove(struct w *p)
{
    struct w *a, *b;		/* predecessor, successor */

    a = p->prev;
    b = p->next;
    if (p->prev == NULL) {	/* removing head element? */
	wlist = b;		    /* reset head pointer */
	if (b != NULL)		    /* if successor, fix prev pointer */
	    b->prev = NULL;
	free(p);
	return;
    }
    if (p->next == NULL) {	/* removing tail element? */
	tail = p->prev;		    /* reset tail pointer */
	if (a != NULL)		    /* if predecessor, fix next pointer */
	    a->next = NULL;
	free(p);
	return;
    }
    /* assert p->prev != NULL && p->next != NULL */
    b->prev = a;
    a->next = b;
    free(p);
}

/*--------------------------------------*/
/* Setup for a move			*/
/* Create a and b arrays		*/
/* Needed data:				*/
/*	p - pointer to moving window	*/
/*	md - desired move distance	*/
/*	*pnb - where to store nbwin	*/
/*	***ppb - where to store b list	*/
/*--------------------------------------*/
void mset(struct w *p, int md[2],
    int *pnb, struct w ***ppb)
{
    struct w M;			/* the move window */
    int hv;			/* horizontal/x (0) or vertical/y (1) move? */
    int lr;			/* left/up (0) or right/down (1) move? */
    int sdist;			/* signed distance to move */
    int adist;			/* absolute distance to move */
    int na, nb;			/* # entries in a, b arrays */
    int i;
    struct w **a;
    struct w **b;
    struct w *r;

    hv = md[0] == 0;
    lr = md[hv] > 0;
    sdist = md[0] + md[1];
    adist = abs(sdist);
    /*---------------------------*/
    /* Setup the move rectangle. */
    /*---------------------------*/
    M.dim[hv] = adist;
    M.dim[!hv] = p->dim[!hv];
    if (md[0] < 0) {			/* move left horizontally */
	M.pos[0] = p->pos[0] - adist;
	M.pos[1] = p->pos[1];
    } else if (md[0] > 0) {		/* move right horizontally */
	M.pos[0] = p->pos[0] + p->dim[0];
	M.pos[1] = p->pos[1];
    } else if (md[1] < 0) {		/* move up vertically */
	M.pos[0] = p->pos[0];
	M.pos[1] = p->pos[1] - adist;
    } else {				/* move down vertically */
	M.pos[0] = p->pos[0];
	M.pos[1] = p->pos[1] + p->dim[1];
    }
    /*-------------------------------------------------------*/
    /* Find all windows that intersect the "move rectangle", */
    /* saving pointers to them in an array.                  */
    /*-------------------------------------------------------*/
    a = (struct w **)malloc(nwin * sizeof(struct w *));
    if (a == NULL) {
	printf("ERROR: out of memory.\n");
	exit(1);
    }
    na = 0;
    for(r=wlist;r!=NULL;r=r->next) {
	if (overlap(M.pos[0], M.pos[0]+M.dim[0]-1,
		    r->pos[0], r->pos[0]+r->dim[0]-1) &&
	    overlap(M.pos[1], M.pos[1]+M.dim[1]-1,
		    r->pos[1], r->pos[1]+r->dim[1]-1)) {
	    a[na] = r;
	    na++;
	}
    }
    /*-------------------------------------------------------------*/
    /* Find all directly encountered windows of p, putting them in */
    /* array b. Theorem 4 used as the basis for this algorithm.    */
    /*-------------------------------------------------------------*/
    b = (struct w **)malloc(na * sizeof(struct w *));
    if (b == NULL) {
	printf("ERROR: out of memory.\n");
	exit(1);
    }
    nb = 0;
    /*----------------------------------------------------------------*/
    /* We check each window q in a against all the other windows in a */
    /* to see if the condition specified by theorem 4 is met. If the  */
    /* condition is met (that is, q is not directly encountered by p  */
    /* when p is moved), q is not copied to b.                        */
    /*----------------------------------------------------------------*/
    for(i=0;i<na;i++) {			/* q is a[i] */
	struct w S;			/* the sweep rectangle */
	struct w *q;			/* q == a[i] */
	int j, isect;
	int bp;

	q = a[i];

	if (hv == 0) {			/* horizontal win between p and a[i] */
	    if (lr == 0) {		    /* left of p */
		S.pos[0] = q->pos[0] + q->dim[0];
		S.dim[0] = p->pos[0] - S.pos[0];
	    } else {			    /* right of p */
		S.pos[0] = p->pos[0] + p->dim[0];
		S.dim[0] = q->pos[0] - S.pos[0];
	    }
	    S.pos[1] = max(p->pos[1],q->pos[1]);
	    bp = min(p->pos[1]+p->dim[1]-1,q->pos[1]+q->dim[1]-1);
	    S.dim[1] = bp - S.pos[1] + 1;
	} else {			/* vertical win between p and a[i] */
	    if (lr == 0) {		    /* above p */
		S.pos[1] = q->pos[1] + q->dim[1];
		S.dim[1] = p->pos[1] - S.pos[1];
	    } else {			    /* below p */
		S.pos[1] = p->pos[1] + p->dim[1];
		S.dim[1] = q->pos[1] - S.pos[1];
	    }
	    S.pos[0] = max(p->pos[0],q->pos[0]);
	    bp = min(p->pos[0]+p->dim[0]-1,q->pos[0]+q->dim[0]-1);
	    S.dim[0] = bp - S.pos[0] + 1;
	}
	isect = 0;			/* no intersections yet */
	for(j=0;j<na;j++) {
	    if (i == j)			/* skip window q */
		continue;
	    if (winolap(&S,a[j])) {
		isect = 1;
		break;		/* omit to enumerate all overlaps */
	    }
	}
	if (!isect)
	    b[nb++] = a[i];
    }
    free(a);
    *ppb = b;
    *pnb = nb;
}

/*---------------------------------------------------------------*/
/* Return the ABSOLUTE VALUE of the distance (in the appropriate */
/* direction that the window 'p' can move. md[] contains the     */
/* signed values of the desired move distances.                  */
/*---------------------------------------------------------------*/
int move1(struct w *p, int md[2], int domove)
{
    int i, mindist;
    struct w **b, *r;
    int nb;

    mset(p, md, &nb, &b);
    /*-------------------------------------------------------------*/
    /* If there were directly encountered windows in b (nb > 0),   */
    /* the distance p can be moved is determined by computing, for */
    /* each entry r in b, the sum of the distance between p and r, */
    /* and the maximum distance r can move. The maximum distance p */
    /* can move is the minimum of these distances.                 */
    /*-------------------------------------------------------------*/
    if (nb > 0) {	/* find distance we can move & push nb windows */
	int maxbdist;	/* minimum of max dist entries in b can move */

	mindist = 0;
	for(i=0;i<nb;i++) {
	    int gapdist;	/* absolute gap distance between p and r */
	    int mvdist[2];	/* signed distance r needs to move */

	    r = b[i];
	    if (md[0] < 0) {		/* horizontal left */
		gapdist = p->pos[0] - (r->pos[0] + r->dim[0]);
		mvdist[0] = md[0] + gapdist;
		mvdist[1] = 0;
	    } else if (md[0] > 0) {	/* horizontal right */
		gapdist = r->pos[0] - (p->pos[0] + p->dim[0]);
		mvdist[0] = md[0] - gapdist;
		mvdist[1] = 0;
	    } else if (md[1] < 0) {	/* vertical up */
		gapdist = p->pos[1] - (r->pos[1] + r->dim[1]);
		mvdist[0] = 0;
		mvdist[1] = md[1] + gapdist;
	    } else {			/* vertical down */
		gapdist = r->pos[1] - (p->pos[1] + p->dim[1]);
		mvdist[0] = 0;
		mvdist[1] = md[1] - gapdist;
	    }
	    maxbdist = move1(r,mvdist,0);
	    maxbdist += gapdist;
	    if (i == 0 || maxbdist < mindist)
		mindist = maxbdist;
	}
    } else {		/* nothing to push; dist to display edge is limit */
	if (md[0] < 0) {
	    mindist = min(-md[0], p->pos[0]);
	} else if (md[0] > 0) {
	    mindist = min(md[0], xmax - (p->pos[0] + p->dim[0]));
	} else if (md[1] < 0) {
	    mindist = min(-md[1], p->pos[1]);
	} else {
	    mindist = min(md[1], ymax - (p->pos[1] + p->dim[1]));
	}
    }
    /*-------------------------------------------------------------*/
    /* Now we push directly encountered windows by the appropriate */
    /* distance, if necessary. Obviously if the gap between p and  */
    /* a DE window is of distance mindist or more, the DE window   */
    /* does not move.                                              */
    /*-------------------------------------------------------------*/
    if (domove) for(i=0;i<nb;i++) {
	int gapdist;		/* absolute gap distance between p and r */
	int mvdist[2];		/* distance r needs to move */

	r = b[i];
	mvdist[0] = mvdist[1] = 0;
	if (md[0] < 0) {		/* horizontal left */
	    gapdist = p->pos[0] - (r->pos[0] + r->dim[0]);
	    if (gapdist < mindist)
		mvdist[0] = -(mindist - gapdist);
	} else if (md[0] > 0) {	/* horizontal right */
	    gapdist = r->pos[0] - (p->pos[0] + p->dim[0]);
	    if (gapdist < mindist)
		mvdist[0] = mindist - gapdist;
	} else if (md[1] < 0) {	/* vertical up */
	    gapdist = p->pos[1] - (r->pos[1] + r->dim[1]);
	    if (gapdist < mindist)
		mvdist[1] = -(mindist - gapdist);
	} else {			/* vertical down */
	    gapdist = r->pos[1] - (p->pos[1] + p->dim[1]);
	    if (gapdist < mindist)
		mvdist[1] = mindist - gapdist;
	}
	if (mvdist[0] + mvdist[1] == 0)
	    continue;
	move2(r,mvdist);
    }
    free(b);
    return mindist;
}

void move2(struct w *p, int md[2])
{
    struct w **b, *r;
    int nb;
    int i;
    int gapdist;		/* absolute gap distance */
    int mvdist[2];

    mset(p, md, &nb, &b);
    for(i=0;i<nb;i++) {
	r = b[i];
	mvdist[0] = mvdist[1] = 0;
	if (md[0] < 0) {		/* horizontal left */
	    gapdist = p->pos[0] - (r->pos[0] + r->dim[0]);
	    if (gapdist >= abs(md[0]))
		continue;
	    else
		mvdist[0] = md[0] + gapdist;
	} else if (md[0] > 0) {	/* horizontal right */
	    gapdist = r->pos[0] - (p->pos[0] + p->dim[0]);
	    if (gapdist >= md[0])
		continue;
	    else
		mvdist[0] = md[0] - gapdist;
	} else if (md[1] < 0) {	/* vertical up */
	    gapdist = p->pos[1] - (r->pos[1] + r->dim[1]);
	    if (gapdist >= abs(md[1]))
		continue;
	    else
		mvdist[1] = md[1] + gapdist;
	} else {			/* vertical down */
	    gapdist = r->pos[1] - (p->pos[1] + p->dim[1]);
	    if (gapdist >= md[1])
		continue;
	    else
		mvdist[1] = md[1] - gapdist;
	}
	if (mvdist[0] + mvdist[1] == 0)
	    continue;
	move2(r,mvdist);
    }
    free(b);
    p->pos[0] += md[0];
    p->pos[1] += md[1];
}

/*-------------------------------------------------------------*/
/* Implement the move command. 'p' identifies the window to be */
/* moved. 'x' and 'y' identify distance to be moved in the     */
/* specified directions. At least one of x and y must be zero. */
/*-------------------------------------------------------------*/
void move(struct w *p, int x, int y)
{
    int ax;			/* actual distance we can move */
    int md[2];			/* move distances */
    int rd;			/* requested distance */

    if (x == 0 && y == 0)	/* no movement required */
	return;
    md[0] = x;
    md[1] = y;
    ax = move1(p, md, 1);	/* get max abs distance we can move */
    rd = md[0] + md[1];
    if (rd < 0)
	ax = -ax;
    if (abs(ax) < abs(md[0]) + abs(md[1]))	/* not far enough? */
	err_move_less(ax,md[0]+md[1]);
    if (x == 0)
	p->pos[1] += ax;
    else
	p->pos[0] += ax;
}

int main(int argc, char *argv[])
{
    FILE *f;
    int cmd;			/* command code or EOF */
    int args[4];		/* command arguments */
    struct w *newwin;		/* new window structure */
    struct w *pwin;		/* ptr to a found window */

    if (argc > 1) {
	f = fopen(argv[1],"r");
	if (f == NULL) {
	    fprintf(stderr,"Cannot open %s\n", argv[1]);
	    exit(1);
	}
    } else
	f = stdin;
    if (fscanf(f,"%d%d\n",&xmax,&ymax) != 2) {
	fprintf(stderr,"ERROR: trouble reading first data line.\n");
	exit(1);
    }
    if (xmax < 1 || xmax > MAXVAL || ymax < 1 || ymax > MAXVAL) {
	fprintf(stderr,"ERROR: xmax and/or ymax are out of bounds.\n");
	exit(1);
    }
    lineno = 2;
    wlist = tail = NULL;		/* no windows yet */
    for(cmdno=1;;cmdno++) {
	cmd = get_command(f,args);
	if (cmd == EOF)
	    break;
	switch(cmd) {
	    case OPEN_CMD:
		if (!winfit(args[0],args[1],args[2],args[3])) {	/* fit? */
		    err_no_fit(OPEN_CMD);
		    continue;
		}
		if (winoverlap(args[0],args[1],args[2],args[3],NULL)) {
		    err_no_fit(OPEN_CMD);
		    continue;
		}
		newwin = (struct w *)malloc(sizeof(struct w));  /* make new */
		if (newwin == NULL) {
		    fprintf(stderr,"ERROR: OUT OF MEMORY AT LINE %d\n",
			lineno);
		    exit(1);
		}
		newwin->cmdorg = cmdno;
		newwin->pos[0] = args[0];
		newwin->pos[1] = args[1];
		newwin->dim[0] = args[2];
		newwin->dim[1] = args[3];
		newwin->prev = tail;			/* add to list end */
		newwin->next = NULL;
		if (wlist == NULL)
		    wlist = newwin;
		else
		    tail->next = newwin;
		tail = newwin;
		nwin++;
		break;
	    case CLOSE_CMD:
		pwin = findwin(args[0],args[1]);	/* check existence */
		if (pwin == NULL) {
		    err_no_win(CLOSE_CMD);
		    continue;
		}
		win_remove(pwin);			/* remove it */
		nwin--;
		break;
	    case RESIZE_CMD:
		pwin = findwin(args[0],args[1]);	/* check existence */
		if (pwin == NULL) {
		    err_no_win(RESIZE_CMD);
		    continue;
		}
		if (!winfit(pwin->pos[0],pwin->pos[1],args[2],args[3])) {
		    err_no_fit(RESIZE_CMD);	/* if it doesn't fit */
		    continue;
		}
		if (winoverlap(pwin->pos[0],pwin->pos[1],
		               args[2],args[3],pwin)) {
		    err_no_fit(RESIZE_CMD);	/* if it overlaps another */
		    continue;
		}
		pwin->dim[0] = args[2];
		pwin->dim[1] = args[3];
		break;
	    case MOVE_CMD:
		pwin = findwin(args[0],args[1]);	/* check existence */
		if (pwin == NULL) {
		    err_no_win(MOVE_CMD);
		    continue;
		}
		move(pwin,args[2], args[3]);	/* make the move */
		break;
	}
    }

    /*----------------------------------------------*/
    /* Display x, y, w, and h for each open window, */
    /* in the order they were opened.               */
    /*----------------------------------------------*/
    printf("%d window(s):\n", nwin);
    for(pwin=wlist;pwin!=NULL;pwin=pwin->next)
	printf("%d %d %d %d\n",
	    pwin->pos[0], pwin->pos[1], pwin->dim[0], pwin->dim[1]);
    exit(0);
}
