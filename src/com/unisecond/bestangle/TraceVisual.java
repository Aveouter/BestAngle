package com.unisecond.bestangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

public class TraceVisual {
	static int FORWARD_SZ = 10;
	
	public static void  main(String args[])
	{
		try {
			
			Vector<ArrayList<Vector<VisualEntity>>> visualFrames = new Vector<ArrayList<Vector<VisualEntity>>>();

			String autoDir = args[0];
			File directory = new File(autoDir);
			File[] fList = directory.listFiles();

	        for (File file : fList){
				FileInputStream stream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader buffer = new BufferedReader(reader);
				
				visualFrames.addElement(VisualEntity.loadVEntities(buffer, false));
				
				buffer.close();reader.close();stream.close();
				
	        }
	        
	        for (int i=0; i<visualFrames.size()-1; i+=4) {
				FileOutputStream fout = new FileOutputStream(args[1]+"/"+i+".txt");

				ArrayList<Vector<VisualEntity>> thisEntityTable = visualFrames.elementAt(i);
	        	VisualEntity.writeVEntities(thisEntityTable, fout);
	        	
	        	for (int j=1; j<FORWARD_SZ && i+j<visualFrames.size(); j++) {
	        		ArrayList<Vector<VisualEntity>> futureEntityTable = visualFrames.elementAt(i+j);
	        		ArrayList<Vector<VisualEntity>> crtEntityTable = new ArrayList<Vector<VisualEntity>>(VisualEntity.NUM_ENTITIES);

					for (int k=0; k<VisualEntity.NUM_ENTITIES; k++)
					{
						crtEntityTable.add(k,VisualEntity.traceEntitiesMultiple(k, thisEntityTable,
																			futureEntityTable.get(k), 
																			Double.parseDouble(args[2]),
																			Double.parseDouble(args[3])));
					}
					thisEntityTable = crtEntityTable;
	        	}
	        	fout.write(("\r\n\r\nTracing:\r\n").getBytes());
	        	VisualEntity.writeVEntities(thisEntityTable, fout);
	        	
	        	fout.close();
	        }

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}

/*
这段代码是一个 Java 程序，主要用于处理视觉实体的轨迹追踪。以下是对代码的主要部分进行的解释：

1. **静态属性：**
   - `FORWARD_SZ`：定义了前向追踪的帧数。

2. **`main` 方法：**
   - `public static void main(String args[])`：程序的主入口方法，从命令行参数中获取文件目录路径，并进行一系列的操作。

3. **文件读取和数据处理：**
   - `Vector<ArrayList<Vector<VisualEntity>>> visualFrames = new Vector<ArrayList<Vector<VisualEntity>>>();`：创建一个向量，用于存储每一帧的实体数据。
   - `FileInputStream`, `InputStreamReader`, `BufferedReader`：用于逐帧读取文件中的实体数据。
   - `VisualEntity.loadVEntities(buffer, false)`：调用 `loadVEntities` 方法加载实体数据，并将其存储到 `visualFrames` 向量中。

4. **实体追踪：**
   - `for (int i=0; i<visualFrames.size()-1; i+=4)`：遍历 `visualFrames` 中的每一帧，以一定步长（这里是4）进行追踪。
   - `FileOutputStream fout = new FileOutputStream(args[1]+"/"+i+".txt");`：创建一个输出流，将实体追踪结果写入文件。
   - `ArrayList<Vector<VisualEntity>> thisEntityTable = visualFrames.elementAt(i);`：获取当前帧的实体数据。
   - `VisualEntity.writeVEntities(thisEntityTable, fout);`：将当前帧的实体数据写入文件。
   - `for (int j=1; j<FORWARD_SZ && i+j<visualFrames.size(); j++)`：循环追踪若干帧（由 `FORWARD_SZ` 定义）的实体。
   - `VisualEntity.traceEntitiesMultiple`：调用实体追踪方法，得到追踪后的实体数据。
   - `fout.write(("\r\n\r\nTracing:\r\n").getBytes());`：在文件中添加一些标记，表示开始进行追踪。
   - `VisualEntity.writeVEntities(thisEntityTable, fout);`：将追踪后的实体数据写入文件。

5. **异常处理：**
   - `catch(Exception e) { e.printStackTrace(); }`：捕捉并打印任何可能的异常。

总体来说，这段代码通过读取一系列包含实体信息的文件，对每一帧进行实体追踪，并将结果写入输出文件。追踪的方式是从当前帧开始，依次追踪若干帧（由 `FORWARD_SZ` 定义），并将追踪后的实体数据写入文件。这样，每个输出文件包含了一系列帧的实体追踪结果。
*/
