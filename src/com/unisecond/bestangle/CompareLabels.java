package com.unisecond.bestangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;

public class CompareLabels {


	public static void  main(String args[])
	{
		try {
			FileOutputStream fout = new FileOutputStream(args[2]); 

			String autoDir = args[0];
						
			File directory = new File(autoDir);
			File[] fList = directory.listFiles();

	        for (File file : fList){
				String fname =file.getName();
				FileInputStream stream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader buffer = new BufferedReader(reader);
				
				ArrayList<Vector<VisualEntity>> autoEntityTable = VisualEntity.loadVEntities(buffer, false);
				
				buffer.close();reader.close();stream.close();
				
				String manFile = args[1]+"/"+fname;
				stream = new FileInputStream(manFile);
				reader = new InputStreamReader(stream);
				buffer = new BufferedReader(reader);
						
				ArrayList<Vector<VisualEntity>> manEntityTable = VisualEntity.loadVEntities(buffer, true);
						
				buffer.close();reader.close();stream.close();
				
				if (manEntityTable==null || autoEntityTable == null) {
					System.out.print(fname+"\r\n");
					continue;
				}
				
				boolean already = true;
				for (int i=0; i<VisualEntity.NUM_ENTITIES; i++)
				{
					if (!VisualEntity.compareEntities(manEntityTable.get(i),autoEntityTable.get(VisualEntity.entityMap[i]))) {
						already = false;
						break;
					}
				}

				if (already) 
					fout.write((fname+"\r\n").getBytes());
	        }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}


/*
这段代码是一个 Java 程序，主要用于比较两组标签（手工标注和自动标注）之间的实体信息是否相符。以下是对代码的主要部分进行的解释：

1. **`main` 方法：**
   - `public static void main(String args[])`：程序的主入口方法，从命令行参数中获取文件目录路径、手工标注的文件路径和输出文件路径，并进行一系列的操作。

2. **文件读取和数据处理：**
   - `FileOutputStream fout = new FileOutputStream(args[2]);`：创建一个输出流，用于将比较结果写入输出文件。
   - `File directory = new File(autoDir);`：创建一个表示自动标注文件目录的 `File` 对象。
   - `File[] fList = directory.listFiles();`：获取目录中的文件列表。

3. **遍历文件列表比较标签：**
   - `for (File file : fList)`：遍历自动标注目录中的文件。
   - `String fname = file.getName();`：获取文件名。
   - `ArrayList<Vector<VisualEntity>> autoEntityTable = VisualEntity.loadVEntities(buffer, false);`：加载自动标注的实体数据。
   - `ArrayList<Vector<VisualEntity>> manEntityTable = VisualEntity.loadVEntities(buffer, true);`：加载手工标注的实体数据。
   - `if (manEntityTable == null || autoEntityTable == null)`：如果手工标注或自动标注的实体数据为空，则打印文件名并继续下一轮循环。
   - `boolean already = true;`：初始化一个布尔值，表示两组标签是否相符。
   - `for (int i=0; i<VisualEntity.NUM_ENTITIES; i++)`：遍历实体类型。
   - `if (!VisualEntity.compareEntities(manEntityTable.get(i), autoEntityTable.get(VisualEntity.entityMap[i])))`：比较手工标注和自动标注的实体数据是否相符，如果不相符，将 `already` 设为 `false`。
   - `if (already) fout.write((fname+"\r\n").getBytes());`：如果两组标签相符，则将文件名写入输出文件。

4. **异常处理：**
   - `catch(Exception e) { e.printStackTrace(); }`：捕捉并打印任何可能的异常。

总体来说，这段代码对于给定的自动标注目录中的每个文件，都加载了手工标注和自动标注的实体数据，然后比较两者是否相符。如果相符，将文件名写入输出文件。这样，输出文件中包含了两组标签相符的文件名。
*/