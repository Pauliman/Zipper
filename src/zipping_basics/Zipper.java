package zipping_basics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This final class offers one method that zipps a file or folder and all its sub-folders into a zip-file of the same structure.
 *  
 * @author pauliman
 *
 */

public final class Zipper {

	private static FileOutputStream fos;
	private static ZipOutputStream zos;
	private static BufferedOutputStream bos;
	private static ZipEntry entry;

	private static FileInputStream fis;
	private static BufferedInputStream bis;

	private static final int BUFFER_CAPACITY = 1024;
	private static byte[] buffer;									// The actual buffer a byte array with a capacity of 1024
	private static int buffer_size;									// The buffer size (not capacity) used by the read()-method of the BufferedInputStream. 
	
	

	/**
	 * This is the only method that can be accessed from the outside.
	 * @param source The unzipped folder to be turned into a zip file.
	 * @param target The yet to be written zip-file that will contain the entire folder (source)
	 * @param compression_level The level of compression expressed by an int-value. See Deflater class for constant details. 
	 * @return True if everything worked as planned, false if not.
	 */
	public static boolean zipFile(File source, File target, int compression_level) {
		boolean check = true;
		try {
			fos = new FileOutputStream(target);						// Primary output stream connecting to the file to be zipped
			zos = new ZipOutputStream(fos);							// Secondary zip-stream that writes zipped data to the primary stream
			zos.setMethod(ZipOutputStream.DEFLATED);				// Method of compression this expression is the default setting (may be omitted)
			zos.setLevel(compression_level);						// Sets the level of compression 0 = no compression 
			bos = new BufferedOutputStream(zos);					// Secondary stream that writes to the Zip-stream in a buffered fashion
		} catch (IOException e) {
			System.out.println("Zipper.zipFile() says: " + e);
			check = false;
		}
		if (source.isDirectory()) {									// If the source is a folder an additional method is called to deal with the peculiarities of folders.
			buffer = new byte[BUFFER_CAPACITY];
			if (manageFolder(source, ""))							// Because of recursive character of the called method the second argument must be empty, if the method is called for the first time.
				check = false;

		} else {													// If the source is just a file it will directly written to the zip-file
			buffer = new byte[BUFFER_CAPACITY];
			if (writeFileToZipStream(source, ""))					
				check = false;
		}
		try {
			zos.finish();											// All streams should always get closed, but make sure you keep those open that you need.
			bos.close();
			zos.close();
			fos.close();
		} catch (Exception e) {
			System.out.println("While closing streams (final), the following happend: " + e);
		}
		return true;
	} // end of zipFile()

	/**
	 * Receives a folder path and extracts all content (files and subfolders) into a File[] if it then detects another folder
	 * it calls itself and passes on the new folder path as the first parameter. The second parameter is the relative path/name of the current sub-folder
	 * seen from the perspective of the root or base folder. As folders get written as part of a file, you don't have to care for folders, just make sure 
	 * your files carry the all folders in their file name and these file names are passed on to the ZipEntry.
	 * @param source_folder The current folder to be written to the ZipOutputStream. Absolute folder
	 * @param name The relative path to the current folder. Empty at first and building up as it goes deeper down the folder hierarchy. 
	 * @return True if everything worked as planned, false if not.
	 */
	private static boolean manageFolder(File source_folder, String name) {
		boolean check = true;
		// System.out.println("Folder_path: " + source_folder.toString() + " -- Folder name: " + name);   Control output, uncomment if you like
		File[] all_files = source_folder.listFiles();													// File[] containing all files and folders of the current folder tier		
		for (File single_file : all_files) {															// Iteration over all the files and folders				
			if (single_file.isDirectory()) {															// If another folder is encountered ...
				manageFolder(single_file, name + File.separator + single_file.getName());				// Call the current method with: Arg1 absolute path to current sub-folder, Arg2 name of current folder(s) + "/" + name of current sub-folder  	
			} else {																					// If a simple file is encountered
				if (!writeFileToZipStream(single_file, name +File.separator + single_file.getName()))	// Call the writeFileToZip()-method with Arg1: absolute path to source file, Arg2 subfolder(s) + "/" + file name 
					check = false;
			}
		}
		return check;
	} // end of manageFolder()

	/**
	 * Writes a file to the BufferedOutputStream.
	 * @param source_file Absloute path a file in the source folder
	 * @param entry_name Relative path to a file starting at the root or base folder level.
	 * @return True if everything worked as planned, false if not.
	 */
	private static boolean writeFileToZipStream(File source_file, String entry_name) {
		entry_name = entry_name.equals("") ? entry_name : entry_name.substring(1);											// Due to initial empty argument the entry_name starts with a "/" which must be deleted.
		// System.out.println("Writing: " + source_file.toString() + " -- Zip Entry: " + entry_name.replaceFirst("", "")); Control output, uncomment if you like
		boolean check = true;
		try {
			fis = new FileInputStream(source_file);
			bis = new BufferedInputStream(fis, BUFFER_CAPACITY);
			entry = new ZipEntry(entry_name.equals("") ? source_file.getName() : entry_name);				//Reacts to an empty argument which happens with the first folder or a single file.
			zos.putNextEntry(entry);
			while ((buffer_size = bis.read(buffer, 0, BUFFER_CAPACITY)) != -1) {							// Reading and writing to a Buffered stream seems to improve data integrity
				bos.write(buffer, 0, buffer_size);
			}
		} catch (IOException e) {
			System.out.println("Zipper.writeFileToZipStream() says: " + e);
			check = false;
		}
		try {
			bos.flush();																					// Don't forget to flush the stream .
			zos.closeEntry();																				// Close every entry before you open a new one.
			bis.close();																					// The input streams will be attached to every file, so they must be closed after one file is done.
			fis.close();																					// Same here.
		} catch (IOException e) {
			System.out.println("While closing streams (file), the following happend: " + e);
		}
		return check;
	} // end of writeImageFileToZioStream()	

} // end of class
