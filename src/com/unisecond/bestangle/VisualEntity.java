package com.unisecond.bestangle;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

public class VisualEntity {
	static double SIMILLARITY_THRESHOLD = 0.75;  //相似性阈值

	static double ADJUST_THRESHOULD = 0.0001f; //调整阈值

	double x;
	double y;
	double w;
	double h;
	public double conf;
	
	static String[] entityNames = {"hand", "head", "wound", "hemostat", "retractor front",
			"scissors", "electrotome", "detector", "tweezer", "porteaiguille", 
			"nesis", "bistoury", "aspirator", "gauze", "injector", 
			"towel forceps", "tissue", "drainage tube", "bowl", "glue", 
			"patches", "retractor back", "retractor"};    //name of entity
	static double[] entityThreshold = {	0.02f,	0.1f, 0.05f,	0.01f,	0.01f,   //实体阈值
			0.1f,	0.01f,	0.1f,	0.1f,	0.02f,
			0.1f,	0.1f,	0.02f,	0.3f,	0.1f,
			0.1f,	0.1f,	0.1f,	0.1f,	0.1f,
			0.1f,	0.01f, 0.01f};
	public static int[]  entityMap = {0,1,2,3,3,
										5,6,7,8,3,
										10,11,3,13,14,
										15,16,3,18,19,
										20,3,3};

	public static int NUM_ENTITIES = entityNames.length;
	
	 // 是在方法签名中的一部分，它表示该方法可能抛出 IOException 异常。VisualEntity的类型在VisualEntity.java文件中定义
	public static void writeVEntities(ArrayList<Vector<VisualEntity>> e, FileOutputStream outf) throws IOException 
	{
		for (int i=0; i<NUM_ENTITIES; i++)
		{
			Vector<VisualEntity> crtType = e.get(i);
			for (int j=0; j<crtType.size(); j++)
				crtType.elementAt(j).write(i, outf);
		}
		
	}
	
	void write(int t, FileOutputStream outf) throws IOException
	{
		outf.write((""+t+" "+x+" "+y+" "+w+" "+h+" "+conf+"\r\n").getBytes());		
	}

	public static ArrayList<Vector<VisualEntity>> loadVEntities(BufferedReader b, boolean man) throws IOException
	{
		ArrayList<Vector<VisualEntity>> ret = new ArrayList<Vector<VisualEntity>>(NUM_ENTITIES);
		for (int i=0; i<NUM_ENTITIES;i++)
			ret.add(i,new Vector<VisualEntity>());

		String line;
		
		line = b.readLine();
		
		while((line = b.readLine()) != null)
		{
			StringTokenizer t = new StringTokenizer(line, " ");
			String id = t.nextToken();
			VisualEntity ve = new VisualEntity(t.nextToken(),t.nextToken(),t.nextToken(),t.nextToken(),man?"1":t.nextToken());
			int idx = Integer.parseInt(id);
			if (idx >= NUM_ENTITIES) {
				return null;
			}
				
			if (ve.conf >= entityThreshold[idx])
				ret.get(idx).addElement(ve);
		}
		
		return ret;
	}
	
	public VisualEntity(String xs, String ys, String ws, String hs, String confs)
	{
		x = Double.parseDouble(xs);
		y = Double.parseDouble(ys);
		w = Double.parseDouble(ws);
		h = Double.parseDouble(hs);
		conf = Double.parseDouble(confs);
	}
	
	public double area()
	{
		return w*h;
	}
	
