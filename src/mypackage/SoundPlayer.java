package mypackage;

import java.io.InputStream;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Audio;
import net.rim.device.api.ui.component.Dialog;

class SoundPlayer extends Thread {

    Player musicPlayer; // Java media player
    InputStream in;
    String sound;

    SoundPlayer(String pSound) {
    	sound = pSound;
        in = getClass().getResourceAsStream("/" + sound);            
    }

    public void run()
    {
        try
        {
            // Set InputStream to a midi file included as resource, as specified by
            
            if (in != null) {
	            musicPlayer = javax.microedition.media.Manager.createPlayer(in, "audio/x-wav");
	            musicPlayer.realize();
	            musicPlayer.prefetch();
	            musicPlayer.start();
            }
            
            //VolumeControl vc = (VolumeControl) musicPlayer.getControl("VolumeControl");                 
            //vc.setLevel(80);

            // Ready the data and start playing it.  To loop indefinitely, we set loopcount
            // to -1.
            //musicPlayer.setLoopCount(-1);

        }
        catch (Exception e) {
            //Dialog.alert("Error playing music: " + e.getMessage());
        	stopSound();
        }
    }

    private void stopSound()
    {
        if (musicPlayer == null)
            return;
        try {
        	musicPlayer.stop();
            try
            {
                if(musicPlayer.getState() == Player.PREFETCHED) {
                	musicPlayer.deallocate();
                }
                if(musicPlayer.getState() == Player.REALIZED || musicPlayer.getState() == Player.UNREALIZED) {
                	musicPlayer.close();
                	musicPlayer = null;
                }
            } 
            catch (Exception e) {
                Dialog.alert("Error stopping music: " + e.getMessage());
            }
            
            
        } catch (MediaException ex) {
            Dialog.alert("MediaException: " + ex.getMessage());
        }
    }

    // Stop playing music
    private void stopMusic()
    {
        try {
            // Tell player to stop playing
            musicPlayer.stop();
        }
        catch (Exception e)
        {
            Dialog.alert("Error stopping music");
        }
        // Then release the data and close out the player
        musicPlayer.deallocate();
        musicPlayer.close();
    }

    // The Playsound method plays a simple combinations of tones to simulate a firing
    // noise.  This was necessary, as due to a bug or limitation of the BlackBerry 8830
    // (the phone I do my testing on), playing a WAV file stopped the midi player and
    // any other sound effects.  Player doesn't appear to mix properly (if at all).  However,
    // a midi file can be played while using the Alert objects startAudio method which
    // can play a sequence of tones, so this is what we've done for now.
    public static void playSound()
    {
        // A sequence of frequencies and durations (eg 1400hz for 15ms, 1350hz for 15ms, etc)
        short[] fire = {1400, 15, 1350, 15, 1320, 20, 1300, 20, 1250, 25, 1200, 35};

        try {
            Alert.startAudio(fire, 50);
        }
        catch (Exception e) {
            Dialog.alert("Error playing sound effect.");
        }   
    }

    // Activates the phone's vibration functionality for a specific number of ms
    void vibrate(int passMilli) {
        Alert.startVibrate(passMilli);
    }
}
