package jplag.volume;

/**
 * This class mocks the internal JPlag data structure and only provides the fields saved in CSV results.
 */
public class Result {

    // TODO SH: This shall be a record class in Java 16+

    private int number;
    private String nameA;
    private String nameB;
    private float percent;

    public Result(int number, String nameA, String nameB, float percent) {
        this.number = number;
        this.nameA = nameA;
        this.nameB = nameB;
        this.percent = percent;
    }

    public Result() {
    }

    public String getNameA() {
        return nameA;
    }

    public void setNameA(String nameA) {
        this.nameA = nameA;
    }

    public String getNameB() {
        return nameB;
    }

    public void setNameB(String nameB) {
        this.nameB = nameB;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
