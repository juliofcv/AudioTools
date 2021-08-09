/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.audiotest;

/**
 *
 * @author imstu
 */
public class Main {
    
    public static void main (String... args) throws Exception {
        FrameSample fs = new FrameSample("D:\\ccc.mp3");
        for (float i : fs.getSamples()) {
            System.out.println(i);
        }
        System.out.println("duration: "+fs.getDuration());
        System.out.println("frame rate: "+fs.getFrameRate());
    }
    
}
