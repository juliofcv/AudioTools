
package com.gd.audio;

import java.io.File;

/**
 *
 * @author Julio Chinchilla
 */
public class Main {
    
    public static void main (String... args) throws Exception {
        FrameSample fs = new FrameSample("D:\\dd.mp3");
        for (float i : fs.getSamples()) {
            System.out.println(i);
        }
        System.out.println("duration: "+fs.getDuration());
        System.out.println("frame rate: "+fs.getFrameRate());
    }
    
}
