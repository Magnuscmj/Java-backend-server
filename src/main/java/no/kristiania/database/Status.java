package no.kristiania.database;

public class Status extends IdEntity {
    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
