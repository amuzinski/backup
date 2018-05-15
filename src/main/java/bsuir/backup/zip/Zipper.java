package bsuir.backup.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;


public class Zipper
{

    private static final int BUFSIZE = 1024;

    /*
     * @param base if base != null &&  f.getPath().startsWith(base.getPath()) 
     * then skip base.getPath() from zip entry name
     */

    public void zip(ZipOutputStream out, File f, File base) throws IOException {
        String name = f.getPath().replace('\\', '/');

        if (base != null)
        {
            String basename = base.getPath().replace('\\', '/');
            
            if (name.startsWith(basename))
            {
                name = name.substring(basename.length());
            }
        }            

        if (name.startsWith("/"))
        {
            name = name.substring(1);
        }

        ZipEntry entry = new ZipEntry(name);
        entry.setTime(f.lastModified());

        out.putNextEntry(entry);

        FileInputStream is = new FileInputStream(f);

        byte[] buf = new byte[BUFSIZE];

        try
		{
        int l;
        while ((l = is.read(buf)) > -1)
        {
            out.write(buf, 0, l);
        }
		}
        finally
		{
        	is.close();
		}
        out.closeEntry();

    }

    public void zip(ZipOutputStream out, Collection c, File vpath) throws IOException {
        Iterator it = c.iterator();
        while (it.hasNext())
        {
            File f = (File) it.next();
            zip(out, f, vpath);
        }
    }

    public static void main(String[] args) throws Exception
    {
        ZipOutputStream z = new ZipOutputStream(new FileOutputStream(
                "d:/temp/myfirst.zip"));

        IOFileFilter filter = new IOFileFilter()
        {

            public boolean accept(java.io.File file)
            {
                return true;
            }

            public boolean accept(java.io.File dir, java.lang.String name)
            {
                return true;
            }
        };

        Collection c = FileUtils.listFiles(new File(
                "/java/javadocs/j2sdk-1.4.1/docs/tooldocs"), filter, filter);

        new Zipper().zip(z, c, new File("/java/javadocs/j2sdk-1.4.1"));

        z.close();

    }
}