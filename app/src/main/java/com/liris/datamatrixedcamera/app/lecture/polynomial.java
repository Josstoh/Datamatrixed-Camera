package com.liris.datamatrixedcamera.app.lecture;
/**
 *
 * A class for define structure of polynomial.
 *
 * @author Benjamin Barras
 * @version 1.10, 13/02/99
 * @email benjamin.barras@epfl.ch
 *
 */

public class polynomial
{
  public int degree;
  public int coefficient[];

  public polynomial(int n)
  {
    coefficient = new int[n];
  }
}
