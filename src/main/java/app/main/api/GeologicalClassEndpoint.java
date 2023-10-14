package app.main.api;

import app.main.dto.GeologicalClassDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.CompletionStage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("geological-classes")
public interface GeologicalClassEndpoint {

    @Operation(summary = "Create geological class", description = "Create geological class and save to DB")
    @PostMapping("/create")
    CompletionStage<GeologicalClassDTO> createGeologicalClass(@RequestBody GeologicalClassDTO geologicalClassDTO);

    @Operation(summary = "Fetch geological class", description = "Fetch geological class by id")
    @GetMapping("/{geological-class-id}/fetch")
    CompletionStage<GeologicalClassDTO> fetchGeologicalClass(@PathVariable("geological-class-id") Long geologicalClassId);

    @Operation(summary = "Update geological class", description = "Update geological class and save to DB")
    @PatchMapping("/{geological-class-id}/update")
    CompletionStage<GeologicalClassDTO> updateGeologicalClass(@PathVariable("geological-class-id") Long geologicalClassId,
                                                                @RequestBody GeologicalClassDTO geologicalClassDTO);

    @Operation(summary = "Delete geological class", description = "Delete geological by id")
    @RequestMapping("/{geological-class-id}/delete")
    CompletionStage<Boolean> deleteGeologicalClass(@PathVariable("geological-class-id") Long geologicalClassId);
}