	public static Vector<Double> edgeOverlap(VisualEntity e1, VisualEntity e2)
	{
		Vector<Double> ret = new Vector<Double>();
		
		double woverlap = 0;
		if (e1.x - e1.w/2 < e2.x - e2.w/2)
			woverlap = overlap(e1.x, e1.w, e2.x, e2.w);
		else
			woverlap = overlap(e2.x, e2.w, e1.x, e1.w);
		
		double hoverlap = 0;
		if (e1.y - e1.h/2 < e2.y - e2.h/2)
			hoverlap = overlap(e1.y, e1.h, e2.y, e2.h);
		else 
			hoverlap = overlap(e2.y, e2.h, e1.y, e1.h);
		
		ret.addElement(new Double(Math.abs(((e1.x - e1.w/2)-(e2.x - e2.w/2))*
						Math.max(e1.h,e2.h)/((hoverlap+ADJUST_THRESHOULD)*Math.min(e1.h,e2.h)))));
		ret.addElement(new Double(Math.abs(((e1.y - e1.h/2 )-( e2.y - e2.h/2))*
						Math.max(e1.w,e2.w)/((woverlap+ADJUST_THRESHOULD)*Math.min(e1.w,e2.w)))));		
		ret.addElement(new Double(Math.abs(((e1.x + e1.w/2)-(e2.x + e2.w/2))*
						Math.max(e1.h,e2.h)/((hoverlap+ADJUST_THRESHOULD)*Math.min(e1.h,e2.h)))));
		ret.addElement(new Double(Math.abs(((e1.y + e1.h/2 )-( e2.y + e2.h/2))*
						Math.max(e1.w,e2.w)/((woverlap+ADJUST_THRESHOULD)*Math.min(e1.w,e2.w)))));		
		
		return ret;
	}

	
	public static double overlapScore(VisualEntity e1, VisualEntity e2)
	{
		double ret =0;
		
		double woverlap = 0;
		if (e1.x - e1.w/2 < e2.x - e2.w/2)
			woverlap = overlap(e1.x, e1.w, e2.x, e2.w);
		else
			woverlap = overlap(e2.x, e2.w, e1.x, e1.w);
		
		double hoverlap = 0;
		if (e1.y - e1.h/2 < e2.y - e2.h/2)
			hoverlap = overlap(e1.y, e1.h, e2.y, e2.h);
		else 
			hoverlap = overlap(e2.y, e2.h, e1.y, e1.h);
		
		return 2*woverlap*hoverlap/(e1.w*e1.h+e2.w*e2.h);
	}
	
	public static double mutualScore(VisualEntity e1, VisualEntity e2)
	{
		double ret =0;
		
		double woverlap = 0;
		if (e1.x - e1.w/2 < e2.x - e2.w/2)
			woverlap = overlap(e1.x, e1.w, e2.x, e2.w);
		else
			woverlap = overlap(e2.x, e2.w, e1.x, e1.w);
		
		double hoverlap = 0;
		if (e1.y - e1.h/2 < e2.y - e2.h/2)
			hoverlap = overlap(e1.y, e1.h, e2.y, e2.h);
		else 
			hoverlap = overlap(e2.y, e2.h, e1.y, e1.h);
		
		double adjust = 0;
		if (woverlap > ADJUST_THRESHOULD && hoverlap > ADJUST_THRESHOULD )
			adjust = 0.8f+(woverlap+hoverlap)*(woverlap+hoverlap);

		return (woverlap*hoverlap+adjust)*(0.5f+e1.conf*e1.conf*e2.conf*e2.conf); ///(e1.area()*e2.area());
	}
	
	static double overlap(double x1, double w1, double x2, double w2)
	{
		if (x1 + w1/2 < x2 - w2/2)
			return 0;
		else if (x1 - w1/2 > x2 + w2/2)
			return 0;		
		else if (x1 - w1/2 < x2 - w2/2) {
			if (x1 + w1/2 < x2 + w2/2)
				return Math.abs(x1 + w1/2 - ( x2 - w2/2 ));
			else
				return w2;
		}
		else {
			if (x1 + w1/2 > x2 + w2/2)
				return Math.abs(x2 + w2/2 - ( x1 - w1/2 ));
			else
				return w1;
		}
	}		
	
	static boolean edgeClose(int num, VisualEntity v1, 
									VisualEntity v2, double ethr)
	{
		Vector<Double> c = edgeOverlap(v1, v2);
		int ec = 0;
		for(int i=0; i<4; i++)
			if (c.elementAt(i)<ethr)
				ec++;
		
		if (ec >= num)
			return true;
		else
			return false;
	}
	
	static Vector<VisualEntity> traceEntitiesMultiple(int idx, ArrayList<Vector<VisualEntity>> frame, Vector<VisualEntity> p2, double thr, double ethr)
	{
		Vector<VisualEntity> ret = new Vector<VisualEntity>();
		
		int crtCluster = entityMap[idx];
		
		for(int j=0; j< p2.size(); j++) {
			VisualEntity crtE1 = p2.elementAt(j);
			boolean getIt = false;
			for(int k=0; k<NUM_ENTITIES; k++)
				if (entityMap[k]==crtCluster) {
					Vector<VisualEntity> p1 = frame.get(k);
					for (int i=0; i<p1.size(); i++) {
						VisualEntity crtE = p1.elementAt(i);
						if( overlapScore(crtE, crtE1)> thr && edgeClose(2, crtE, crtE1,ethr)) 
						{
							ret.addElement(crtE1);
							getIt = true;
							break;
						}
					}
					if (getIt)
						break;
				}
		}
		
		return ret;
	} 
	
