package com.xusheng94.leyu.admin.cache;

import jakarta.annotation.PostConstruct;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class TableMetadataCache {

  private final Map<String, TableMetadata> metadataByTableName;
  private final String entityPackage;

  public TableMetadataCache(@Value("${project.base-package}") String basePackage) {
    this.entityPackage = basePackage + ".entity";
    this.metadataByTableName = scanEntityClasses().stream()
        .map(this::buildMetadata)
        .collect(Collectors.toUnmodifiableMap(
            metadata -> metadata.getTableName().toLowerCase(Locale.ROOT),
            metadata -> metadata));
  }

  public TableMetadata get(String tableName) {
    if (tableName == null) {
      return null;
    }
    return metadataByTableName.get(tableName.toLowerCase(Locale.ROOT));
  }

  @PostConstruct
  public void logMetadata() {
    String metadataSummary = metadataByTableName.values().stream()
        .map(metadata -> String.format(
            "%s[tenant=%s, dept=%s, creator=%s]",
            metadata.getTableName(),
            metadata.isHasTenantId(),
            metadata.isHasDeptId(),
            metadata.isHasCreatorId()))
        .sorted()
        .collect(Collectors.joining(", "));

    log.info("TableMetadataCache loaded from package {}: {}", entityPackage, metadataSummary);
  }

  private TableMetadata buildMetadata(Class<?> entityClass) {
    TableName tableName = entityClass.getAnnotation(TableName.class);
    if (tableName == null || tableName.value().isBlank()) {
      throw new IllegalStateException("Entity missing @TableName: " + entityClass.getName());
    }

    Set<String> columnNames = collectFields(entityClass)
        .map(this::resolveColumnName)
        .map(name -> name.toLowerCase(Locale.ROOT))
        .collect(Collectors.toSet());

    return new TableMetadata(
        tableName.value(),
        columnNames.contains("tenant_id"),
        columnNames.contains("dept_id"),
        columnNames.contains("creator_id"));
  }

  private Set<Class<?>> scanEntityClasses() {
    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(TableName.class));

    Set<Class<?>> entityClasses = new LinkedHashSet<>();
    scanner.findCandidateComponents(entityPackage).forEach(beanDefinition -> {
      String className = beanDefinition.getBeanClassName();
      if (className == null || className.isBlank()) {
        return;
      }
      try {
        entityClasses.add(Class.forName(className));
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Failed to load entity class: " + className, e);
      }
    });

    if (entityClasses.isEmpty()) {
      throw new IllegalStateException("No entity classes found under package: " + entityPackage);
    }

    return entityClasses;
  }

  private Stream<Field> collectFields(Class<?> type) {
    Stream<Field> currentFields = Arrays.stream(type.getDeclaredFields());
    Class<?> superclass = type.getSuperclass();
    if (superclass == null || Object.class.equals(superclass)) {
      return currentFields;
    }
    return Stream.concat(currentFields, collectFields(superclass));
  }

  private String resolveColumnName(Field field) {
    TableField tableField = field.getAnnotation(TableField.class);
    if (tableField != null && !tableField.value().isBlank()) {
      return tableField.value();
    }
    return camelToSnake(field.getName());
  }

  private String camelToSnake(String value) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char current = value.charAt(i);
      if (Character.isUpperCase(current) && i > 0) {
        builder.append('_');
      }
      builder.append(Character.toLowerCase(current));
    }
    return builder.toString();
  }

  @Getter
  public static class TableMetadata {
    private final String tableName;
    private final boolean hasTenantId;
    private final boolean hasDeptId;
    private final boolean hasCreatorId;

    public TableMetadata(String tableName, boolean hasTenantId, boolean hasDeptId, boolean hasCreatorId) {
      this.tableName = tableName;
      this.hasTenantId = hasTenantId;
      this.hasDeptId = hasDeptId;
      this.hasCreatorId = hasCreatorId;
    }
  }
}