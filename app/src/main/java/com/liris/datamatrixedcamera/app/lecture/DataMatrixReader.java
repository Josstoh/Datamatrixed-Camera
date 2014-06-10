package com.liris.datamatrixedcamera.app.lecture;

public class DataMatrixReader {
    
    
    public int [] readMatrix (int [][]matrix)
    {
        int [] codewords = null;
        int size = matrix.length;
        System.out.println(size);
        switch (size)
        {
            case 16 : 
                codewords = readMatrix16x16(matrix);
                break;
            case 18 : 
                codewords = readMatrix18x18(matrix);
                break;
            case 20 : 
                codewords = readMatrix20x20(matrix);
                break;
        }
        
        return codewords;
    }

/*-----------------------------------------------------------------------*/    
    public int [] readMatrix16x16 (int [][] matrix)
    {
        int word = 0;
        int [] codewords = new int [24];
        
        for(int i = 0; i<24; i++)
        {
            switch (i+1)
            {   
            case 1 : 
                word = readBlocOne16x16(matrix);
               break;
            case 2 : 
                word = readRegularBloc(matrix,3,3);
                break;
            case 3 :
                word = readBlocThree16x16(matrix);
                break;
            case 4 : 
                word = readBlocFour16x16(matrix);
                break;
            case 5 : 
                word = readRegularBloc(matrix,4,6);
                break;
            case 6 : 
                word  = readRegularBloc(matrix,6,4);
                break;
            case 7 : 
                word = readBlocSeven16x16(matrix);
                break;
            case 8 : 
                word = readBlocEight16x16(matrix);
                break;
            case 9 : 
                word = readRegularBloc(matrix,11,3);
                break;
            case 10 : 
                word = readRegularBloc(matrix,9,5);
                break;
            case 11 : 
                word = readRegularBloc(matrix,7,7);
                break;
            case 12 : 
                word = readRegularBloc(matrix,5,9);
                break;
            case 13 : 
                word = readRegularBloc(matrix,3,11);
                break;
            case 14 : 
                word = readRegularBloc(matrix,4,14);
                break;
            case 15 : 
                word = readRegularBloc(matrix,6,12);
                break;
            case 16 : 
                word = readRegularBloc(matrix,8,10);
                break;
            case 17 : 
                word = readRegularBloc(matrix,10,8);
                break;
            case 18 : 
                word  = readRegularBloc(matrix,12,6);
                break;
            case 19 : 
                word = readRegularBloc(matrix,14,4);
                break;
            case 20 : 
                word = readRegularBloc(matrix,13,9);
                break;
            case 21 : 
                word = readRegularBloc(matrix,11,11);
                break;
            case 22 : 
                word = readRegularBloc(matrix,9,13);
                break;
            case 23 : 
                word = readRegularBloc(matrix,12,14);
                break;
            case 24 : 
                word = readRegularBloc(matrix,14,12);
                break;
            default : 
                break;
            }
        codewords[i] = word;
        }
        return codewords;
     }
    
/*-----------------------------------------------------------------------*/    
    public int [] readMatrix18x18 (int [][] matrix)
    {
        int word = 0;
        int [] codewords = new int [32];
        for(int i = 0; i<32; i++)
        {
            switch (i+1)
            {   
            case 1 : 
                word = readBlocOne18x18(matrix);
               break;
            case 2 : 
                word = readRegularBloc(matrix,3,3);
                break;
            case 3 :
                word = readBlocThree18x18(matrix);
                break;
            case 4 : 
                word = readBlocFour18x18(matrix);
                break;
            case 5 : 
                word = readRegularBloc(matrix,4,6);
                break;
            case 6 : 
                word  = readRegularBloc(matrix,6,4);
                break;
            case 7 : 
                word = readBlocSeven18x18(matrix);
                break;
            case 8 : 
                word = readBlocEight18x18(matrix);
                break;
            case 9 : 
                word = readRegularBloc(matrix,11,3);
                break;
            case 10 : 
                word = readRegularBloc(matrix,9,5);
                break;
            case 11 : 
                word = readRegularBloc(matrix,7,7);
                break;
            case 12 : 
                word = readRegularBloc(matrix,5,9);
                break;
            case 13 : 
                word = readRegularBloc(matrix,3,11);
                break;
            case 14 : 
                word = readBlocFourteen18x18(matrix);
                break;
            case 15 : 
                word = readBlocFifteen18x18(matrix);
                break;
            case 16 : 
                word = readRegularBloc(matrix,4,14);
                break;
            case 17 : 
                word = readRegularBloc(matrix,6,12);
                break;
            case 18 : 
                word  = readRegularBloc(matrix,8,10);
                break;
            case 19 : 
                word = readRegularBloc(matrix,10,8);
                break;
            case 20 : 
                word = readRegularBloc(matrix,12,6);
                break;
            case 21 : 
                word = readRegularBloc(matrix,14,4);
                break;
            case 22 : 
                word = readBlocTwentyTwo18x18(matrix);
                break;
            case 23 : 
                word = readRegularBloc(matrix,15,7);
                break;
            case 24 : 
                word = readRegularBloc(matrix,13,9);
                break;
            case 25 : 
                word = readRegularBloc(matrix,11,11);
                break;
            case 26 : 
                word = readRegularBloc(matrix,9,13);
                break;
            case 27 : 
                word = readRegularBloc(matrix,7,15);
                break;
            case 28 : 
                word = readRegularBloc(matrix,10,16);
                break;
            case 29 : 
                word = readRegularBloc(matrix,12,14);
                break;
            case 30 : 
                word = readRegularBloc(matrix,14,12);
                break;
            case 31 : 
                word = readRegularBloc(matrix,16,10);
                break;
            case 32 : 
                word = readRegularBloc(matrix,15,15);
                break;
            default : 
                break;
            }
        codewords[i] = word;
        }
        return codewords;
     }
/*-----------------------------------------------------------------------*/
    
