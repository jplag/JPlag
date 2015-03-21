package de.jbb;
 
public enum Land {
 
  DEUTSCHLAND ("Berlin", 82169000, 357104.07),
  RUSSLAND ("Moskau", 142400000, 17075400),
  USA ("Washington D. C.", 304482526, 9826630),
  FRANKREICH ("Paris", 64473140, 672352);
 
  private int einwohner;
  private final double flaeche;
  private final String hauptstadt;
 
  private Land(String hauptstadt, int einwohner, double flaeche) {
    this.hauptstadt = hauptstadt;
    this.einwohner = einwohner;
    this.flaeche = flaeche;
  }
 
  public int getEinwohnerProQKM() {
    return (int)(this.einwohner / this.flaeche);
  }
 
  public void setEinwohner(int einwohner) { this.einwohner = einwohner; }
 
  public String getHauptstadt() { return this.hauptstadt; }
}