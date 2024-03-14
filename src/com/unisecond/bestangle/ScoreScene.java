..
package com.unisecond.bestangle;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;

public class ScoreScene {
	static final String outImgType = "png";
	
	static final double THIRD_PART_MULTIPLE = 250.0;
	static final double FOUR_PART_MULTIPLE = 2.5;
	static final double WOUND_AREA_MULTIPLE = 128;
	static final double HAND_EXPAND_RATIO = 0.2;

	static final int HAND_INDX = 0;
	static final int HEMOSTAT_INDX = 3;
	static final int GUILLE_INDX = 9; //porteaiguille
	static final int ASPIRATOR_INDX = 12;
	static final int RETRACTORFRONT_INDX = 4;
	static final int RETRACTORBACK_INDX = 21;
	static final int RETRACTOR_INDX = 22;
	static final int WOUND_INDX = 2;
	static final int KNIFE_INDX = 6;
	
	
	static boolean debugmode = false;
	/*

 
	*/
	
	public static void  main(String args[])
	{
		try {
			if (args[5].equals("d"))
				debugmode = true;

			int boxStart =Integer.parseInt(args[1]);
			int boxEnd = Integer.parseInt(args[2]);
			int baseIndx = Integer.parseInt(args[3]);

			if (args.length == 10 ) {
				String imgDir = args[4];
				String cropDir = args[6];
				
				for(int i=boxStart; i<boxStart+boxEnd; i++)
				{
					int fidx = i-boxStart+baseIndx;
					if (debugmode) System.out.print(i+"\r\n");
					
					String boxFile = args[0]+"/exp"+i+"/labels/"+args[7];
					FileInputStream stream = new FileInputStream(boxFile);
					InputStreamReader reader = new InputStreamReader(stream);
					BufferedReader buffer = new BufferedReader(reader);

					File imgfile = new File(imgDir+"/"+args[8]+fidx+".png");
			        BufferedImage image = ImageIO.read(imgfile);
			        
					cropHand1(image, buffer, fidx, cropDir, Double.parseDouble(args[9]));
					
					buffer.close();reader.close();stream.close();

				}				
			}
			else if (args.length == 7 ) {
				String imgDir = args[4];
				String cropDir = args[6];
				
				for(int i=boxStart; i<boxEnd; i++)
				{
					int fidx = i-boxStart+baseIndx;
					if (debugmode) System.out.print(i+"\r\n");
					String crtDir = args[0]+"/exp"+i+"/labels";
					
					File directory = new File(crtDir);
					File[] fList = directory.listFiles();
	
			        for (File file : fList){
						String fname =file.getName();
						fname = fname.substring(0,fname.indexOf("."));

						File imgfile = new File(imgDir+"/"+fidx+"/"+fname+".png");
				        BufferedImage image = ImageIO.read(imgfile);

						FileInputStream stream = new FileInputStream(file);
						InputStreamReader reader = new InputStreamReader(stream);
						BufferedReader buffer = new BufferedReader(reader);

						cropWound1(image, buffer, fname, ""+fidx, cropDir);
						
						buffer.close();reader.close();stream.close();
			        }
				}
			}
			else {

				FileOutputStream fout = new FileOutputStream(args[4]); 
	
				for(int i=boxStart; i<boxEnd; i++)
				{
					if (debugmode) System.out.print(i+"\r\n");
					String crtDir = args[0]+"/exp";
					crtDir = crtDir+i+"/labels";
					
					File directory = new File(crtDir);
					File[] fList = directory.listFiles();
	
					double maxscore =-1;
					String maxIdx = "";
			        for (File file : fList){
						String fname =file.getName();
						if (debugmode) System.out.print(fname+"\r\n");
			        	
						//Processing a file
						FileInputStream stream = new FileInputStream(file);
						InputStreamReader reader = new InputStreamReader(stream);
						BufferedReader buffer = new BufferedReader(reader);
						 
						double score = score_scene(buffer);
			
						buffer.close();reader.close();stream.close();
						
						if ( score >= maxscore) {
							maxscore = score;
							maxIdx = fname;
						}
			        }	
			        fout.write((""+i+","+maxIdx+","+maxscore+"\r\n").getBytes());
				}
		        fout.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	static void cropHand(BufferedImage image, BufferedReader b, int idx, String odir, double thr) throws IOException
	{
		ArrayList<Vector<VisualEntity>> entityTable = VisualEntity.loadVEntities(b, false);
		
		Vector<VisualEntity> hands = entityTable.get(HAND_INDX);
		
		int oldW = image.getWidth();
		int oldH = image.getHeight();

		if (hands.size()>0) {
			for (int i=0; i<hands.size(); i++) {
				VisualEntity crtHand = hands.elementAt(i);
				if (crtHand.conf>=thr) {
					int lx = (int)Math.round(oldW*(crtHand.x-crtHand.w/2));
					int ux = (int)Math.round(oldW*(crtHand.x+crtHand.w/2));
					int ly = (int)Math.round(oldH*(crtHand.y-crtHand.h/2));
					int uy = (int)Math.round(oldH*(crtHand.y+crtHand.h/2));
					
					BufferedImage dest = image.getSubimage(lx, ly, ux-lx, uy-ly);
					Image resultingImage = dest.getScaledInstance(ux-lx, uy-ly, Image.SCALE_DEFAULT);
				    BufferedImage outputImage = new BufferedImage(ux-lx, uy-ly, BufferedImage.TYPE_INT_RGB);
				    outputImage.getGraphics().drawImage(resultingImage, 
				    									0, 
				    									0, null);	
					/*
				    BufferedImage outputImage = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
				    outputImage.getGraphics().drawImage(resultingImage, 
				    									(oldW-(ux-lx))/2, 
				    									(oldH-(uy-ly))/2, null);	
				    									*/	    

					File fo = new File(odir+"/"+idx+"_"+i+"."+outImgType);
					if (!fo.exists())
						fo.createNewFile();
					
			        // Writing to file taking type and path as
			        ImageIO.write(outputImage, outImgType, fo);

				}
			}
		}
	}
	
	static void cropHand1(BufferedImage image, BufferedReader b, int idx, String odir, double thr) throws IOException
	{
		ArrayList<Vector<VisualEntity>> entityTable = VisualEntity.loadVEntities(b, false);
		if (entityTable.get(WOUND_INDX).size()>0)
		{
			Vector<Vector<VisualEntity>> tools_list = new Vector<Vector<VisualEntity>>(2);
			tools_list.addElement(entityTable.get(WOUND_INDX));
			tools_list.addElement(entityTable.get(KNIFE_INDX));
			tools_list.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list.addElement(entityTable.get(RETRACTORFRONT_INDX));
			tools_list.addElement(entityTable.get(ASPIRATOR_INDX));
			tools_list.addElement(entityTable.get(GUILLE_INDX));
			
			Vector<VisualEntity> hands = entityTable.get(HAND_INDX);
			
			int oldW = image.getWidth();
			int oldH = image.getHeight();
	
			if (hands.size()>0) {
				for (int i=0; i<hands.size(); i++) {
					VisualEntity crtHand = hands.elementAt(i);
					int crtlx = (int)Math.round(oldW*(crtHand.x-crtHand.w/2));
					int crtux = (int)Math.round(oldW*(crtHand.x+crtHand.w/2));
					int crtly = (int)Math.round(oldH*(crtHand.y-crtHand.h/2));
					int crtuy = (int)Math.round(oldH*(crtHand.y+crtHand.h/2));
					
					if (crtlx < 0)
						crtlx =0;
					if (crtux >= oldW)
						crtux =oldW-1;
					if (crtly < 0)
						crtly =0;
					if (crtuy >= oldH)
						crtuy =oldH-1;
					
					
					double maxthr =0;
					int maxj =-1;
					int maxk =-1;
					
					for (int j=0; j<tools_list.size(); j++) {
						for (int k=0; k<tools_list.elementAt(j).size(); k++) {
							double crtScore = VisualEntity.mutualScore(crtHand, tools_list.elementAt(j).elementAt(k));
							if(crtScore > thr && crtScore > maxthr) {
								maxthr = crtScore;
								maxj =j;
								maxk =k;
							}
						}
					}
					
					if (maxj > -1) {
						int j = maxj;
						int k = maxk;
						int thelx = (int)Math.round(oldW*(tools_list.elementAt(j).elementAt(k).x-
																			tools_list.elementAt(j).elementAt(k).w*HAND_EXPAND_RATIO));
						int theux = (int)Math.round(oldW*(tools_list.elementAt(j).elementAt(k).x+
																			tools_list.elementAt(j).elementAt(k).w*HAND_EXPAND_RATIO));
						int thely = (int)Math.round(oldH*(tools_list.elementAt(j).elementAt(k).y-
																			tools_list.elementAt(j).elementAt(k).h*HAND_EXPAND_RATIO));
						int theuy = (int)Math.round(oldH*(tools_list.elementAt(j).elementAt(k).y+
																			tools_list.elementAt(j).elementAt(k).h*HAND_EXPAND_RATIO));
						
						if (thelx < 0)
							thelx =0;
						if (theux >= oldW)
							theux =oldW-1;
						if (thely < 0)
							thely =0;
						if (theuy >= oldH)
							theuy =oldH-1;
						
						if (thelx < crtlx)
							crtlx = thelx;
						if (theux > crtux)
							crtux = theux;
						if (thely < crtly)
							crtly = thely;
						if (theuy > crtuy)
							crtuy = theuy;
							
						BufferedImage dest = image.getSubimage(crtlx, crtly, crtux-crtlx, crtuy-crtly);
						Image resultingImage = dest.getScaledInstance(crtux-crtlx, crtuy-crtly, Image.SCALE_DEFAULT);
					    BufferedImage outputImage = new BufferedImage(crtux-crtlx, crtuy-crtly, BufferedImage.TYPE_INT_RGB);
					    outputImage.getGraphics().drawImage(resultingImage, 
					    									0, 
					    									0, null);	
							/*
						    BufferedImage outputImage = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
						    outputImage.getGraphics().drawImage(resultingImage, 
						    									(oldW-(ux-lx))/2, 
						    									(oldH-(uy-ly))/2, null);	
						    									*/	    
		
						File fo = new File(odir+"/"+idx+"_"+i+"."+outImgType);
						if (!fo.exists())
							fo.createNewFile();
						
				        // Writing to file taking type and path as
				        ImageIO.write(outputImage, outImgType, fo);
					}
				}
			}
		}
	}

	
	static void cropWound(BufferedImage image, BufferedReader b, String ord, String fname, String odir) throws IOException
	{
		ArrayList<Vector<VisualEntity>> entityTable = VisualEntity.loadVEntities(b, false);
		
		if (entityTable.get(WOUND_INDX).size()>0)
		{
			Vector<Vector<VisualEntity>> tools_list = new Vector<Vector<VisualEntity>>(2);
			tools_list.addElement(entityTable.get(KNIFE_INDX));
			tools_list.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list.addElement(entityTable.get(RETRACTORFRONT_INDX));
			tools_list.addElement(entityTable.get(ASPIRATOR_INDX));
			tools_list.addElement(entityTable.get(GUILLE_INDX));
			

			Vector<Vector<VisualEntity>> tools_list1 = new Vector<Vector<VisualEntity>>(2);
			tools_list1.addElement(entityTable.get(KNIFE_INDX));
			tools_list1.addElement(entityTable.get(HEMOSTAT_INDX));
			
			Vector<Vector<VisualEntity>> tools_list2 = new Vector<Vector<VisualEntity>>(2);
			tools_list2.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list2.addElement(entityTable.get(RETRACTORFRONT_INDX));
			tools_list2.addElement(entityTable.get(GUILLE_INDX));
			
			tools_list2.addElement(entityTable.get(HAND_INDX));
			
			Vector<Integer> wound_multiples = gatherWoundTools(entityTable.get(WOUND_INDX),tools_list);
				
			cropThreeParts(image, entityTable.get(WOUND_INDX),tools_list1,tools_list2,entityTable.get(HAND_INDX), 
							wound_multiples,
							ord,fname,odir);
		}
	}
	


	static void cropWound1(BufferedImage image, BufferedReader b, String ord, String fname, String odir) throws IOException
	{
		ArrayList<Vector<VisualEntity>> entityTable = VisualEntity.loadVEntities(b, false);
		
		if (entityTable.get(WOUND_INDX).size()>0)
		{
			Vector<Vector<VisualEntity>> tools_list = new Vector<Vector<VisualEntity>>(2);
			tools_list.addElement(entityTable.get(KNIFE_INDX));
			tools_list.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list.addElement(entityTable.get(RETRACTORFRONT_INDX));
			tools_list.addElement(entityTable.get(ASPIRATOR_INDX));
			tools_list.addElement(entityTable.get(GUILLE_INDX));
			
			//Vector<Vector<VisualEntity>> tools_list1 = new Vector<Vector<VisualEntity>>(2);
			
			Vector<Vector<VisualEntity>> tools_list2 = new Vector<Vector<VisualEntity>>(2);
			tools_list2.addElement(entityTable.get(KNIFE_INDX));
			tools_list2.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list2.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list2.addElement(entityTable.get(RETRACTORFRONT_INDX));
			tools_list2.addElement(entityTable.get(GUILLE_INDX));
			
			tools_list2.addElement(entityTable.get(HAND_INDX));
			
			Vector<Integer> wound_multiples = gatherWoundTools(entityTable.get(WOUND_INDX),tools_list);
				
			cropThreeParts(image, entityTable.get(WOUND_INDX),null,tools_list2,entityTable.get(HAND_INDX), 
							wound_multiples,
							ord,fname,odir);
		}
	}


	
	static double score_scene(BufferedReader b) throws IOException
	{
		ArrayList<Vector<VisualEntity>> entityTable = VisualEntity.loadVEntities(b, false);
		
		double score =0;
		if (entityTable.get(WOUND_INDX).size()>0)
		{
			Vector<Vector<VisualEntity>> tools_list = new Vector<Vector<VisualEntity>>(3);
			tools_list.addElement(entityTable.get(KNIFE_INDX));
			tools_list.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list.addElement(entityTable.get(RETRACTOR_INDX));
			tools_list.addElement(entityTable.get(RETRACTORFRONT_INDX ));
			tools_list.addElement(entityTable.get(ASPIRATOR_INDX ));
			tools_list.addElement(entityTable.get(GUILLE_INDX));

			Vector<Vector<VisualEntity>> tools_list1 = new Vector<Vector<VisualEntity>>(3);
			tools_list1.addElement(entityTable.get(HAND_INDX));

			Vector<Vector<VisualEntity>> tools_list2 = new Vector<Vector<VisualEntity>>(3);
			tools_list2.addElement(entityTable.get(KNIFE_INDX));
			tools_list2.addElement(entityTable.get(HEMOSTAT_INDX));
			tools_list2.addElement(entityTable.get(RETRACTORFRONT_INDX ));

			Vector<Integer> wound_multiples = gatherWoundTools(entityTable.get(WOUND_INDX),tools_list2);
			
			/*
			if (debugmode) System.out.print("wound-knife-hand\r\n");
			score +=evalThreeParts(entityTable.get(WOUND_INDX),entityTable.get(KNIFE_INDX),entityTable.get(HAND_INDX), wound_multiples);
			if (debugmode) System.out.print("wound-HEMOSTAT-hand\r\n");
			score +=0.01*evalThreeParts(entityTable.get(WOUND_INDX),entityTable.get(HEMOSTAT_INDX),entityTable.get(HAND_INDX),wound_multiples);
			if (debugmode) System.out.print("wound-RETRACTOR-hand\r\n");
			score +=0.01*evalThreeParts(entityTable.get(WOUND_INDX),entityTable.get(RETRACTOR_INDX),entityTable.get(HAND_INDX),wound_multiples);
			*/
			
			if (debugmode) System.out.print("wound-tools-hand\r\n");
			score +=evalSelectThreeParts(entityTable.get(WOUND_INDX),tools_list,entityTable.get(HAND_INDX), wound_multiples);
			if (debugmode) System.out.print("score:"+score+"\r\n");

			if (debugmode) System.out.print("wound-hand-retback\r\n");
			score +=evalSelectThreeParts(entityTable.get(WOUND_INDX),tools_list1,entityTable.get(RETRACTORBACK_INDX), wound_multiples)
					/FOUR_PART_MULTIPLE;
			if (debugmode) System.out.print("score:"+score+"\r\n");

			if (debugmode) System.out.print("wound-tools-hand-retback\r\n");
			score += FOUR_PART_MULTIPLE*evalSelectFourParts(entityTable.get(WOUND_INDX),entityTable.get(RETRACTORFRONT_INDX),entityTable.get(HAND_INDX),entityTable.get(RETRACTORBACK_INDX), wound_multiples);
			if (debugmode) System.out.print("score:"+score+"\r\n");

			if (debugmode) System.out.print("wound-hand\r\n");
			score +=3*evalTwoParts(entityTable.get(WOUND_INDX),entityTable.get(HAND_INDX));
			if (debugmode) System.out.print("score:"+score+"\r\n");
		}
		
		if (debugmode) System.out.print("KNIFE-hand\r\n");
		score +=evalTwoParts(entityTable.get(KNIFE_INDX),entityTable.get(HAND_INDX));
		if (debugmode) System.out.print("score:"+score+"\r\n");
		score +=evalTwoParts(entityTable.get(GUILLE_INDX),entityTable.get(HAND_INDX));
		if (debugmode) System.out.print("score:"+score+"\r\n");
		
		/*
		if (debugmode) System.out.print("HEMOSTAT-hand\r\n");
		score +=0.5*evalTwoParts(entityTable.get(HEMOSTAT_INDX),entityTable.get(HAND_INDX));
		if (debugmode) System.out.print("score:"+score+"\r\n");
		
		if (debugmode) System.out.print("RETRACTOR-hand\r\n");
		score +=0.5*evalTwoParts(entityTable.get(RETRACTOR_INDX),entityTable.get(HAND_INDX));
		score +=0.5*evalTwoParts(entityTable.get(RETRACTORFRONT_INDX),entityTable.get(HAND_INDX));
		if (debugmode) System.out.print("score:"+score+"\r\n");
		 */
		
		return score;
	}
	static Vector<Integer> gatherWoundTools(Vector<VisualEntity> w, Vector<Vector<VisualEntity>> tlist)
	{
		Vector<Integer> ml = new Vector<Integer>(w.size());
		for(int i=0; i<w.size(); i++)
			ml.addElement(new Integer(0));

		for (int i=0; i<w.size(); i++) {
			for(int j=0; j< tlist.size(); j++) {
				for(int k=0; k< tlist.elementAt(j).size(); k++) {
					double ovlp =  VisualEntity.mutualScore(w.elementAt(i), tlist.elementAt(j).elementAt(k));
					if (ovlp > 0.0)
						ml.set(i, new Integer(ml.elementAt(i)+1));
				}
			}	
		}

		return ml;
	}

	
	static double evalTwoParts(Vector<VisualEntity> p1, Vector<VisualEntity> p2)
	{
		double ret =0;
		if (p1.size()>0) {
			for (int i=0; i<p1.size(); i++) {
				VisualEntity crtE = p1.elementAt(i);
				ret += crtE.conf;
				for(int j=0; j< p2.size(); j++) {
					VisualEntity crtE1 = p2.elementAt(j);
					ret += crtE1.conf;
					if (debugmode) System.out.print("Blocks:"+crtE.conf+" "+crtE1.conf+"\r\n");
					ret +=  VisualEntity.mutualScore(crtE, crtE1);
					if (debugmode) System.out.print(ret+"\r\n");
				}	
			
				ret *= (1+WOUND_AREA_MULTIPLE*p1.elementAt(i).area());
			}
		}
		return ret/THIRD_PART_MULTIPLE;
	}
	
	static double evalThreeParts(Vector<VisualEntity> p1, Vector<VisualEntity> p2, Vector<VisualEntity> p3, Vector<Integer> ml)
	{
		double ret =0;
		if (p1.size()>0) {
			Vector<Double> wound_knife_scores = new Vector<Double>();
			Vector<VisualEntity> wound_knife = new Vector<VisualEntity>();
			
			for (int i=0; i<p1.size(); i++) {
				VisualEntity crtWound = p1.elementAt(i);
				ret += 	crtWound.conf;
				for(int j=0; j< p2.size(); j++) {
					VisualEntity crtE1 = p2.elementAt(j);
					ret += crtE1.conf;
					double crtScore = VisualEntity.mutualScore(crtWound, crtE1);
					if(crtScore > 0) {
						ret += crtScore;
						if (ml==null)
							wound_knife_scores.addElement(new Double(crtScore));
						else
							wound_knife_scores.addElement(new Double(crtScore*(1+ml.elementAt(i)*ml.elementAt(i))));
						wound_knife.addElement(crtE1);
					}
				}	
			}
			
			for (int i=0; i<wound_knife.size(); i++) {
				VisualEntity crtKnife = wound_knife.elementAt(i);
				for(int j=0; j< p3.size(); j++) {
					double crtScore = VisualEntity.mutualScore(crtKnife, p3.elementAt(j));
					ret += wound_knife_scores.elementAt(i)*crtScore;
					if (debugmode)  System.out.print("Blocks:"+crtKnife.conf+" "+p3.elementAt(j).conf+"\r\n");
					if (debugmode)  System.out.print(wound_knife_scores.elementAt(i)+" "+crtScore+"\r\n");				}
			}
		}
		
		return ret;
	}
	
	static void cropThreeParts(BufferedImage image,
								Vector<VisualEntity> p1, 
								Vector<Vector<VisualEntity>> pList1, 
								Vector<Vector<VisualEntity>> pList2,
								Vector<VisualEntity> p3, 
								Vector<Integer> ml,
								String ord, String fname, String odir) throws IOException
	{
		int lx=0;
		int ux=0;
		int ly=0;
		int uy=0;
		
		int oldW = image.getWidth();
		int oldH = image.getHeight();

		if (p1.size()>0) {
			int crtlx=oldW;
			int crtux=0;
			int crtly=oldH;
			int crtuy=0;
						
			double maxScore = -1.0;
			for (int i=0; i<p1.size(); i++) {
				double crtW = 0.0f;
				Vector<Double> wound_knife_scores = new Vector<Double>();
				Vector<VisualEntity> wound_knife = new Vector<VisualEntity>();
				
				VisualEntity crtWound = p1.elementAt(i);
				if (debugmode)  System.out.print("Wound:"+crtWound.conf+"\r\n");
				crtlx = (int)Math.round(oldW*(crtWound.x-crtWound.w/2));
				crtux = (int)Math.round(oldW*(crtWound.x+crtWound.w/2));
				crtly = (int)Math.round(oldH*(crtWound.y-crtWound.h/2));
				crtuy = (int)Math.round(oldH*(crtWound.y+crtWound.h/2));
				
				if (pList1 != null)
				for(int n=0; n < pList1.size(); n++)
				{
					Vector<VisualEntity> p2 = pList1.elementAt(n);

					for(int j=0; j< p2.size(); j++) {
						double crtScore = VisualEntity.mutualScore(crtWound, p2.elementAt(j));
						if(crtScore > 0) {
							if (ml==null)
								wound_knife_scores.addElement(new Double(crtScore));
							else
								wound_knife_scores.addElement(new Double(crtScore*(1+ml.elementAt(i)*ml.elementAt(i))));
							wound_knife.addElement(p2.elementAt(j));
							
							int thelx = (int)Math.round(oldW*(p2.elementAt(j).x-p2.elementAt(j).w/2));
							int theux = (int)Math.round(oldW*(p2.elementAt(j).x+p2.elementAt(j).w/2));
							int thely = (int)Math.round(oldH*(p2.elementAt(j).y-p2.elementAt(j).h/2));
							int theuy = (int)Math.round(oldH*(p2.elementAt(j).y+p2.elementAt(j).h/2));
							
							if (thelx < 0)
								thelx =0;
							if (theux >= oldW)
								theux =oldW-1;
							if (thely < 0)
								thely =0;
							if (theuy >= oldH)
								theuy =oldH-1;

							if (thelx < crtlx)
								crtlx = thelx;
							if (theux > crtux)
								crtux = theux;
							if (thely < crtly)
								crtly = thely;
							if (theuy > crtuy)
								crtuy = theuy;
						}
					}	
				}
				for (int j=0; j<wound_knife.size(); j++) {
					VisualEntity crtKnife = wound_knife.elementAt(j);
					for(int k=0; k< p3.size(); k++) {
						double crtScore = VisualEntity.mutualScore(crtKnife, p3.elementAt(k));
						if (debugmode)  System.out.print("Blocks:"+crtKnife.conf+" "+p3.elementAt(k).conf+"\r\n");
						if (debugmode)  System.out.print(wound_knife_scores.elementAt(j)+" "+crtScore+"\r\n");
						
						int thelx = (int)Math.round(oldW*(p3.elementAt(k).x-p3.elementAt(k).w/2));
						int theux = (int)Math.round(oldW*(p3.elementAt(k).x+p3.elementAt(k).w/2));
						int thely = (int)Math.round(oldH*(p3.elementAt(k).y-p3.elementAt(k).h/2));
						int theuy = (int)Math.round(oldH*(p3.elementAt(k).y+p3.elementAt(k).h/2));
						
						if (thelx < 0)
							thelx =0;
						if (theux >= oldW)
							theux =oldW-1;
						if (thely < 0)
							thely =0;
						if (theuy >= oldH)
							theuy =oldH-1;

						if (thelx < crtlx)
							crtlx = thelx;
						if (theux > crtux)
							crtux = theux;
						if (thely < crtly)
							crtly = thely;
						if (theuy > crtuy)
							crtuy = theuy;
					}
					
				}
				
				if (pList2 != null)
				for(int n=0; n < pList2.size(); n++)
				{
					Vector<VisualEntity> p2 = pList2.elementAt(n);

					for(int j=0; j< p2.size(); j++) {
						double crtScore = VisualEntity.mutualScore(crtWound, p2.elementAt(j));
						if(crtScore > 0) {
							if (ml==null)
								crtW += crtScore;
							else
								crtW += crtScore*(1+ml.elementAt(i)*ml.elementAt(i));
							
							int thelx = (int)Math.round(oldW*(p2.elementAt(j).x-p2.elementAt(j).w/2));
							int theux = (int)Math.round(oldW*(p2.elementAt(j).x+p2.elementAt(j).w/2));
							int thely = (int)Math.round(oldH*(p2.elementAt(j).y-p2.elementAt(j).h/2));
							int theuy = (int)Math.round(oldH*(p2.elementAt(j).y+p2.elementAt(j).h/2));
							
							if (thelx < 0)
								thelx =0;
							if (theux >= oldW)
								theux =oldW-1;
							if (thely < 0)
								thely =0;
							if (theuy >= oldH)
								theuy =oldH-1;
							
							if (thelx < crtlx)
								crtlx = thelx;
							if (theux > crtux)
								crtux = theux;
							if (thely < crtly)
								crtly = thely;
							if (theuy > crtuy)
								crtuy = theuy;
						}
					}	
				}
				
				if (crtW > maxScore)
				{
					maxScore = crtW;
					 lx=crtlx;
					 ux=crtux;
					 ly=crtly;
					 uy=crtuy;
				}
			}
		}
		else
			return;
		
		BufferedImage dest = image.getSubimage(lx, ly, ux-lx, uy-ly);
		int newW, newH;
		if ((ux-lx)*oldH > (uy-ly)*oldW) {
			newW = oldW;
			newH = (int)Math.floor((uy-ly)*(double)oldW/(double)(ux-lx));
		}
		else {
			newW = (int)Math.floor((ux-lx)*(double)oldH/(double)(uy-ly));
			newH = oldH;		
		}
			
		Image resultingImage = dest.getScaledInstance(newW, newH, Image.SCALE_DEFAULT);
	    BufferedImage outputImage = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
	    outputImage.getGraphics().drawImage(resultingImage, (oldW-newW)/2, (oldH-newH)/2, null);		    

		File fo = new File(odir+"/"+fname+"_"+ord+"."+outImgType);
		if (!fo.exists())
			fo.createNewFile();
		
        // Writing to file taking type and path as
        ImageIO.write(outputImage, outImgType, fo);
	}
	
	static double evalSelectFourParts(Vector<VisualEntity> p1, 
									Vector<VisualEntity> p2, 
									Vector<VisualEntity> p3, 
									Vector<VisualEntity> p4,
									Vector<Integer> ml)
	{
		double ret =0;
		
		if (p1.size()>0) {
			for (int i=0; i<p1.size(); i++) {
				double crtW = 0.0f;
				Vector<Double> wound_knife_scores = new Vector<Double>();
				Vector<VisualEntity> wound_knife = new Vector<VisualEntity>();
				
				VisualEntity crtWound = p1.elementAt(i);
				if (debugmode)  System.out.print("Wound:"+crtWound.conf+"\r\n");
				
				for(int j=0; j< p2.size(); j++) {
					double crtScore = VisualEntity.mutualScore(crtWound, p2.elementAt(j));
					if(crtScore > 0) {
						if (debugmode)  System.out.print("crtScore:"+crtScore+"\r\n");
						if (ml==null)
							wound_knife_scores.addElement(new Double(crtScore));
						else
							wound_knife_scores.addElement(new Double(crtScore*(1+ml.elementAt(i)*ml.elementAt(i))));
						wound_knife.addElement(p2.elementAt(j));
					}
				}
				
				Vector<Double> wound_knife_hand_scores = new Vector<Double>();
				Vector<VisualEntity> wound_knife_hand = new Vector<VisualEntity>();
				for(int j=0; j< wound_knife.size(); j++) {
					VisualEntity crtknife = wound_knife.elementAt(j);
					if (debugmode)  System.out.print("crtknife:"+crtknife.conf+"\r\n");

					for(int k=0; k< p3.size(); k++) {
						double crtScore = VisualEntity.mutualScore(crtknife, p3.elementAt(k));
						if(crtScore > 0) {
							if (debugmode)  System.out.print("crtScore:"+crtScore+"\r\n");
							if (ml==null)
								wound_knife_hand_scores.addElement(new Double(crtScore));
							else
								wound_knife_hand_scores.addElement(new Double(crtScore*(1+ml.elementAt(i)*ml.elementAt(i))));
							wound_knife_hand.addElement(p3.elementAt(k));
						}
					}
				}
					
				for (int j=0; j<wound_knife_hand.size(); j++) {
					VisualEntity crthand = wound_knife_hand.elementAt(j);
					for(int k=0; k< p4.size(); k++) {
						double crtScore = VisualEntity.mutualScore(crthand, p4.elementAt(k));
						crtW += wound_knife_hand_scores.elementAt(j)*crtScore;
						if (debugmode)  System.out.print("Blocks:"+crthand.conf+" "+p4.elementAt(k).conf+"\r\n");
						if (debugmode)  System.out.print(wound_knife_hand_scores.elementAt(j)+" "+crtScore+" "+crtW+"\r\n");
					}
				}
				crtW *= (1+WOUND_AREA_MULTIPLE*crtWound.area());
				
				if (crtW > ret)
					ret = crtW;
			}
		}
		
		return ret;
	}
	
	static double evalSelectThreeParts(Vector<VisualEntity> p1, Vector<Vector<VisualEntity>> pList, Vector<VisualEntity> p3, Vector<Integer> ml)
	{
		double ret =0;
		
		if (p1.size()>0) {
			for (int i=0; i<p1.size(); i++) {
				double crtW = 0.0f;
				Vector<Double> wound_knife_scores = new Vector<Double>();
				Vector<VisualEntity> wound_knife = new Vector<VisualEntity>();
				
				VisualEntity crtWound = p1.elementAt(i);
				if (debugmode)  System.out.print("Wound:"+crtWound.conf+"\r\n");
				
				for(int n=0; n < pList.size(); n++)
				{
					Vector<VisualEntity> p2 = pList.elementAt(n);

					for(int j=0; j< p2.size(); j++) {
						double crtScore = VisualEntity.mutualScore(crtWound, p2.elementAt(j));
						if(crtScore > 0) {
							if (debugmode)  System.out.print("crtScore:"+crtScore+"\r\n");
							if (ml==null)
								wound_knife_scores.addElement(new Double(crtScore));
							else
								wound_knife_scores.addElement(new Double(crtScore*(1+ml.elementAt(i)*ml.elementAt(i))));
							wound_knife.addElement(p2.elementAt(j));
						}
					}	
				}
					
				for (int j=0; j<wound_knife.size(); j++) {
					VisualEntity crtKnife = wound_knife.elementAt(j);
					for(int k=0; k< p3.size(); k++) {
						double crtScore = VisualEntity.mutualScore(crtKnife, p3.elementAt(k));
						crtW += wound_knife_scores.elementAt(j)*crtScore;
						if (debugmode)  System.out.print("Blocks:"+crtKnife.conf+" "+p3.elementAt(k).conf+"\r\n");
						if (debugmode)  System.out.print(wound_knife_scores.elementAt(j)+" "+crtScore+" "+crtW+"\r\n");
					}
				}
				
				crtW *= (1+WOUND_AREA_MULTIPLE*crtWound.area());

				
				if (crtW > ret)
					ret = crtW;
			}
			
			
		}
		
		return ret;
	}
}
