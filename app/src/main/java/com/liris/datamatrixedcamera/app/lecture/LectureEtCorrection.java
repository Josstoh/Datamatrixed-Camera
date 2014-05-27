package com.liris.datamatrixedcamera.app.lecture;

public class LectureEtCorrection
{
    private boolean isNotCorrected = true;
    private int [] codewordsCorrected;

    public boolean lecture(int [][] matrix)
    {
        DataMatrixReader reader = new DataMatrixReader();
        RSCorrector rsc = new RSCorrector();
        int codewords [];
        RotateMatrix.rotate(matrix);
      
      codewords = reader.readMatrix(matrix);
      
      int size = matrix.length;
      System.out.println("Codewords : ");
      for (int i = 0; i<codewords.length; i++)
        {
            System.out.print(" " + codewords[i]);
        }
      System.out.println("");
      
     
      codewordsCorrected = rsc.Correction(codewords);
      isNotCorrected = rsc.getIsNotCorrected();
      
     if (!isNotCorrected)
     {
        for (int i = 0; i<codewordsCorrected.length; i++)
            {
                if (codewordsCorrected[i] != codewords[i])
                {
                    System.out.print(" X" + codewordsCorrected[i] + "X");
                }
                else
                {
                    System.out.print(" " + codewordsCorrected[i]);
                }
            }
            System.out.println("");
      }

      return isNotCorrected;
    }
}

