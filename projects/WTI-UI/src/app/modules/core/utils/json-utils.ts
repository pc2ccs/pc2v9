

/**
 * Ensure a value is always returned as an array.  This is important because the
 * WTI Server may send certain elements as null or single objects while the WTI-UI
 * code tries to use them as an Iterable (for example, in a *ngFor loop in HTML templates).
 *
 * - If value is already an array → return it
 * - If value is a single object → wrap it into an array
 * - If value is null/undefined → return empty array
 */
export function ensureArray<T>(value: T | T[] | null | undefined): T[] {
  if (Array.isArray(value)) {
    return value;
  }
  if (value !== null && value !== undefined) {
    return [value];
  }
  return [];
}
