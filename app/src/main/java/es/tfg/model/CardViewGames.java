package es.tfg.model;

public class CardViewGames {
    private String id;
    private String image;
    private String name;

    public CardViewGames() {
    }

    public CardViewGames(String id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CardViewGames{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}



