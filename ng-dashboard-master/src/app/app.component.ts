import { Component } from '@angular/core';
import {Login} from "./models/login";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  loginInfo:Login = {
      first_name:'Nileshkumar',
      last_name:'Shegokar',
      avatar:'administrator.png',
      title:'Solution Architect'
  };
}
