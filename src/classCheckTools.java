import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class classCheckTools {

    /**
     * Java �汾����
     *
     * @author wuzhengwei
     *
     */


    private static final int JAVA_CLASS_MAGIC = 0xCAFEBABE;

    public final static int JDK_1_2 = 46;
    public final static int JDK_1_3 = 47;
    public final static int JDK_1_4 = 48;
    public final static int JDK_5 = 49;
    public final static int JDK_6 = 50;
    public final static int JDK_7 = 51;
    public final static int JDK_8 = 52;
    public final static int JDK_9 = 53;
    public final static int JDK_10 = 54;
    public final static int JDK_11 = 55;
    public final static int JDK_12 = 56;
    public final static int JDK_13 = 57;
    public final static int JDK_14 = 58;
    public final static int JDK_15 = 59;
    public final static int JDK_16 = 60;
    public final static int JDK_17 = 61;
    public final static int JDK_18 = 62;
    public final static int JDK_19 = 63;
    public final static int JDK_20 = 64;
    public final static int JDK_21 = 65;
    public final static int JDK_22 = 66;
    public final static int JDK_23 = 67;
    public final static int JDK_24 = 68;

    public static int getJDKVersion()
    {
        String version = System.getProperty("java.version");
        if (version != null) {
            if (version.matches("1\\.\\d.*")) {
                int v = Integer.parseInt(version.charAt(2) + "");
                if (v >= 2) {
                    return 44 + v;
                }
            } else {
                String[] parts = version.split("\\.");
                int v = Integer.parseInt(parts[0]);
                if (v >= 9) {
                    return 44 + v;
                }
            }
        }
        return -1;
    }

    public static int getCompileVersion(File file) throws Exception
    {
        if (file == null || !file.isFile() || !file.getName().matches(".*\\.((jar)|(class))"))
        {
            return -1;
        }
        int version = -1;
        if (file.getName().endsWith("jar"))
        {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements())
            {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class"))
                {
                    InputStream in = jarFile.getInputStream(entry);
                    version = getVersion(in);
                    in.close();
                    break;
                }
            }
            jarFile.close();
        }
        else
        {
            InputStream in = new FileInputStream(file);
            version = getVersion(in);
            in.close();
        }
        return version;
    }


    private static int getVersion(InputStream in) throws Exception
    {
        DataInputStream dis = new DataInputStream(in);
        int magic = dis.readInt();
        if (magic == JAVA_CLASS_MAGIC)
        {
            dis.readUnsignedShort();
            int majorVersion = dis.readUnsignedShort();
            return majorVersion;
        }
        return -1;
    }

}
