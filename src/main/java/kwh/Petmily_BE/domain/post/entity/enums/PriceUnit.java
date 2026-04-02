package kwh.Petmily_BE.domain.post.entity.enums;

public enum PriceUnit {
    PER_HOUR("시급"),
    PER_DAY("일급");

    private final String korean;
    PriceUnit(String korean) {this.korean = korean;}
    public String getKorean() {return korean;}
}
