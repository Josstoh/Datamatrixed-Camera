package com.liris.datamatrixedcamera.app.traitement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


public class Action {

//static	Mat autocseuil;
   
//------------------------------------GrayScale----------------------------------------------------------//	
	
	public  Mat grayScale(Mat tmp) {
	       Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
	            return tmp;
	 }	
    
	
	//------------------------------------GrayScale----------------------------------------------------------// 
    
	
	
	
	public  Mat OurgrayScale(Mat tmp) {
	       for(int i=0;i<=tmp.rows();i++)	
	       for(int j=0;j<=tmp.cols();j++)
	    		{
	    	   double x=0.3 * tmp.get(i, j)[0]+0.59 * tmp.get(i, j)[1]+0.11 * tmp.get(i, j)[2];
	    	   tmp.put(i, j,x); }
               return tmp;
	 }	
 
	
	
	
	
	//-------------------------------------------AutoCorrelation----------------------------------------------------------// 

	
	
    public Mat autoCorrelation(Mat input)
	    {
	   
		 
		 System.out.println("Begin1");
         int N=Core.getOptimalDFTSize(input.cols());
	     int M=Core.getOptimalDFTSize(input.rows());
         List<Mat> planes = new ArrayList<Mat>() ;
	     Mat padded=new Mat();
	     //Log.e("input type:", input.type()+"");
	     input.convertTo(input,CvType. CV_32F );
	     //Log.e("input:", input.type()+"");
	     Imgproc.copyMakeBorder(input, padded, 0, M-input.rows(), 0, N-input.cols(), Imgproc.BORDER_CONSTANT);
	     Mat zero=  Mat.zeros( M, N, CvType.CV_32F); 
         planes.add(padded);
	     planes.add(zero);
//	     Log.e("padded:", padded+"");
	     Mat complexImg=new Mat();
	    
	    

	    Core.merge(planes ,complexImg);

	   // Log.e("before dft:", complexImg+"");

	    Core.dft(complexImg,complexImg); // add a boolean variable. 
	    
	    
	    
	    Core.mulSpectrums(complexImg, complexImg, complexImg, 0, true);



        Core.idft(complexImg, complexImg);
        Core.split(complexImg, planes);

        Mat auto=planes.get(0);



        // rearrange the quadrants of Fourier image  so that the origin is at the image center

        int cx = auto.cols()/2;
        int cy = auto.rows()/2;
        org.opencv.core.Rect rectq0 = new  org.opencv.core.Rect  (0, 0, cx, cy);
        org.opencv.core.Rect rectq1 = new  org.opencv.core.Rect (cx, 0, cx, cy);
        org.opencv.core.Rect rectq2 = new  org.opencv.core.Rect  (0, cy, cx, cy);
        org.opencv.core.Rect rectq3 = new  org.opencv.core.Rect  (cx, cy, cx, cy);

        Mat q0=new Mat (auto, rectq0);
        Mat q1=new Mat (auto, rectq1);
        Mat q2=new Mat (auto, rectq2);
        Mat q3=new Mat (auto, rectq3);
        Mat tmp=new Mat();                           // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);

        Core.normalize(auto, auto, 0, 255,Core.NORM_MINMAX);

        return auto;
      
	    }
	 

//---------------------------------------------AutoCorrelation----------------------------------------------------------//	 
	 
	 
	 
	 
	 
	 
	 
//---------------------------------------------Local Max Extraction----------------------------------------------------------//	 
	 
	 public Mat maxExtraction (Mat input, int size,Mat grayscaleMatrix)
	    {
	    	int FSize=4;   // changement 
 	    	

	        
 Mat autocseuil=Mat.zeros(size, size, CvType.CV_8U);
	    for (int nl =FSize+1; nl<=size-FSize-1;nl++)    
	    {

		    for (int nc =FSize+1; nc<=size-FSize-1;nc++)
		    {
		     
		    	
		    	double max=-0;    
        for(int i=nc-FSize; i<=nc+FSize;i++)
        {
     	   if (input.get(nl-FSize, i)[0]>max) max=input.get(nl-FSize, i)[0];
        }
    	
     for(int i=nc-FSize; i<=nc+FSize;i++)
     {
  	   if (input.get(nl+FSize, i)[0]>max) max=input.get(nl+FSize, i)[0];
     }	
     	   
     	   
     	   
         
		    if (input.get(nl,nc)[0] < max)
         autocseuil.put(nl, nc, 0);
     else
     	autocseuil.put(nl, nc, 255);

		    	
		    }
	
	    }
	    
	   
Mat ess_var=autocseuil.submat(new org.opencv.core.Rect(192,192,128,128));

	    
	    
   gridParameters_(ess_var,grayscaleMatrix);
//      Mat imRedressee =gridParameters(autocseuil,grayscaleMatrix);
	   //Mat x=accumulation(autocseuil);
	   //SaveImage(autocseuil);
         //return imRedressee ;
       return ess_var;
	    }


	 
	 //---------------------------------------------------------------------------------------------------------------------------
	 
	 
	 
