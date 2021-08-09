package com.gd.audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

/**
 *
 * @author Julio Chinchilla
 */
public class FrameSample {
    
    private final float duration;
    private float[] absSamples;
    private final float frameRate;
    
    private final static String PCM16 = "pcm_s16le";

    public float getDuration() {
        return duration;
    }

    public float[] getSamples() {
        return absSamples;
    }

    public float getFrameRate() {
        return frameRate;
    }
    
    /**
     * Análisis de audio a partir de un archivo de audio en cualquier formato
     * soportado, se convierte a PCM 16 Bits de forma temporal y se extrae
     * la duración y un análisis de frame rate de la pista, convertido a valores
     * absolutos en punto flotante, para un análsis de niveles de audio en pista
     * @param f
     * @throws IOException
     * @throws Exception 
     */
    public FrameSample (String f) throws IOException, Exception {
        File file = new File(f);
        float[] samples;
        File temporalDecodedFile = File.createTempFile("decoded_audio", ".wav");
        transcodeAudioStereo(file, temporalDecodedFile, PCM16, "wav", 44100);
        temporalDecodedFile.deleteOnExit();
        AudioInputStream in = AudioSystem.getAudioInputStream(temporalDecodedFile);
        AudioFormat fmt = in.getFormat();
        frameRate = fmt.getFrameRate();
        duration = in.getFrameLength() / fmt.getFrameRate();
        if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
            throw new UnsupportedAudioFileException("unsigned");
        boolean big = fmt.isBigEndian();
        int chans = fmt.getChannels();
        int bits = fmt.getSampleSizeInBits();
        int bytes = bits + 7 >> 3;	
        int frameLength = (int) in.getFrameLength();
        int bufferLength = chans * bytes * 1024;
	samples = new float[frameLength];
        byte[] buf = new byte[bufferLength];		
        int i = 0;
        int bRead;
        while ((bRead = in.read(buf)) > -1) {			
            for (int b = 0; b < bRead;) {
            double sum = 0;			
            for (int c = 0; c < chans; c++) {
                if (bytes == 1) {
		sum += buf[b++] << 8;
                } else {
                int sample = 0;
                if (big) {
                    sample |= ( buf[b++] & 0xFF ) << 8;
                    sample |= ( buf[b++] & 0xFF );
                    b += bytes - 2;
                    } else {
                        b += bytes - 2;
                        sample |= ( buf[b++] & 0xFF );
                        sample |= ( buf[b++] & 0xFF ) << 8;
                    }			
                    final int sign = 1 << 15;
                    final int mask = -1 << 16;
                    if ( ( sample & sign ) == sign) {
                    sample |= mask;
                }				
                sum += sample;
                }
            }			
                samples[i++] = Math.abs((float) ( sum / chans ));
            }
        }
        temporalDecodedFile.delete();
        temporalDecodedFile.deleteOnExit();
        this.absSamples = samples;
    }
        
    
    /**
     * Transcode audio
     * @param sourceFile
     * @param destinationFile
     * @throws Exception 
     */
    private static void transcodeAudioStereo(File sourceFile , File destinationFile, String codec, String format, int sampling) throws Exception {
	AudioAttributes audio = new AudioAttributes();        
	audio.setCodec(codec);
	audio.setChannels(2);
	audio.setSamplingRate(sampling);
	EncodingAttributes attributes = new EncodingAttributes();
	attributes.setFormat(format);
	attributes.setAudioAttributes(audio);
	new Encoder().encode(new MultimediaObject(sourceFile), destinationFile, attributes);		
    }
    
}
