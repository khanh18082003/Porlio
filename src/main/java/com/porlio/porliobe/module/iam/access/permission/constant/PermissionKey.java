package com.porlio.porliobe.module.iam.access.permission.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionKey {

  // ── AUTH MODULE ───────────────────────────────────────
  PERMISSION_CREATE("permission:create", "Create Permission", "POST", "/api/v1/admin/permissions",
      "AUTH"),
  PERMISSION_READ("permission:read", "View Permission Details", "GET",
      "/api/v1/admin/permissions/{id}", "AUTH"),
  PERMISSION_LIST("permission:list", "List Permissions", "GET", "/api/v1/admin/permissions",
      "AUTH"),
  ROLE_CREATE("role:create", "Create Role", "POST", "/api/v1/admin/roles", "AUTH"),
  ROLE_READ("role:read", "View Role Details", "GET", "/api/v1/admin/roles/{id}", "AUTH"),
  ROLE_LIST("role:list", "List Roles", "GET", "/api/v1/admin/roles", "AUTH"),
  ROLE_PERMISSION_ASSIGN("role:permission:assign", "Assign Permission to Role", "POST",
      "/api/v1/admin/roles/{id}/permissions", "AUTH"),

  // ── USER MODULE ──────────────────────────────────────
  USER_READ("user:read", "View User Details", "GET", "/api/v1/admin/users/{id}", "USER"),
  USER_LIST("user:list", "List Users", "GET", "/api/v1/admin/users", "USER"),
  USER_BAN("user:ban", "Ban / Unban User", "PATCH", "/api/v1/admin/users/{id}/ban", "USER"),
  USER_DELETE("user:delete", "Delete User", "DELETE", "/api/v1/admin/users/{id}", "USER"),
  USER_ROLE_ASSIGN("user:role:assign", "Assign Role to User", "POST",
      "/api/v1/admin/users/{id}/roles", "USER"),

  // ── PORTFOLIO MODULE ─────────────────────────────────
  PORTFOLIO_READ("portfolio:read", "View Portfolio", "GET", "/api/v1/admin/portfolios/{id}",
      "PORTFOLIO"),
  PORTFOLIO_LIST("portfolio:list", "List Portfolios", "GET", "/api/v1/admin/portfolios",
      "PORTFOLIO"),
  PORTFOLIO_UNPUBLISH("portfolio:unpublish", "Unpublish Portfolio", "PATCH",
      "/api/v1/admin/portfolios/{id}/unpublish", "PORTFOLIO"),
  PORTFOLIO_DELETE("portfolio:delete", "Delete Portfolio", "DELETE",
      "/api/v1/admin/portfolios/{id}", "PORTFOLIO"),

  // ── TEMPLATE MODULE ──────────────────────────────────
  TEMPLATE_CREATE("template:create", "Create Template", "POST", "/api/v1/admin/templates",
      "TEMPLATE"),
  TEMPLATE_UPDATE("template:update", "Update Template", "PUT", "/api/v1/admin/templates/{id}",
      "TEMPLATE"),
  TEMPLATE_DELETE("template:delete", "Delete Template", "DELETE", "/api/v1/admin/templates/{id}",
      "TEMPLATE"),
  TEMPLATE_TOGGLE("template:toggle", "Toggle Template Status", "PATCH",
      "/api/v1/admin/templates/{id}/toggle", "TEMPLATE"),

  // ── SYSTEM MODULE ────────────────────────────────────
  SYSTEM_STATS("system:stats", "View System Statistics", "GET", "/api/v1/admin/stats", "SYSTEM"),
  SYSTEM_AUDIT("system:audit", "View Audit Logs", "GET", "/api/v1/admin/audit-logs", "SYSTEM");

  private final String key;
  private final String name;
  private final String method;
  private final String urlPattern;
  private final String module;
}