    public int [] readMatrix20x20 (int [][] matrix)
    {
        int word = 0;
        int [] codewords = new int [40];
        for(int i = 0; i<40; i++)
        {
            switch (i+1)
            {   
            case 1 : 
                word = readBlocOne20x20(matrix);
               break;
            case 2 : 
                word = readRegularBloc(matrix,3,3);
                break;
            case 3 :
                word = readBlocThree20x20(matrix);
                break;
            case 4 : 
                word = readBlocFour20x20(matrix);
                break;
            case 5 : 
                word = readRegularBloc(matrix,4,6);
                break;
            case 6 : 
                word  = readRegularBloc(matrix,6,4);
                break;
            case 7 : 
                word = readBlocSeven20x20(matrix);
                break;
            case 8 : 
                word = readBlocEight20x20(matrix);
                break;
            case 9 : 
                word = readRegularBloc(matrix,11,3);
                break;
            case 10 : 
                word = readRegularBloc(matrix,9,5);
                break;
            case 11 : 
                word = readRegularBloc(matrix,7,7);
                break;
            case 12 : 
                word = readRegularBloc(matrix,5,9);
                break;
            case 13 : 
                word = readRegularBloc(matrix,3,11);
                break;
            case 14 : 
                word = readBlocFourteen20x20(matrix);
                break;
            case 15 : 
                word = readBlocFifteen20x20(matrix);
                break;
            case 16 : 
                word = readRegularBloc(matrix,4,14);
                break;
            case 17 : 
                word = readRegularBloc(matrix,6,12);
                break;
            case 18 : 
                word  = readRegularBloc(matrix,8,10);
                break;
            case 19 : 
                word = readRegularBloc(matrix,10,8);
                break;
            case 20 : 
                word = readRegularBloc(matrix,12,6);
                break;
            case 21 : 
                word = readRegularBloc(matrix,14,4);
                break;
            case 22 : 
                word = readBlocTwentyTwo20x20(matrix);
                break;
            case 23 : 
                word = readRegularBloc(matrix,17,5);
                break;
            case 24 : 
                word = readRegularBloc(matrix,15,7);
                break;
            case 25 : 
                word = readRegularBloc(matrix,13,9);
                break;
            case 26 : 
                word = readRegularBloc(matrix,11,11);
                break;
            case 27 : 
                word = readRegularBloc(matrix,9,13);
                break;
            case 28 : 
                word = readRegularBloc(matrix,7,15);
                break;
            case 29 : 
                word = readRegularBloc(matrix,5,17);
                break;
            case 30 : 
                word = readRegularBloc(matrix,8,18);
                break;
            case 31 : 
                word = readRegularBloc(matrix,10,16);
                break;
            case 32 : 
                word = readRegularBloc(matrix,12,14);
                break;
            case 33 : 
                word = readRegularBloc(matrix,14,12);
                break;
            case 34 : 
                word = readRegularBloc(matrix,16,10);
                break;
            case 35 : 
                word = readRegularBloc(matrix,18,8);
                break;
            case 36 : 
                word = readRegularBloc(matrix,17,13);
                break;
            case 37 : 
                word = readRegularBloc(matrix,15,15);
                break;
            case 38 : 
                word = readRegularBloc(matrix,13,17);
                break;
            case 39 : 
                word = readRegularBloc(matrix,16,18);
                break;
            case 40 : 
                word = readRegularBloc(matrix,18,16);
                break;
            default : 
                break;
            }
        codewords[i] = word;
        }
        return codewords;
     }
    
/*-----------------------------------------------------------------------*/
    public int readRegularBloc(int [][] matrix, int i, int j)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp [0] = matrix [i][j];
        temp [1] = matrix [i][j-1];
        temp [2] = matrix [i][j-2];
        temp [3] = matrix [i-1][j];
        temp [4] = matrix [i-1][j-1];
        temp [5] = matrix [i-1][j-2];
        temp [6] = matrix [i-2][j-1];
        temp [7] = matrix [i-2][j-2];
        
