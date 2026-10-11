taskflow-frontend/
│
├── public/
│
├── src/
│ │
│ ├── app/
│ │ │
│ │ ├── core/
│ │ │ ├── auth/
│ │ │ │ ├── auth.service.ts
│ │ │ │ ├── token-storage.service.ts
│ │ │ │ ├── jwt.util.ts
│ │ │ │ └── auth.models.ts
│ │ │ │
│ │ │ ├── guards/
│ │ │ │ ├── auth.guard.ts
│ │ │ │ ├── guest.guard.ts
│ │ │ │ └── role.guard.ts
│ │ │ │
│ │ │ ├── interceptors/
│ │ │ │ ├── auth.interceptor.ts
│ │ │ │ ├── error.interceptor.ts
│ │ │ │ └── loading.interceptor.ts
│ │ │ │
│ │ │ ├── http/
│ │ │ │ └── api-base.service.ts
│ │ │ │
│ │ │ ├── config/
│ │ │ │ └── app-config.token.ts
│ │ │ │
│ │ │ └── notification/
│ │ │ └── toast.service.ts
│ │ │
│ │ ├── shared/
│ │ │ ├── ui/
│ │ │ │ ├── button/
│ │ │ │ ├── input/
│ │ │ │ ├── modal/
│ │ │ │ ├── dropdown/
│ │ │ │ ├── badge/
│ │ │ │ ├── avatar/
│ │ │ │ ├── spinner/
│ │ │ │ ├── empty-state/
│ │ │ │ ├── pagination/
│ │ │ │ └── confirm-dialog/
│ │ │ │
│ │ │ ├── pipes/
│ │ │ │ ├── relative-time.pipe.ts
│ │ │ │ ├── initials.pipe.ts
│ │ │ │ └── task-status.pipe.ts
│ │ │ │
│ │ │ ├── directives/
│ │ │ │ ├── click-outside.directive.ts
│ │ │ │ └── autofocus.directive.ts
│ │ │ │
│ │ │ └── validators/
│ │ │ └── password-match.validator.ts
│ │ │
│ │ ├── features/
│ │ │ │
│ │ │ ├── auth/
│ │ │ │ ├── pages/
│ │ │ │ │ ├── login/
│ │ │ │ │ └── register/
│ │ │ │ ├── components/
│ │ │ │ │ └── auth-card/
│ │ │ │ ├── services/
│ │ │ │ │ └── auth-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ ├── login-request.model.ts
│ │ │ │ │ ├── register-request.model.ts
│ │ │ │ │ └── auth-response.model.ts
│ │ │ │ └── auth.routes.ts
│ │ │ │
│ │ │ ├── dashboard/
│ │ │ │ ├── pages/
│ │ │ │ │ └── dashboard/
│ │ │ │ ├── components/
│ │ │ │ │ ├── stat-card/
│ │ │ │ │ ├── my-tasks-widget/
│ │ │ │ │ └── activity-feed/
│ │ │ │ ├── services/
│ │ │ │ │ └── dashboard-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ └── dashboard.routes.ts
│ │ │ │
│ │ │ ├── organizations/
│ │ │ │ ├── pages/
│ │ │ │ │ ├── organization-list/
│ │ │ │ │ ├── organization-detail/
│ │ │ │ │ └── organization-settings/
│ │ │ │ ├── components/
│ │ │ │ │ ├── organization-card/
│ │ │ │ │ ├── member-list/
│ │ │ │ │ ├── invite-member-modal/
│ │ │ │ │ └── role-badge/
│ │ │ │ ├── services/
│ │ │ │ │ └── organization-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ ├── organization.model.ts
│ │ │ │ │ ├── organization-member.model.ts
│ │ │ │ │ └── organization-role.enum.ts
│ │ │ │ └── organizations.routes.ts
│ │ │ │
│ │ │ ├── projects/
│ │ │ │ ├── pages/
│ │ │ │ │ ├── project-list/
│ │ │ │ │ ├── project-detail/
│ │ │ │ │ └── project-settings/
│ │ │ │ ├── components/
│ │ │ │ │ ├── project-card/
│ │ │ │ │ ├── project-members/
│ │ │ │ │ └── create-project-modal/
│ │ │ │ ├── services/
│ │ │ │ │ └── project-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ └── project.model.ts
│ │ │ │ └── projects.routes.ts
│ │ │ │
│ │ │ ├── tasks/
│ │ │ │ ├── pages/
│ │ │ │ │ ├── task-board/ (Kanban)
│ │ │ │ │ ├── task-list/
│ │ │ │ │ └── task-detail/
│ │ │ │ ├── components/
│ │ │ │ │ ├── task-card/
│ │ │ │ │ ├── task-column/
│ │ │ │ │ ├── task-form/
│ │ │ │ │ ├── task-filters/
│ │ │ │ │ ├── task-comments/
│ │ │ │ │ ├── task-labels/
│ │ │ │ │ └── task-activity/
│ │ │ │ ├── services/
│ │ │ │ │ └── task-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ ├── task.model.ts
│ │ │ │ │ ├── task-status.enum.ts
│ │ │ │ │ ├── task-priority.enum.ts
│ │ │ │ │ └── task-filter.model.ts
│ │ │ │ └── tasks.routes.ts
│ │ │ │
│ │ │ ├── comments/
│ │ │ │ ├── services/
│ │ │ │ │ └── comment-api.service.ts
│ │ │ │ └── models/
│ │ │ │ └── comment.model.ts
│ │ │ │
│ │ │ ├── notifications/
│ │ │ │ ├── pages/
│ │ │ │ │ └── notification-list/
│ │ │ │ ├── components/
│ │ │ │ │ ├── notification-bell/
│ │ │ │ │ └── notification-item/
│ │ │ │ ├── services/
│ │ │ │ │ └── notification-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ └── notification.model.ts
│ │ │ │ └── notifications.routes.ts
│ │ │ │
│ │ │ ├── profile/
│ │ │ │ ├── pages/
│ │ │ │ │ └── profile/
│ │ │ │ ├── services/
│ │ │ │ │ └── user-api.service.ts
│ │ │ │ ├── models/
│ │ │ │ │ └── user.model.ts
│ │ │ │ └── profile.routes.ts
│ │ │ │
│ │ │ └── settings/
│ │ │ ├── pages/
│ │ │ │ └── settings/
│ │ │ └── settings.routes.ts
│ │ │
│ │ ├── layout/
│ │ │ ├── main-layout/
│ │ │ │ └── main-layout.component.ts
│ │ │ ├── auth-layout/
│ │ │ │ └── auth-layout.component.ts
│ │ │ ├── components/
│ │ │ │ ├── sidebar/
│ │ │ │ ├── topbar/
│ │ │ │ ├── user-menu/
│ │ │ │ └── breadcrumbs/
│ │ │ └── layout.routes.ts
│ │ │
│ │ ├── app.component.ts
│ │ ├── app.config.ts
│ │ └── app.routes.ts
│ │
│ ├── environments/
│ │ ├── environment.ts
│ │ └── environment.production.ts
│ │
│ ├── styles/
│ │ ├── tailwind.css
│ │ ├── \_variables.css
│ │ └── \_utilities.css
│ │
│ ├── main.ts
│ └── index.html
│
├── angular.json
├── tailwind.config.js
├── postcss.config.js
├── tsconfig.json
├── package.json
└── README.md
