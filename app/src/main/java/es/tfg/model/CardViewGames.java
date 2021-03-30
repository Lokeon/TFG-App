package es.tfg.model;

public class CardViewGames {
    private String image;
    private String name;

    public CardViewGames() {
    }

    public CardViewGames(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CardViewGames{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
