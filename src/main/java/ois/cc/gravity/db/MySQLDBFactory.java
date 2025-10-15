package ois.cc.gravity.db;

import ois.radius.cc.entities.sys.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ois.cc.gravity.AppConst;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MySQLDBFactory
{

    private final static Logger logger = LoggerFactory.getLogger(MySQLDBFactory.class);

    public static MySQLDB CreateGravityDB(Tenant client) throws ClassNotFoundException, IOException, Exception
    {
        ArrayList<Class> enclsnames = findEntityClassNames(client);
        return new MySQLDB(client, enclsnames);
    }

    public static MySQLDB CreateGravitySysDB(Tenant client) throws ClassNotFoundException, IOException, Exception
    {
        ArrayList<Class> enclsnames = findEntityClassNames(client);
        return new MySQLDB(client, enclsnames);
    }



    private static ArrayList<Class> findEntityClassNames(Tenant tnt) throws ClassNotFoundException, IOException
    {
        if (tnt.getCode().equals(AppConst.SYS_CLIENT_CODE))
        {
            return FindEntityClassInPkg(AppConst.RAD_CC_SYS_ENTITY_TENANT_PKG);
        }
        return FindEntityClassInPkg(AppConst.RAD_CC_ENTITY_TENANT_PKG);

    }

    private static ArrayList<Class> FindEntityClassInPkg(String packname) throws ClassNotFoundException, IOException, IOException
    {
        ArrayList<Class> classes = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = loader.getResources(packname.replace(".", "/"));

        for (URL url = null; resources.hasMoreElements()
                && ((url = resources.nextElement()) != null);)
        {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection)
            {
                checkJarFile((JarURLConnection) connection, packname, classes);
            }
            else
            {
                checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), packname, classes);
            }
        }
        return classes;
    }

    /**
     *
     * @param directory The directory to start with
     * @param pckgname The package name to search for. Will be needed for getting the Class object.
     * @param classes if a file isn't loaded but still is in the directory
     */
    private static void checkDirectory(File directory, String pckgname, ArrayList<Class> classes) throws ClassNotFoundException, IOException
    {
        File tmpDirectory;
        if (directory.exists() && directory.isDirectory())
        {
            String[] files = directory.list();
            for (String file : files)
            {
                if (file.endsWith(".class"))
                {
                    try
                    {
                        classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                    catch (NoClassDefFoundError e)
                    {
                        // do nothing. this class hasn't been found by the loader
                        logger.error(e.getMessage(), e);
                    }
                }
                else if ((tmpDirectory = new File(directory, file)).isDirectory())
                {
                    checkDirectory(tmpDirectory, pckgname + "." + file, classes);
                }
            }
        }
    }

    /**
     * @param connection the connection to the jar
     * @param pckgname the package name to search for
     * @param classes the current ArrayList of all classes. This method will simply add new classes.
     */
    private static void checkJarFile(JarURLConnection connection, String pckgname, ArrayList<Class> classes) throws ClassNotFoundException, IOException
    {
        JarFile jarFile = connection.getJarFile();

        Enumeration<JarEntry> entries = jarFile.entries();

        for (JarEntry jarEntry = null; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);)
        {
            String name = jarEntry.getName();
            if (name.contains(".class"))
            {
                name = name.substring(0, name.length() - 6).replace('/', '.');
                if (name.contains(pckgname))
                {
                    classes.add(Class.forName(name));
                }
            }
        }
    }
}
