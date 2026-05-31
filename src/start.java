import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class start {
    private JButton button1;
    private JTextField textField1;
    private JTable table1;
    private JScrollPane FileResult;
    private JPanel start;

    private Object[][] webAppsStr;
    private TableModel tm;
    private String[] Names = {"File", "Version"};

    private List<File> fileList;
    private List<classCheck> filecheck;

    private String systempath = System.getProperty("java.io.tmpdir");
    private String showtxt;

    private static final Map<Integer, String> VERSION_MAP = new LinkedHashMap<Integer, String>();
    static {
        VERSION_MAP.put(45, "JDK_1.1");
        VERSION_MAP.put(46, "JDK_1.2");
        VERSION_MAP.put(47, "JDK_1.3");
        VERSION_MAP.put(48, "JDK_1.4");
        VERSION_MAP.put(49, "JDK_5");
        VERSION_MAP.put(50, "JDK_6");
        VERSION_MAP.put(51, "JDK_7");
        VERSION_MAP.put(52, "JDK_8");
        VERSION_MAP.put(53, "JDK_9");
        VERSION_MAP.put(54, "JDK_10");
        VERSION_MAP.put(55, "JDK_11");
        VERSION_MAP.put(56, "JDK_12");
        VERSION_MAP.put(57, "JDK_13");
        VERSION_MAP.put(58, "JDK_14");
        VERSION_MAP.put(59, "JDK_15");
        VERSION_MAP.put(60, "JDK_16");
        VERSION_MAP.put(61, "JDK_17");
        VERSION_MAP.put(62, "JDK_18");
        VERSION_MAP.put(63, "JDK_19");
        VERSION_MAP.put(64, "JDK_20");
        VERSION_MAP.put(65, "JDK_21");
        VERSION_MAP.put(66, "JDK_22");
        VERSION_MAP.put(67, "JDK_23");
        VERSION_MAP.put(68, "JDK_24");
    }

    private static String getVersionName(int ver) {
        String v = VERSION_MAP.get(ver);
        return v != null ? v : "JDK_" + ver + " (UNKNOWN)";
    }

    private void setStatus(String text) {
        textField1.setText(text);
        textField1.paintImmediately(0, 0, textField1.getWidth(), textField1.getHeight());
    }

    public start() {
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                webAppsStr = null;
                tm = new DefaultTableModel(webAppsStr, Names);
                table1.setModel(tm);

                if (systempath == null || "".equals(systempath)) {
                    setStatus("system temp path = null");
                    return;
                }

                JFileChooser jf = new JFileChooser();
                jf.showDialog(null, null);
                File fi = jf.getSelectedFile();

                File distDir = new File(systempath + File.separator + "tempzipfile");
                if (!distDir.exists()) distDir.mkdirs();

                File dist = new File(distDir.getAbsolutePath() + File.separator + fi.getName());
                if (dist.exists()) dist.delete();

                try {
                    setStatus("get file:" + fi.getName());
                    copyFileUsingFileStreams(fi, dist);

                    if (dist.exists()) {
                        File unzipDir = new File(dist.getAbsoluteFile() + "_unzip");
                        if (unzipDir.exists()) {
                            unzipDir.delete();
                        } else {
                            unzipDir.mkdirs();
                        }

                        try {
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dist));
                            ZipInputStream zis = new ZipInputStream(bis);
                            BufferedOutputStream bos = null;
                            ZipEntry entry;

                            while ((entry = zis.getNextEntry()) != null) {
                                String entryName = entry.getName();
                                setStatus("unzip file:" + entryName);
                                System.out.println(entry.getName());
                                File temp = new File(unzipDir.getAbsoluteFile() + File.separator + entryName);

                                if (!entry.isDirectory() && entryName.matches(".*\\.((jar)|(class))")) {
                                    if (temp.exists()) {
                                        temp.delete();
                                    } else {
                                        temp.getParentFile().mkdirs();
                                    }

                                    bos = new BufferedOutputStream(new FileOutputStream(temp));
                                    int b;
                                    while ((b = zis.read()) != -1) {
                                        bos.write(b);
                                    }
                                    bos.flush();
                                    bos.close();
                                }
                            }
                            zis.close();
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }

                        fileList = showFiles(new File(dist.getAbsoluteFile() + "_unzip"));
                        filecheck = new ArrayList<classCheck>();

                        for (File temp : fileList) {
                            classCheck t = new classCheck();
                            t.setFilename(temp.getName());
                            int ver = classCheckTools.getCompileVersion(temp);

                            String v = getVersionName(ver);
                            setStatus("check file:" + temp.getName() + ":version=" + v);
                            t.setFileClass(v);
                            t.setCode(ver);
                            filecheck.add(t);
                        }

                        Map<String, List<String>> textshow = new HashMap<String, List<String>>();
                        Collections.sort(filecheck);
                        for (classCheck c : filecheck) {
                            List<String> tc = textshow.get(c.getFileClass());
                            if (tc != null) {
                                tc.add(c.getFilename());
                            } else {
                                List<String> newtc = new ArrayList<String>();
                                newtc.add(c.getFilename());
                                textshow.put(c.getFileClass(), newtc);
                            }
                        }

                        StringBuilder sb = new StringBuilder();
                        for (String sbtemp : textshow.keySet()) {
                            sb.append(sbtemp).append("=").append(textshow.get(sbtemp).size()).append("  ");
                        }
                        System.out.println(sb.toString());

                        showtxt = sb.toString();
                        if ("".equals(showtxt)) showtxt = "No java class!";
                        setStatus(showtxt);

                        webAppsStr = new String[filecheck.size()][2];
                        for (int i = 0; i < filecheck.size(); i++) {
                            webAppsStr[i][0] = filecheck.get(i).getFilename();
                            webAppsStr[i][1] = filecheck.get(i).getFileClass();
                        }

                        tm = new DefaultTableModel(webAppsStr, Names);
                        table1.setModel(tm);
                    }
                } catch (Exception es) {
                    es.printStackTrace();
                    setStatus("No java class!");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Java-version by wzw");
        frame.setContentPane(new start().start);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void copyFileUsingFileStreams(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            if (input != null) input.close();
            if (output != null) output.close();
        }
    }

    public List<File> showFiles(File f) {
        List<File> result = new ArrayList<File>();
        File[] flist = f.listFiles();
        if (flist != null) {
            for (File temp : flist) {
                if (temp.isDirectory()) {
                    result.addAll(showFiles(temp.getAbsoluteFile()));
                } else {
                    if (temp.getName().matches(".*\\.((jar)|(class))"))
                        result.add(temp);
                }
            }
        }
        return result;
    }
}