	 public Mat maxExtraction_ (Mat input, int size,Mat grayscaleMatrix)
	    {
	    	int FSize=3;   // changement 
	    	
	        
Mat autocseuil=Mat.zeros(128, 128, CvType.CV_8U);
	    for (int nl =FSize+1+192; nl<=FSize-1+192+128;nl++)    
	    {

		    for (int nc =FSize+1+192; nc<=FSize-1+192+128;nc++)
		    {
		     
		    	
		    	double max=-0;    
     for(int i=nc-FSize; i<=nc+FSize;i++)
     {
  	   if (input.get(nl-FSize, i)[0]>max) max=input.get(nl-FSize, i)[0];
     }
 	
  for(int i=nc-FSize; i<=nc+FSize;i++)
  {
	   if (input.get(nl+FSize, i)[0]>max) max=input.get(nl+FSize, i)[0];
  }	
  	   
  	   
  	   
      int i=nl-FSize-1-192;
      int j=nc-FSize-1-192;
		    if (input.get(nl,nc)[0] < max)
      autocseuil.put(i, j, 0);
  else
  	autocseuil.put(i, j, 255);

		    	
		    }
	
	    }
	    
	    int nmbzero=0;
	    for (int i=0;i<autocseuil.rows();i++)
	    	for (int j=0;j<autocseuil.cols();j++) if (autocseuil.get(i, j)[0]==255) nmbzero++;
	    
	    System.out.println(" le nombre de zero " +nmbzero );
  // gridParameters_(autocseuil,grayscaleMatrix);
   Mat imRedressee =gridParameters(autocseuil,grayscaleMatrix);
	   //Mat x=accumulation(autocseuil);
	   //SaveImage(autocseuil);
  //   return autocseuil ;
   return imRedressee;
	    }


	 
	 
	//---------------------------------------------Local Max Extraction----------------------------------------------------------//	 

	 
	

    
    
    public void  gridParameters_(Mat input, Mat grayscaleMatrix)
    {
     	System.out.println("GridParamters "+ input.rows()+ " "+input.cols());
    	Vector<Bary> blob=new  Vector<Bary>();   
    for(int l=0;l<input.rows();l++){
        for (int c=0;c<input.cols();c++){
                if (input.get(l, c)[0]>128){
        		    Pair <Object,Object> x;
        		    x=remplir(input,l,c);
        		   Integer tab[]=  (Integer[]) x.getfirst();
        		     input=(Mat) x.getsecond(); 
        		    blob.add(new Bary(tab[0],tab[1],0));
        	}
        }
        }
    
    

    	
    	
    	System.out.println("le nombre de points de Barycenter "+ blob.size());   
    
    
    	for (int i=0;i<blob.size();i++)
        {
        	Bary b=blob.get(i);
        	
        	System.out.println(" "+ b.l + "  " + b.c   );
        	
        }
    
    
    }
    
    
    
    
    
    
    
    
    
