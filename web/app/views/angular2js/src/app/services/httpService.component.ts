import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';

@Injectable()
export class HTTPService {
    constructor(private http: Http) { }

    post(url:string, data: Object) {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.http.post(url, data, options)
                        .map(this.extractData)
                        .catch(this.handleError);
    }
    get(url: string, data: Object) {
         let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.http.post(url, data, options)
                        .map(this.extractData)
                        .catch(this.handleError);
    }

    private extractData(res: Response) {
      let body = res.json();
        return body.data || { };
    }

  private handleError (error: Response | any) {
    // In a real world app, we might use a remote logging infrastructure
    /*let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);*/
    return error;
  }
}