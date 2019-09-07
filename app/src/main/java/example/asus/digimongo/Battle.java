package example.asus.digimongo;

public class Battle {
    private String email;
    private int ATK;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getATK() {
        return ATK;
    }

    public void setATK(int ATK) {
        this.ATK = ATK;
    }

    public Battle(String email, int ATK) {
        this.email = email;
        this.ATK = ATK;

    }
}