    public Mat gridParameters(Mat input, Mat grayscaleMatrix)
    {
    	System.out.println("GridParamters");
    	Vector<Bary> blob=baryCenter(input);
        //Vector<Bary> blob=rempli_bary();
    	Vector<Bary> directionVerticale =new Vector<Bary>();
    	Vector<Bary> directionHorizontale=new Vector<Bary>();

    for (int i=0;i<blob.size();i++)
    {
    	Bary b=blob.get(i);
    	
    	System.out.println(" "+ b.l + "  " + b.c   );
    	
    }

    	
    	
    	System.out.println("le nombre de points de Barycenter "+ blob.size());
    	 
    	


    	
    	for (int n=0; n<blob.size();n++){
    		int D2Hmin,D2Vmin;
    		D2Hmin=D2Vmin= 1000000;
    		int iVmin,iHmin;
    		    iVmin=iHmin=0;
    		for (int i=0;i<blob.size();i++){
    			
    			if (i==n){
    				continue;
    			}
    			
    			int deltaL=blob.get(n).l-blob.get(i).l;
    			int deltaC=blob.get(n).c-blob.get(i).c;
    			int D2= deltaL*deltaL+deltaC*deltaC;
    		
    			 if (Math.abs(deltaL)>Math.abs(deltaC))
    			 {
    	            if (D2<D2Vmin) //point i en dessus ou en dessous de point n
    	                {
    	            	D2Vmin=D2;
    	                
    	                iVmin=i;
    	                }
    	        
    			 }    
    			 
    			 else{ // point i � gauche ou � droite
    	            if (D2<D2Hmin)
    	                {D2Hmin=D2;
    	                iHmin=i;
    	                }
    			 }
    			
    			
    			
    		}
    	
    	
    	
    	     // Stockage des candidats direction verticale
    		int i=iVmin;
    		int deltaL=blob.get(n).l-blob.get(i).l;
    		int deltaC=blob.get(n).c-blob.get(i).c;
    		int D2= deltaL*deltaL+deltaC*deltaC;
    	    if (deltaL<0)
    	    {
    	       deltaL=-deltaL;
    	       deltaC=-deltaC;
    	    }
    	    directionVerticale.add(new Bary(deltaL,deltaC,D2));
    	    
    	    
    	    // Stockage des candidats direction horizontale
    	    i=iHmin;
    	      deltaL=blob.get(n).l-blob.get(i).l;
    		  deltaC=blob.get(n).c-blob.get(i).c;
    	    D2=deltaL*deltaL+deltaC*deltaC;
    	    if (deltaC<0)
    	       {deltaL=-deltaL;
    	       deltaC=-deltaC;
    	       }
    	    directionHorizontale.add(new Bary(deltaL,deltaC,D2));
    	
    	}
    	

    	
 	//   for (int k=0;k<directionHorizontale.size();k++) System.out.println(directionHorizontale.size() +" : " + directionHorizontale.get(k).value);

    	Collections.sort(directionVerticale, new Comparator<Bary>() {
    	    public int compare(Bary s1, Bary s2) {
    	       
    	        return (s1.value.compareTo(s2.value));
    	    }
    	});
     
    	
    	
    	Collections.sort(directionHorizontale, new Comparator<Bary>() {
    	    public int compare(Bary s1, Bary s2) {
    	       
    	        return (s1.value.compareTo(s2.value));
    	    }
    	});
    	

    	
    	//System.out.println("--------------------------------directionVerticale-------------------------------------------"+ directionVerticale.size());
    	//for (int i=0;i<directionVerticale.size();i++){
    		//System.out.println(directionVerticale.get(i).l+ "     "+directionVerticale.get(i).c+"       "+directionVerticale.get(i).value);
    	//}
    	
    	//System.out.println("---------------------------------directionHorizontale------------------------------------------"+directionHorizontale.size());
    	
//    	for (int i=0;i<directionHorizontale.size();i++){
    	//	System.out.println(directionHorizontale.get(i).l+ "     "+directionHorizontale.get(i).c+"       "+directionHorizontale.get(i).value);
    	//}

    	
    	
    	
    	
    //Extraction des caract�ristiques sur partie verticale
    	int NbBary=blob.size();
    	
    	int NbBaryS2=   Math.round(NbBary/2)+1;
    	int deltaMedian=    Math.round(NbBary/20)+1;

    	System.out.println("NbBary=  "+ NbBary);
    	System.out.println("start=  "+ NbBaryS2);
    	System.out.println("end= "+ deltaMedian);
   double distanceGrilleVerticale=this.mean(directionVerticale.subList(NbBaryS2-deltaMedian-1, NbBaryS2+deltaMedian),3); // a verifier 


   
   System.out.println("  distanceGrilleVerticale= " +distanceGrilleVerticale);
     int  n=0;
    Vector<Bary> newDV =new Vector<Bary>();
    for (int i=0;i<directionVerticale.size();i++){
    	if (Math.abs(directionVerticale.get(i).value-distanceGrilleVerticale)<0.1*distanceGrilleVerticale){
    		n++;
    		newDV.add(directionVerticale.get(i));
    	}
    	
    }
    int NbNewDV=n;
    int NbNewDVs2=Math.round(NbNewDV/2);
    deltaMedian=Math.round(NbNewDV/5)+1;
	System.out.println(" NbNewDVs2=  "+  NbNewDVs2);
	
	System.out.println("deltaMedian=  "+ deltaMedian);
    
    Collections.sort(newDV, new Comparator<Bary>() {
        public int compare(Bary s1, Bary s2) {
           
            return (s1.l.compareTo(s2.l));
        }
    });

    Vector<Bary>deltaLsort=newDV;
    Log.i("ACTION",String.valueOf(deltaLsort.size()));



    double deltaLGrilleVerticale=mean(deltaLsort.subList(NbNewDVs2-deltaMedian-1, NbNewDVs2+deltaMedian),1); // a verifier
    Collections.sort(newDV, new Comparator<Bary>() {
        public int compare(Bary s1, Bary s2) {
           
            return (s1.c.compareTo(s2.c));
        }
    });

     Vector<Bary>  deltaCsort=  newDV;
    double deltaCGrilleVerticale=mean(deltaCsort.subList(NbNewDVs2-deltaMedian-1, NbNewDVs2+deltaMedian),2); // a verifier 

  

    
   // List<Bary> v=deltaCsort.subList(NbNewDVs2-deltaMedian-1, NbNewDVs2+deltaMedian);
    //for(int i=0;i<v.size();i++){System.out.println(deltaCsort.size()+" " +v.size()+" " + v.get(i).c);}
    
    
    
    System.out.println("  deltaLGrilleVerticale= " +deltaLGrilleVerticale);
    System.out.println("  deltaCGrilleVerticale= " +deltaCGrilleVerticale);

    // Extraction des caract�ristiques sur partie horizontale

    NbBaryS2=Math.round(NbBary/2)+1;
    deltaMedian=Math.round(NbBary/20)+1;
    
    double distanceGrilleHorizontale=mean(directionHorizontale.subList(NbBaryS2-deltaMedian-1, NbBaryS2+deltaMedian),3); // a verifier 

    System.out.println("  distanceGrilleHorizontale= " +distanceGrilleHorizontale);



    n=0;
    Vector<Bary> newDH =new Vector<Bary>(); 
    for (int i=0;i<NbBary;i++){

    	if (Math.abs(directionHorizontale.get(i).value-distanceGrilleHorizontale)<0.1*distanceGrilleHorizontale){
          n++;
          newDH.add(directionHorizontale.get(i)); 
           
    	}
    }

    int NbNewDH=n;
    int NbNewDHs2=Math.round(NbNewDH/2);
      deltaMedian=Math.round(NbNewDH/5)+1;
      System.out.println("  NbNewDHs2= " +NbNewDHs2);
      System.out.println("  deltaMedian= " +deltaMedian);
  
      
      
    Collections.sort(newDH, new Comparator<Bary>() {
        public int compare(Bary s1, Bary s2) {
           
            return (s1.l.compareTo(s2.l));
        }
    });
    deltaLsort= newDH;
     

    double deltaLGrilleHorizontale=mean(deltaLsort.subList(NbNewDHs2-deltaMedian-1,NbNewDHs2+deltaMedian),1);
      Collections.sort(newDH, new Comparator<Bary>() {
    	    public int compare(Bary s1, Bary s2) {
    	       
    	        return (s1.c.compareTo(s2.c));
    	    }
    	});
    	deltaCsort= newDH;
     double deltaCGrilleHorizontale=mean(deltaCsort.subList(NbNewDHs2-deltaMedian-1,NbNewDHs2+deltaMedian),2);
     System.out.println("  deltaCGrilleHorizontale= " +deltaCGrilleHorizontale);

     System.out.println("  deltaLGrilleHorizontale= " +deltaLGrilleHorizontale);

    System.out.println("Transformation de l'image pour redressement du marquage");
    // Transformation de l'image pour redressement du marquage

    Mat imRedressee=Mat.zeros(256,256,CvType.CV_32FC1);
    int echelleZoom=10;

    double[][]A=new double[2][2]; 
    A[0][0]=deltaLGrilleHorizontale/echelleZoom;  
    A[0][1]=deltaCGrilleHorizontale/echelleZoom;
    A[1][0]=deltaLGrilleVerticale/echelleZoom;  
    A[1][1]=deltaCGrilleVerticale/echelleZoom;
    System.out.println(deltaLGrilleHorizontale);
    System.out.println(deltaCGrilleHorizontale);
    System.out.println(deltaLGrilleVerticale);
    System.out.println(deltaCGrilleVerticale);

    System.out.println(A[0][0]+" " + A[0][1]+" "+ A[1][0]+" "+ A[1][1]);
    for (int l=0;l<=255;l++)
    {
    	for (int c=0;c<=255;c++){
    		
    		double p1=(A[0][0]*(l-128))+(A[0][1]*(c-128));
    		double p2=(A[1][0]*(l-128))+(A[1][1]*(c-128));
    		double ll=p1+256;
    		double cc=p2+256;
    		double alpha=cc-Math.floor(cc);
    		double beta=ll-Math.floor(ll);
    		int x=(int) Math.floor(ll);
    		int y=(int) Math.floor(cc);

    if ((x>0)&&(x<509)&&(y>0)&&(y<509)){
        double data=Math.floor(grayscaleMatrix.get(x, y)[0]*(1-alpha)*(1-beta)+grayscaleMatrix.get(x+1, y)[0]*(1-alpha)*(beta)+grayscaleMatrix.get(x, y+1)[0]*(alpha)*(1-beta)+grayscaleMatrix.get(x+1, y+1)[0]*(alpha)*(beta));
    	imRedressee.put(l, c, data);
    		}
    		
    	}


    }
    
    
    
    return imRedressee;
    }


    
    
    
 

public Mat profils(Mat imRedressee )   {
    System.out.println("Affichage des profils");


    //for (int i=0;i<imRedressee.cols();i++){System.out.println(" "+imRedressee.get(0, i)[0]);}
     
    //Projections horizontales et verticales.
    double Profils10Ligne[]=new double[256];
    double Profils20Ligne[]=new double[256];
    double ProfilsLigne[]=new double[256];
    double Profils10Colonne[]=new double[256];
    double Profils20Colonne[]=new double[256];
    double ProfilsColonne[]=new double[256];
    for(int l=0;l<256;l++){
    double somme10=AnalyseProfil10(imRedressee.row(l));
    double somme20=AnalyseProfil20(imRedressee.row(l));
    Profils10Ligne[l]=somme10;	
    Profils20Ligne[l]=2*somme20;
    ProfilsLigne[l]=somme10+2*somme20;
    somme10=AnalyseProfilColonne10(imRedressee.col(l));
    somme20=AnalyseProfilColonne20(imRedressee.col(l));
    Profils10Colonne[l]=somme10;	
    Profils20Colonne[l]=2*somme20;
    ProfilsColonne[l]=somme10+2*somme20;
    }

    System.out.println("verification");
    for(int i=0;i<Profils10Colonne.length;i++) System.out.println("signal10 "+Profils10Colonne[i]);


    //Binarisation des variations du marquage.
    //Analyse des plages positives et n�gatives.
    double [] signal10=AnalyseProfil_2_10(ProfilsLigne);
    double [] signal20=AnalyseProfil_2_20(ProfilsLigne);
    double [] signeSignal10= new double[signal10.length];
    int L=signal10.length;
    System.out.println(" the size of signal10 is "+signal10.length );
  int   n=0;
    int debut=0;
    if (signal10[n]>0) signeSignal10[n]=1; else  signeSignal10[n]=0;
    int noZone=-1;
    debut=n;
    double maxi=Math.abs(signal10[n]);
    int postitionmaxi=n;
    double signatureProfil[][]=new double[256][7];
    for (n=1;n<L;n++){
    	if (signal10[n]>0) signeSignal10[n]=1; else signeSignal10[n]=0;
    	
    	//Analyse
    	
    	if (signeSignal10[n-1]==signeSignal10[n]){
    		if (maxi<= Math.abs(signal10[n])){
    			maxi=Math.abs(signal10[n]);
    			postitionmaxi=n;
    		}
    	}
    	
    	else {
    		noZone++;
    		signatureProfil[noZone][0]=debut; // debut de la zone
    		signatureProfil[noZone][1]=n-1;// fin de la zone
    		signatureProfil[noZone][2]=n-debut; // langeur de la zone 
    		signatureProfil[noZone][3]=postitionmaxi;// % position du maxi de la zone
    		signatureProfil[noZone][4]=maxi; // valeur du maxi de la zone
    		 if (signeSignal10[n-1]>0)
             signatureProfil[noZone][5]=1; // signe du maxi de la zone
         else
        	  signatureProfil[noZone][5]=0; // signe du maxi de la zone
              signatureProfil[noZone][6]=0; // estimation de la qualit� du groupe
        	  debut=n;
              maxi=Math.abs(signal10[n]); //mki
    	}
    	
    }

    System.out.println("noZone======"+noZone);
    int NombreZones=noZone;

    noZone=16;//mki
    if (signatureProfil[noZone][5]==1)   noZone++;

    // Detection du centre de la zone la plus probable
    // Il faudrait tenir compte de la largeur de chaque zone 
    while (noZone<NombreZones-18)
    {
    	for (n=9;n<=15;n++)//mki
        {     signatureProfil[noZone][6]=signatureProfil[noZone][6]+signatureProfil[noZone-n][4]+signatureProfil[noZone+n][4];}
       
        noZone=noZone+2;
    }

    System.out.println("Affichage Finale");

    for (int i=0;i<NombreZones;i++)
    {System.out.println(signatureProfil[i][0]+" "+signatureProfil[i][1]+" "+signatureProfil[i][2]+" "+signatureProfil[i][3]+" "+signatureProfil[i][4]+" "+signatureProfil[i][5]+" "+signatureProfil[i][6]+" ");}

    System.out.println("Affichage Finale");

    //calculer le max de la colonne 7

    

     
     

     
    
     
     // Profil Colonne
     signal10=null;
     signal20=null;
     signal10=new double[256];
     signal10=AnalyseProfil_2_10(ProfilsColonne);
     signal20=AnalyseProfil_2_20(ProfilsColonne);
     signeSignal10= new double[signal10.length];
     L=signal10.length;
     
     
    System.out.println(" Profil Colonne the size of signal10 is "+signal10.length );
     n=0;
     debut=0;
     if (signal10[n]>0) signeSignal10[n]=1; else  signeSignal10[n]=0;
     noZone=-1;
     debut=n;
      maxi=Math.abs(signal10[n]);
      postitionmaxi=n;
     double signatureProfilColonne[][]=new double[256][7];
     for (n=1;n<L;n++){
     	if (signal10[n]>0) signeSignal10[n]=1; else signeSignal10[n]=0;
     	
     	//Analyse
     	
     	if (signeSignal10[n-1]==signeSignal10[n]){
     		if (maxi<= Math.abs(signal10[n])){
     			maxi=Math.abs(signal10[n]);
     			postitionmaxi=n;
     		}
     	}
     	
     	else {
     		noZone++;
     		signatureProfilColonne[noZone][0]=debut; // debut de la zone
     		signatureProfilColonne[noZone][1]=n-1;// fin de la zone
     		signatureProfilColonne[noZone][2]=n-debut; // langeur de la zone 
     		signatureProfilColonne[noZone][3]=postitionmaxi;// % position du maxi de la zone
     		signatureProfilColonne[noZone][4]=maxi; // valeur du maxi de la zone
     		 if (signeSignal10[n-1]>0)
              signatureProfilColonne[noZone][5]=1; // signe du maxi de la zone
          else
         	  signatureProfilColonne[noZone][5]=0; // signe du maxi de la zone
               signatureProfilColonne[noZone][6]=0; // estimation de la qualit� du groupe
         	  debut=n;
               maxi=Math.abs(signal10[n]);
     	}
     	
     }

     
     
     System.out.println("noZone======"+noZone);
     NombreZones=noZone;

     noZone=16;
     if (signatureProfilColonne[noZone][5]==1)   noZone++;

     // Detection du centre de la zone la plus probable
     // Il faudrait tenir compte de la largeur de chaque zone 
     while (noZone<NombreZones-17)
     {
     	for (n=9;n<=15;n++)
     {signatureProfilColonne[noZone][6]=signatureProfilColonne[noZone][6]+signatureProfilColonne[noZone-n][4]+signatureProfilColonne[noZone+n][4];}
        
    noZone=noZone+2;
     }


     
     
     double max=signatureProfil[0][6];
     int posmax=0;
     for (int i=1;i<=NombreZones;i++){
     if (signatureProfil[i][6]>max){
     	max=signatureProfil[i][6];
     	posmax=i;
     }

     	
      }
    System.out.println(" kk "+ max+ "  tt " + posmax);
     n=posmax;
     Core.line(imRedressee, new org.opencv.core.Point(0,signatureProfil[n][3]), new org.opencv.core.Point(255,signatureProfil[n][3]), new Scalar(255) );
    Log.i("Ligne 1","n="+n+"("+0+","+signatureProfil[n][3]+")"+" ("+255+","+signatureProfil[n][3]+")");
     int delta=(int) (signatureProfil[n][3]-signatureProfil[n-2][3]);
     n=posmax;
     while((delta>7) && (delta<14) && (n>2)){
     	n=n-2;
     	Core.line(imRedressee, new org.opencv.core.Point(0,signatureProfil[n][3]), new org.opencv.core.Point(255,signatureProfil[n][3]), new Scalar(0,255,0) );
         Log.i("Ligne 2","n="+n+"("+0+","+signatureProfil[n][3]+")"+" ("+255+","+signatureProfil[n][3]+")");
     	delta=  (int) (signatureProfil[n][3]-signatureProfil[n-2][3]);
     	
     }
      n=posmax;
      delta=(int) (signatureProfil[n+2][3]-signatureProfil[n][3]);
      while((delta>7) && (delta<14) && (n<NombreZones-2)){
      n=n+2;
      Core.line(imRedressee, new org.opencv.core.Point(0,signatureProfil[n][3]), new org.opencv.core.Point(255,signatureProfil[n][3]), new Scalar(0,255,0) );
          Log.i("Ligne 3","n="+n+"("+0+","+signatureProfil[n][3]+")"+" ("+255+","+signatureProfil[n][3]+")");
      delta=(int) (signatureProfil[n+2][3]-signatureProfil[n][3]);
      }
     
     
     
     
     
     
     
     

System.out.println("Test 1");
     
    //calculer le max de la colonne 7

     max=signatureProfilColonne[0][6];
    posmax=0;
    for (int i=1;i<=NombreZones;i++){
    if (signatureProfilColonne[i][6]>max){
    	max=signatureProfilColonne[i][6];
    	posmax=i;
    }

    	
    }

    
    System.out.println("Test 2");
    
    n=posmax;
    Core.line(imRedressee, new org.opencv.core.Point(signatureProfilColonne[n][3],0), new org.opencv.core.Point(signatureProfilColonne[n][3],255), new Scalar(255) );
    Log.i("Ligne 4","n="+n+"("+signatureProfil[n][3]+","+0+")"+" ("+signatureProfil[n][3]+","+255+")");
     
    System.out.println("Test 3");
     delta=(int) (signatureProfilColonne[n][3]-signatureProfilColonne[n-2][3]);
    n=posmax;
    while((delta>7) && (delta<14) && (n>2)){
    	n=n-2;
    	
     System.out.println(" "+signatureProfilColonne[n][3]);    	
    	Core.line(imRedressee, new org.opencv.core.Point(signatureProfilColonne[n][3],0), new org.opencv.core.Point(signatureProfilColonne[n][3],255), new Scalar(0,255,0) );
        Log.i("Ligne 5","n="+n+"("+signatureProfil[n][3]+","+0+")"+" ("+signatureProfil[n][3]+","+255+")");
    	delta=  (int) (signatureProfilColonne[n][3]-signatureProfilColonne[n-2][3]);
    }
    n=posmax;
    delta=(int) (signatureProfilColonne[n+2][3]-signatureProfilColonne[n][3]);
    
  
    
    System.out.println("Test 4");

    
    while((delta>7) && (delta<14) && (n<NombreZones-2)){
    n=n+2;
    Core.line(imRedressee, new org.opencv.core.Point(signatureProfilColonne[n][3],0), new org.opencv.core.Point(signatureProfilColonne[n][3],255), new Scalar(0,255,0) );
        Log.i("Ligne 6","n="+n+"("+signatureProfil[n][3]+","+0+")"+" ("+signatureProfil[n][3]+","+255+")");
    delta=(int) (signatureProfilColonne[n+2][3]-signatureProfilColonne[n][3]);
    }



    System.out.println("Test 5");

     

    
    
    
    
    
    
    
    return  imRedressee;
     
    }
 
    
    
    
    
