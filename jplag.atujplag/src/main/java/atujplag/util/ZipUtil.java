/*
 * Created on 10.02.2005
 */
package atujplag.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	/**
	 * Zips the given directory "dir" into a zip file in "dest"
	 * 
	 * @param dir
	 *            File denoting the directory to be zipped
	 * @param dest
	 *            Name of the directory where the zipped file will be stored,
	 *            which will be named dir.getName()+".zip"
	 * @return zipped file
	 */
	public static File zip(File dir, String dest) {
		File zippedFile = new File(dest + "/" + dir.getName() + ".zip");
		zipTo(dir, zippedFile);
		return zippedFile;
	}

	/**
	 * Zips the given directory "dir" into the zip file "destFile". If
	 * "destFile" already exists, it will be overwritten
	 * 
	 * @param dir
	 *            Directory to be zipped
	 * @param destFile
	 *            Destination file
	 */
	public static void zipTo(File dir, File destFile) {
		FileOutputStream ops = null;
		ZipOutputStream zos = null;
		try {
			ops = new FileOutputStream(destFile);
			zos = new ZipOutputStream(ops);
			zipDir(dir, zos, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zos != null)
					zos.close();
				else if (ops != null)
					ops.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Recursively zips all files in "dir" and its subdirectories into the given
	 * ZipOutputStream "zos" using the given path prefix for their names
	 */
	private static void zipDir(File dir, ZipOutputStream zos, String prefix) {
		File[] entries = dir.listFiles();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].isDirectory()) {
				// generate directory entry
				ZipEntry zi = new ZipEntry(prefix + "/" + entries[i].getName() + "/");
				try {
					zos.putNextEntry(zi);
					zos.closeEntry();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
				zipDir(entries[i], zos, prefix + "/" + entries[i].getName());
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(entries[i]);
					ZipEntry zi = new ZipEntry(prefix + "/" + entries[i].getName());
					zos.putNextEntry(zi);
					copystream(fis, zos);
					zos.closeEntry();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * Zips all files in "fileVector" to the zipfile "destFile". The pathnames
	 * of all files in fileVector must start with baseDir!
	 * 
	 * @param fileVector
	 *            Files to be zipped
	 * @param baseDir
	 *            Root directory for this zip file
	 * @param destFile
	 *            Destination file
	 */
	public static void zipFilesTo(Vector<File> fileVector, String baseDir, File destFile) {
		FileOutputStream ops = null;
		ZipOutputStream zos = null;
		int basedirlen = baseDir.length();
		if (!baseDir.endsWith(File.separator))
			basedirlen++;
		try {
			ops = new FileOutputStream(destFile);
			zos = new ZipOutputStream(ops);

			Iterator<File> iter = fileVector.iterator();
			while (iter.hasNext()) {
				File file = iter.next();
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					String name = file.getPath().substring(basedirlen);
					name = name.replace('\\', '/'); // Zip uses '/' as separator
					ZipEntry zi = new ZipEntry(name);
					zos.putNextEntry(zi);
					copystream(fis, zos);
					zos.closeEntry();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (Exception e) {
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zos != null)
					zos.close();
				else if (ops != null)
					ops.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Unzips the zip file "file" into the directory "dest"
	 * 
	 * @param file
	 *            The zip file
	 * @param destDir
	 *            Directory where the content of the zip file will be saved
	 */
	public static void unzip(File file, File destDir) {
		destDir.mkdir();
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				if (ze.isDirectory())
					(new File(destDir, ze.getName())).mkdir();
				else {
					// make sure directories exist in case the client
					// didn't provide directory entries!

					File f = new File(destDir, ze.getName());
					(new File(f.getParent())).mkdirs();

					FileOutputStream fos = null;
					BufferedOutputStream bos = null;
					InputStream in = null;
					try {
						fos = new FileOutputStream(f);
						bos = new BufferedOutputStream(fos);
						in = zipFile.getInputStream(ze);
						copystream(in, bos);
					} finally {
						if (bos != null)
							bos.close();
						else if (fos != null)
							fos.close();
						if (in != null)
							in.close();
					}
				}
			}
			zipFile.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	/**
	 * Copies the input stream to the output stream using a 1 kB buffer
	 * 
	 * @throws IOException
	 */
	private static void copystream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
	}

	public static void main(String[] args) {
		// zip(new File("/home/bikiri/Desktop/jplag-old"),
		// "/home/bikiri/Desktop");
		unzip(new File("/home/bikiri/Desktop/emma.zip"), new File("/home/bikiri/Desktop", "unzipresult"));
	}
}
