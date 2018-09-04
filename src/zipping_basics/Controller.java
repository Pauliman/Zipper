package zipping_basics;

import java.io.File;
import java.util.zip.Deflater;

public class Controller {
	
	private static final int BEST_COMPRESSION = Deflater.BEST_COMPRESSION;
	private static final int NO_COMPRESSION = Deflater.NO_COMPRESSION;

	public static void main(String[] args) {
		File source_file = new File("C:\\Users\\user\\Sources\\" , "installer.log");
		File source_folder = new File("C:\\Users\\user\\Sources\\Artistry\\");
		File source_folders = new File("C:\\Users\\user\\Sources\\PycharmProjects\\");
		
		File target_file = new File("C:\\Users\\user\\Targets\\", "SingleFile.zip");
		File target_folder = new File("C:\\Users\\user\\Targets\\", "SingleFolder.zip");
		File target_folders = new File("C:\\Users\\user\\Targets\\", "MultipleFolders.zip");
		
		 if(Zipper.zipFile(source_file, target_file, BEST_COMPRESSION))
			 System.out.println("Done");
		/* if(Zipper.zipFile(source_folder, target_folder, BEST_COMPRESSION))
			 System.out.println("Done");
		if(Zipper.zipFile(source_folders, target_folders, BEST_COMPRESSION))
			System.out.println("Done");*/

	}

}
