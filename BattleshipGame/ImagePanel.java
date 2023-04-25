package PartIV;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
private int height=700;
private int width=1000;

ImagePanel(){
}
//    private Image img;
//    private int dotx;
//    private int doty;
//    private int h=444;
//    private int w=700;
//    private int os;
//    private int box;
//    
//    public void setdots(int score) {
//    if(score<25) {
//this.dotx=os-10+(box*(score%5))+(box/2);
//this.doty=os-10+(box*(score/5))-(box/2);
//    }else {
//    this.dotx=os-10+(box*(24%5))+(box/2);
//this.doty=os-10+(box*(24/5))-(box/2);
//    }
////System.out.println("dotx: "+dotx+", doty: "+doty+", os: "+os+", box: "+box);
//}
//    
//    public int getDotx() {
//return dotx;
//}
//
//
//public void setDotx(int dotx) {
//this.dotx = dotx;
//}
//
//
//public int getDoty() {
//return doty;
//}
//
//
//public void setDoty(int doty) {
//this.doty = doty;
//}
//
//public ImagePanel() {
//this(new ImageIcon("dummy.png").getImage());
//setdots(0);
//}
//
//public ImagePanel(String img) {
//        this(new ImageIcon(img).getImage());
//        System.out.println(img+" is img");
//    }
//    
//    public void setimagepath(String imagepath) {
//    this.img=new ImageIcon(imagepath).getImage();
//    System.out.println("new img is "+img);
//    }
//    
//    public ImagePanel(Image img) {
//        this.img = img;
//        setdots(0);
//        this.os=(w-h)/2;
//        this.box=h/7;
//    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
//    setPreferredSize(size);
//    setLayout(null);
//    }
//
//    public void paintComponent(Graphics g) {
//    g.drawImage(img, 50, 0, null);
//    }
}
