package application.usecase;

import application.services.BuildBiologicalObjectService;
import domain.models.BiologicalObject;
import domain.port.in.FindBiologicalObject;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
public class FindBiologicalObjectUseCase implements FindBiologicalObject {

    private final BuildBiologicalObjectService buildBiologicalObjectService;

    @Override
    public BiologicalObject execute(String label) {
        return buildBiologicalObjectService.execute(label);
    }

}
