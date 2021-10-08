/**
 * This is so pointless, I hope nobody looks at test cases, e.g., in 25 years from now
 */
public class AnotherClass {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String name;
    private String id;

    public AnotherClass(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