        for (int k = 0; k<8; k++)
        {
           word  = word + (temp[k] << k);
        }
         return word;
    }
    
/*--Specials blocs for Dm 16x16------------------------------------------*/    
    public int readBlocOne16x16(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[5][1];
        temp[1] = matrix[7][14];
        temp[2] = matrix[7][13];
        temp[3] = matrix[4][1];
        temp[4] = matrix[6][14];
        temp[5] = matrix[6][13];
        temp[6] = matrix[5][14];
        temp[7] = matrix[5][13];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
    
    public int readBlocThree16x16(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[1][5];
        temp[1] = matrix[1][4];
        temp[2] = matrix[1][3];
        temp[3] = matrix[14][7];
        temp[4] = matrix[14][6];
        temp[5] = matrix[14][5];
        temp[6] = matrix[13][6];
        temp[7] = matrix[13][5];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
    
    public int readBlocFour16x16(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][8];
        temp[1] = matrix[2][7];
        temp[2] = matrix[2][6];
        temp[3] = matrix[1][8];
        temp[4] = matrix[1][7];
        temp[5] = matrix[1][6];
        temp[6] = matrix[14][9];
        temp[7] = matrix[14][8];
     
        for (int k = 0; k<8; k++)
        {
            word = word + (temp[k] << k);
        }
        return word;
      }
    
    public int readBlocSeven16x16(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[8][2];
        temp[1] = matrix[8][1];
        temp[2] = matrix[10][14];
        temp[3] = matrix[7][2];
        temp[4] = matrix[7][1];
        temp[5] = matrix[9][14];
        temp[6] = matrix[6][1];
        temp[7] = matrix[8][14];
     
        for (int k = 0; k<8; k++)
        {
            word = word + (temp[k] << k);
        }
        return word;
      }
    
    public int readBlocEight16x16(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][14];
        temp[1] = matrix[1][14];
        temp[2] = matrix[1][13];
        temp[3] = matrix[1][12];
        temp[4] = matrix[1][11];
        temp[5] = matrix[14][1];
        temp[6] = matrix[13][1];
        temp[7] = matrix[12][1];
     
        for (int k = 0; k<8; k++)
        {
            word = word + (temp[k] << k);
        }
        return word;
      }
    
