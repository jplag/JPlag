package de.jbb;
 
public enum Country {
 
  GERMANY ("Berlin", 82169000, 357104.07),
  RUSSIA ("Moscow", 142400000, 17075400),
  USA ("Washington D. C.", 304482526, 9826630),
  FRANCE ("Paris", 64473140, 672352);
 
  private int inhabitants;
  private final double area;
  private final String capitol;
 
  private Land(String capitol, int inhabitants, double area) {
    this.capitol = capitol;
    this.inhabitants = inhabitants;
    this.area = area;
  }
 
  public int getInhabitantsPerSquareKilometers() {
    return (int)(this.inhabitants / this.area);
  }
 
  public void setInhabitants(int inhabitants) { this.inhabitants = inhabitants; }
 
  public String getCapitol() { return this.capitol; }
}