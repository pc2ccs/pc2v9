// Use appsettings.json in assets directory to set these at runtime!

export const environment = {
  production: true,
  baseUrl: 'http://placeholder-path/wti-api',
  websocketUrl: 'ws://placeholder-path/wti-api/ws',
  useMock: false,
  // Overwritten from assets/appconfig.json, which the WTI rewrites from pc2v9.ini at startup.
  hideWTIScoreboard: false,
  showOnlyMeOnWTIScoreboard: false,
  // Empty unless pc2v9.ini showRegistrationURL is an http or https URL.
  showRegistrationURL: ''
};
