import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import * as Constants from 'src/constants';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit {
  configLoaded = false;

  constructor(private _httpClient: HttpClient, public router: Router) { }

  ngOnInit(): void {
  
    //check if we're loading for the first time
    if (getCurrentPage() == null) {
        //we have no current page so we must be starting from scratch
        console.log ('Starting from scratch...');
        
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
	} else {
	    //there is a current page stored; we must be reloading from (e.g.) an F5 refresh
	    console.log ('Restarting after refresh navigation');
	    
	    // transfer to the (former) "current page".
	    let page = getCurrentPage();
	    console.log ('Transferring to previous page: ' + page);
	    
	    //navigate to the most recently saved page
	    // TODO:  consider whether using history.pushState()/popState() is a better solution for this...
	    this.router.navigate(['/', page])
	      .then(nav => {
	        console.log("Call to router.navigate('/'," + page + ") returned " + nav);  //true if navigation is successful
	      }, err => {
	         console.log("Call to router.navigate('/'," + page + ") returned " + err)
	      });
	    
	}
	
  }
  
}

  //save the current page in sessionStorage so a subsequent F5 (refresh) can return to that page
  export function saveCurrentPage(page:string) {
      sessionStorage.setItem(Constants.CURRENT_PAGE_KEY, page) ;
  }
  
  //return the most recently saved page
  export function getCurrentPage() {
      return (sessionStorage.getItem(Constants.CURRENT_PAGE_KEY));
  }
  
  //clear any record of a "current page"
  export function clearCurrentPage() {
      sessionStorage.removeItem(Constants.CURRENT_PAGE_KEY);
  }
  
  //clear all data in sessionStorage
  export function clearSessionStorage() {
      sessionStorage.clear();
  }
