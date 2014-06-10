package com.liris.datamatrixedcamera.app.lecture;
/**
 * Reed-Solomon algorithm.
 *
 * A class for define structure and algorithms of Reed-Solomon codes.
 *
 * @author Benjamin Barras
 * @version 1.10, 13/02/99
 * @email benjamin.barras@epfl.ch
 *
 */

public class RSAlgorithm
{
  public int n,k,t;
  public field gf;
  public boolean failed;
  public int errorNumber;
  public polynomial generator,syndrome;
  public polynomial messageSend,messageReceive;
  public polynomial codeSend,codeReceive,codeError,codeCorrected;
  public polynomial locator,evaluator,coevaluator;
  public polynomial errorPosition,errorValue;

  public RSAlgorithm(int n,int t)
  {
    this.n = n;
    this.k = 8;
    this.t = t;
    gf = new field();
    gf.generate(k,301);
    syndrome = new polynomial(n);
    generator = new polynomial(n);
    codeCorrected = new polynomial(n);
    messageReceive = new polynomial(n);
    locator = new polynomial(n);
    evaluator = new polynomial(n); 
    coevaluator = new polynomial(n); 
    errorPosition = new polynomial(n); 
    errorValue = new polynomial(n); 
  }

  public void copy(polynomial p, polynomial q)
  {
    int i;
    
    q.degree = p.degree;
    for (i = 0; i < n; i++) q.coefficient[i] = p.coefficient[i];
  }

  public void clear(polynomial p)
  {
    int i;
    
    p.degree = 0;
    for (i = 0; i < n; i++) p.coefficient[i] = 0;
  }

  public boolean nonZero(polynomial p)
  {
    boolean f;

    f = (p.degree > 0) || (p.coefficient[0] > 0);
    return f;
  }

  public int degree(polynomial p)
  {
    int i,d;
    
    d = 0;
    for (i = 0; i < n; i++)
    {
      if (p.coefficient[i] > 0) d = i;
    }
    return d;
  }

  public void shr(polynomial p, int o, polynomial q)
  {
    int i,l;

    clear(q);
    l = p.degree - o;
    if (l >= 0)
    {
      q.degree = l;
      for (i = 0; i <= q.degree; i++)
      {
        q.coefficient[i] = p.coefficient[i + o];
      }
    }
  }

  public void add(polynomial p, polynomial q, polynomial s)
  {
    int i,m;

    clear(s);
    if (p.degree == q.degree)
    {
      m = p.degree;
      for (i = 0; i <= m; i++)
      {
        s.coefficient[i] = gf.add(p.coefficient[i],q.coefficient[i]);
        if (s.coefficient[i] > 0) s.degree = i;
      }
    }
    else
    {
      if (p.degree > q.degree) {copy(p,s); m = q.degree;} 
      else {copy(q,s); m = p.degree;}
      for (i = 0; i <= m; i++)
      {
        s.coefficient[i] = gf.add(p.coefficient[i],q.coefficient[i]);
      }
    }
  }

  public int eval(polynomial p, int x)
  {
    int i,m,a;

    a = 0;
    for (i = p.degree; i >= 0; i--)
    {
      m = gf.mul(x,a);
      a = gf.add(p.coefficient[i],m);
    }
    return a;
  }

  public int evald(polynomial p, int x)
  {
    int i,a,m,b;

    a = 0;
    b = 0;
    if (p.degree > 0)
    {
      for (i = p.degree; i > 0; i--)
      {
        m = gf.mul(x,a);
        a = gf.add(p.coefficient[i],m);
        m = gf.mul(x,b);
        b = gf.add(a,m);
      }
    }
    return b;
  }

  public void mul(polynomial p, polynomial q, polynomial r)
  {
    int i,j,d,c,a,m;

    clear(r);
    if (nonZero(p) & nonZero(q))
    {
      r.degree = p.degree + q.degree;
      for (i = 0; i <= q.degree; i++)
      {
        c = q.coefficient[i];
        if (c > 0)
        {
          for (j = 0; j <= p.degree; j++)
          {
            d = i + j;
            m = gf.mul(c,p.coefficient[j]);
            a = gf.add(r.coefficient[d],m);
            r.coefficient[d] = a;
         }
        }
      }
    }
  }

  public void div(polynomial s, polynomial t, polynomial q, polynomial r)
  {
    int i,j,k,d,c,a,m;

    clear(q);
    copy(s,r);
    if (s.degree >= t.degree)
    {
      q.degree = s.degree - t.degree;
      if (nonZero(t))
      {
        k = s.degree;
        for (i = q.degree; i >= 0; i--)
        {
          c = gf.div(r.coefficient[k],t.coefficient[t.degree]);
          q.coefficient[i] = c;
          if (c > 0)
          {
            for (j = 0; j <= t.degree; j++)
            {
              d = i + j;
              m = gf.mul(t.coefficient[j],c);
              a = gf.add(r.coefficient[d],m);
              r.coefficient[d] = a;
            }
          }
          k = k - 1;
        }
        r.degree = degree(r);
      }
    }
  }