    public double[] AnalyseProfil_2_10(double[] signal){
    	double[] signal10=new double[signal.length];
    	int  L=signal.length;
    	int  delta10=5;

    	for (int l=delta10;l<=L-delta10-2;l++){
    	signal10[l]=2*signal[l]-signal[l-delta10]-signal[l+delta10];	
    	}
    	return signal10;	
    	}


    	public double[] AnalyseProfil_2_20(double[] signal){
    	double[] signal20=new double[signal.length];
    	int  L=signal.length;
    	int  delta20=15;

    	for (int l=delta20;l<=L-delta20-2;l++){
    	signal20[l]=2*signal[l]-signal[l-delta20]-signal[l+delta20];	
    	}
    	return signal20;	
    	}




    	public double AnalyseProfil10(Mat signal){
    		
    	double somme=0;
    	int delta10=5;
    	int L=signal.cols();
    	for (int l=delta10;l<=L-delta10-2;l++)
    		somme=somme+Math.abs(signal.get(0, l-delta10)[0]+signal.get(0, l+delta10)[0]-2*signal.get(0, l)[0]);
    		return somme;
    	}


    	public double AnalyseProfil20(Mat signal){
    		
    	double somme=0;
    	int delta20=10;
    	int L=signal.cols();
    	for (int l=delta20;l<=L-delta20-2;l++)
    		somme=somme+Math.abs(signal.get(0, l-delta20)[0]+signal.get(0, l+delta20)[0]-2*signal.get(0, l)[0]);
    		return somme;
    	}






