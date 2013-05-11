package com.modcrafting.mbd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class LibVerification extends JFrame {
    private static final long serialVersionUID = 7479571104450429337L;

    private void restartApplication() throws URISyntaxException, IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Chekkit.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        System.out.println(currentJar.getAbsolutePath());
        
        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            JOptionPane.showMessageDialog(this, "Please restart the application manually.");
            return;
        }
            

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }

    public void displayError() {
        

        Object[] options = {"Download libraries", "Exit" };
        File root = new File(Chekkit.PATH + File.separator + "lib");
        root.mkdir();
        File destination = new File(Chekkit.PATH + File.separator + "libs.zip");
        int exit = JOptionPane.showOptionDialog(null, "One or more libraries could not be located.\nTry downloading them from http://lol768.com/chekkit_lib.zip.", "Libraries not found.", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        if (exit == 0) {
            try {

                FileUtils.copyURLToFile(new URL("http://www.lol768.com/chekkit_lib.zip"), destination); //Please do feel free to mirror/change the location of this
                ZipFile zipFile = new ZipFile(destination);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File entryDestination = new File(Chekkit.PATH, entry.getName());
                    if (entryDestination.isDirectory()) {
                        continue;
                    }
                    System.out.println(entryDestination.getAbsolutePath());

                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                    
                }
                destination.delete();
                this.restartApplication();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Please try a manual download instead.");
                return;
            }
            
        }

    }

}
