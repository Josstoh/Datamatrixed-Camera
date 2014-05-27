package com.liris.datamatrixedcamera.app.lecture;
/**
 * Galois field algorithm.
 *
 * A class for define structure of Galois field.
 *
 * @author Benjamin Barras 
 * @version 1.10, 13/02/99
 * @email benjamin.barras@epfl.ch
 *
 */

public class field
{
  public int n,q;
  public int k,primitive;
  public int [] powerOfElement,elementOfPower;

  public field()
  {
  }

  public void generate(int k, int primitive)
  {
    int e,p,mask;

    mask = 1;
    mask = mask << k;
    q = mask;
    n = q - 1;
    this.k = k;
    this.primitive = primitive;
    powerOfElement = new int [q];
    elementOfPower = new int [q];
    powerOfElement[0] = -1;
    elementOfPower[n] = 1;
    e = 1;
    for (p = 0; p < n; p++)
    {
      powerOfElement[e] = p;
      elementOfPower[p] = e;
      e = e << 1;
      if ((e & mask) > 0) e = e ^ primitive;
    }
  }

  public int inField(int a)
  {
    int i,j,e,mask,r;
   
    r = 0;
    e = 1;
    j = 1;
    mask = 1;
    mask = mask << k;
    for (i = 0; i < 32; i++)
    {
      if ((a & j) > 0) r = r ^ e;
      j = j << 1;
      e = e << 1;
      if ((e & mask) > 0) e = e ^ primitive;
    }
    return r;
  }

  public int add(int a, int b)
  {
    int c;

    c = a ^ b;
    return c;
  }

  public int mul(int a, int b)
  {
    int pa,pb,p,c;

    if ((a > 0) & (b > 0))
    {
      pa = powerOfElement[a];
      pb = powerOfElement[b];
      p = (pa + pb) % n;
      c = elementOfPower[p];
    }
    else c = 0;
    return c;
  }

  public int div(int a, int b)
  {
    int pa,pb,p,c;

    if ((a > 0) & (b > 0))
    {
      pa = powerOfElement[a] + n;
      pb = powerOfElement[b];
      p = (pa - pb) % n;
      c = elementOfPower[p];
    }
    else
    {
      if (a == 0) c = 0; else c = -1;
    }
    return c;
  }

  public int inv(int a)
  {
    int pa,p,c;

    if (a > 0)
      {
        pa = powerOfElement[a];
        p = n - pa;
        c = elementOfPower[p];
      }
    else c = -1;
    return c;
  }

  public int pow(int a, int b)

  {
    int i,p,c;

    c = 1;
    if (b > 0)
    {
      for (i = 1; i <= b; i++) { c = mul(c,a); }
    }
    return c;
  }

  public int getPowerOfElement(int e)
  {
    return powerOfElement[e];
  }

  public int getElementOfPower(int p)
  {
    return elementOfPower[p];
  }

  public int getPrimitive()
  {
    return primitive;
  }

  public int getDegreeOfPrimitive()
  {
    return k;
  }

  public String polynomialToString(int p, String x)
  {
    int i,m;
    String str,plus;

    m = 1;
    str = "";
    plus = "";
    m = m << 30;
    for (i = 30; i >= 0; i--)
    {
      if ((p & m) > 0)
      {
        if (i > 0) str = str + plus + x + "^" + i; 
        else str = str + plus + "1";
        plus = " + ";
      }
      m = m >> 1;
    }
    return str;
  }

}