    	public double AnalyseProfilColonne10(Mat signal){
    		
    	double somme=0;
    	int delta10=5;
    	int L=signal.rows();
    	for (int l=delta10;l<=L-delta10-2;l++)
    		somme=somme+Math.abs(signal.get(l-delta10,0 )[0]+signal.get(l+delta10,0 )[0]-2*signal.get(l, 0)[0]);
    		return somme;
    	}


    	public double AnalyseProfilColonne20(Mat signal){
    		
    	double somme=0;
    	int delta20=10;
    	int L=signal.rows();
    	for (int l=delta20;l<=L-delta20-2;l++)
    		somme=somme+Math.abs(signal.get( l-delta20,0)[0]+signal.get( l+delta20,0)[0]-2*signal.get( l,0)[0]);
    		return somme;
    	}


    	 public Vector<Bary> baryCenter  (Mat input){
    			  
    	//int [] listBarryCenter =new int []
    			  Vector<Bary> blob=new Vector<Bary>();
    			       Mat label_image=new Mat();
    				   Mat binary=new Mat();   

    				
    			//Apply thresholding
    				   binary=convertToBinary(input);


    	binary.convertTo(binary, CvType.CV_32FC1);

    	int label_count = 2; // starts at 2 because 0,1 are used already




    	    for(int y=0; y < binary.rows(); y++) {
    			          for(int x=0; x < binary.cols(); x++) {
    			        	  if( binary.get(y, x)[0] != 1) {
    			                  continue;
    			              }

    			              
    			              org.opencv.core.Rect rect=new org.opencv.core.Rect();
    			             
    			              Mat mask = Mat.zeros(binary.rows(), label_image.cols(), CvType.CV_8UC1);
    			             
    	Imgproc.floodFill(binary,mask ,new org.opencv.core.Point(x,y), new org.opencv.core.Scalar(label_count), rect,new org.opencv.core.Scalar(0),new org.opencv.core.Scalar(0),4);


    	 		              int bary=0;
    	                      int nbpoint=0;
    			              for(int i=rect.y; i < (rect.y+rect.height); i++) {
    			                  for(int j=rect.x; j < (rect.x+rect.width); j++) {
    			                      if((int)binary.get(i,j)[0] == label_count) {
    			                          bary=bary+label_count;

    			                    	nbpoint++;  
    			                      }

    			                      
    			                  }
    			              }

    			              bary=bary/nbpoint;
    			              blob.add(new Bary(y,x,bary));

    			              label_count++;
    			          }
    			      }
    			  
    		  
    	 	  
    		  return blob;
    		  }

    	 
    	 
    	 
    	 
    	
    	 public Pair <Object,Object> remplir  (Mat input,int l, int c){

         Vector<Bary> blob=new Vector<Bary>();
    	 
         
         int ncourant,n,N,sommeL,sommeC;
         blob.add(new Bary(l,c,0));
         n=0;
         ncourant=0;
         N=0;
         sommeL=sommeC=0;
         
           while(ncourant<=n){
        	 Bary b=blob.get(ncourant);
        	 l=b.l; 
        	 c=b.c;
        	if(input.get(l, c)[0]>128){
        	  N++;
        	  input.put(l, c, 100);
        	  sommeL=sommeL+l;
        	  sommeC=sommeC+c;
        	
        	 //4 connexe
        	  
        	  if(input.get(l-1, c)[0]>128){
        		n++;  
        		blob.add(new Bary(l-1,c,0));
        	  }
        	 
        	  

        	  if(input.get(l, c-1)[0]>128){
        		n++;  
        		blob.add(new Bary(l,c-1,0));
        	  }
        	  
        	  

        	  if(input.get(l+1, c)[0]>128){
        		n++;  
        		blob.add(new Bary(l+1,c,0));
        	  }
        	  
        	  

        	  if(input.get(l, c+1)[0]>128){
        		n++;  
        		blob.add(new Bary(l,c+1,0));
        	  }
        	  
        	  
         	 //8 connexe 	  
        	  
        	  
        	  if(input.get(l-1, c-1)[0]>128){
          		n++;  
          		blob.add(new Bary(l-1,c-1,0));
          	  }
        	  
        	  
        	  if(input.get(l-1, c+1)[0]>128){
          		n++;  
          		blob.add(new Bary(l-1,c+1,0));
          	  }
        	  
        	  
        	  if(input.get(l+1, c-1)[0]>128){
          		n++;  
          		blob.add(new Bary(l+1,c-1,0));
          	  }
        	  
        	  
        	  if(input.get(l+1, c+1)[0]>128){
          		n++;  
          		blob.add(new Bary(l+1,c+1,0));
          	  }
        		 
        		 
        	 }
        	 
        	ncourant++;
         }
         
         
           Integer tab[]=new Integer[2];
           tab[0]=new Integer(sommeL/N);
           tab[1]=new Integer(sommeC/N);
           
           return new Pair (tab,input);
    	 }
    	 
    	 
    	 
    	 
    	 

    		  

    		  
    		  public Mat convertToBinary(  Mat   source)
    		  {
    		      
    			  Mat destination = new Mat();
    			  int minThreshold = 0;
    		      int maxThreshold = 1;
    		      
    		      // A matrix to hold the grayscale image
    		     // Mat grayscale;

    		// Create thresholded image in destination Mat
    		      Imgproc.threshold(source, destination, minThreshold, maxThreshold, Imgproc.THRESH_BINARY);
    		      return destination;
    		  }



    		   	    
    		    
    		    
    		    public void SaveImage (Mat mat) {
    		        File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
    		 	   path.mkdirs();
    		 	   File file = new File(path, "image.png");
    		        String filename = file.toString();
    		 	   filename= "/storage/extSdCard/Data-matrix/autocorr.jpg";
    		 	   Boolean bool = Highgui.imwrite(filename, mat);
    		        if (bool)
    		 	    Log.i("0", "SUCCESS writing image to external storage " + filename);
    		 	   else
    		 	    Log.i("0", "Fail writing image to external storage");
    		 	}
    			
    		    
    		    
    		    
    		    
    		    
    		    
    		    

    			
    		    public   double mean(List<Bary> v, int code) {
    		        
    		    	double sum = 0;

    		    if (code==1){	
    		        for (int i = 0; i < v.size(); i++) {
    		            sum += v.get(i).l;
    		        }
    		        
    		    }
    		    
    		     
    		    	  
    		    	if (code==2){
    		    	for (int i = 0; i < v.size(); i++) {
    			            sum += v.get(i).c;
    			        }
    			        
    			    }	
    		     
    		    	if (code==3){
    		    		for (int i = 0; i < v.size(); i++) {
    			            sum += v.get(i).value;
    			        }	
    		    	}
    		    	
    		    
    		        return sum / v.size();
    		    }
    		    
    		    
    		      

    		    
    		    
