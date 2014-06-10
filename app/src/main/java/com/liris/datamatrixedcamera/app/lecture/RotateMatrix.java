package com.liris.datamatrixedcamera.app.lecture;

/* The purpose of this class is to position the matrix correctly for reading.
 * (the line and the column of 1 is supposed to be located in the 
 * bottom-left corner)
 */

public class RotateMatrix 
{
    
    public static void rotate(int[][] matrix) 
    {
        while(matrix[14][15] != 0 && matrix[0][1] != 0)
      {
            int N = matrix.length;
            for(int i = 0; i < N/2; ++i) 
            {
                for(int j = 0; j < (N+1)/2; ++j) 
                {
                    int temp = matrix[i][j];
                    matrix[i][j] = matrix[N-1-j][i];
                    matrix[N-1-j][i] = matrix[N-1-i][N-1-j];
                    matrix[N-1-i][N-1-j] = matrix[j][N-1-i];
                    matrix[j][N-1-i] = temp;
                }
            }
       }
    }
    
     static void displayMatrice(int [][] matrix)
    {
        for (int i=0; i<16; i++)
        {
            for(int j=0; j<16; j++)
            {
                System.out.print(" "+matrix[i][j]);
            }
            System.out.println("");
        }
    }
    
}
