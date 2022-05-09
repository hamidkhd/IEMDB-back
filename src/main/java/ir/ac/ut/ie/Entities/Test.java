package ir.ac.ut.ie.Entities;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Test {
    @Id
    private int id;
    private String name;

    public Test(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Test() {

    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
