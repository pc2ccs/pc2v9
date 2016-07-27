size(1500);
defaultpen(1.8);
bbox(white, Fill);

int n = stdin;
path a;
pair amid = (0,0);
for (int i = 0; i < n; ++i)
{
  pair p = (stdin, stdin);
  amid += p;
  if (size(a) == 0)
	a = p;
  else
	a = a -- p;
}
amid /= n;
a = a -- cycle;
pair av = (stdin, stdin);

int m = stdin;
path b;
pair bmid = (0,0);
for (int i = 0; i < m; ++i)
{
    pair p = (stdin, stdin);
	bmid += p;
    if (size(b) == 0)
        b = p;
    else
        b = b -- p;
}
bmid /= m;
b = b -- cycle;
pair bv = (stdin, stdin);

real t = stdin;


filldraw(a, fillpen=lightred, drawpen=black);
filldraw(b, fillpen=lightgreen, drawpen=black);

picture isect;
if (t != -1) {
  path ia = shift(t*av) * a;
  path ib = shift(t*bv) * b;
  picture isect;
  fill(isect, ia, p=gray);
  clip(isect, ib);
  draw(isect, ia, p=dashed+red);
  draw(isect, ib, p=dashed+darkgreen);
  add(isect);
}

path avec = amid -- (amid+av);
draw(avec, arrow=Arrow, p=darkblue);
path bvec = bmid -- (bmid+bv);
draw(bvec, arrow=Arrow, p=darkblue);


shipout(bbox(xmargin=10, white, Fill));
