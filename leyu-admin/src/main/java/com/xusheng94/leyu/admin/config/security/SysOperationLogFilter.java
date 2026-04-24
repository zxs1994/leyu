package com.xusheng94.leyu.admin.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xusheng94.leyu.admin.cache.SysPermissionCache;
import com.xusheng94.leyu.admin.config.security.jwt.JwtUtils;
import com.xusheng94.leyu.admin.entity.SysOperationLog;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.service.ISysOperationLogService;
import com.xusheng94.leyu.admin.util.CurrentUser;
import com.xusheng94.leyu.admin.util.SysPermissionMatcher;
import com.xusheng94.leyu.common.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SysOperationLogFilter extends OncePerRequestFilter {

	private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

	private final ISysOperationLogService sysOperationLogService;
	private final SysPermissionCache sysPermissionCache;
	private final JwtUtils jwtUtils;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		if (shouldSkip(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

		try {
			filterChain.doFilter(wrappedRequest, wrappedResponse);
		} finally {
			persistOperationLog(wrappedRequest, wrappedResponse);
			wrappedResponse.copyBodyToResponse();
		}
	}

	private boolean shouldSkip(HttpServletRequest request) {
		return !WRITE_METHODS.contains(request.getMethod());
	}

	private void persistOperationLog(ContentCachingRequestWrapper request,
			ContentCachingResponseWrapper response) {
		try {
			JsonNode responseJson = readJson(response.getContentAsByteArray(), response.getCharacterEncoding());
			boolean success = resolveSuccess(response, responseJson);
			String path = request.getRequestURI();
			String method = request.getMethod();
			Long userId = resolveUserId(request, responseJson, success);
			String requestBody = getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding());
			JsonNode requestJson = readJson(requestBody);
			SysPermission matchedPermission = resolvePermission(method, path);

			SysOperationLog operationLog = new SysOperationLog();
			operationLog.setUserId(userId);
			operationLog.setTenantId(CurrentUser.getTenantId());
			operationLog.setAction(resolveAction(matchedPermission));
			operationLog.setModule(resolveModule(matchedPermission));
			operationLog.setDataId(resolveDataId(path, requestJson, responseJson, matchedPermission));
			operationLog.setMethod(method);
			operationLog.setPath(truncate(path, 512));
			operationLog.setStatus(success);
			operationLog
					.setErrorMsg(success ? null : resolveErrorMessage(response, responseJson, matchedPermission));
			operationLog.setIp(truncate(resolveIp(request), 50));
			operationLog.setUserAgent(truncate(request.getHeader("User-Agent"), 512));
			sysOperationLogService.saveAsync(operationLog);
		} catch (Exception ex) {
			log.warn("Persist operation log failed, method={}, path={}", request.getMethod(), request.getRequestURI(), ex);
		}
	}

	private Long resolveUserId(ContentCachingRequestWrapper request, JsonNode responseJson, boolean success) {
		Long currentUserId = CurrentUser.getUserId();
		if (currentUserId != null) {
			return currentUserId;
		}

		if (!success || !"/auth/login".equals(request.getRequestURI()) || responseJson == null) {
			return null;
		}

		JsonNode dataNode = responseJson.get("data");
		if (dataNode == null || dataNode.isNull()) {
			return null;
		}

		JsonNode tokenNode = dataNode.get("token");
		if (tokenNode == null || tokenNode.isNull() || !StringUtils.hasText(tokenNode.asText())) {
			return null;
		}

		try {
			return jwtUtils.getSysUserIdFromToken(tokenNode.asText());
		} catch (Exception ex) {
			log.warn("Resolve login userId from token failed, path={}", request.getRequestURI(), ex);
			return null;
		}
	}

	private SysPermission resolvePermission(String method, String path) {
		return SysPermissionMatcher.matchExactThenGlobal(sysPermissionCache.listAll(), method, path);
	}

	private String resolveAction(SysPermission matchedPermission) {
		if (matchedPermission != null && StringUtils.hasText(matchedPermission.getName())) {
			return matchedPermission.getName();
		}
		return "未知操作";
	}

	private String resolveModule(SysPermission matchedPermission) {
		if (matchedPermission != null && StringUtils.hasText(matchedPermission.getModuleName())) {
			return matchedPermission.getModuleName();
		}

		return "未知模块";
	}

	private String resolveDataId(String path, JsonNode requestJson, JsonNode responseJson,
			SysPermission matchedPermission) {
		String pathDataId = resolveDataIdFromPath(path, matchedPermission);
		if (StringUtils.hasText(pathDataId)) {
			return pathDataId;
		}

		String requestDataId = resolveDataIdFromJson(requestJson);
		if (StringUtils.hasText(requestDataId)) {
			return requestDataId;
		}

		String responseDataId = resolveDataIdFromResponseData(responseJson);
		if (StringUtils.hasText(responseDataId)) {
			return responseDataId;
		}

		return null;
	}

	private String resolveDataIdFromResponseData(JsonNode responseJson) {
		if (responseJson == null || !responseJson.has("data")) {
			return null;
		}
		return resolveDataIdFromJson(responseJson.get("data"));
	}

	private String resolveDataIdFromPath(String path, SysPermission matchedPermission) {
		if (matchedPermission == null || !StringUtils.hasText(matchedPermission.getPath())) {
			return null;
		}

		String permissionPath = matchedPermission.getPath();
		if (!permissionPath.contains("{")) {
			return null;
		}

		LinkedHashMap<String, String> variables = new LinkedHashMap<>(
				PATH_MATCHER.extractUriTemplateVariables(permissionPath, path));
		if (variables.isEmpty()) {
			return null;
		}

		String idValue = variables.get("id");
		if (StringUtils.hasText(idValue)) {
			return idValue;
		}
		return variables.values().stream().filter(StringUtils::hasText).findFirst().orElse(null);
	}

	private String resolveDataIdFromJson(JsonNode jsonNode) {
		if (jsonNode == null || jsonNode.isNull()) {
			return null;
		}
		if (jsonNode.isIntegralNumber()) {
			return jsonNode.bigIntegerValue().toString();
		}
		if (jsonNode.isFloatingPointNumber()) {
			return jsonNode.decimalValue().toPlainString();
		}
		if (jsonNode.isTextual()) {
			String value = jsonNode.textValue();
			return StringUtils.hasText(value) ? value : null;
		}
		if (jsonNode.has("id") && !jsonNode.get("id").isNull()) {
			return resolveDataIdFromJson(jsonNode.get("id"));
		}
		return null;
	}

	private boolean resolveSuccess(ContentCachingResponseWrapper response, JsonNode responseJson) {
		if (responseJson != null && responseJson.has("success")) {
			return responseJson.get("success").asBoolean();
		}
		return response.getStatus() < HttpServletResponse.SC_BAD_REQUEST;
	}

	private String resolveErrorMessage(ContentCachingResponseWrapper response,
			JsonNode responseJson, SysPermission matchedPermission) {
		if (responseJson != null && responseJson.has("msg")) {
			String msg = responseJson.get("msg").asText();
			if (StringUtils.hasText(msg)) {
				return msg;
			}
		}

		String rawBody = getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
		if (StringUtils.hasText(rawBody)) {
			return truncate(rawBody, 1000);
		}

		int status = response.getStatus();
		if (status == HttpServletResponse.SC_FORBIDDEN) {
			if (matchedPermission != null &&
					StringUtils.hasText(matchedPermission.getName())) {
				return "没有【" + matchedPermission.getName() + "】权限";
			}
			return "没有权限";
		}

		return response.getStatus() >= HttpServletResponse.SC_BAD_REQUEST
				? ApiResponse.getMsgByStatus(status)
				: null;
	}

	private JsonNode readJson(byte[] body, String encoding) {
		if (body == null || body.length == 0) {
			return null;
		}
		try {
			// 直接按字节解析 JSON，避免因 response characterEncoding 不准确导致中文乱码
			return objectMapper.readTree(body);
		} catch (Exception ex) {
			return null;
		}
	}

	private JsonNode readJson(String body) {
		if (!StringUtils.hasText(body)) {
			return null;
		}
		try {
			return objectMapper.readTree(body);
		} catch (Exception ex) {
			return null;
		}
	}

	private String getContentAsString(byte[] body, String encoding) {
		if (body == null || body.length == 0) {
			return null;
		}
		Charset charset = StringUtils.hasText(encoding) ? Charset.forName(encoding) : StandardCharsets.UTF_8;
		return new String(body, charset);
	}

	private String resolveIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(forwardedFor)) {
			String firstIp = forwardedFor.split(",")[0].trim();
			if (StringUtils.hasText(firstIp) && !"unknown".equalsIgnoreCase(firstIp)) {
				return normalizeIp(firstIp);
			}
		}

		String realIp = request.getHeader("X-Real-IP");
		if (StringUtils.hasText(realIp) && !"unknown".equalsIgnoreCase(realIp)) {
			return normalizeIp(realIp.trim());
		}

		return normalizeIp(request.getRemoteAddr());
	}

	private String normalizeIp(String ip) {
		if (!StringUtils.hasText(ip)) {
			return ip;
		}
		if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			return "127.0.0.1";
		}
		return ip;
	}

	private String truncate(String value, int maxLength) {
		if (!StringUtils.hasText(value) || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength);
	}
}