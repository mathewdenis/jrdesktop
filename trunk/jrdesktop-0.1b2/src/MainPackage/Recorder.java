package MainPackage;


import RMI.Viewer;

/**
 *
 * @author benbac
 */

public class Recorder extends Thread {
    
    private boolean recording = false;          // control recording
    private boolean viewOnly = false;
    private boolean pause = false;
    private String args[];
    
    private Player player;
    
    public Recorder(Player player, String args[]) {
        this.player = player;
        this.args = args;
    }
    
    @Override
    public void  run()
    { 
        while (true) {
            Wait();

            while (recording && !pause) {
                player.viewer.sendData();
                player.viewer.recieveData();
            } 
        }
    }
   
    public void Wait() {
        try {
            synchronized(this) {    
                wait();
            }
        }
        catch (Exception e) {
            e.getStackTrace();
        }         
    }
    
    public void Notify() {
        try {
            synchronized(this){            
                notify();
            }    
        }
        catch (Exception e) {
            e.getStackTrace();
        }         
    }
    
    public void Stop() {
        recording = false;   
        pause = true;
        viewOnly = false;
        player.screenPlayer.removeAdapters();
        if (player.screenPlayer.isFullScreenMode())
            player.screenPlayer.setScreenMode(false);
        player.screenPlayer.setDefaultScreenRect();
        player.screenPlayer.clearScreen();           
        player.viewer.disconnect();     
    }
    
    public void Start() {  
        player.viewer = new Viewer(player, args);                
        player.viewer.connect();
        recording = true;            
        pause = false;        
        player.screenPlayer.addAdapters();        
        Notify();     
    }
                
    public boolean isRecording () { 
        return recording;
    }
    
    public boolean isPaused() {
        return pause;
    }
    
    public void setPause(boolean bool) {
        pause = bool;
        if (pause)
               player.screenPlayer.removeAdapters();
        else
        {            
            if (recording && !viewOnly) {
               player.screenPlayer.addAdapters();
               Notify();
            }
        }
    }
    
    public void setViewOnly(boolean bool){
        viewOnly = bool;
        if (viewOnly)
            player.screenPlayer.removeAdapters();
        else
        {
            if (recording && !pause)
                player.screenPlayer.addAdapters();
        }
    }
        
    public boolean isViewOnly() {
        return viewOnly;
    }
}
