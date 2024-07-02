import { type Ref, watch } from 'vue';
import type { RouteLocationNormalized, Router } from 'vue-router';

export function syncQueryParam<T>(
  variable: Ref<T>,
  router: Router,
  route: RouteLocationNormalized,
  queryParam: string,
  serializer: (value: T) => string | null | undefined,
  deserializer: (value: string) => T
) {
  function updateRouteQuery() {
    router.replace({ query: { ...route.query, [queryParam]: serializer(variable.value) } });
  }

  if (route.query[queryParam] !== undefined) {
    variable.value = deserializer(route.query[queryParam] as string);
  }

  watch(variable, () => {
    updateRouteQuery();
  });

  return {
    updateRouteQuery
  };
}

export function syncStringQueryParam(
  variable: Ref<string | null>,
  router: Router,
  route: RouteLocationNormalized,
  queryParam: string
) {
  return syncQueryParam(
    variable,
    router,
    route,
    queryParam,
    (value) => value || undefined,
    (value) => value
  );
}

export function syncBooleanQueryParam(
  variable: Ref<boolean>,
  router: Router,
  route: RouteLocationNormalized,
  queryParam: string
) {
  return syncQueryParam(
    variable,
    router,
    route,
    queryParam,
    (value) => (value ? null : undefined),
    (value) => value !== undefined
  );
}