  public void calculateGenerator()
  {
    int i;
    polynomial p,q;
    
    clear(generator);
    p = new polynomial(n);
    q = new polynomial(n);
    q.degree = 1;
    q.coefficient[1] = 1;
    generator.degree = 0;
    generator.coefficient[0] = 1;
    for (i = 1; i <= (2 * t); i++)
    {
      q.coefficient[0] = gf.getElementOfPower(i);
      mul(generator,q,p);
      copy(p,generator);
    }
  }

  public void calculateCodeReceive(polynomial data)
  {
      codeReceive = data;
  }

  public void calculateSyndrome()
  { 
    int i,j,e,s;
    
    clear(syndrome);
    for (i = 1; i <= (2 * t); i++)
    {
      j = i - 1;
      e = gf.getElementOfPower(i);
      s = eval(codeReceive,e);
      syndrome.coefficient[j] = s;
      if (s > 0) syndrome.degree = j;
    }
  }

  public void EuclidAlgorithm()
  { 
    polynomial p,q,s,c,l,e;
    
    p = new polynomial(n);
    q = new polynomial(n);
    s = new polynomial(n);
    c = new polynomial(n);
    e = new polynomial(n);
    l = new polynomial(n);
    e.degree = 2 * t;
    e.coefficient[e.degree] = 1;
    copy(syndrome,evaluator);
    c.degree = 0; c.coefficient[0] = 1;
    locator.degree = 0; locator.coefficient[0] = 1;
    while (evaluator.degree >= t)
    {
      div(e,evaluator,q,p);
      copy(evaluator,e);
      copy(p,evaluator);
      mul(q,coevaluator,p);
      add(c,p,s);
      copy(coevaluator,c);
      copy(s,coevaluator);
      mul(q,locator,p);
      add(l,p,s);
      copy(locator,l);
      copy(s,locator);
    }
  }

  public void searchRoot()
  {
    int i,c,e,p;

    c = eval(locator,0);
    if (c != 0)
    {
      for (i = 1; i <= gf.n; i++)
      {
        e = gf.getElementOfPower(i);
        c = eval(locator,e);
        if (c == 0)
	{
          p = gf.n - i;
          errorNumber += 1;
          if ((p >= 0) && (p < n))
          {
            if (errorPosition.coefficient[p] > 0) failed = true;
            else errorPosition.coefficient[p] = e;
          }
          else failed = true;
        }
      }
      errorPosition.degree = degree(errorPosition);
      if (errorNumber != locator.degree) failed = true;
    }
    else failed = true;
  }

  public void errorCorrection()
  {
    int i,j,e,w,l;

    j = errorPosition.degree;
    errorValue.degree = j;
    for (i = 0; i <= j; i++)
    {
      e = errorPosition.coefficient[i];
      if (e > 0) 
      {
        w = eval(evaluator,e); 
        l = evald(locator,e);
        errorValue.coefficient[i] = gf.div(w,l);
      }
    }
  }

  public void errorProcessing()
  {
    failed = false;
    errorNumber = 0;
    clear(locator);
    clear(evaluator);
    clear(coevaluator);
    clear(errorValue);
    clear(errorPosition);
    if (nonZero(syndrome))
    {
      if (syndrome.degree < t) failed = true;
      else
      {
        EuclidAlgorithm();
        if (nonZero(evaluator))
        {
          searchRoot();
          if (!failed) errorCorrection();
        }
        else failed = true;
      }
    }
    if (failed)
    {
        int err = 0;
        switch (n)
        {
            case 24 :
                err = 6;
                break;
            case 32 : 
                err = 7;
                break;
            case 40 : 
                err = 9;
                break;
        }
        System.out.println("Correction failed : more than " + err + " false codewords");
    }
    else
    {
         System.out.println("Code Corrected : ");
    }
  }

  public polynomial calculateMessageReceive()
  {
    int o;
    
    o = 2 * t;
    add(codeReceive,errorValue,codeCorrected);
    shr(codeCorrected,o,messageReceive);
    return codeCorrected;
  }

  public polynomial processing(polynomial data)
  {
    clear(codeCorrected);
    calculateGenerator();
    calculateCodeReceive(data);
    calculateSyndrome();
    errorProcessing();
    return codeCorrected = calculateMessageReceive();
  }

  public String polynomialToString(polynomial p)
  {
    int i,c;
    String s;

    s = "";
    for (i = p.degree; i >= 0; i--)
    {
      c = p.coefficient[i];
      if (i == p.degree) 
      {
        if (i > 0) s = s + c + "X^" + i; else s = s + c;
      }
      else 
      {
        if (i > 0) { if (c > 0) s = s + " + " + c + "X^" + i; }
        else { if (c > 0) s = s + " + " + c; }
      }
    }
    return s;
  }

}

