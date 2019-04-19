package tudo.streamingrec.algorithms.dtos;

public class ItemCounterDto {
    public Long itemId;
    public long count;

    public ItemCounterDto(long itemId, long count) {
        this.itemId = itemId;
        this.count = count;
    }
}
