package bsuir.backup.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;

//import de.jwi.ftp.FTPUploader;
import bsuir.backup.manager.servlets.Controller;
import bsuir.backup.zip.Unzipper;
import bsuir.backup.zip.Zipper;

public class Folder
{

	private boolean isNotInContext;

	String path;

	String url;

	private File myFile;

	File[] children;

	private FileWrapper[] wrappers;
	
	private Map<String,File> nameToFile;
	
	private List<FileWrapper> wrappersList;

	private List parents;

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	
	private boolean calcRecursiveFolderSize = false;
	
	
    public static final int SORT_NAME_UP = 1;
    public static final int SORT_NAME_DOWN = 2;
    public static final int SORT_DATE_UP = 3;
    public static final int SORT_DATE_DOWN = 4;
    public static final int SORT_SIZE_UP = 5;
    public static final int SORT_SIZE_DOWN = 6;

	public boolean isCalcRecursiveFolderSize()
	{
		return calcRecursiveFolderSize;
	}

	public List getParents()
	{
		return parents;
	}

	private Folder()
	{
		// NOP
	}

	public Folder(File f, String path, String url) throws IOException {
		myFile = f;
		this.path = path;
		this.url = url;

		if (!myFile.exists())
		{
			throw new IOException(f.getPath() + " does not exist.");
		}
	}

	public List<FileWrapper> getFiles()
	{
		return wrappersList;
	}
	
	public void load() {

		children = myFile.listFiles();
		
		if (children == null)
		{
			return; // Windows special folders
		}

		wrappers = new FileWrapper[children.length];
		
		nameToFile = new HashMap<String,File>(children.length);

		for (int i = 0; i < children.length; i++)
		{
			String name = children[i].getName();

			wrappers[i] = new FileWrapper(this, i);
			
			nameToFile.put(name, children[i]);
		}

		wrappersList = Arrays.asList(wrappers);
		
		String[] pp = path.split("/");

		if ("/".equals(path))
		{
			pp = new String[1];
		}

		pp[0] = "/";

		HRef[] parentLinks = new HRef[pp.length];
		String s;
		int p = 0;
		for (int i = 0; i < pp.length - 1; i++)
		{
			s = path.substring(0, 1 + path.indexOf("/", p));
			p = s.length();
			parentLinks[i] = new HRef(pp[i], s);
		}
		parentLinks[pp.length - 1] = new HRef(pp[pp.length - 1], null);

		parents = Arrays.asList(parentLinks);
		
		sort(SORT_NAME_UP);
	}

	private boolean checkFileName(String name) {
		if (name.indexOf("..") > -1)
		{
			return false;
		}
		return true;
	}

	private String rename(String[] selectedIDs, String target) throws OutOfSyncException {
		if (selectedIDs.length > 1)
		{
			return "More than 1 file selected";
		}

		if (!checkFileName(target))
		{
			return "Illegal target name";
		}

		File f = checkAndGet(selectedIDs[0]);

		if (null == f)
		{
			throw new OutOfSyncException();
		}

		File f1 = new File(f.getParent(), target);

		if (f1.exists())
		{
			return target + " allready exists";
		}

		if (!f.renameTo(f1))
		{
			return "failed to rename " + f.getName();
		}

		return "";
	}

	private File getTargetFile(String target) throws IOException {
		File f = null;
		
		if (target.startsWith(File.separator))
		{
			f = new File(target);
		}
		else
		{
			f = new File(myFile, target);
		}

		f = f.getCanonicalFile();

		return f;
	}

	private File checkAndGet(String id) {
		String s = null;
		try
		{
			s = URLDecoder.decode(id, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// NOP
		}

		String s1 = s.substring(0, s.lastIndexOf('.'));
		String s2 = s.substring(s.lastIndexOf('.') + 1);

		File f = nameToFile.get(s1);

		if (null == f)
		{
			return null; // File not found
		}

		long l = f.lastModified();

		if (!(Long.toString(l).equals(s2)))
		{
			return null; // File modification changed
		}

		return f;

	}

	public void sum()
	{
		calcRecursiveFolderSize = true;
	}

	public void sort(int mode) {
		Comparator<File> c = null;

		switch (mode)
		{
			case SORT_NAME_UP:
				c = NameFileComparator.NAME_COMPARATOR;
				break;
			case SORT_NAME_DOWN:
				c = NameFileComparator.NAME_REVERSE;
				break;
			case SORT_SIZE_UP:
				c = SizeFileComparator.SIZE_COMPARATOR;
				break;
			case SORT_SIZE_DOWN:
				c = SizeFileComparator.SIZE_REVERSE;
				break;
			case SORT_DATE_UP:
				c = LastModifiedFileComparator.LASTMODIFIED_COMPARATOR;
				break;
			case SORT_DATE_DOWN:
				c = LastModifiedFileComparator.LASTMODIFIED_REVERSE;
				break;
		}

		Arrays.sort(children, c);
	}

	private String unzip(String[] selectedIDs) throws OutOfSyncException {
		StringBuffer sb = new StringBuffer();
		boolean done;

		for (int i = 0; i < selectedIDs.length; i++)
		{
			File f = checkAndGet(selectedIDs[i]);

			if (null == f)
			{
				throw new OutOfSyncException();
			}

			FileInputStream is = null;
			try
			{
				is = new FileInputStream(f);
				Unzipper.unzip(is, myFile);
				done = true;
			}
			catch (FileNotFoundException e)
			{
				done = false;
			}
			catch (IOException e)
			{
				done = false;
			}
			finally
			{
				if (null != is)
				{
					try
					{
						is.close();
					}
					catch (IOException e)
					{
						// NOP
					}
				}
			}
			if (!done)
			{
				sb.append(f.getName());
			}
		}

		String s = sb.toString();

		if (!"".equals(s))
		{
			return "failed to unzip " + s;
		}

		return "";
	}

	private String zip(OutputStream out, String[] selectedIDs) throws IOException, OutOfSyncException {

		Collection c = null;

		List l = new ArrayList();

		for (int i = 0; i < selectedIDs.length; i++)
		{
			File f = checkAndGet(selectedIDs[i]);

			if (null == f)
			{
				throw new OutOfSyncException();
			}

			if (f.isDirectory())
			{
				c = FileUtils.listFiles(f, TrueFileFilter.INSTANCE,
						TrueFileFilter.INSTANCE);
				l.addAll(c);
			}
			else
			{
				l.add(f);
			}
		}

		ZipOutputStream z = new ZipOutputStream(out);
		try
		{
			new Zipper().zip(z, l, myFile);
		}
		finally
		{
			z.close();
		}

		return null;
	}

	public String action(int action, OutputStream out, String[] selectedIDs,String target, HttpSession session) throws IOException, OutOfSyncException	{
		String res = null;

		switch (action)
		{
			case Controller.RENAME_ACTION:
				res = rename(selectedIDs, target);
				break;
			case Controller.UNZIP_ACTION:
				res = unzip(selectedIDs);
				break;
			case Controller.ZIP_ACTION:
				res = zip(out, selectedIDs);
				break;
		}

		if ("".equals(res)) // no error, action succeded.
		{
			load();
		}

		return res;
	}

	public void upload(FileItem item, boolean unzip) throws Exception {
		String name = item.getName();

		name = name.replaceAll("\\\\", "/");
		int p = name.lastIndexOf('/');
		if (p > -1)
		{
			name = name.substring(p);
		}
		if (unzip)
		{
			InputStream is = item.getInputStream();
			Unzipper.unzip(is, myFile);
		}
		else
		{
			File f = new File(myFile, name);
			item.write(f);
		}
	}

}