package njt.NJT_Projekat.mapper;

public interface DtoEntityMapper<D, E> {
    D toDto(E entity);
    E toEntity(D dto);
}