    public Vector<Bary> rempli_bary(){
    		    
    	 Vector <Bary> blob= new  Vector <Bary>();       
    	        blob.add(new Bary(196,202,0));
    	        blob.add(new Bary(198,222,0)); 
    	        blob.add(new Bary(199,242,0)); 
    	        blob.add(new Bary(199,262,0)); 
    	        blob.add(new Bary(200,282,0)); 
    	        blob.add(new Bary(202,302,0)); 
    	        blob.add(new Bary(202,321,0)); 
    	        blob.add(new Bary(215,200,0)); 
    	        blob.add(new Bary(217,220,0)); 
    	        blob.add(new Bary(218,241,0)); 
    	        blob.add(new Bary(218,260,0)); 
    	        blob.add(new Bary(220,280,0)); 
    	        blob.add(new Bary(221,300,0)); 
    	        blob.add(new Bary(222,321,0)); 
    	        blob.add(new Bary(236,199,0)); 
    	        blob.add(new Bary(237,219,0)); 
    	        blob.add(new Bary(238,239,0)); 
    	        blob.add(new Bary(239,259,0)); 
    	        blob.add(new Bary(239,278,0)); 
    	        blob.add(new Bary(240,298,0)); 
    	        blob.add(new Bary(241,318,0)); 
    	        blob.add(new Bary(252,270,0)); 
    	        blob.add(new Bary(255,197,0)); 
    	        blob.add(new Bary(256,218,0)); 
    	        blob.add(new Bary(257,238,0)); 
    	        blob.add(new Bary(257,257,0)); 
    	        blob.add(new Bary(257,276,0)); 
    	        blob.add(new Bary(259,297,0)); 
    	        blob.add(new Bary(260,317,0)); 
    	        blob.add(new Bary(262,244,0)); 
    	        blob.add(new Bary(273,196,0)); 
    	        blob.add(new Bary(274,216,0)); 
    	        blob.add(new Bary(275,236,0)); 
    	        blob.add(new Bary(276,256,0)); 
    	        blob.add(new Bary(277,276,0)); 
    	        blob.add(new Bary(278,296,0)); 
    	        blob.add(new Bary(279,316,0)); 
    	        blob.add(new Bary(293,194,0)); 
    	        blob.add(new Bary(293,214,0)); 
    	        blob.add(new Bary(294,234,0)); 
    	        blob.add(new Bary(296,254,0)); 
    		    blob.add(new Bary(297,274,0)); 
    		    blob.add(new Bary(297,294,0)); 
    		    blob.add(new Bary(299,314,0)); 
    		    blob.add(new Bary(312,193,0)); 
    		    blob.add(new Bary(313,213,0)); 
    		    blob.add(new Bary(314,232,0)); 
    		    blob.add(new Bary(315,252,0)); 
    		    blob.add(new Bary(315,272,0)); 
    		    blob.add(new Bary(317,293,0)); 
    		    blob.add(new Bary(318,312,0)); 
    		    return blob;
    }    
   
    private int round(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;            
        }else{
            return d<0 ? -(i+1) : i+1;          
        }
    }  
	
}
