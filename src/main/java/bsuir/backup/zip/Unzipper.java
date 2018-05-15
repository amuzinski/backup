package bsuir.backup.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper
{
    private static final int BUFSIZE = 1024;
    
    public static void unzip(InputStream is, File targetDir) throws FileNotFoundException, IOException
    {
        ZipEntry entry;
        ZipInputStream zis = new ZipInputStream(is);
        byte[] buf = new byte[BUFSIZE];
        
        if (!targetDir.exists())
        {
            throw new FileNotFoundException(targetDir.toString() + " does not exist.");
        }

        if (!targetDir.isDirectory())
        {
            throw new FileNotFoundException(targetDir.toString() + " is not a directory.");
        }
        
        while ((entry = zis.getNextEntry()) != null)
        {
            String name = entry.getName(); 
            
            long size = entry.getSize();
            
            long time = entry.getTime();
            time = (time != -1) ? time : new Date().getTime();
            
            File f = new File(targetDir,name);
            
            
            
            if (entry.isDirectory())
            {
                f.mkdirs();
            }
            else
            {
                f.getParentFile().mkdirs();
                
                FileOutputStream fos = new FileOutputStream(f); 
                int len;
                while ((len = zis.read(buf,0,BUFSIZE)) > 0)
                {
                    fos.write(buf,0,len);
                    size-=len;
                }
                fos.close(); 
            }
            
            // size should be 0 here
            
            f.setLastModified(time);

            zis.closeEntry();
            
        }
        zis.close();
    }

    public static void main(String[] args) throws Exception
    {
        FileInputStream fis = new FileInputStream(args[0]);
        unzip(fis,new File(args[1]));
    }
}
