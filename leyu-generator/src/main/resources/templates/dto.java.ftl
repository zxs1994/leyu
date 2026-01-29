package ${package.Dto};

import ${package.Entity}.${entity};
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ${entity}Dto extends ${entity} {

    // TODO DTO 扩展字段写在这里

}