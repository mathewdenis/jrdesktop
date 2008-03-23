package MainPackage;

import RMI.Server;
import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author benbac
 */

public class robot extends Robot {

    private float CompressionQuality = 1.0f;
    Server server;
    private Rectangle screenRect = null;
    private Rectangle defaultScreenRect = null;
    private Rectangle emptyRect = new Rectangle(0, 0, 0, 0);
    
    private Toolkit tk = null;
    private BufferedImage screen = null;
    
    public robot(Server server) throws AWTException {
        this.server = server;
        tk = Toolkit.getDefaultToolkit();
        setDefaultScreenRect();  
        screenRect = defaultScreenRect;
    }

    public BufferedImage captureScreen() {
        BufferedImage image;
        screen = createScreenCapture(defaultScreenRect);    
        if (screenRect.equals(defaultScreenRect))
            return screen;  
        else {
            image = screen.getSubimage(
                screenRect.x, screenRect.y, 
                screenRect.width, screenRect.height);
            
            BufferedImage PartialScreen = new BufferedImage (
                    screenRect.width, screenRect.height, screen.getType());
            
            Graphics2D g2d = PartialScreen.createGraphics ();
            g2d.drawImage (image, null, 0, 0);
            g2d.dispose ();            
            return PartialScreen;
        }
    }

    public byte[] compressedCaptureScreen() {  
        return ImageKit.toByteArray(captureScreen(), CompressionQuality);
       //return ImageUtils.compressImage(captureScreen(), CompressionQuality);
    }    
    
    
    public void setDefaultScreenRect() {
        defaultScreenRect = new Rectangle(tk.getScreenSize());   
    }     
    
    public Rectangle getScreenRect() {
        return screenRect;
    }
    
    public void setScreenRect(Rectangle rect) {
        setDefaultScreenRect();
        if (rect.equals(emptyRect)) 
            screenRect = defaultScreenRect;
        else
            screenRect = getCustomScreenRect(rect);    
    }
    
    // pick to suitable size between custom and real screen rect    
    public Rectangle getCustomScreenRect(Rectangle rect) {
        int x = rect.x;
        int y = rect.y;        
        int width = rect.width;        
        int height = rect.height;

        if ((x < defaultScreenRect.x) || (x >= defaultScreenRect.width)) 
            x = defaultScreenRect.x;
        if ((y < defaultScreenRect.y) || (y >= defaultScreenRect.height)) 
            y = defaultScreenRect.y;        
        if ((width > defaultScreenRect.width) || (width <= defaultScreenRect.x)) 
            width = defaultScreenRect.width;        
        if ((height > defaultScreenRect.height) || (height <= defaultScreenRect.y)) 
            height = defaultScreenRect.height;
  
        return new Rectangle(x, y, width, height);
    }        
    
    public void AnalyseObject(Object object) {        
        long time = 0;        
        ArrayList Objects = (ArrayList) object;      
        for (int i=0; i<Objects.size(); i++) {            
            Object obj = Objects.get(i);
            long newTime = 0;
            if(obj instanceof InputEvent){
                newTime = ((InputEvent)obj).getWhen();
                if ((time != 0) & (newTime != time)){
                    try{
                        synchronized(this){                          
                             wait(newTime - time);
                        }
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }        
            }
            time = newTime;

            if (obj instanceof MouseEvent)
                applyMouseEvent((MouseEvent)obj);
            else if (obj instanceof KeyEvent)
                applyKeyEvent((KeyEvent)obj); 
            else if (obj instanceof Float)
                setCompressionQuality((Float) obj);            
            else if (obj instanceof Rectangle)
                setScreenRect((Rectangle)obj);
        }
    }
    
    public void applyMouseEvent(MouseEvent evt) {
        mouseMove(evt.getX() + screenRect.x, evt.getY() + screenRect.y);
        int buttonMask = 0;
        int buttons = evt.getButton();
        if ((buttons == MouseEvent.BUTTON1)) buttonMask = InputEvent.BUTTON1_MASK;
        if ((buttons == MouseEvent.BUTTON2)) buttonMask |= InputEvent.BUTTON2_MASK;
        if ((buttons == MouseEvent.BUTTON3)) buttonMask |= InputEvent.BUTTON3_MASK;     
        switch(evt.getID()) {         
            case MouseEvent.MOUSE_PRESSED: mousePress(buttonMask); break;
            case MouseEvent.MOUSE_RELEASED: mouseRelease(buttonMask); break;
            case MouseEvent.MOUSE_WHEEL: mouseWheel(((MouseWheelEvent) evt).getUnitsToScroll()); break;
        }          
    }
    
    public void applyKeyEvent(KeyEvent evt) {
        switch(evt.getID()) {
            case KeyEvent.KEY_PRESSED: keyPress(evt.getKeyCode()); break;
            case KeyEvent.KEY_RELEASED: keyRelease(evt.getKeyCode()); break; 
        }
    }     
    
    public void setCompressionQuality(Float quality) {
        CompressionQuality = quality;
    }
}