/*--Specials blocs for Dm 18x18------------------------------------------*/

     public int readBlocOne18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[5][1];
        temp[1] = matrix[5][16];
        temp[2] = matrix[5][15];
        temp[3] = matrix[4][1];
        temp[4] = matrix[4][16];
        temp[5] = matrix[4][15];
        temp[6] = matrix[3][16];
        temp[7] = matrix[3][15];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
     
     public int readBlocThree18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[1][5];
        temp[1] = matrix[1][4];
        temp[2] = matrix[1][3];
        temp[3] = matrix[16][5];
        temp[4] = matrix[16][4];
        temp[5] = matrix[16][3];
        temp[6] = matrix[15][4];
        temp[7] = matrix[15][3];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
     
      public int readBlocFour18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][8];
        temp[1] = matrix[2][7];
        temp[2] = matrix[2][6];
        temp[3] = matrix[1][8];
        temp[4] = matrix[1][7];
        temp[5] = matrix[1][6];
        temp[6] = matrix[16][7];
        temp[7] = matrix[16][6];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
      
       public int readBlocSeven18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[8][2];
        temp[1] = matrix[8][1];
        temp[2] = matrix[8][16];
        temp[3] = matrix[7][2];
        temp[4] = matrix[7][1];
        temp[5] = matrix[7][16];
        temp[6] = matrix[6][1];
        temp[7] = matrix[6][16];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
       
        public int readBlocEight18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[13][1];
        temp[1] = matrix[13][16];
        temp[2] = matrix[13][15];
        temp[3] = matrix[12][1];
        temp[4] = matrix[12][16];
        temp[5] = matrix[12][15];
        temp[6] = matrix[11][16];
        temp[7] = matrix[11][15];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
        
       public int readBlocFourteen18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[1][13];
        temp[1] = matrix[1][12];
        temp[2] = matrix[1][11];
        temp[3] = matrix[16][13];
        temp[4] = matrix[16][12];
        temp[5] = matrix[16][11];
        temp[6] = matrix[15][12];
        temp[7] = matrix[15][11];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
       
        public int readBlocFifteen18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][16];
        temp[1] = matrix[2][15];
        temp[2] = matrix[2][14];
        temp[3] = matrix[1][16];
        temp[4] = matrix[1][15];
        temp[5] = matrix[1][14];
        temp[6] = matrix[16][15];
        temp[7] = matrix[16][14];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
        
       public int readBlocTwentyTwo18x18(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[16][2];
        temp[1] = matrix[16][1];
        temp[2] = matrix[16][16];
        temp[3] = matrix[15][2];
        temp[4] = matrix[15][1];
        temp[5] = matrix[15][16];
        temp[6] = matrix[14][1];
        temp[7] = matrix[14][16];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
    
    
/*--Specials blocs for Dm 20x20------------------------------------------*/
    
    public int readBlocOne20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[5][1];
        temp[1] = matrix[3][18];
        temp[2] = matrix[3][17];
        temp[3] = matrix[4][1];
        temp[4] = matrix[2][18];
        temp[5] = matrix[2][17];
        temp[6] = matrix[1][18];
        temp[7] = matrix[1][17];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
     
     public int readBlocThree20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[1][5];
        temp[1] = matrix[1][4];
        temp[2] = matrix[1][3];
        temp[3] = matrix[18][3];
        temp[4] = matrix[18][2];
        temp[5] = matrix[18][1];
        temp[6] = matrix[17][2];
        temp[7] = matrix[17][1];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
     
      public int readBlocFour20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][8];
        temp[1] = matrix[2][7];
        temp[2] = matrix[2][6];
        temp[3] = matrix[1][8];
        temp[4] = matrix[1][7];
        temp[5] = matrix[1][6];
        temp[6] = matrix[18][5];
        temp[7] = matrix[18][4];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
      
       public int readBlocSeven20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[8][2];
        temp[1] = matrix[8][1];
        temp[2] = matrix[6][18];
        temp[3] = matrix[7][2];
        temp[4] = matrix[7][1];
        temp[5] = matrix[5][18];
        temp[6] = matrix[6][1];
        temp[7] = matrix[4][18];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
       
        public int readBlocEight20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[13][1];
        temp[1] = matrix[11][18];
        temp[2] = matrix[11][17];
        temp[3] = matrix[12][1];
        temp[4] = matrix[10][18];
        temp[5] = matrix[10][17];
        temp[6] = matrix[9][18];
        temp[7] = matrix[9][17];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
        
       public int readBlocFourteen20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[1][13];
        temp[1] = matrix[1][12];
        temp[2] = matrix[1][11];
        temp[3] = matrix[18][11];
        temp[4] = matrix[18][10];
        temp[5] = matrix[18][9];
        temp[6] = matrix[17][10];
        temp[7] = matrix[17][9];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
       
        public int readBlocFifteen20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[2][16];
        temp[1] = matrix[2][15];
        temp[2] = matrix[2][14];
        temp[3] = matrix[1][16];
        temp[4] = matrix[1][15];
        temp[5] = matrix[1][14];
        temp[6] = matrix[18][13];
        temp[7] = matrix[18][12];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
        
       public int readBlocTwentyTwo20x20(int [][] matrix)
    {
        int word = 0;
        int temp [] = new int [8];
        
        temp[0] = matrix[16][2];
        temp[1] = matrix[16][1];
        temp[2] = matrix[14][18];
        temp[3] = matrix[15][2];
        temp[4] = matrix[15][1];
        temp[5] = matrix[13][18];
        temp[6] = matrix[14][1];
        temp[7] = matrix[12][18];
     
        for (int k = 0; k<8; k++)
        {
          word = word + (temp[k] << k);
        }
        return word;
      }
}