	static Vector<VisualEntity> traceEntities(Vector<VisualEntity> p1, Vector<VisualEntity> p2, double thr)
	{
		Vector<VisualEntity> ret = new Vector<VisualEntity>();
		for (int i=0; i<p1.size(); i++) {
			VisualEntity crtE = p1.elementAt(i);
			double thisMatch = -1;
			VisualEntity maxEntity = null;
			for(int j=0; j< p2.size(); j++) {
				VisualEntity crtE1 = p2.elementAt(j);
				double val = overlapScore(crtE, crtE1);
				if (val > thr && val >thisMatch) {
					thisMatch = val;
					maxEntity = crtE1;
				}
			}	
			if (maxEntity != null )
				ret.addElement(maxEntity);
		}
		return ret;
	}

	static boolean compareEntities(Vector<VisualEntity> p1, Vector<VisualEntity> p2)
	{
		if (p1.size()>0) {
			for (int i=0; i<p1.size(); i++) {
				VisualEntity crtE = p1.elementAt(i);
				boolean thisMatch = false;
				for(int j=0; j< p2.size(); j++) {
					VisualEntity crtE1 = p2.elementAt(j);
					double val = overlapScore(crtE, crtE1);
					if (val > SIMILLARITY_THRESHOLD) {
						thisMatch = true;
					}
				}	
				if (!thisMatch)
					return false;
			}
		}
		return true;
	}

}

/*
这段代码定义了一个名为 `VisualEntity` 的类，用于表示视觉实体，并提供了一些与实体比较和处理相关的方法。以下是对代码的主要部分进行的解释：

1. **静态属性和数组定义：**
   - `SIMILLARITY_THRESHOLD` 和 `ADJUST_THRESHOULD`：表示相似性阈值和调整阈值。
   - `entityNames`：表示实体的名称数组。
   - `entityThreshold`：表示每个实体的阈值数组。
   - `entityMap`：表示实体类型的映射数组。
   - `NUM_ENTITIES`：表示实体的总数。

2. **构造方法：**
   - `public VisualEntity(String xs, String ys, String ws, String hs, String confs)`：构造方法，用于根据传递的字符串参数初始化 `VisualEntity` 对象的属性。

3. **实例属性：**
   - `double x, y, w, h, conf`：表示实体的位置和大小属性，以及置信度。

4. **静态方法：**
   - `public static void writeVEntities(ArrayList<Vector<VisualEntity>> e, FileOutputStream outf) throws IOException`：将 `VisualEntity` 数据写入到输出流中的静态方法。
   - `public static ArrayList<Vector<VisualEntity>> loadVEntities(BufferedReader b, boolean man) throws IOException`：从输入流中加载 `VisualEntity` 数据的静态方法。
   - `public static Vector<VisualEntity> traceEntitiesMultiple(int idx, ArrayList<Vector<VisualEntity>> frame, Vector<VisualEntity> p2, double thr, double ethr)`：追踪多个实体的方法。
   - `public static Vector<VisualEntity> traceEntities(Vector<VisualEntity> p1, Vector<VisualEntity> p2, double thr)`：追踪实体的方法。
   - `public static boolean compareEntities(Vector<VisualEntity> p1, Vector<VisualEntity> p2)`：比较实体的方法。

5. **辅助方法：**
   - `public double area()`：计算实体的面积。
   - `public static Vector<Double> edgeOverlap(VisualEntity e1, VisualEntity e2)`：计算两个实体边缘的重叠。
   - `public static double overlapScore(VisualEntity e1, VisualEntity e2)`：计算两个实体的重叠分数。
   - `public static double mutualScore(VisualEntity e1, VisualEntity e2)`：计算两个实体的互斥分数。
   - `public static double overlap(double x1, double w1, double x2, double w2)`：计算两个范围的重叠。
   - `public static boolean edgeClose(int num, VisualEntity v1, VisualEntity v2, double ethr)`：判断两个实体的边缘是否靠近。

6. **其他方法：**
   - `public void write(int t, FileOutputStream outf) throws IOException`：将实体的信息写入输出流的方法。

总体来说，这个类用于表示和处理视觉实体，提供了一些静态和实例方法，用于计算实体之间的重叠、相似性等，并提供了写入和加载实体数据的方法。这些方法可能用于在视觉处理或计算机视觉中进行实体检测、匹配和比较。
*/
