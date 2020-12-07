import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  configLoaded = false;

  constructor(private _httpClient: HttpClient) { }

  ngOnInit(): void {
    // Load appconfig.json from assets directory, overwrite environment.ts
    // with these values
    this._httpClient.get('assets/appconfig.json')
      .subscribe((data: any) => {
        this.configLoaded = true;
        if (!data) { return; }
        Object.keys(data).forEach((key: string) => environment[key] = data[key]);
      }, (error: any) => {
        console.log('could not find appconfig.json in assets directory. using default values!');
        this.configLoaded = true;
      });
  }
}
