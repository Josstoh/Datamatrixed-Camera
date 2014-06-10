package com.liris.datamatrixedcamera.app.lecture;

/* The purpose of this class is to create a polynomial 
 * with the codewords read in the datamatrix and to correct those 
 * words using Reed Solomon code.
 * This code is able to correct between 6 and 9 false codewords (depends
 * on the matrix's size)
 * If there is too many false codewords in the matrix,
 * the code will return the original codewords.
 * Warning : only works with 16x16, 18x18 DataMatrix (ECC 200), to use it with
 * others matrix, you have to change the initialization of the parameters
 * n,k,t and generator.
 * 
 */

public class RSCorrector {
    
    private polynomial data;
    private int nMin,nMax,tMax,kMin;
    private int n,k,t,generator;
    private field gf = new field();
    private RSAlgorithm rsAlg;
    private boolean isNotCorrected = false;
    
    public int [] Correction (int [] mots)
    {
        //initialization 
        double d;
        Double D;
        n = mots.length;
        k = 8;
        switch (n)
        {
            case 24 : 
                t = 6;
                break;
            case 32 : 
                t = 7;
                break;
            case 40 : 
                t = 9;
                break;
            default : 
                break;
        }
        rsAlg = new RSAlgorithm(n,t);
        nMin = (2 * t) + 1;
        tMax = (int) (n-1) / 2;
        nMax = (1 << k) - 1;
        d = Math.log(n) / Math.log(2);
        D = new Double(d);
        kMin = (int) D.intValue() + 1;
        generator = 301;
        data = new polynomial(n);
        
        //processing
        int cpt1 = n-1;
        int cpt2 = n-1;
        polynomial temp;
        int [] codewordsCorrected = new int[n]; 
        
        for (int j = 0; j<n; j++)
        {
            data.coefficient[cpt1] = mots[j];
            cpt1--;
        }
        data.degree = rsAlg.degree(data);
        
        temp = rsAlg.processing(data);
        temp.degree = rsAlg.degree(temp);
        
        this.isNotCorrected = rsAlg.failed;
        
        for (int j = 0; j<n; j++)
        {
            codewordsCorrected[j] = temp.coefficient[cpt2];
            cpt2--;
        }
        return codewordsCorrected;
    }
    
    public boolean getIsNotCorrected()
    {
        return isNotCorrected;
    }
    
}
