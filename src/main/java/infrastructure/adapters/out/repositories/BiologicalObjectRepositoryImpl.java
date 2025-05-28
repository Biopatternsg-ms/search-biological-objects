package infrastructure.adapters.out.repositories;

import domain.models.BiologicalObject;
import domain.port.out.repositories.BiologicalObjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BiologicalObjectRepositoryImpl implements BiologicalObjectRepository {

    @Override
    public BiologicalObject save(BiologicalObject biologicalObject) {
        return biologicalObject;
    }
}
