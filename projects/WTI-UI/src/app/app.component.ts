import { Component } from '@angular/core';

/**
 * This AppComponent class is the container for the visible part of the WTI-UI Angular application.
 * The class AppModule (in file app.module.ts) arranges that the application is initialized
 * using Angular's APP_INITIALIZER mechanism.  Specifically, the AppModule invokes method 
 * AppInitService.initializeApp() to arrange that all app initialization
 * is done prior to the rendering of the AppComponent.
 */

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent { }