import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DrawingPanel extends JPanel  {

    private Color drawingColor = Color.BLACK;
    private Map map = null;

    public void setMap(Map map) {
        this.map = map;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(drawingColor);
        g.fillRect(0, 0, getSize().width, getSize().height);

        if (map != null) {
            int x = (getSize().width / 2) - (map.getWidth() / 2);
            int y = (getSize().height / 2) - (map.getHeight() / 2);
            map.draw(g, x, y);
        }
    }

    public int mapOffsetX (){
        int x = 0;
        if (map != null) {
            x = (getSize().width / 2) - (map.getWidth() / 2);
        }
        return x;
    }

    public int mapOffsetY (){
        int y = 0;
        if (map != null) {
            y = (getSize().height / 2) - (map.getHeight() / 2);
        }
        return y;
    }

}