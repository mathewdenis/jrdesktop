package MainPackage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author benbac
 */

public class ScreenPlayer extends JLabel {

    Player player;
    int i = 0;
    private float CompressionQuality = 1.0f;
    
    private Image img;
    public ImageIcon icon;
    protected Rectangle ScreenRect = new Rectangle(0 ,0 ,0 ,0);
    protected Rectangle prefferedScreenRect = new Rectangle(0 ,0 ,0 ,0);

    private boolean PartialScreenSize = false; // capture part or all the screen size
    private boolean fullScreenMode = false;
    
    private KeyAdapter keyAdapter;
    private MouseAdapter mouseAdapter;    
    private MouseWheelListener mouseWheelListener;
    private MouseMotionAdapter mouseMotionAdapter;
    
    private boolean isSelecting = false;
    
    // mouse cordination for selection
    private int srcx, srcy, destx, desty;

    // Stroke-defined outline of selection rectangle.
    private BasicStroke bs;

    // used to create a distinctive-looking selection rectangle outline.
    private GradientPaint gp;
   
    public ScreenPlayer(Player player) { 
        this.player = player;
        
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_F11)
                    setScreenMode(!fullScreenMode); 
                ScreenPlayer.this.player.viewer.AddObject(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e)
            {
                ScreenPlayer.this.player.viewer.AddObject(e);
            }          
        };
       
        mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                ScreenPlayer.this.player.viewer.AddObject(e);
            }
        };
        
        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ScreenPlayer.this.player.viewer.AddObject(e);
            } 
            
            @Override
            public void mouseDragged(MouseEvent e) {
                 if (isSelecting) {
                    destx = e.getX ();
                    desty = e.getY ();                 
                 }
                 else
                    ScreenPlayer.this.player.viewer.AddObject(e);                     
            }             
        };  
        
        mouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isSelecting) {
                    destx = srcx = e.getX ();
                    desty = srcy = e.getY ();
                }
                else
                    ScreenPlayer.this.player.viewer.AddObject(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                 ScreenPlayer.this.player.viewer.AddObject(e);
                 DoneSelecting();           
            }        
        };    

        setFocusable(true);
        InitialSelectionRect();
    };

    public void addAdapters() {
        addKeyListener(keyAdapter); 
        addMouseWheelListener(mouseWheelListener);
        addMouseMotionListener(mouseMotionAdapter);
        addMouseListener(mouseAdapter);        
    }
     
    public void removeAdapters() {
        removeKeyListener(keyAdapter);
        removeMouseWheelListener(mouseWheelListener);
        removeMouseMotionListener(mouseMotionAdapter);
        removeMouseListener(mouseAdapter);
    }
    
    public void UpdateScreen(ImageIcon img) {
        this.img = img.getImage();
        repaint(); 
        // setIcon(img);   
    }
    
    public void UpdateScreen(byte[] img) {
        if (PartialScreenSize) {
            setSize(ScreenRect.getSize());
            setLocation(ScreenRect.getLocation());
        }
        //this.img = ImageUtils.getImage(img); 
        this.img = ImageKit.read(img);
        repaint(); 
        // setIcon(img);
    }   
    
    public void setCompressionQuality(float cq) {
        CompressionQuality = cq;
    }
    
    public float getCompressionQuality() {
        return CompressionQuality;
    }    
    
    public boolean isCompressionEnabled() {
        return (CompressionQuality != -1 ? true : false);
    }
            
    @Override
    public void paint(Graphics g) {
     //   super.paint(g);
        g.drawImage(img, 0, 0, ScreenRect.width, ScreenRect.height, this);            
        DrawSelectingRect(g);
    }        
    
    public void clearScreen() {
        img = createImage(WIDTH, HEIGHT);
        repaint();
    }
    
    public void setDefaultScreenRect() {   
        PartialScreenSize = false;
        prefferedScreenRect = new Rectangle(0, 0, 0, 0);
    }  
    
    public void setScreenRect(Rectangle rect) {   
        ScreenRect = rect;  
    }  
    
    public Rectangle getPrefferedScreenRect() {
        return prefferedScreenRect;
    }    
            
    public boolean isPartialScreenSizeMode() {
        return PartialScreenSize;
    }
    
    public boolean isFullScreenMode() {
        return fullScreenMode;
    }
    
    public void setScreenMode(boolean mode) {        
        GraphicsDevice device = getGraphicsConfiguration().getDevice();
        
        if (device.isFullScreenSupported())
        {
            if (mode) {  
                player.dispose();
                player.setUndecorated(true);
                device.setFullScreenWindow(player);
                player.setVisible(true);
            }
            else {     
                player.dispose();
                player.setUndecorated(false);
                device.setFullScreenWindow(null);
                player.setVisible(true);
            }
            fullScreenMode = !fullScreenMode;            
        }
    }
       
    public void InitialSelectionRect() {
        // Define the stroke for drawing selection rectangle outline.
        bs = new BasicStroke (5, BasicStroke.CAP_ROUND, 
                               BasicStroke.JOIN_ROUND,
                               0, new float [] { 12, 12 }, 0);

        // Define the gradient paint for coloring selection rectangle outline.
        gp = new GradientPaint (0.0f, 0.0f, Color.red, 1.0f, 1.0f, Color.white, true);    
    }  
    
    public void DrawSelectingRect(Graphics g) {
        if (isSelecting)
            if (srcx != destx || srcy != desty)
            {
                // Compute upper-left and lower-right coordinates for selection
                // rectangle corners.

                int x1 = (srcx < destx) ? srcx : destx;
                int y1 = (srcy < desty) ? srcy : desty;

                int x2 = (srcx > destx) ? srcx : destx;
                int y2 = (srcy > desty) ? srcy : desty;

                // Establish selection rectangle origin.
                prefferedScreenRect.x = x1;
                prefferedScreenRect.y = y1;

                // Establish selection rectangle extents.
                prefferedScreenRect.width = (x2-x1)+1;
                prefferedScreenRect.height = (y2-y1)+1;

                // Draw selection rectangle.
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke (bs);
                g2d.setPaint (gp);
                g2d.draw (prefferedScreenRect);
          
                PartialScreenSize = true;
      }         
    }
    
    public boolean isSelecting () {
        return isSelecting;
    }
    
    public void startSelection() {
        isSelecting = true;
        Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
        setCursor(cursor);
    }
    
    public void DoneSelecting ()
    {        
        if (isSelecting) {
            isSelecting = false;
            if (PartialScreenSize)
                ScreenPlayer.this.player.jBtnPartialComplete.setText("Default screen");

            srcx = destx;
            srcy = desty;     
        
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(cursor);   
        }
    }
}